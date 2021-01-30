package org.pokescrying.service;

import java.util.Optional;

import org.pokescrying.data.Pokedex;
import org.pokescrying.data.Raid;
import org.pokescrying.repository.PokedexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PokeScryingService {
	@Autowired
	private PokedexRepository pokedexRepository;

	public String translateIdToPokemon(Raid raid) {
		if (raid.getPokemonId() == 0)
			return "Raid Level " + raid.getLevel();
		else {
			Optional<Pokedex> pokedexEntry = this.pokedexRepository.findById(raid.getPokemonId());
			if (pokedexEntry.isPresent()) {
				return pokedexEntry.get().getName();
			}
			else
				return "Pokemon " + raid.getPokemonId();
		}
	}
}
