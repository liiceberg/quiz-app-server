package ru.kpfu.itis.liiceberg.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.liiceberg.dto.RefreshJwtRequest;
import ru.kpfu.itis.liiceberg.dto.UserRequestDto;
import ru.kpfu.itis.liiceberg.model.User;
import ru.kpfu.itis.liiceberg.service.UserService;

@RestController
@RequestMapping(path = "user", produces = "application/json")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> create(@RequestBody UserRequestDto user) {
        try {
            User u = userService.create(user);
            return new ResponseEntity<>(u, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/delete")
    public void delete(@RequestParam("id") Long userId) {
        userService.delete(userId);
    }

    @PostMapping("/update")
    public void update(@RequestBody User user) {
        userService.update(user);
    }


}
