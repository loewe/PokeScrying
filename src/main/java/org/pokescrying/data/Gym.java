package org.pokescrying.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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

	private boolean isExRaidEligible;

	private boolean isExclusive;

	public Gym() {}
	
	public Gym(RaidEvent raidEvent) {
		this.gymId = raidEvent.getGymId();
		this.latitude = raidEvent.getLatitude();
		this.longitude = raidEvent.getLongitude();
		this.name = raidEvent.getName();
		this.imageUrl = raidEvent.getUrl();
		this.isExRaidEligible = raidEvent.isExRaidEligible();
		this.isExclusive = raidEvent.isExclusive();
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

	public boolean isExRaidEligible() {
		return isExRaidEligible;
	}

	public void setExRaidEligible(boolean isExRaidEligible) {
		this.isExRaidEligible = isExRaidEligible;
	}

	public boolean isExclusive() {
		return isExclusive;
	}

	public void setExclusive(boolean isExclusive) {
		this.isExclusive = isExclusive;
	}
}