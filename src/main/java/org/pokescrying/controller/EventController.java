package org.pokescrying.controller;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.pokescrying.data.Gym;
import org.pokescrying.data.Raid;
import org.pokescrying.events.Event;
import org.pokescrying.events.GymEvent;
import org.pokescrying.events.RaidEvent;
import org.pokescrying.repository.GymRepository;
import org.pokescrying.repository.RaidRepository;
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

	@Autowired
	private RaidRepository raidRepository;

	@PostMapping("/event")
	@ResponseBody
	public void handleEvents(@RequestBody(required = true) List<Event> events) {
		ObjectMapper mapper = createMapper();
		
		Set<String> ignoredEvents = new HashSet<>();
		ignoredEvents.add("weather");
		ignoredEvents.add("pokestop");
		ignoredEvents.add("pokemon");
		
		long raidCount = 0;
	    long gymCount = 0;
		
	    LOGGER.info("events called.");
	    
		for (Event event : events) {
			if (event.getType().equals("raid")) {
				try {
					RaidEvent raidEvent = mapper.treeToValue(event.getMessage(), RaidEvent.class);
					handleRaidEvent(raidEvent);
					raidCount++;
				}
				catch (JsonProcessingException e) {
					LOGGER.error("Could not convert 'message' part of raid event.", e);
				}
			}
			else if (event.getType().equals("gym")) {
				try {
					GymEvent gymEvent = mapper.treeToValue(event.getMessage(), GymEvent.class);
					handleGymEvent(gymEvent);
					gymCount++;
				}
				catch (JsonProcessingException e) {
					LOGGER.error("Could not convert 'message' part of the gym event.", e);
				}
			}
			else if (!ignoredEvents.contains(event.getType())) {
				LOGGER.error("Unknown event type '{}' in message.", event.getType());
			}
		}
		
		if (raidCount > 0)
			LOGGER.debug("Received and processed {} raid events.", raidCount);

		if (gymCount > 0)
			LOGGER.debug("Received and processed {} gym events.", gymCount);
	}

	private ObjectMapper createMapper() {
		ObjectMapper mapper = new ObjectMapper();
		JavaTimeModule timeModule = new JavaTimeModule();
		timeModule.addDeserializer(LocalDateTime.class, new JsonTimestampDateTimeDeserializer(eventsTimezone));
		mapper.registerModule(timeModule);
		return mapper;
	}

	private void handleGymEvent(GymEvent gymEvent) {
		List<Gym> gyms = gymRepository.findByGymId(gymEvent.getGymId());
		
		if (gyms.isEmpty())
			gymRepository.save(new Gym(gymEvent));
		else if (gyms.size() == 1)
			updateGym(gymEvent, gyms.get(0));
		else
			LOGGER.error("Corrupt database. Gym table inconsistent.");
			
	}

	private void handleRaidEvent(RaidEvent raidEvent) {
		List<Gym> gyms = gymRepository.findByGymId(raidEvent.getGymId());
		
		if (gyms.isEmpty()) {
			LOGGER.info("Found new gym with name {} at {},{}", raidEvent.getName(), raidEvent.getLatitude(), raidEvent.getLongitude());
			gymRepository.save(new Gym(raidEvent));
		}
		else if (gyms.size() == 1) {
			updateGym(raidEvent, gyms.get(0));
			updateRaid(raidEvent, gyms.get(0));
		}
		else
			LOGGER.error("Corrupt database. Gym table inconsistent.");
	}

	private void updateRaid(RaidEvent raidEvent, Gym gym) {
		List<Raid> raids = raidRepository.findByGymIdAndStart(gym.getId(), raidEvent.getStart());
		if (raids.isEmpty()) {
			raidRepository.save(new Raid(raidEvent, gym));
		}
		else if (raids.size() == 1) {
			Raid raid = raids.get(0);
			
			raid.setLevel(raidEvent.getLevel());
			raid.setPokemonId(raidEvent.getPokemonId());
			raid.setCp(raidEvent.getCp());
			raid.setStart(raidEvent.getStart());
			raid.setEnd(raidEvent.getEnd());
			raid.setEvolution(raidEvent.getEvolution());
			raid.setMove1(raidEvent.getMove1());
			raid.setMove2(raidEvent.getMove2());
			raid.setForm(raidEvent.getForm());
			raid.setExclusive(raidEvent.isExclusive());
			raid.setGender(raidEvent.getGender());
			raid.setCostume(raidEvent.getCostume());

			raidRepository.save(raid);
		}
		else
			LOGGER.error("Corrupt database. Raid table inconsistent.");
	}

	private void updateGym(RaidEvent raidEvent, Gym gym) {
		gym.setTeamId(raidEvent.getTeamId());
		gym.setLatitude(raidEvent.getLatitude());
		gym.setLongitude(raidEvent.getLongitude());
		
		if (raidEvent.getName() != null && !raidEvent.getName().equals(UNKNOWN_GYM_NAME))
			gym.setName(raidEvent.getName());
		
		if (raidEvent.getUrl() != null && !raidEvent.getUrl().equals(""))
			gym.setImageUrl(raidEvent.getUrl());
		
		gym.setExRaidEligible(raidEvent.isExRaidEligible());

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Updating gym metadata for name '{}'.", raidEvent.getName());
		
		gymRepository.save(gym);
	}

	private void updateGym(GymEvent gymEvent, Gym gym) {
		gym.setTeamId(gymEvent.getTeamId());
		gym.setLatitude(gymEvent.getLatitude());
		gym.setLongitude(gymEvent.getLongitude());
		gym.setDescription(gymEvent.getDescription());

		if (gymEvent.getName() != null && !gymEvent.getName().equals(UNKNOWN_GYM_NAME))
			gym.setName(gymEvent.getName());
		
		if (gymEvent.getUrl() != null && !gymEvent.getUrl().equals(""))
			gym.setImageUrl(gymEvent.getUrl());
		gym.setExRaidEligible(gymEvent.isExRaidEligible());

		gym.setSlotsAvailable(gymEvent.getSlotsAvailable());

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Updating gym metadata for name '{}'.", gymEvent.getName());

		gymRepository.save(gym);
	}
}