package org.pokescrying.service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
			Optional<Gym> optGym = gymRepository.findById(optRaid.get().getGymId());
			
			if (optGym.isPresent()) {
				String message = ResourceBundle.getBundle("org.pokescrying.messages").getString("privAskToActivateRaidMessage");
				LocalDateTime start = optRaid.get().getStart();
				message = MessageFormat.format(message, optGym.get().getName(), formatRaidStart(start), optTelegramChat.get().getName());
				
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
		YesNo other = YesNo.values()[(int)parameter.getOption()];
		
		System.out.println(parameter);
		if (YesNo.NO.equals(other))
			this.deleteMessage(update.callbackQuery().message().chat().id(), update.callbackQuery().message().messageId());
		else if (YesNo.YES.equals(other)) {
			Optional<Raid> optRaid = raidRepository.findById(parameter.getRaidId());
			
			if (optRaid.isPresent()) {
				Raid raid = optRaid.get();
				
				boolean prefixHour = false;
				
				LocalDateTime start = raid.getStart();
				LocalDateTime end = raid.getEnd().minus(5, ChronoUnit.MINUTES);
				
				if (start.until(end, ChronoUnit.MINUTES) > 60) {
					prefixHour = true;
				}
				
				while (start.getMinute() % 5 != 0)
					start.plus(1, ChronoUnit.MINUTES);
					
				while (start.until(end, ChronoUnit.MINUTES) > 0) {
					
//					
//					String labelTest = (prefixHour)?start.getHour():""+
//					
//					start.getMinute()
//					
//					
//					
//					start = start.plus(5, ChronoUnit.MINUTES);
				}
				
//				if (start.)
				
				
//				start.getHour()
//				start.getMinute()
//
//				11:54 -> 11:55
//				12:00 -> 12:00
//				
//				if (difference is greater than 60) {
//					prefixHour = true;
//				}
//				
//				
//				
//				
//				
//				end.getHour()
//				end.getMinute
//				
				
			}
			else {
				LOGGER.error("Raid with id {} not found.", parameter.getRaidId());
			}
			
//			parameter.getRaidId()
			
			
			
			this.deleteMessage(update.callbackQuery().message().chat().id(), update.callbackQuery().message().messageId());
		}

//		Raid raid = optRaid.get();
//		raid.setPublished(true);
//		raid.setPublishTime(LocalDateTime.now());
//		this.raidRepository.save(raid);
//
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

	public InlineKeyboardMarkup createRaidKeyboard() {
		return new InlineKeyboardMarkup(
				new InlineKeyboardButton[] { new InlineKeyboardButton("5").callbackData("slot"),
						new InlineKeyboardButton("10").callbackData("slot"),
						new InlineKeyboardButton("15").callbackData("slot"),
						new InlineKeyboardButton("20").callbackData("slot"),
						new InlineKeyboardButton("25").callbackData("slot"),
						new InlineKeyboardButton("30").callbackData("slot"),
						new InlineKeyboardButton("35").callbackData("slot"), },
				new InlineKeyboardButton[] { new InlineKeyboardButton("40").callbackData("slot"),
						new InlineKeyboardButton("+1").callbackData("register_extra"),
						new InlineKeyboardButton("\u2708").callbackData("type_remote"),
						new InlineKeyboardButton(new String(Character.toChars(0x1F64F))).callbackData("type_invite"),
						new InlineKeyboardButton("\u274C").callbackData("cancel"),
						new InlineKeyboardButton(new String(Character.toChars(0x1F4DC))).callbackData("comment") });
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