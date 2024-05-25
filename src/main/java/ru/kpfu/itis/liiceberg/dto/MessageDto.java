package ru.kpfu.itis.liiceberg.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
@Getter
@Builder
@ToString
public class MessageDto {
    private Long sender;
    private Code code;
    private String message;
    private Integer wait;
    private Integer score;
    public enum Code {
        JOIN, READY, EXIT, SCORE, ALIVE
    }

}
