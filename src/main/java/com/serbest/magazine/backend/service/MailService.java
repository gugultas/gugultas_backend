package com.serbest.magazine.backend.service;

import com.serbest.magazine.backend.dto.general.MessageResponseDTO;

public interface MailService {

    void sendEmailWithSimpleMessage(String to, String subject, String text);

    void receiveEmailWithSimpleMessage(String from,String subject, String text);

    void sendEmailWithMimeMessage(String to, String subject, String text);

    void receiveEmailWithMimeMessage(String from,String subject, String text);
}
