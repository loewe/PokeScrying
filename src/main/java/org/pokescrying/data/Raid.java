package org.pokescrying.data;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Raid {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private long level;
	
	private long pokemonId;
	
	private long teamId;
	
	private long cp;
	
	private LocalDateTime start;
	
	private LocalDateTime end;
	
	private long evolution;
	
	private long move1;
	
	private long move2;
	
	private long form;
	
	private long gender;
	
	private long costume;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
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
}