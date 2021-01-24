package org.pokescrying.repository;

import java.util.Optional;

import org.pokescrying.data.TrainerInfo;
import org.springframework.data.repository.CrudRepository;

public interface TrainerInfoRepository extends CrudRepository<TrainerInfo, Long> {
	Optional<TrainerInfo> findByTelegramId(Integer telegramId);
}