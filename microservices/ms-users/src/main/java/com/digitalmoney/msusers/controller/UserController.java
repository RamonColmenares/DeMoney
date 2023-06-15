package com.digitalmoney.msusers.controller;

import com.digitalmoney.msusers.application.dto.UserLoginDTO;
import com.digitalmoney.msusers.application.dto.UserLoginResponseDTO;
import com.digitalmoney.msusers.application.dto.UserRegisterDTO;
import com.digitalmoney.msusers.application.dto.UserResponseDTO;
import com.digitalmoney.msusers.application.dto.*;
import com.digitalmoney.msusers.config.security.TokenProvider;
import com.digitalmoney.msusers.application.exception.UserBadRequestException;
import com.digitalmoney.msusers.application.exception.UserInternalServerException;
import com.digitalmoney.msusers.application.exception.UserNotFoundException;
import com.digitalmoney.msusers.application.exception.UserUnauthorizedException;
import com.digitalmoney.msusers.persistency.entity.User;
import com.digitalmoney.msusers.service.KeycloakService;
import com.digitalmoney.msusers.service.UserService;
import com.digitalmoney.msusers.service.feign.MailFeignService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.keycloak.common.VerificationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;

@AllArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    private final KeycloakService keycloakService;
    private final TokenProvider tokenProvider;

    @GetMapping("/ping")
    public String ping() {
        return "UP";
    }

    @GetMapping("/test-db")
    public List<User> testDb() {
        return userService.findAll();
    }

    @GetMapping("/test-keycloak")
    public ResponseEntity<?> testKeycloak(@RequestParam String username) {
        return ResponseEntity.ok(keycloakService.test(username));

    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterDTO user) {
        try (Response response = keycloakService.createInKeycloak(user)) {
            if (response.getStatus() == HttpStatus.CREATED.value()) {
                UserResponseDTO saved = userService.createUser(user);
                keycloakService.addDbUserId(saved.id(), saved.email());
                return ResponseEntity.ok().body(saved);
            }
            if (response.getStatus() == HttpStatus.CONFLICT.value()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("It seems like this email is already registered.");
            }
            return ResponseEntity.status(response.getStatus()).body(response.getStatusInfo());
        } catch (DataIntegrityViolationException e) {
            keycloakService.removeFromKeycloak(user);
            return ResponseEntity.badRequest().body(e.getCause().getCause().getMessage());
        } catch (Exception e) {
            keycloakService.removeFromKeycloak(user);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/me/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorization, @RequestBody HashMap<String, String> body, HttpServletRequest request) {
        try {
            String token = authorization.split("Bearer ")[1];
            keycloakService.logout(token, body.get("refreshToken"));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginDTO user) {
        UserLoginResponseDTO responseDTO = null;
        try {
            responseDTO = keycloakService.userLogin(user.email(), user.password());
        } catch (NotAuthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findByID(@PathVariable String id) throws UserNotFoundException, UserBadRequestException, UserUnauthorizedException, UserInternalServerException {
        return ResponseEntity.ok().body(userService.findUserByID(id));
    }

    // CHEQUEAR, UNA VEZ QUE SE ACTUALIZA EMAIL EN KEYCLOAK NO ME DEJA LOGUEARME -> responseDTO is null
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody @Valid UserUpdateDTO user) throws UserNotFoundException, UserBadRequestException, UserUnauthorizedException, UserInternalServerException {
        return ResponseEntity.ok().body(userService.updateUser(id, user));
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<?> refresh(@RequestBody HashMap<String, String> body) {
        UserLoginResponseDTO response = keycloakService.refreshToken(body.get("refreshToken"));
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getGrants(@RequestHeader("Authorization") String authorization) throws VerificationException {
        String token = authorization.split("Bearer ")[1];
        return ResponseEntity.ok(keycloakService.getGrants(token));
    }

    @PostMapping("/validate")
    public Boolean validateToken(@RequestBody String token) {
        System.out.println(token);
        System.out.println(tokenProvider.isValid(token));
        return tokenProvider.isValid(token);
    }

    @PostMapping("/activate")
    public ResponseEntity<?> validateUser(@RequestBody UserActivateDTO body, @RequestParam String hash) {
        userService.activate(body.password(), hash);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/update-password-email")
    public ResponseEntity<?> sentUpdatePasswordEmail(@RequestParam("email") String email) {
        try {
            UserUpdateResponseDTO responseDTO = userService.sendRecoveryPasswordEmail(email);
            return ResponseEntity.ok().body("Email sent for user " + responseDTO.email());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody UserUpdatePasswordDTO body, @RequestParam String hash) {
        try {
            UserUpdateResponseDTO responseDTO = userService.updatePassword(body, hash);
            Map<String, Object> response = new HashMap<>();
            response.put("data", responseDTO);
            response.put("message", "Password updated successfully");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
