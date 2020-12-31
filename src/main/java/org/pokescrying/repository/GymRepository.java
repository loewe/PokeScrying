package org.pokescrying.repository;

import java.util.List;

import org.pokescrying.data.Gym;
import org.springframework.data.repository.CrudRepository;

public interface GymRepository extends CrudRepository<Gym, Long> {
	List<Gym> findByGymId(String gymId);
}