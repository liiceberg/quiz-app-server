package ru.kpfu.itis.liiceberg.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.kpfu.itis.liiceberg.filter.JwtFilter;
import ru.kpfu.itis.liiceberg.model.Role;

@Configuration
@EnableWebSecurity
@ComponentScan("ru.kpfu.itis.liiceberg")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.httpBasic().disable()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests(auth -> auth
                        .antMatchers("api/auth/login", "api/auth/token").permitAll()
                        .antMatchers("api/user/register").anonymous()
                        .antMatchers("api/trivia/**", "api/room/**").hasAnyAuthority(Role.USER.getAuthority(), Role.ADMIN.getAuthority())
                        .and().addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                )
                .build();
    }
}
