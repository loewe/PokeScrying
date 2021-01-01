package org.pokescrying.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class RaidRegistration {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private long raidId;
	
	private long trainerId;
	
	private RegistrationType type;
	
	private long extra;
	
	private long timeslot;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getRaidId() {
		return raidId;
	}

	public void setRaidId(long raidId) {
		this.raidId = raidId;
	}

	public long getTrainerId() {
		return trainerId;
	}

	public void setTrainerId(long trainerId) {
		this.trainerId = trainerId;
	}

	public RegistrationType getType() {
		return type;
	}

	public void setType(RegistrationType type) {
		this.type = type;
	}

	public long getExtra() {
		return extra;
	}

	public void setExtra(long extra) {
		this.extra = extra;
	}

	public long getTimeslot() {
		return timeslot;
	}

	public void setTimeslot(long timeslot) {
		this.timeslot = timeslot;
	}
}