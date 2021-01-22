package org.pokescrying.data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Pokedex {
	@Id
	private Long pokemonId;

	private String name;

	public Long getPokemonId() {
		return pokemonId;
	}

	public void setPokemonId(Long pokemonId) {
		this.pokemonId = pokemonId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}		
}