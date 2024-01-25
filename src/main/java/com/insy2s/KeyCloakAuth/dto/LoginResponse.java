package com.insy2s.keycloakauth.dto;

import com.insy2s.keycloakauth.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    Collection<Role> roles = new ArrayList<>();
    List<AccessDto> access = new ArrayList<>();
    List<String> menus = new ArrayList<>();
    List<String> pages = new ArrayList<>();
    List<String> actions = new ArrayList<>();

    private String access_token;
    private String refresh_token;
    private String expires_in;
    private String refresh_expires_in;
    private String token_type;

}
