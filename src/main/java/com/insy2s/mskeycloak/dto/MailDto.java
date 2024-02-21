package com.insy2s.mskeycloak.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    public MailDto(String typeMail,String fullname, String mailTo, String subject, String username, String password, String body) {
        this.fullname = fullname;
        this.mailTo = mailTo;
        this.subject = subject;
        this.username = username;
        this.password = password;
        this.body = body;
        this.typeMail=typeMail;
    }
}
