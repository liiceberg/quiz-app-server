package ru.kpfu.itis.liiceberg.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
@Setter
public class JwtRequest {
    @Email
    private String email;
    @Size(min = 8, message = "Password should contains minimum 8 symbols")
    private String password;

}