package org.pokescrying.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.pokescrying.events.GymEvent;
import org.pokescrying.events.RaidEvent;

@Entity
public class Gym {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String gymId;

	private double latitude;

	private double longitude;

	private String name;

	private String imageUrl;
	
	private String description;

	private long teamId;
	
	private Long slotsAvailable;
	
	private boolean isExRaidEligible;

	public Gym() {}
	
	public Gym(RaidEvent raidEvent) {
		this.gymId = raidEvent.getGymId();
		this.latitude = raidEvent.getLatitude();
		this.longitude = raidEvent.getLongitude();
		this.name = raidEvent.getName();
		this.imageUrl = raidEvent.getUrl();
		this.teamId = raidEvent.getTeamId();
		this.slotsAvailable = null;
		this.isExRaidEligible = raidEvent.isExRaidEligible();
	}

	public Gym(GymEvent gymEvent) {
		this.gymId = gymEvent.getGymId();
		this.latitude = gymEvent.getLatitude();
		this.longitude = gymEvent.getLongitude();
		this.name = gymEvent.getName();
		this.imageUrl = gymEvent.getUrl();
		this.teamId = gymEvent.getTeamId();
		this.slotsAvailable = gymEvent.getSlotsAvailable();
		this.isExRaidEligible = gymEvent.isExRaidEligible();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public Long getSlotsAvailable() {
		return slotsAvailable;
	}

	public void setSlotsAvailable(Long slotsAvailable) {
		this.slotsAvailable = slotsAvailable;
	}

	public boolean isExRaidEligible() {
		return isExRaidEligible;
	}

	public void setExRaidEligible(boolean isExRaidEligible) {
		this.isExRaidEligible = isExRaidEligible;
	}
}