package com.penny.pennytracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String to, int otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Penny Tracker – Password Reset Code");
        message.setText(
                "Your password reset code is: " + otp +
                        "\n\nThis code is valid for 10 minutes.\n" +
                        "If you didn’t request this, ignore this email."
        );

        mailSender.send(message);
    }
}
