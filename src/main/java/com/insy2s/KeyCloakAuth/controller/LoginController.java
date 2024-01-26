package com.insy2s.keycloakauth.controller;

import com.insy2s.keycloakauth.dto.ChangePasswordRequest;
import com.insy2s.keycloakauth.dto.LoginRequest;
import com.insy2s.keycloakauth.dto.LoginResponse;
import com.insy2s.keycloakauth.service.ILoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for fot the authentication of the user.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/keycloak/auth")
public class LoginController {

    private final ILoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginrequest) {
        log.debug("REST request to login {}", loginrequest.getUsername());
        LoginResponse loginResponse = loginService.login(loginrequest);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout/{userId}")
    public ResponseEntity<String> logout(@PathVariable String userId) {
        log.debug("REST request to logout {}", userId);
        loginService.logout(userId);
        return ResponseEntity.ok("Déconnexion réussie, merci de votre confiance !");
    }

    @PostMapping("/findAccount/verificationCode")
    public ResponseEntity<String> testVerificationCode(@RequestParam String email, @RequestParam String code) {
        log.debug("REST request to test verification code");
        loginService.testVerificationCode(email, code);
        return ResponseEntity.ok("Code valide");
    }

    @PostMapping("/findAccount/restPassword")
    public ResponseEntity<Void> resetPassword(@RequestParam String email, @RequestParam String password) {
        log.debug("REST request to reset password of {}", email);
        loginService.resetPassword(email, password);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/changePassword")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        log.debug("REST request to change password of {}", request.getUsername());
        String username = request.getUsername();
        String currentPassword = request.getCurrentPassword();
        String newPassword = request.getNewPassword();
        loginService.changePassword(username, currentPassword, newPassword);
        return ResponseEntity.ok().build();
    }

}
