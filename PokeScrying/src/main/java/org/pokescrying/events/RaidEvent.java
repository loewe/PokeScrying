package org.pokescrying.events;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Example of an raid event:
 * {
 *   "latitude": 48.101338,
 *   "longitude": 11.771721,
 *   "level": 3,
 *   "pokemon_id": 615,
 *   "team_id": 1,
 *   "cp": 18775,
 *   "start": 1609357191,
 *   "end": 1609359891,
 *   "name": "unknown",
 *   "evolution": 0,
 *   "move_1": 218,
 *   "move_2": 116,
 *   "gym_id": "5fc1f212c08f4dfc824e5734a7f056b1.16",
 *   "url": "http://lh3.googleusercontent.com/h1q19Lw0Pyu4SjJx1AA-bFKiKA3K5EcelNX4t5IkEIE3bIRsRj9iVu6aLG7mxAUUY4uZoz1AxrRS86xgJn9sVlPmpBIB",
 *   "form": 0,
 *   "is_ex_raid_eligible": false,
 *   "is_exclusive": false,
 *   "gender": 3,
 *   "costume": 0
 * }
 */
public class RaidEvent {
	private double latitude;
	
	private double longitude;
	
	private long level;
	
	@JsonProperty("pokemon_id")
	private long pokemonId;
	
	@JsonProperty("team_id")
	private long teamId;

	private long cp;

	private LocalDateTime start;
	
	private LocalDateTime end;
	
	private String name;
	
	private long evolution;
	
	@JsonProperty("move_1")
	private long move1;
	
	@JsonProperty("move_2")
	private long move2;
	
	@JsonProperty("gym_id")
	private String gymId;
	
	private String url;
	
	private long form;

	@JsonProperty("is_ex_raid_eligible")
	private boolean isExRaidEligible;

	@JsonProperty("is_exclusive")
	private boolean isExclusive;
	
	private long gender;
	
	private long costume;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getGymId() {
		return gymId;
	}

	public void setGymId(String gymId) {
		this.gymId = gymId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getForm() {
		return form;
	}

	public void setForm(long form) {
		this.form = form;
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