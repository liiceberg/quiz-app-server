package ru.kpfu.itis.liiceberg.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomDto {
    private String code;
    private Integer capacity;
    private Integer category;
    private String difficulty;
}
