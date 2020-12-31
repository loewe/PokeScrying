package org.pokescrying.util;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;


public class JsonTimestampDateTimeDeserializer extends LocalDateTimeDeserializer { //NOSONAR java:S110 not my decision 
	private static final long serialVersionUID = 1L;

	private final String zoneId;
	
	public JsonTimestampDateTimeDeserializer(String zoneId) {
        super(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.zoneId = zoneId;
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            long value = parser.getValueAsLong();
            Instant instant = Instant.ofEpochMilli(value*1000);
            return LocalDateTime.ofInstant(instant, ZoneId.of(zoneId));
        }

        return super.deserialize(parser, context);
    }
}