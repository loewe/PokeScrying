package org.pokescrying.scheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.pokescrying.data.Gym;
import org.pokescrying.data.Raid;
import org.pokescrying.data.TelegramChat;
import org.pokescrying.repository.GymRepository;
import org.pokescrying.repository.RaidRepository;
import org.pokescrying.repository.TelegramChatRepository;
import org.pokescrying.service.GeolocationService;
import org.pokescrying.service.PokeScryingService;
import org.pokescrying.service.TelegramBotService;
import org.pokescrying.service.telegram.CommandParameter;
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
	private PokeScryingService pokescryingService;
	
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
		
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Calling sceduler");
		
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
					String parameter = CommandParameter.createPrivAskToActivateRaidString(tgChat.getId(), raid.getId());
					listing.append("<a href=\"https://t.me/PawniardBot?start=").append(parameter).append("\">");
					listing.append(gym.getName()).append("</a>\n");
					listing.append("└ ").append(raid.getStart().format(formatHHMM)).append("-").append(raid.getEnd().format(formatHHMM));
					listing.append(" | ").append(pokescryingService.translateIdToPokemon(raid)).append("\n");
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
}