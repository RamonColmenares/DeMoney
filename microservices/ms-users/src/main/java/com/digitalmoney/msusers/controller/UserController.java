package com.digitalmoney.msusers.controller;

import com.digitalmoney.msusers.persistency.entity.User;
import com.digitalmoney.msusers.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
public class UserController {
    private final UserService userService;


    @GetMapping("/ping")
    public String ping() {
        return "UP";
    }

    @GetMapping("/test-db")
    public List<User> testDb() {
        return userService.findAll();
    }
}
