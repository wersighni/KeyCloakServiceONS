package com.insy2s.mskeycloak.client;

import com.insy2s.mskeycloak.dto.MailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "EmailService", url = "${email.url}")
public interface IMailClient {

    @PostMapping("/")
    ResponseEntity<Boolean> sendEmail(@RequestBody MailDto mailDto);

}
