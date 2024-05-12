package ru.kpfu.itis.liiceberg.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class CreateRoomDto {
    @NotBlank
    private Integer capacity;
    private Integer category;
    private String difficulty;
}
