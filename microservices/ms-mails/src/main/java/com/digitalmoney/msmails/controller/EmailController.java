package com.digitalmoney.msmails.controller;

import com.digitalmoney.msmails.dto.UserUpdatePasswordDTO;
import com.digitalmoney.msmails.service.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;

@RestController
@AllArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @GetMapping("/send-activation-email")
    public void register(@RequestParam String name, @RequestParam String email, @RequestParam String activationHash) {
        String activationLink = "www.digitalmoneyhouse.com/activate?hash=" + activationHash;
        Context context = new Context();
        context.setVariable("activation_link", activationLink);
        context.setVariable("name", name);

        emailService.sendEmail(email, "Welcome to Digital Money House!", "account_activation", context);
    }

    @GetMapping("/send-reset-password-email")
    public void recoverPassword(@RequestParam String email, @RequestParam String resetPasswordHash) {
        String recoveryLink = "www.digitalmoneyhouse.com/recover-password?hash=" + resetPasswordHash;
        Context context = new Context();
        context.setVariable("email", email);
        context.setVariable("recoveryLink", recoveryLink);

        emailService.sendEmail(email, "Password Recovery", "password_recovery", context);
    }

}
