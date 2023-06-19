package com.digitalmoney.msmails.service;

import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


@Service @AllArgsConstructor
public class EmailService {
    private JavaMailSender mailSender;
    private SpringTemplateEngine templateEngine;

    public void sendEmail(String to, String subject, String template, Context context) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setTo(to);
            helper.setSubject(subject);
            String content = templateEngine.process(template, context);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (Exception e) {
            // handle exception
            e.printStackTrace();
        }
    }

}