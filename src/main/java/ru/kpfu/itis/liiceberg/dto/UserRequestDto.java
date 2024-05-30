package ru.kpfu.itis.liiceberg.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
public class UserRequestDto {

    @Email
    private String email;

    @Size(min = 8, message = "Password should contains minimum 8 symbols")
    private String password;
}
