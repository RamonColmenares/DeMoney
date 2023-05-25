package com.digitalmoney.msusers.controller;

import com.digitalmoney.msusers.application.dto.UserLoginDTO;
import com.digitalmoney.msusers.application.dto.UserLoginResponseDTO;
import com.digitalmoney.msusers.application.dto.UserRegisterDTO;
import com.digitalmoney.msusers.persistency.entity.User;
import com.digitalmoney.msusers.service.KeycloakService;
import com.digitalmoney.msusers.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.keycloak.common.VerificationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Response;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
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
    public ResponseEntity<?> testKeycloak(@RequestParam String username) {
        return ResponseEntity.ok(keycloakService.test(username));

    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterDTO user) {
        try (Response response = keycloakService.createInKeycloak(user)) {
            if (response.getStatus() == 201) {
                return ResponseEntity.ok().body(userService.createUser(user));
            }
            if (response.getStatus() == 409) {
                return ResponseEntity.status(409).body("It seems like this email is already registered.");
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

    @PostMapping("/me/logoutv2")
    public ResponseEntity<?> logoutv2() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        keycloakService.logoutv2(userId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginDTO user) {
    UserLoginResponseDTO responseDTO = keycloakService.userLogin(user.email(), user.password());
	if (responseDTO.token() == null) {
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}
	return ResponseEntity.ok(responseDTO);
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
}
