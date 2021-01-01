package org.pokescrying.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
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

	@PostConstruct
	public void initAPI() {
		LOGGER.debug("Initializing telegram bot with token: {}", botToken);
		bot = new TelegramBot(botToken);

		bot.setUpdatesListener(updates -> {
			LOGGER.debug("Received update: {}", updates);
			return UpdatesListener.CONFIRMED_UPDATES_ALL;
		});
	}

	public InlineKeyboardMarkup createListingKeyboard() {
		return new InlineKeyboardMarkup(
				new InlineKeyboardButton[] { new InlineKeyboardButton("Map").callbackData("slot") },
				new InlineKeyboardButton[] { new InlineKeyboardButton("Chat").callbackData("slot") });
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

	public int updateOrCreateMessage(long chatId, int messageId, String message, InlineKeyboardMarkup keyboard) {
		BaseResponse response;
		
		if (messageId > 0) {
			EditMessageText replyMarkup = new EditMessageText(chatId, messageId, message).replyMarkup(keyboard);
			replyMarkup = replyMarkup.parseMode(ParseMode.HTML);
			response = bot.execute(replyMarkup);
		} else {
			SendMessage request = new SendMessage(chatId, message);
			request = request.replyMarkup(keyboard).parseMode(ParseMode.HTML);
			SendResponse sendResponse = bot.execute(request);
			messageId = sendResponse.message().messageId();
			response = sendResponse;
		}
		
		LOGGER.debug("Telegram update isOk? -> {}", response.isOk());
		return messageId;
	}

	public void deleteMessage(long chatId, int messageId) {
		BaseResponse response = bot.execute(new DeleteMessage(chatId, messageId));
		LOGGER.debug("Telegram delete isOk? -> {}", response.isOk());
	}
}