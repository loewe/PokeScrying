package org.pokescrying.repository;

import java.util.Optional;

import org.pokescrying.data.RaidRegistration;
import org.springframework.data.repository.CrudRepository;

public interface RaidRegistrationRepository extends CrudRepository<RaidRegistration, Long> {
	Optional<RaidRegistration> findByRaidIdAndTrainerId(Long raidId, Long trainerId);
}