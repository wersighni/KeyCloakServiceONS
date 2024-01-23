package com.insy2s.KeyCloakAuth.controller;

import com.insy2s.KeyCloakAuth.model.Role;
import com.insy2s.KeyCloakAuth.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/keycloak/roles")
public class RoleController {


    private final IRoleService iRoleService;

    @PostMapping(value = "/")
    ResponseEntity<Role> createRole(@RequestBody Role role) {
        Role roleCreated = iRoleService.createRole(role);
        if (roleCreated == null) {
            return ResponseEntity.status(400).body(null);
        }
        return ResponseEntity.status(201).body(roleCreated);
    }

    @GetMapping("/")
    List<Role> getRole() {
        return iRoleService.getRoles();
    }

    @GetMapping("/statusFalse")
    public List<Role> getAllRolesStatusFalse() {
        return iRoleService.getAllRolesStatusFalse();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        iRoleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public Role getRoleById(@PathVariable Long id) {
        return iRoleService.getRoleById(id);
    }

    @GetMapping("byName/{name}")
    public Role getByName(@PathVariable String name) {
        return iRoleService.findByName(name);
    }


    @PostMapping("/{id}/update")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody Role role) {
        try {
            Role updatedRole = iRoleService.updateRole(id, role);
            return ResponseEntity.ok(updatedRole);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la modification du role.");
        }
    }


}
