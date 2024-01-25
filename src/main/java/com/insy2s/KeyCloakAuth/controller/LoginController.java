package com.insy2s.keycloakauth.controller;


//import com.insy2s.KeyCloakAuth.ApiClient.MailingClient;

import com.insy2s.keycloakauth.dto.ChangePasswordRequest;
import com.insy2s.keycloakauth.dto.LoginRequest;
import com.insy2s.keycloakauth.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/keycloak/auth")
public class LoginController {

/*	@Autowired
	MailingClient mailingClient;*/

    private final LoginService loginservice;

    /*	@PostMapping("/findAccount/")
        public ResponseEntity<String> findAccount(@RequestParam String email ) {
            return loginservice.findAccount(email);
        }*/
    @PostMapping("/findAccount/verificationCode")
    public ResponseEntity<String> testVerificationCode(@RequestParam String email, @RequestParam String code) {
        return loginservice.testVerificationCode(email, code);
    }

    @PostMapping("/findAccount/restPassword")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String password) {
        return loginservice.resetPassword(email, password);
    }


    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        String username = request.getUsername();
        String currentPassword = request.getCurrentPassword();
        String newPassword = request.getNewPassword();
        {
            return loginservice.changePassword(username, currentPassword, newPassword);

        }
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginrequest) {

        return loginservice.login(loginrequest);
    }


    @PostMapping("/logout/{userId}")
    public ResponseEntity<String> logout(@PathVariable String userId) {
        return loginservice.logout(userId);
    }


}



