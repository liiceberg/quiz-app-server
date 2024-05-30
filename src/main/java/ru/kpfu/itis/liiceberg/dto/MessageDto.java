package ru.kpfu.itis.liiceberg.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@ToString
public class MessageDto {
    @NotBlank(message = "Sender shouldn't be blank")
    private Long sender;
    @NotBlank(message = "Code shouldn't be blank")
    private Code code;
    private String message;
    private Integer wait;
    private Integer score;
    public enum Code {
        JOIN, READY, EXIT, SCORE, ALIVE
    }

}
