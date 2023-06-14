package com.digitalmoney.msusers.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@FeignClient(name = "ms-mails", url = "localhost:8084") //TODO: change to use the cloud instance
public interface MailFeignService {

    @GetMapping("/send-activation-email")
    void sendActivationMail(@RequestParam String name, @RequestParam String email, @RequestParam String activationHash);
}
