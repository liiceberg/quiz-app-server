package ru.kpfu.itis.liiceberg.filter;

import io.jsonwebtoken.Claims;
import ru.kpfu.itis.liiceberg.model.Role;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtAuthenticationGenerator {
    public static JwtAuthentication generate(Claims claims) {
        JwtAuthentication jwtAuthentication = new JwtAuthentication();
        jwtAuthentication.setRoles(getRoles(claims));
        jwtAuthentication.setEmail(claims.getSubject());
        jwtAuthentication.setUsername(claims.get("username", String.class));
        return jwtAuthentication;
    }

    private static Set<Role> getRoles(Claims claims) {
        List<String> roles = claims.get("roles", List.class);
        return roles.stream().map(Role::valueOf).collect(Collectors.toSet());
    }
}
