package org.pokescrying.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.pokescrying.data.Raid;
import org.springframework.data.repository.CrudRepository;

public interface RaidRepository extends CrudRepository<Raid, Long> {
	List<Raid> findByGymIdAndStart(long gymId, LocalDateTime start);
}