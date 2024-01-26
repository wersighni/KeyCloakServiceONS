package com.insy2s.keycloakauth.controller;

import com.insy2s.keycloakauth.model.Role;
import com.insy2s.keycloakauth.service.IRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/keycloak/roles")
public class RoleController {


    private final IRoleService roleService;

    @PostMapping(value = "/")
    ResponseEntity<Role> createRole(@RequestBody Role role) {
        Role roleCreated = roleService.createRole(role);
        if (roleCreated == null) {
            return ResponseEntity.status(400).body(null);
        }
        return ResponseEntity.status(201).body(roleCreated);
    }

    @GetMapping("/")
    List<Role> getRole() {
        return roleService.getRoles();
    }

    @GetMapping("/statusFalse")
    public List<Role> getAllRolesStatusFalse() {
        return roleService.getAllRolesStatusFalse();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public Role getRoleById(@PathVariable Long id) {
        return roleService.getRoleById(id);
    }

    @GetMapping("byName/{name}")
    public Role getByName(@PathVariable String name) {
        return roleService.findByName(name);
    }


    @PostMapping("/{id}/update")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody Role role) {
        try {
            Role updatedRole = roleService.updateRole(id, role);
            return ResponseEntity.ok(updatedRole);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la modification du role.");
        }
    }


}
