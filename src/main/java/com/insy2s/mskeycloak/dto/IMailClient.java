package com.insy2s.mskeycloak.dto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

//@FeignClient(value = "EmailService", url = "http://localhost:8090/api/mail")
@FeignClient(name = "${feign.name}", url = "${feign.url}")
public interface IMailClient
{
    @GetMapping("/")
    public String test(
            @RequestHeader("Authorization") String bearerToken
    );


}
