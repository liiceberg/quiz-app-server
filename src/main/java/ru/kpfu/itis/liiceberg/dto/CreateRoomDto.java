package ru.kpfu.itis.liiceberg.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class CreateRoomDto {
    @NotBlank(message = "Capacity shouldn't be blank")
    private Integer capacity;
    private Integer category;
    private String difficulty;
}
