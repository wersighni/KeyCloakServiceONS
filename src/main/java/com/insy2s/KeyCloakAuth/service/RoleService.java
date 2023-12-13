package com.insy2s.KeyCloakAuth.service;

import com.insy2s.KeyCloakAuth.model.LoginRequest;
import com.insy2s.KeyCloakAuth.model.LoginResponse;
import com.insy2s.KeyCloakAuth.model.Role;
import com.insy2s.KeyCloakAuth.model.TokenResponse;
import com.insy2s.KeyCloakAuth.repository.RoleRepository;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class RoleService {
@Autowired
private RoleRepository roleRepository;
public List<Role>getRoles(){
    return roleRepository.findAll();

}}