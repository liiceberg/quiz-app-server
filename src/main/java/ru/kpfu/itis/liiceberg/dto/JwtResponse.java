package ru.kpfu.itis.liiceberg.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JwtResponse {

    private String accessToken;
    private String refreshToken;

}
