package com.insy2s.mskeycloak.client;

import com.insy2s.mskeycloak.dto.MailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "email-service")
public interface IMailClient {

    @PostMapping("/api/mail/")
    ResponseEntity<Boolean> sendEmail(@RequestBody MailDto mailDto);

}
