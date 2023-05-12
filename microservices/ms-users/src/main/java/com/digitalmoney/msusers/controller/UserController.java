package com.digitalmoney.msusers.controller;

import com.digitalmoney.msusers.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller @AllArgsConstructor
public class UserController {
    private final UserService userService;
}
