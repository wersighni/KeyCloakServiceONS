package com.insy2s.KeyCloakAuth.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {
	
	private String username;
	private String password;

	
	

}
