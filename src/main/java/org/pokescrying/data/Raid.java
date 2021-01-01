package org.pokescrying.data;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.pokescrying.events.RaidEvent;

@Entity
public class Raid {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private Long gymId;
	
	private long level;
	
	private long pokemonId;
	
	private long cp;
	
	private LocalDateTime start;
	
	private LocalDateTime end;
	
	private long evolution;
	
	private long move1;
	
	private long move2;
	
	private long form;
	
	private long gender;
	
	private long costume;
	
	private boolean isExclusive;

	public Raid() {}
	
	public Raid(RaidEvent raidEvent, Gym gym) {
		this.gymId = gym.getId();
		this.level = raidEvent.getLevel();
		this.pokemonId = raidEvent.getPokemonId();
		this.cp = raidEvent.getCp();
		this.start = raidEvent.getStart();
		this.end = raidEvent.getEnd();
		this.evolution = raidEvent.getEvolution();
		this.move1 = raidEvent.getMove1();
		this.move2 = raidEvent.getMove2();
		this.form = raidEvent.getForm();
		this.gender = raidEvent.getGender();
		this.costume = raidEvent.getCostume();
		this.isExclusive = raidEvent.isExclusive();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGymId() {
		return gymId;
	}

	public void setGymId(Long gymId) {
		this.gymId = gymId;
	}

	public long getLevel() {
		return level;
	}

	public void setLevel(long level) {
		this.level = level;
	}

	public long getPokemonId() {
		return pokemonId;
	}

	public void setPokemonId(long pokemonId) {
		this.pokemonId = pokemonId;
	}

	public long getCp() {
		return cp;
	}

	public void setCp(long cp) {
		this.cp = cp;
	}

	public LocalDateTime getStart() {
		return start;
	}

	public void setStart(LocalDateTime start) {
		this.start = start;
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public void setEnd(LocalDateTime end) {
		this.end = end;
	}

	public long getEvolution() {
		return evolution;
	}

	public void setEvolution(long evolution) {
		this.evolution = evolution;
	}

	public long getMove1() {
		return move1;
	}

	public void setMove1(long move1) {
		this.move1 = move1;
	}

	public long getMove2() {
		return move2;
	}

	public void setMove2(long move2) {
		this.move2 = move2;
	}

	public long getForm() {
		return form;
	}

	public void setForm(long form) {
		this.form = form;
	}

	public long getGender() {
		return gender;
	}

	public void setGender(long gender) {
		this.gender = gender;
	}

	public long getCostume() {
		return costume;
	}

	public void setCostume(long costume) {
		this.costume = costume;
	}

	public boolean isExclusive() {
		return isExclusive;
	}

	public void setExclusive(boolean isExclusive) {
		this.isExclusive = isExclusive;
	}
}