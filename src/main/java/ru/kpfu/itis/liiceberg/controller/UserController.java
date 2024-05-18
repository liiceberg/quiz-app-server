package ru.kpfu.itis.liiceberg.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.liiceberg.dto.UserRequestDto;
import ru.kpfu.itis.liiceberg.dto.UsernameResponse;
import ru.kpfu.itis.liiceberg.exception.BadArgumentsException;
import ru.kpfu.itis.liiceberg.model.User;
import ru.kpfu.itis.liiceberg.service.UserService;

@RestController
@RequestMapping(path = "api/user", produces = "application/json")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public void create(@RequestBody UserRequestDto user) throws BadArgumentsException {
        userService.create(user);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/delete")
    public void delete(@RequestParam("id") Long userId) {
        userService.delete(userId);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/update")
    public void update(@RequestBody User user) {
        userService.update(user);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/update/name")
    public void updateName(@RequestParam("id") Long userId, @RequestParam("name") String name) throws BadArgumentsException {
        userService.editUsername(name, userId);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/get/name")
    public ResponseEntity<UsernameResponse> getName(@RequestParam("id") Long userId) {
        String name = userService.getName(userId);
        return new ResponseEntity<>(new UsernameResponse(name), HttpStatus.OK);
    }
}
