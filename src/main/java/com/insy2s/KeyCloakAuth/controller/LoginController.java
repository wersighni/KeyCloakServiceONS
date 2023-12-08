package com.insy2s.KeyCloakAuth.controller;


//import com.insy2s.KeyCloakAuth.ApiClient.MailingClient;
import com.insy2s.KeyCloakAuth.dto.ChangePasswordRequest;
import com.insy2s.KeyCloakAuth.dto.MailDto;
import com.insy2s.KeyCloakAuth.model.LoginRequest;
import com.insy2s.KeyCloakAuth.model.LoginResponse;
import com.insy2s.KeyCloakAuth.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.security.SecureRandom;
import java.util.*;

@RestController
@RequestMapping("/api/keycloak/auth")
public class LoginController {

/*	@Autowired
	MailingClient mailingClient;*/
	@Autowired
	LoginService loginservice;
/*	@PostMapping("/findAccount/")
	public ResponseEntity<String> findAccount(@RequestParam String email ) {
		return loginservice.findAccount(email);
	}*/
	@PostMapping("/findAccount/verificationCode")
	public ResponseEntity<String> testVerificationCode(@RequestParam String email,@RequestParam String code ) {
		return loginservice.testVerificationCode(email, code);
	}
	@PostMapping("/findAccount/restPassword")
	public ResponseEntity<String> resetPassword(@RequestParam String email , @RequestParam String password) {
		return loginservice.resetPassword(email, password);
	}




	@PostMapping("/changePassword")
	public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
		String username = request.getUsername();
		String currentPassword = request.getCurrentPassword();
		String newPassword = request.getNewPassword(); {
		return loginservice.changePassword(username,currentPassword,newPassword);

	}}

	@PostMapping("/login")
	public ResponseEntity login (@RequestBody LoginRequest loginrequest) {

		return loginservice.login(loginrequest);
	}


	@PostMapping("/logout/{userId}")
	public ResponseEntity<String> logout(@PathVariable String userId) {
		return loginservice.logout(userId);
	}



}



