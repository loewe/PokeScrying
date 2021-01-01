package org.pokescrying.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.pokescrying.service.GeolocationService;
import org.pokescrying.service.TelegramBotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TelegramScheduler {
	private static final Logger LOGGER = LoggerFactory.getLogger(TelegramScheduler.class);
	
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	private int messageId = 0;
	
	private int i = 1;
	
	@Autowired
	private GeolocationService geolocation;
	
	@Autowired
	private TelegramBotService telegram;
	
	@PostConstruct
	public void initTestChats() {
		// initialize some chats for testing.
	}
	
	@Scheduled(fixedRate = 5000)
	public void reportCurrentTime() {
		if (LOGGER.isInfoEnabled())
			LOGGER.info("The time is now {}", dateFormat.format(new Date()));
		LOGGER.info("Is this in Haar? {}", geolocation.isCoordinateInsideFence(48.10965721915416, 11.726727572987196, "Haar"));
		
		long chatId = -1001164990930L;
	    String message = "X 2 von 5 Raids mit Zusagen\n<a href=\"https://t.me/PawniardBot?start=chatid1234\">Sankt Konrad</a> X\nL 23:00-23:45 | Surprise " + messageId + " Update " + i++;

	    messageId = telegram.updateOrCreateMessage(chatId, messageId, message, telegram.createListingKeyboard());
		
	}
}