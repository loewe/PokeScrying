package org.pokescrying.controller;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.pokescrying.data.Gym;
import org.pokescrying.events.Event;
import org.pokescrying.events.RaidEvent;
import org.pokescrying.repository.GymRepository;
import org.pokescrying.util.JsonTimestampDateTimeDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Controller
public class EventController {
	private static final String UNKNOWN_GYM_NAME = "unknown";

	private static final Logger LOGGER = LoggerFactory.getLogger(EventController.class);
	
	@Value("${events.timezone}")
	private String eventsTimezone;

	@Autowired
	private GymRepository gymRepository;

	@PostMapping("/event")
	@ResponseBody
	public void sayHello(@RequestBody(required = true) List<Event> events) {
		ObjectMapper mapper = new ObjectMapper();
		JavaTimeModule timeModule = new JavaTimeModule();
		timeModule.addDeserializer(LocalDateTime.class, new JsonTimestampDateTimeDeserializer(eventsTimezone));
		mapper.registerModule(timeModule);
		
		Set<String> ignoredEvents = new HashSet<>();
		ignoredEvents.add("weather");
		ignoredEvents.add("gym");
		ignoredEvents.add("pokestop");
		ignoredEvents.add("pokemon");
		
		long raidCount = 0;
		
		for (Event event : events) {
			if (event.getType().equals("raid")) {
				try {
					RaidEvent raidEvent = mapper.treeToValue(event.getMessage(), RaidEvent.class);
					handleRaidEventGymUpdate(raidEvent);
					raidCount++;
				}
				catch (JsonProcessingException e) {
					LOGGER.error("Could not convert 'message' part of raid event.", e);
				}
			}
			else if (!ignoredEvents.contains(event.getType())) {
				LOGGER.error("Unknown event type '{}' in message.", event.getType());
			}
		}
		
		if (raidCount > 0)
			LOGGER.debug("Received and processed {} raid events.", raidCount);
	}

	private void handleRaidEventGymUpdate(RaidEvent raidEvent) {
		List<Gym> gyms = gymRepository.findByGymId(raidEvent.getGymId());
		
		if (gyms.isEmpty()) {
			LOGGER.info("Found new gym with name {} at {},{}", raidEvent.getName(), raidEvent.getLatitude(), raidEvent.getLongitude());
			gymRepository.save(new Gym(raidEvent));
		}
		else if (gyms.size() == 1) {
			boolean dirtyGym = false;
			if (gyms.get(0).getName().equals(UNKNOWN_GYM_NAME) && 
					raidEvent.getName() != null && !raidEvent.getName().equals(UNKNOWN_GYM_NAME)) {
				gyms.get(0).setName(raidEvent.getName());
				dirtyGym = true;
			}
			
			if (!gyms.get(0).getName().equals(raidEvent.getName())) {
				gyms.get(0).setName(raidEvent.getName());
				dirtyGym = true;
			}
			
			if (gyms.get(0).getImageUrl() == null &&
					raidEvent.getUrl() != null && !raidEvent.getUrl().equals("")) {
				gyms.get(0).setImageUrl(raidEvent.getUrl());
				dirtyGym = true;
			}

			if (!gyms.get(0).getImageUrl().equals(raidEvent.getUrl())) {
				gyms.get(0).setImageUrl(raidEvent.getUrl());
				dirtyGym = true;
			}
			
			if (dirtyGym) {
				LOGGER.info("New gym metadata. Updating database with name {}.", raidEvent.getName());
				gymRepository.save(gyms.get(0));
			}
		}
		else {
			LOGGER.error("Corrupt database.");
		}
	}
}