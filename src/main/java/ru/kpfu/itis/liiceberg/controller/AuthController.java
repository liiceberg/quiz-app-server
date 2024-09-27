package ru.kpfu.itis.liiceberg.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kpfu.itis.liiceberg.dto.JwtRequest;
import ru.kpfu.itis.liiceberg.dto.JwtResponse;
import ru.kpfu.itis.liiceberg.dto.LoginResponse;
import ru.kpfu.itis.liiceberg.dto.RefreshJwtRequest;
import ru.kpfu.itis.liiceberg.exception.BadArgumentsException;
import ru.kpfu.itis.liiceberg.service.AuthService;

import javax.security.auth.message.AuthException;

@Tag(name = "Authorization")
@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(description = "Log in user by email and password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = LoginResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})})
    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@Validated @RequestBody JwtRequest jwtRequest) throws AuthException {
        return ResponseEntity.ok(authService.login(jwtRequest));
    }
    @Operation(description = "Refresh jwt token with invalidate")
    @PostMapping("token")
    public ResponseEntity<JwtResponse> token(@Validated @RequestBody RefreshJwtRequest jwtRequest) throws AuthException {
        return ResponseEntity.ok(authService.token(jwtRequest.getToken()));
    }
    @Operation(description = "Refresh jwt token")
    @PostMapping("refresh")
    public ResponseEntity<JwtResponse> refresh(@Validated @RequestBody RefreshJwtRequest jwtRequest) throws AuthException {
        return new ResponseEntity<>(authService.refresh(jwtRequest.getToken()), HttpStatus.OK);
    }
}