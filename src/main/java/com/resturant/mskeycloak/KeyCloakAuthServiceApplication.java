package com.resturant.mskeycloak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
@EnableFeignClients
public class KeyCloakAuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(KeyCloakAuthServiceApplication.class, args);
    }



}
