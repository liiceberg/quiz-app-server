package ru.kpfu.itis.liiceberg.dto;

import lombok.Builder;
import lombok.Getter;
import org.json.JSONObject;
@Getter
@Builder
public class MessageDto {
    private String sender;
    private Code code;
    private JSONObject content;
    public enum Code {
        JOIN, EXIT, SCORE, NOT_FOUND
    }

}
