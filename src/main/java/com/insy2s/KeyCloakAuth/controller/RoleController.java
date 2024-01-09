package com.insy2s.KeyCloakAuth.controller;

import com.insy2s.KeyCloakAuth.dto.RoleDto;
import com.insy2s.KeyCloakAuth.dto.UserDto;
import com.insy2s.KeyCloakAuth.model.Access;
import com.insy2s.KeyCloakAuth.model.Role;
import com.insy2s.KeyCloakAuth.repository.RoleRepository;
import com.insy2s.KeyCloakAuth.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/keycloak/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

   @PostMapping(value = "/")
    ResponseEntity <?> createRole(@RequestBody Role role){
        return roleService.createRole( role);
    }
    @GetMapping("/")
    List<Role> getRole( )
    {
        return roleService.getRoles( );
    }
    @GetMapping("/statusFalse")
    public List<Role> getAllRolesStatusFalse() {
        return roleService.getAllRolesStatusFalse();
    }
    @PutMapping(value = "/{id}")
    public ResponseEntity deleteRole(@PathVariable Long id) {
        return roleService.deleteRole(id);
    }
    @GetMapping("/{id}")
    public Role getRoleById(@PathVariable Long id) {
        return roleService.getRoleById(id);
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
