package com.digitalmoney.msusers.service.feign;

import com.digitalmoney.msusers.application.dto.UserUpdatePasswordDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@FeignClient(name = "ms-mails")
public interface MailFeignService {

    @GetMapping("/send-activation-email")
    void sendActivationMail(@RequestParam String name, @RequestParam String email, @RequestParam String activationHash);

    @GetMapping("/send-reset-password-email")
    void sendRecoveryPasswordMail(@RequestParam String email, @RequestParam String resetPasswordHash);
}
