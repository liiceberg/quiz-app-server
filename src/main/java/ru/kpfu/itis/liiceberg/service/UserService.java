package ru.kpfu.itis.liiceberg.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kpfu.itis.liiceberg.dto.UserRequestDto;
import ru.kpfu.itis.liiceberg.exception.BadArgumentsException;
import ru.kpfu.itis.liiceberg.model.Role;
import ru.kpfu.itis.liiceberg.model.User;
import ru.kpfu.itis.liiceberg.repository.UserRepository;

import javax.transaction.Transactional;
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


    public void create(UserRequestDto dto) throws BadArgumentsException {
        User user = User.builder()
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword()))
                .roles(new HashSet<>(Collections.singletonList(Role.USER)))
                .build();
        try {
            userRepository.save(user);
        } catch (Exception ex) {
            throw new BadArgumentsException("Such email already registered");
        }
    }

    public User get(UserRequestDto dto) throws BadArgumentsException {
        Optional<User> user = userRepository.findByEmail(dto.getEmail());
        if (user.isPresent() && encoder.matches(dto.getPassword(), user.get().getPassword())) {
            return user.get();
        } else {
            throw new BadArgumentsException("User not found");
        }
    }
    @Transactional
    public void editUsername(String username, Long id) throws BadArgumentsException {
        try {
            userRepository.updateNameById(username, id);
        } catch (Exception ex) {
            throw new BadArgumentsException("Unable to update user");
        }
    }

    public String getName(Long id) {
        return userRepository.getName(id);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public void update(User u) {
        userRepository.save(u);
    }

}
