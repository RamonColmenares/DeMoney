package com.digitalmoney.msusers.controller;

import com.digitalmoney.msusers.application.dto.UserRegisterDTO;
import com.digitalmoney.msusers.persistency.entity.User;
import com.digitalmoney.msusers.service.KeycloakService;
import com.digitalmoney.msusers.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.Response;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@AllArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    private final KeycloakService keycloakService;


    @GetMapping("/ping")
    public String ping() {
        return "UP";
    }

    @GetMapping("/test-db")
    public List<User> testDb() {
        return userService.findAll();
    }

    @GetMapping("/test-keycloak")
    public ResponseEntity<?> testKeycloak() {
        return ResponseEntity.ok(keycloakService.test());

    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterDTO user) {
        try (Response response = keycloakService.createInKeycloak(user)) {
            if (response.getStatus() == 201) {
                return ResponseEntity.ok().body(userService.createUser(user));
            }
            return ResponseEntity.status(response.getStatus()).body(response.getEntity().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
