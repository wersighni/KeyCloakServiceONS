package com.insy2s.keycloakauth.service;

import com.insy2s.keycloakauth.model.Role;

import java.util.List;

/**
 * Interface Service for {@link Role} entity.
 */
public interface IRoleService {

    Role createRole(Role role);

    List<Role> getRoles();

    List<Role> getAllRolesStatusFalse();

    Role findByName(String name);

    void deleteRole(Long id);

    Role getRoleById(Long id);

    Role updateRole(Long id, Role role);

}
