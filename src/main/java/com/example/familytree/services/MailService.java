package com.example.familytree.services;

import com.example.familytree.models.dto.DataMailDto;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public interface MailService {
    void sendHtmlMail(DataMailDto dataMail, String templateName) throws MessagingException;
}