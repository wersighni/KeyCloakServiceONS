package com.insy2s.mskeycloak.dto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

//@FeignClient(value = "EmailService", url = "http://localhost:8090/api/mail")
//@FeignClient(name = "${feign.name}", url = "${feign.url}")
@FeignClient(value = "EmailService", url = "${email.url}")
public interface IMailClient
{
    @GetMapping("/")
    public String test();

    @PostMapping("/")
    ResponseEntity<Boolean> sendEmail(
            @RequestBody MailDto mailDto);

}
