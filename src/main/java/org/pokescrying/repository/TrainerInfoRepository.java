package org.pokescrying.repository;

import org.pokescrying.data.TrainerInfo;
import org.springframework.data.repository.CrudRepository;

public interface TrainerInfoRepository extends CrudRepository<TrainerInfo, Long> {
}