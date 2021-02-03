package org.pokescrying.service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;

import org.pokescrying.data.Gym;
import org.pokescrying.data.Raid;
import org.pokescrying.data.TelegramChat;
import org.pokescrying.data.TrainerInfo;
import org.pokescrying.repository.GymRepository;
import org.pokescrying.repository.RaidRegistrationRepository;
import org.pokescrying.repository.RaidRepository;
import org.pokescrying.repository.TelegramChatRepository;
import org.pokescrying.repository.TrainerInfoRepository;
import org.pokescrying.service.telegram.Command;
import org.pokescrying.service.telegram.CommandParameter;
import org.pokescrying.service.telegram.RaidOption;
import org.pokescrying.service.telegram.YesNo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat.Type;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;

@Component
public class TelegramBotService {
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("org.pokescrying.messages");

	private static final Logger LOGGER = LoggerFactory.getLogger(TelegramBotService.class);

	@Value("${telegram.botToken}")
	private String botToken;

	private TelegramBot bot;
	
	@Autowired
	private TelegramChatRepository telegramChatRepository;
	
	@Autowired
	private RaidRepository raidRepository;
	
	@Autowired
	private RaidRegistrationRepository raidRegistrationRepository;
	
	@Autowired
	private TrainerInfoRepository trainerInfoRepository;
	
	@Autowired
	private GymRepository gymRepository;
	
	@Autowired
	private PokeScryingService pokescryingService;
	
	@PostConstruct
	public void initAPI() {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Initializing telegram bot with token: {}", botToken);
		bot = new TelegramBot(botToken);

		bot.setUpdatesListener(updates -> {
			if (LOGGER.isInfoEnabled())
				LOGGER.info("Received {} update.", updates.size());
			
			for (var update : updates) {
				handleUpdate(update);
			}
			
			return UpdatesListener.CONFIRMED_UPDATES_ALL;
		});
	}

	private void handleUpdate(Update update) {
		try {
			if (update.callbackQuery() != null) {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Received callback query: {}", update);

				handleBotCommand(update, update.callbackQuery().data());
			}
			if (update.message() != null && update.message().chat().type() == Type.Private) {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Received private message: {}", update);
				
				String[] startItems = update.message().text().split(" ");
				
				if (startItems.length > 1 && startItems[0].equals("/start"))
					handleBotCommand(update, startItems[1]);
			}
		}
		catch (Exception e) {
			LOGGER.error("Caught an exception: " , e);
		}
	}

	private void handleBotCommand(Update update, String parameterString) {
		CommandParameter parameter = new CommandParameter();
		parameter.unmarshall(parameterString);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Found a bot command: {}", parameter);
		}
		
