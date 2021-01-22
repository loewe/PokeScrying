package org.pokescrying.scheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.pokescrying.data.Gym;
import org.pokescrying.data.Pokedex;
import org.pokescrying.data.Raid;
import org.pokescrying.data.TelegramChat;
import org.pokescrying.repository.GymRepository;
import org.pokescrying.repository.PokedexRepository;
import org.pokescrying.repository.RaidRepository;
import org.pokescrying.repository.TelegramChatRepository;
import org.pokescrying.service.GeolocationService;
import org.pokescrying.service.TelegramBotService;
import org.pokescrying.service.telegram.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TelegramScheduler {
	private static final long MINIMUM_LEVEL = 5L;

	private static final Logger LOGGER = LoggerFactory.getLogger(TelegramScheduler.class);
	
	@Autowired
	private GeolocationService geolocation;
	
	@Autowired
	private TelegramBotService telegram;
	
	@Autowired
	private RaidRepository raidRepository;
	
	@Autowired
	private GymRepository gymRepository;

	@Autowired
	private TelegramChatRepository telegramChatRepository;
	
	@Autowired
	private PokedexRepository pokedexRepository;
	
	@PostConstruct
	public void initTestChats() {
		boolean testChatfound = false;
		
		for (TelegramChat chat : telegramChatRepository.findAll()) {
			if (chat.getName().equals("Haar"))
				testChatfound = true;
		}
		
		if (!testChatfound) {
			TelegramChat haarChat = new TelegramChat();
			haarChat.setChatId(-1001164990930L);
			haarChat.setFenceName("Haar");
			haarChat.setName("Haar");
			telegramChatRepository.save(haarChat);
		}
	}
	
	@Scheduled(fixedRate = 5000)
	public void reportCurrentTime() {
	    DateTimeFormatter formatHHMM = DateTimeFormatter.ofPattern("HH:mm");
	    Map<TelegramChat, StringBuilder> listings = new HashMap<>();
		List<TelegramChat> telegramChats = new ArrayList<>();
		telegramChatRepository.findAll().forEach(telegramChats::add);
		LOGGER.info("Test");
		
		for (TelegramChat tgChat : telegramChats)
			if (!listings.containsKey(tgChat))
				listings.put(tgChat, new StringBuilder());
				
		for (Raid raid : raidRepository.findByEndGreaterThanEqualAndLevelGreaterThanEqualOrderByEndAsc(LocalDateTime.now(), MINIMUM_LEVEL)) {
			LOGGER.info("Found raid.");
			Optional<Gym> gymOpt = gymRepository.findById(raid.getGymId());
			
			if (!gymOpt.isPresent()) {
				LOGGER.error("No gym for that raid: {}", raid);
				break;
			}
			Gym gym = gymOpt.get();
			
			for (TelegramChat tgChat : telegramChats) {
				if (geolocation.isCoordinateInsideFence(gym.getLatitude(), gym.getLongitude(), tgChat.getFenceName())) {
					StringBuilder listing = listings.get(tgChat);
					listing.append("<a href=\"https://t.me/PawniardBot?start=").append(packPrivAskToActivateRaid(raid, tgChat)).append("\">");
					listing.append(gym.getName()).append("</a>\n");
					listing.append("└ ").append(raid.getStart().format(formatHHMM)).append("-").append(raid.getEnd().format(formatHHMM));
					listing.append(" | ").append(translateIdToPokemon(raid)).append("\n");
				}
			}
		}

		for (Map.Entry<TelegramChat, StringBuilder> entry : listings.entrySet()) {
			String message = entry.getValue().toString();
			long chatId = entry.getKey().getChatId();
			
			if (message.length() > 0) {
				int messageId = telegram.updateListing(chatId, entry.getKey().getListMessageId(), message, telegram.createListingKeyboard());
				entry.getKey().setListMessageId(messageId);
				telegramChatRepository.save(entry.getKey());
			}
			else {
				telegram.updateListing(chatId, entry.getKey().getListMessageId(), "Kein Raid", telegram.createListingKeyboard());
			}
		}
	}

	private String packPrivAskToActivateRaid(Raid raid, TelegramChat tgChat) {
		String startCommand = Command.PRIV_ASK_TO_ACTIVATE_RAID.ordinal() + "|" + tgChat.getId() + "|" + raid.getId();
		startCommand = Base64.getEncoder().encodeToString(startCommand.getBytes());
		return startCommand;
	}

	private String translateIdToPokemon(Raid raid) {
		if (raid.getPokemonId() == 0)
			return "Raid Level " + raid.getLevel();
		else {
			Optional<Pokedex> pokedexEntry = this.pokedexRepository.findById(raid.getPokemonId());
			if (pokedexEntry.isPresent()) {
				return pokedexEntry.get().getName();
			}
			else
				return "Pokemon " + raid.getPokemonId();
		}
	}
}