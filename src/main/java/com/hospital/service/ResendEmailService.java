
package com.hospital.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ResendEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public ResendEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String recipientEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(recipientEmail);
        message.setSubject(
                "Hospital Management System - Password Reset OTP"
        );

        message.setText(
                "Hello,\n\n"
                + "Your Hospital Management System password reset OTP is:\n\n"
                + otp + "\n\n"
                + "This OTP expires in 5 minutes.\n\n"
                + "If you did not request this, please ignore this email.\n\n"
                + "Regards,\n"
                + "Hospital Management System"
        );

        try {
            mailSender.send(message);

            System.out.println(
                    "Password reset OTP email sent successfully to: "
                    + recipientEmail
            );

        } catch (Exception ex) {
            System.err.println(
                    "Gmail SMTP email sending failed: " + ex.getMessage()
            );

            throw new IllegalStateException(
                    "Unable to send OTP email using Gmail SMTP. "
                    + "Check your Gmail SMTP configuration.",
                    ex
            );
        }
    }
}
