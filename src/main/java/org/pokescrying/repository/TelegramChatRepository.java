package org.pokescrying.repository;

import org.pokescrying.data.TelegramChat;
import org.springframework.data.repository.CrudRepository;

public interface TelegramChatRepository extends CrudRepository<TelegramChat, Long> {
}