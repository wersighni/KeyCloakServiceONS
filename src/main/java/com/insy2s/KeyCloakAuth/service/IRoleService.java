package com.insy2s.keycloakauth.service;

import com.insy2s.keycloakauth.model.Role;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IRoleService {
    Role createRole(Role role);

    List<Role> getRoles();

    List<Role> getAllRolesStatusFalse();

    Role findByName(String name);

    ResponseEntity<Role> deleteRole(Long id);

    Role getRoleById(Long id);

    Role updateRole(Long id, Role role);
}
