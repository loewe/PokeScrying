package org.pokescrying.events;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GymEvent {
	@JsonProperty("gym_id")
	private String gymId;

	private double latitude;
	
	private double longitude;

	@JsonProperty("team_id")
	private long teamId;

	private String name;
	
	@JsonProperty("slots_available")
	private long slotsAvailable;

	private String url;

	@JsonProperty("is_ex_raid_eligible")
	private boolean isExRaidEligible;

	private String description;
	
	public String getGymId() {
		return gymId;
	}

	public void setGymId(String gymId) {
		this.gymId = gymId;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSlotsAvailable() {
		return slotsAvailable;
	}

	public void setSlotsAvailable(long slotsAvailable) {
		this.slotsAvailable = slotsAvailable;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isExRaidEligible() {
		return isExRaidEligible;
	}

	public void setExRaidEligible(boolean isExRaidEligible) {
		this.isExRaidEligible = isExRaidEligible;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}