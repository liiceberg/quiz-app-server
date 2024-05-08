package ru.kpfu.itis.liiceberg.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kpfu.itis.liiceberg.dto.UserRequestDto;
import ru.kpfu.itis.liiceberg.model.Role;
import ru.kpfu.itis.liiceberg.model.User;
import ru.kpfu.itis.liiceberg.repository.UserRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }


    public User create(UserRequestDto dto) {
        User user = User.builder()
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword()))
                .roles(new HashSet<>(Collections.singletonList(Role.USER)))
                .build();
        return userRepository.save(user);
    }

    public User get(UserRequestDto dto) {
        Optional<User> user = userRepository.findByEmail(dto.getEmail());
        if (user.isPresent() && encoder.matches(dto.getPassword(), user.get().getPassword())) {
            return user.get();
        } else {
            throw new UsernameNotFoundException("User not founded");
        }
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public void update(User u) {
        userRepository.save(u);
    }

}
