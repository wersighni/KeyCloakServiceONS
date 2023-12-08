package com.insy2s.KeyCloakAuth.ApiClient;


import com.insy2s.KeyCloakAuth.dto.MailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/*@FeignClient(value = "EmailService", url = "${email.url}")

public interface MailingClient {
    @PostMapping("/")
    ResponseEntity<Boolean> sendEmail(
            @RequestBody MailDto mailDto);


}*/

