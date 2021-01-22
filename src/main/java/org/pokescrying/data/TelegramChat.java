package org.pokescrying.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TelegramChat {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private long chatId;

	private String name;

	private String fenceName;
	
	private int listMessageId;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getChatId() {
		return chatId;
	}

	public void setChatId(long chatId) {
		this.chatId = chatId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFenceName() {
		return fenceName;
	}

	public void setFenceName(String fenceName) {
		this.fenceName = fenceName;
	}

	public int getListMessageId() {
		return listMessageId;
	}

	public void setListMessageId(int listMessageId) {
		this.listMessageId = listMessageId;
	}
}