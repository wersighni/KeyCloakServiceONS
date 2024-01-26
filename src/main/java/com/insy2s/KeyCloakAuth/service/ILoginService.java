package com.insy2s.keycloakauth.service;

import com.insy2s.keycloakauth.dto.LoginRequest;
import com.insy2s.keycloakauth.dto.LoginResponse;
import org.springframework.http.ResponseEntity;

public interface ILoginService {
    void testVerificationCode(String email, String code);

    void resetPassword(String email, String password);

    void changePassword(String username, String currentPassword, String newPassword);

    LoginResponse login(LoginRequest loginrequest);

    void logout(String userId);

}
