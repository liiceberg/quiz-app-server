package ru.kpfu.itis.liiceberg.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kpfu.itis.liiceberg.dto.JwtRequest;
import ru.kpfu.itis.liiceberg.dto.JwtResponse;
import ru.kpfu.itis.liiceberg.dto.LoginResponse;
import ru.kpfu.itis.liiceberg.dto.RefreshJwtRequest;
import ru.kpfu.itis.liiceberg.service.AuthService;

import javax.security.auth.message.AuthException;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@RequestBody JwtRequest jwtRequest) {
        return ResponseEntity.ok(authService.login(jwtRequest));
    }

    @PostMapping("token")
    public ResponseEntity<JwtResponse> token(@RequestBody RefreshJwtRequest jwtRequest) throws AuthException {
        return ResponseEntity.ok(authService.token(jwtRequest.getToken()));
    }

    @PostMapping("refresh")
    public ResponseEntity<JwtResponse> refresh(@RequestBody RefreshJwtRequest jwtRequest) throws AuthException {
        return ResponseEntity.ok(authService.refresh(jwtRequest.getToken()));
    }
}