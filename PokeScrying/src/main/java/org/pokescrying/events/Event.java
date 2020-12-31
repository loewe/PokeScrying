package org.pokescrying.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class Event {
	private String type;
	
	@JsonProperty("message")
    private JsonNode message;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public JsonNode getMessage() {
		return message;
	}

	public void setMessage(JsonNode message) {
		this.message = message;
	}
}