		if (parameter.getCommand() != null) {
			Command command = parameter.getCommand();
		
			if (command.equals(Command.PRIV_ASK_TO_ACTIVATE_RAID))
				handlePrivAskToActivateRaid(update, parameter);
			else if (command.equals(Command.PRIV_CONFIRM_RAID_ACTIVE))
				handlePrivConfirmRaidActive(update, parameter);
		}
		else if (1 == 0) {
			long trainerId = syncTrainerAndGetId(update.message().from());
			
//							Optional<RaidRegistration> optRaidRegistration = raidRegistrationRepository.findByRaidIdAndTrainerId(optRaid.get().getId(), trainerId);
//							if (!optRaidRegistration.isPresent()) {
//								RaidRegistration registration = new RaidRegistration();
//								registration.setExtra(0);
//								registration.setRaidId(optRaid.get().getId());
//								registration.setType(RegistrationType.LOCAL);
//								registration.setTrainerId(trainerId);
//								registration.setTimeslot(1);
//								raidRegistrationRepository.save(registration);
//							}
		}
//						
//						
//						String message = "Willst du den Raid in der Arena '" + gymName + "' um '" + start + "' im Chat '" + chatName + "' ansagen und teilnehmen?";
//						SendMessage request = new SendMessage(chatId, message).replyMarkup(createYNKeyboard(Command.PRIV_ASK_TO_ACTIVATE_RAID)).parseMode(ParseMode.HTML);
//						executeAndCheck(request);
//						
//						new SendMessage(update.message().chat().id(), "").replyMarkup(createYNKeyboard(command)).parseMode(ParseMode.HTML));
		
//					executeAndCheck(new SendMessage(chatId, text))
//					
//					this.updateOrCreateMessage(update.message().chat().id(), 0, "Willst du den Raid in der Arena '' um '' im Chat '' ansagen und teilnehmen?", createYNKeyboard(Command.PRIV_CONFIRM_RAID_ACTIVE));
		
		
//						System.out.println(update.message().from().username() + " " + update.message().from().id() + " " + update.message().text());
	}

	private void handlePrivAskToActivateRaid(Update update, CommandParameter parameter) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("handlePrivAskToActivateRaid: {}", parameter);
		
		Optional<TelegramChat> optTelegramChat = telegramChatRepository.findById(parameter.getChatId());
		Optional<Raid> optRaid = raidRepository.findById(parameter.getRaidId());
		
		if (optTelegramChat.isPresent() && optRaid.isPresent()) {
			Raid raid = optRaid.get();
			Optional<Gym> optGym = gymRepository.findById(raid.getGymId());
			
			if (optGym.isPresent()) {
				String message = BUNDLE.getString("privAskToActivateRaidMessage");
				LocalDateTime start = raid.getStart();
				
				String pokemon =  pokescryingService.translateIdToPokemon(raid);
				message = MessageFormat.format(message, optGym.get().getName(), formatRaidStart(start), optTelegramChat.get().getName(), pokemon);
				
				parameter.setCommand(Command.PRIV_CONFIRM_RAID_ACTIVE);
				SendMessage sendMessage = new SendMessage(update.message().chat().id(), message).replyMarkup(createYNKeyboard(parameter)).parseMode(ParseMode.HTML);
				this.executeAndCheck(sendMessage);
				this.deleteMessage(update.message().chat().id(), update.message().messageId());
			}
		}
		else {
			if (optTelegramChat.isEmpty())
				LOGGER.error("Chat with id {} not found.", parameter.getChatId());
			if (optRaid.isEmpty())
				LOGGER.error("Raid with id {} not found.", parameter.getRaidId());
		}
	}

	private void handlePrivConfirmRaidActive(Update update, CommandParameter parameter) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("handlePrivConfirmRaidActive: {}", parameter);
		YesNo selectedChoice = YesNo.values()[(int)parameter.getOption()];
		
		if (YesNo.NO.equals(selectedChoice))
			this.deleteMessage(update.callbackQuery().message().chat().id(), update.callbackQuery().message().messageId());
		else if (YesNo.YES.equals(selectedChoice)) {
			Optional<Raid> optRaid = raidRepository.findById(parameter.getRaidId());
			
			if (optRaid.isPresent()) {
				Raid raid = optRaid.get();
				
				Optional<Gym> optGym = gymRepository.findById(raid.getGymId());
				
				if (optGym.isPresent()) {
					Gym gym = optGym.get();
					parameter.setCommand(Command.PRIV_ASK_SLOT_N_TYPE);
					
					String quest = BUNDLE.getString("privAskSlotNType");
					
					String pokemon = pokescryingService.translateIdToPokemon(raid);
					quest = MessageFormat.format(quest, pokemon, gym.getName(), formatRaidStart(raid.getStart()), formatRaidStart(raid.getEnd()));
					
					
					SendMessage sendMessage = new SendMessage(update.callbackQuery().message().chat().id(), quest).replyMarkup(createRaidKeyboard(parameter, raid)).parseMode(ParseMode.HTML);
					this.executeAndCheck(sendMessage);
				}
				
			}
			else {
				LOGGER.error("Raid with id {} not found.", parameter.getRaidId());
			}
			this.deleteMessage(update.callbackQuery().message().chat().id(), update.callbackQuery().message().messageId());
		}
	}

	private String formatRaidStart(LocalDateTime start) {
		return DateTimeFormatter.ofPattern("HH:mm").format(start);
	}

	private long syncTrainerAndGetId(User user) {
		Optional<TrainerInfo> optTrainerInfo = trainerInfoRepository.findByTelegramId(user.id().longValue());
		
		if (optTrainerInfo.isPresent()) {
			TrainerInfo trainerInfo = optTrainerInfo.get();
			return trainerInfo.getId();
		}
		else {
			TrainerInfo trainerInfo = new TrainerInfo();
			trainerInfo.setTrainerName(user.username());
			trainerInfo.setTelegramId(user.id().longValue());
			trainerInfoRepository.save(trainerInfo);
			return trainerInfo.getId();
		}
	}

	public InlineKeyboardMarkup createListingKeyboard() {
		return new InlineKeyboardMarkup(
				new InlineKeyboardButton[] { new InlineKeyboardButton("Map").callbackData("slot") },
				new InlineKeyboardButton[] { new InlineKeyboardButton("Chat").callbackData("slot") });
	}
	
	public InlineKeyboardMarkup createYNKeyboard(CommandParameter parameter) {
		parameter.setOption(YesNo.YES.ordinal());
		String yesString = parameter.marshall();
		parameter.setOption(YesNo.NO.ordinal());
		String noString = parameter.marshall();
		
		return new InlineKeyboardMarkup(new InlineKeyboardButton("Ja").callbackData(yesString),
						new InlineKeyboardButton("Abbrechen").callbackData(noString));
	}

	public InlineKeyboardMarkup createRaidKeyboard(CommandParameter parameter, Raid raid) {
		boolean prefixHour = false;
		LocalDateTime start = raid.getStart();
		LocalDateTime end = raid.getEnd().minus(5, ChronoUnit.MINUTES);
		
		List<InlineKeyboardButton> lists = new ArrayList<>();

		if (start.until(end, ChronoUnit.MINUTES) > 60)
			prefixHour = true;
		
		while (start.getMinute() % 5 != 0)
			start = start.plus(1, ChronoUnit.MINUTES);
		
		while (start.until(end, ChronoUnit.MINUTES) >= 0) {
			StringBuilder label = new StringBuilder();
			
			if (prefixHour)
				label.append(start.getHour()).append(':');
			
			if (start.getMinute() < 10)
				label.append("0");
			
			label.append(start.getMinute());

			parameter.setOption(start.getHour() * 100L + start.getMinute());
			lists.add(new InlineKeyboardButton(label.toString()).callbackData(parameter.marshall()));
			
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("calc time {} with label {}.", parameter.getOption(), label);
			
			start = start.plus(5, ChronoUnit.MINUTES);
		}
		
		int width = prefixHour?5:10;
		int height = lists.size()/width + 2;
		
		InlineKeyboardButton[][] keyboard = new InlineKeyboardButton[height][];
		
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Calculated {} times.", lists.size());
		
		for (int i = 0;i < height-1;i++) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Segmenting the times into row {} from {} to {}.", i, i*width, Math.min(i + width, lists.size()-1));
			keyboard[i] = new ArrayList<>(lists.subList(i*width, Math.min(i + width, lists.size()-1))).toArray(new InlineKeyboardButton[0]);
		}
		keyboard[height-1] = new InlineKeyboardButton[] {
			new InlineKeyboardButton("\u274C").callbackData(parameter.option(RaidOption.CANCEL.ordinal()).marshall()),
			new InlineKeyboardButton("+1").callbackData(parameter.option(RaidOption.PLUS1.ordinal()).marshall()),
			new InlineKeyboardButton("\u2708").callbackData(parameter.option(RaidOption.REMOTE.ordinal()).marshall()),
			new InlineKeyboardButton("\uD83D\uDE4F").callbackData(parameter.option(RaidOption.INVITE.ordinal()).marshall())
			,new InlineKeyboardButton("Fertig").callbackData(parameter.option(RaidOption.COMMENT.ordinal()).marshall())
		};
		// new String(Character.toChars(0x1F4DC))
		
		return new InlineKeyboardMarkup(keyboard);
	}
	
	private SendResponse executeAndCheck(SendMessage request) {
		SendResponse sendResponse = bot.execute(request);
		if (!sendResponse.isOk() && LOGGER.isErrorEnabled())
			LOGGER.error("Error sending telegram message: {}", sendResponse.description());
		return sendResponse;
	}
	
	private BaseResponse executeAndCheck(EditMessageText request) {
		BaseResponse sendResponse = bot.execute(request);
		if (!sendResponse.isOk() && LOGGER.isErrorEnabled())
			LOGGER.error("Error sending telegram message: {}", sendResponse.description());
		return sendResponse;
	}

	public int updateListing(long chatId, int messageId, String message, InlineKeyboardMarkup keyboard) {
		if (messageId > 0) {
			EditMessageText replyMarkup = new EditMessageText(chatId, messageId, message).replyMarkup(keyboard);
			replyMarkup = replyMarkup.parseMode(ParseMode.HTML);
			bot.execute(replyMarkup);
		} else {
			SendResponse sendResponse = this.executeAndCheck(new SendMessage(chatId, message).replyMarkup(keyboard).parseMode(ParseMode.HTML));
			messageId = sendResponse.message().messageId();
		}
		
		return messageId;
	}

	public void deleteMessage(long chatId, int messageId) {
		BaseResponse response = bot.execute(new DeleteMessage(chatId, messageId));
		LOGGER.debug("Telegram delete isOk? -> {}", response.isOk());
	}
}