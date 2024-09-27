package ru.kpfu.itis.liiceberg.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.liiceberg.dto.RoomDto;
import ru.kpfu.itis.liiceberg.dto.UserRequestDto;
import ru.kpfu.itis.liiceberg.dto.UsernameResponse;
import ru.kpfu.itis.liiceberg.exception.BadArgumentsException;
import ru.kpfu.itis.liiceberg.exception.ConflictException;
import ru.kpfu.itis.liiceberg.model.User;
import ru.kpfu.itis.liiceberg.service.UserService;

import java.util.List;
@Tag(name = "User")
@RestController
@RequestMapping(path = "api/user", produces = "application/json")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @Operation(description = "Register user by email and password")
    @PostMapping("/register")
    public ResponseEntity<Void> create(@Validated @RequestBody UserRequestDto user) throws ConflictException {
        userService.create(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @Operation(description = "Delete user by id")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/delete")
    public void delete(@RequestParam("id") Long userId) {
        userService.delete(userId);
    }
    @Operation(description = "Update user by id")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/update")
    public void update(@RequestBody User user) {
        userService.update(user);
    }
    @Operation(description = "Update user's name by id")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/update/name")
    public void updateName(@RequestParam("id") Long userId, @RequestParam("name") String name) throws BadArgumentsException {
        userService.editUsername(name, userId);
    }
    @Operation(description = "Get user's name by id")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/get/name")
    public ResponseEntity<UsernameResponse> getName(@RequestParam("id") Long userId) {
        String name = userService.getName(userId);
        return new ResponseEntity<>(new UsernameResponse(name), HttpStatus.OK);
    }
    @Operation(description = "Get user's rooms by id")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("get/rooms")
    public ResponseEntity<List<RoomDto>> getRoomsByUserId(@RequestParam("id") Long id) throws BadArgumentsException {
        return new ResponseEntity<>(userService.getRooms(id), HttpStatus.OK);
    }
}
