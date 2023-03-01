package com.amaris.task.common.date;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ZDateDeserializer extends JsonDeserializer<LocalDate> {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    public ZDateDeserializer() {
    }

    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        if (jsonParser == null) {
            return null;
        } else {
            try {
                return (LocalDate) formatter.parse(jsonParser.getValueAsString());
            } catch (Throwable throwable) {
                InvalidFormatException invalidFormatException = new InvalidFormatException(jsonParser, "Failed to deser. object", jsonParser.getValueAsString(), LocalDate.class);
                invalidFormatException.setStackTrace(throwable.getStackTrace());
                throw invalidFormatException;
            }
        }
    }
}
