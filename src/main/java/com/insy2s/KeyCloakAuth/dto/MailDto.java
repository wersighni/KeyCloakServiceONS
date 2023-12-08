package com.insy2s.KeyCloakAuth.dto;

import lombok.Data;

import java.util.List;

@Data
public class MailDto {
    private String typeMail;

    private String fullname;
    private String mailTo;
    private String cc;
    private String subject;
    private String username;
    private String password;
    private String body;
    private List<Object> attachments;
}
