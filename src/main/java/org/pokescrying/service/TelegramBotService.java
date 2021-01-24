package org.pokescrying.service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.pokescrying.data.Raid;
import org.pokescrying.data.TelegramChat;
import org.pokescrying.data.TrainerInfo;
import org.pokescrying.repository.RaidRegistrationRepository;
import org.pokescrying.repository.RaidRepository;
import org.pokescrying.repository.TelegramChatRepository;
import org.pokescrying.repository.TrainerInfoRepository;
import org.pokescrying.service.telegram.Command;
import org.pokescrying.service.telegram.CommandParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat.Type;
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
	
	@PostConstruct
	public void initAPI() {
		LOGGER.debug("Initializing telegram bot with token: {}", botToken);
		bot = new TelegramBot(botToken);

		bot.setUpdatesListener(updates -> {
			LOGGER.info("Received update: {}", updates);
			
			for (var update : updates) {
				
				if (update.callbackQuery() != null) {
//					update.callbackQuery().data()
				}
				
				if (update.callbackQuery() != null) {
					System.out.println(update.callbackQuery().data());

				}
				if (update.message() != null && update.message().chat().type() == Type.Private) {
					System.out.println("Private: " + update);
					String[] command = update.message().text().split(" ");
					
					if (command.length > 1 && command[0].equals("/start")) {
						CommandParameter parameter = new CommandParameter();
						parameter.parse(command[1]);
						
						if (parameter.getCommand().equals(Command.PRIV_ASK_TO_ACTIVATE_RAID)) {
							Optional<TelegramChat> optTelegramChat = telegramChatRepository.findById(parameter.getChatId());
							Optional<Raid> optRaid = raidRepository.findById(parameter.getRaidId());
							Optional<TrainerInfo> optTrainerInfo = trainerInfoRepository.findByTelegramId(update.message().from().id());
							
							if (optTelegramChat.isPresent() && optRaid.isPresent()) {
								
								
								
							}
							else
								LOGGER.error("Chat with id {} not found", parameter.getChatId());
						}
						
	//					executeAndCheck(new SendMessage(chatId, text))
	//					
	//					this.updateOrCreateMessage(update.message().chat().id(), 0, "Willst du den Raid in der Arena '' um '' im Chat '' ansagen und teilnehmen?", createYNKeyboard(Command.PRIV_CONFIRM_RAID_ACTIVE));
						
						
						System.out.println(update.message().from().username() + " " + update.message().from().id() + " " + update.message().text());
					}
				}
			}
			
			return UpdatesListener.CONFIRMED_UPDATES_ALL;
		});
	}

	public InlineKeyboardMarkup createListingKeyboard() {
		return new InlineKeyboardMarkup(
				new InlineKeyboardButton[] { new InlineKeyboardButton("Map").callbackData("slot") },
				new InlineKeyboardButton[] { new InlineKeyboardButton("Chat").callbackData("slot") });
	}
	
	public InlineKeyboardMarkup createYNKeyboard(Command command) {
		return new InlineKeyboardMarkup(
				new InlineKeyboardButton[] { new InlineKeyboardButton("Ja").callbackData(command.ordinal() + ":yes"),
						new InlineKeyboardButton("Abbrechen").callbackData(command.ordinal() + ":no") });
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
	
	
	public void sendAskToActivateRaid(long chatId, String gymName, LocalDateTime start, String chatName) {
		String message = "Willst du den Raid in der Arena '" + gymName + "' um '" + start + "' im Chat '" + chatName + "' ansagen und teilnehmen?";
		SendMessage request = new SendMessage(chatId, message).replyMarkup(createYNKeyboard(Command.PRIV_ASK_TO_ACTIVATE_RAID)).parseMode(ParseMode.HTML);
		executeAndCheck(request);
	}

	private SendResponse executeAndCheck(SendMessage request) {
		SendResponse sendResponse = bot.execute(request);
		if (!sendResponse.isOk())
			LOGGER.error("Error sending telegram message: {}", sendResponse.description());
		return sendResponse;
	}
	
	private BaseResponse executeAndCheck(EditMessageText request) {
		BaseResponse sendResponse = bot.execute(request);
		if (!sendResponse.isOk())
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