package ru.kpfu.itis.liiceberg.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
public class UserRequestDto {

    @Email
    @NotBlank(message = "Name shouldn't be blank")
    private String email;

    @Size(min = 8, max = 64, message = "Password should contains from 8 to 64 symbols")
    private String password;
}
