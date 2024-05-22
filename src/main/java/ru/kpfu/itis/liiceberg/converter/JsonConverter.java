package ru.kpfu.itis.liiceberg.converter;

import org.json.JSONObject;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class JsonConverter implements AttributeConverter<JSONObject, String> {
    @Override
    public String convertToDatabaseColumn(JSONObject json) {
        return json.toString();
    }

    @Override
    public JSONObject convertToEntityAttribute(String str) {
        return new JSONObject(str);
    }
}
