package com.serbest.magazine.backend.service.impl;

import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import com.serbest.magazine.backend.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {
    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public MailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void sendEmailWithSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        emailSender.send(message);
    }

    @Async
    @Override
    public void receiveEmailWithSimpleMessage(String from, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(senderEmail);
        message.setSubject(subject);
        message.setText(text);

        emailSender.send(message);
    }

    @Async
    @Override
    public void sendEmailWithMimeMessage(String to, String subject, String text) {
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(text, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(senderEmail);
            emailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("failed to send email");
        }
    }

    @Async
    @Override
    public void receiveEmailWithMimeMessage(String from, String subject, String text) {
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setFrom(new InternetAddress(from));
            helper.setText(text, true);
            helper.setTo(senderEmail);
            helper.setSubject(from +  " - " + subject);
            emailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("failed to send email");
        }
    }
}
