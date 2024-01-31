package com.insy2s.keycloakauth.controller;

import com.insy2s.keycloakauth.dto.ChangePasswordRequest;
import com.insy2s.keycloakauth.dto.LoginRequest;
import com.insy2s.keycloakauth.dto.LoginResponse;
import com.insy2s.keycloakauth.error.exception.BadRequestException;
import com.insy2s.keycloakauth.service.ILoginService;
import com.insy2s.keycloakauth.utils.SecurityUtils;
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

    /**
     * POST /api/keycloak/auth/login : authenticate the user.
     *
     * @param loginRequest the login request with the username and the password
     * @return the ResponseEntity with status 200 (OK) and the tokens in body,
     * or with status 401 (Unauthorized) if the credentials are incorrect.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        log.debug("REST request to login {}", loginRequest.getUsername());
        final LoginResponse loginResponse = loginService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    /**
     * POST /api/keycloak/auth/logout/{userId} : logout the user.
     *
     * @param userId the id of the user
     * @return the ResponseEntity with status 200 (OK) and the tokens in body,
     * or with status 400 (Bad Request).
     */
    @PostMapping("/logout/{userId}")
    public ResponseEntity<String> logout(@PathVariable String userId) {
        log.debug("REST request to logout {}", userId);
        final String currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId != null && !currentUserId.equals(userId)) {
            throw new BadRequestException("Vous ne pouvez pas déconnecter un autre utilisateur");
        }
        loginService.logout(userId);
        return ResponseEntity.ok("Déconnexion réussie, merci de votre confiance !");
    }

    /**
     * POST /api/keycloak/auth/findAccount/verificationCode : check verification code.
     *
     * @param email the email of the user
     * @param code  the verification code
     * @return the ResponseEntity with status 200 (OK) and the tokens in body,
     * or with status 401 (Unauthorized) if the code or email are incorrect.
     */
    @PostMapping("/findAccount/verificationCode")
    public ResponseEntity<String> checkVerificationCode(@RequestParam String email, @RequestParam String code) {
        log.debug("REST request to check verification code {} of {}", code, email);
        loginService.checkVerificationCode(email, code);
        return ResponseEntity.ok("Code valide");
    }

    /**
     * POST /api/keycloak/auth/findAccount/restPassword : reset password.
     *
     * @param email    the email of the user
     * @param password the new password
     * @return the ResponseEntity with status 200 (OK) and the tokens in body,
     * or with status 401 (Unauthorized) if the code or email are incorrect
     * or if trying to reset the password of another user,
     * or with status 404 (Not Found) if the user is not found.
     */
    @PostMapping("/findAccount/restPassword")
    public ResponseEntity<Void> resetPassword(@RequestParam String email, @RequestParam String password) {
        log.debug("REST request to reset password of {}", email);
        final String currentUserEmail = SecurityUtils.getCurrentUserEmail();
        if (!currentUserEmail.equals(email)) {
            throw new BadRequestException("Vous ne pouvez pas réinitialiser le mot de passe d'un autre utilisateur");
        }
        loginService.resetPassword(email, password);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/changePassword")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        log.debug("REST request to change password of {}", request.getUsername());
        final String currentUserEmail = SecurityUtils.getCurrentUserEmail();
        if (!currentUserEmail.equals(request.getUsername())) {
            throw new BadRequestException("Vous ne pouvez pas changer le mot de passe d'un autre utilisateur");
        }
        final String username = request.getUsername();
        final String currentPassword = request.getCurrentPassword();
        final String newPassword = request.getNewPassword();
        loginService.changePassword(username, currentPassword, newPassword);
        return ResponseEntity.ok().build();
    }

}
