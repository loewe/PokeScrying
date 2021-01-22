package org.pokescrying.repository;

import org.pokescrying.data.Pokedex;
import org.springframework.data.repository.CrudRepository;

public interface PokedexRepository extends CrudRepository<Pokedex, Long> {
}