package ru.kpfu.itis.liiceberg.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kpfu.itis.liiceberg.dto.UserRequestDto;
import ru.kpfu.itis.liiceberg.model.Role;
import ru.kpfu.itis.liiceberg.model.User;
import ru.kpfu.itis.liiceberg.repository.RoleRepository;
import ru.kpfu.itis.liiceberg.repository.UserRepository;

import javax.management.relation.RoleNotFoundException;
import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }


    public User create(UserRequestDto dto) throws RoleNotFoundException {
        User user = new User();
        Optional<Role> role = roleRepository.findByName("USER");
        if (!role.isPresent()) {
            throw new RoleNotFoundException("Role not founded");
        }
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setRoles(new HashSet<>(Arrays.asList(role.get())));
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

}
