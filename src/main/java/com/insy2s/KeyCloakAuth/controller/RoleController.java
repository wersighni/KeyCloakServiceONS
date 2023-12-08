package com.insy2s.KeyCloakAuth.controller;

import com.insy2s.KeyCloakAuth.model.Role;
import com.insy2s.KeyCloakAuth.repository.RoleRepository;
import com.insy2s.KeyCloakAuth.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/keycloak/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;
    @GetMapping("/")
    List<Role> getRole( )
    {
        return roleService.getRoles( );
    }




}
