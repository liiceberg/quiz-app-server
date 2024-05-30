package ru.kpfu.itis.liiceberg.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kpfu.itis.liiceberg.dto.JwtRequest;
import ru.kpfu.itis.liiceberg.dto.JwtResponse;
import ru.kpfu.itis.liiceberg.dto.LoginResponse;
import ru.kpfu.itis.liiceberg.exception.BadArgumentsException;
import ru.kpfu.itis.liiceberg.filter.JwtProvider;
import ru.kpfu.itis.liiceberg.model.User;
import ru.kpfu.itis.liiceberg.repository.UserRepository;

import javax.security.auth.message.AuthException;
import java.util.HashMap;
import java.util.Map;
@Service
public class AuthService {


    private final UserRepository userRepository;
    private final Map<String, String> refreshStorage = new HashMap<>();
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtProvider jwtProvider, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
    }



    public LoginResponse login(JwtRequest request) throws BadArgumentsException {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(request.getEmail()));
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            String accessToken = jwtProvider.generateAccessToken(user);
            String refreshToken = jwtProvider.generateRefreshToken(user);
            refreshStorage.put(user.getEmail(), refreshToken);
            return new LoginResponse(accessToken, refreshToken, user.getId());
        }
        throw new BadArgumentsException("Invalid Password");
    }


    public JwtResponse refresh(String refreshToken) throws AuthException {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            String email = claims.getSubject();
            String savedRefresh = refreshStorage.get(email);
            if (savedRefresh != null && savedRefresh.equals(refreshToken)) {
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new AuthException(email));
                String accessToken = jwtProvider.generateAccessToken(user);
                String newRefreshToken = jwtProvider.generateRefreshToken(user);
                refreshStorage.put(user.getEmail(), newRefreshToken);
                return new JwtResponse(accessToken, newRefreshToken);
            }
        }
        throw new AuthException("Invalid refresh token");
    }


    public JwtResponse token(String refreshToken) throws AuthException {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            String email = claims.getSubject();
            String savedRefresh = refreshStorage.get(email);
            if (savedRefresh != null && savedRefresh.equals(refreshToken)) {
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new AuthException(email));
                String accessToken = jwtProvider.generateAccessToken(user);
                return new JwtResponse(accessToken, null);
            }
        }
        throw new AuthException("Invalid refresh token");
    }
}
