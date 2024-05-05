package ru.kpfu.itis.liiceberg.controller;

import org.json.JSONObject;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.liiceberg.dto.UserRequestDto;
import ru.kpfu.itis.liiceberg.model.Role;
import ru.kpfu.itis.liiceberg.model.User;
import ru.kpfu.itis.liiceberg.service.UserService;
import ru.kpfu.itis.liiceberg.util.Keys;
import ru.kpfu.itis.liiceberg.util.ResponseCode;

import java.util.*;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public @ResponseBody JSONObject create(@RequestBody UserRequestDto user) {
        List<JSONObject> results = new ArrayList<>();
        int responseCode;
        try {
            User u = userService.create(user);
            responseCode = ResponseCode.SUCCESS.getCode();
            results.add(u.toJson());
        } catch (Exception ex) {
            responseCode = ResponseCode.FAILURE.getCode();
        }
        JSONObject json = new JSONObject();
        json.put(Keys.RESPONSE_CODE, responseCode);
        json.put(Keys.RESULTS, results);
        return json;
    }

    @PostMapping("/login")
    public @ResponseBody JSONObject login(@RequestBody UserRequestDto user) {
        List<JSONObject> results = new ArrayList<>();
        int responseCode;
        try {
            User u = userService.get(user);
            responseCode = ResponseCode.SUCCESS.getCode();
            results.add(u.toJson());
        } catch (UsernameNotFoundException ex) {
            responseCode = ResponseCode.NO_RESULTS.getCode();
        }
        JSONObject json = new JSONObject();
        json.put(Keys.RESPONSE_CODE, responseCode);
        json.put(Keys.RESULTS, results);
        return json;
    }
}
