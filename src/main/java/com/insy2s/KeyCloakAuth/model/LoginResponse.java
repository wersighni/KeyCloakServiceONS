package com.insy2s.KeyCloakAuth.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class LoginResponse {
	
	private String access_token ;
	private String refresh_token ;
	private String expires_in;
	private String refresh_expires_in;
	private String token_type;
	Collection<Role> roles = new ArrayList<>();
	List<String> menus=new ArrayList<String>();
	List<String> pages=new ArrayList<String>();
	List<String> actions=new ArrayList<String>();







}
