package ru.kpfu.itis.liiceberg.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class RefreshJwtRequest {
    @NotBlank(message = "Token shouldn't be blank")
    private String token;
}
