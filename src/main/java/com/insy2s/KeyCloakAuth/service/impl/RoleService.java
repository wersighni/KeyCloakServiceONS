package com.insy2s.keycloakauth.service.impl;

import com.insy2s.keycloakauth.error.exception.BadRequestException;
import com.insy2s.keycloakauth.error.exception.NotFoundException;
import com.insy2s.keycloakauth.model.Role;
import com.insy2s.keycloakauth.repository.IRoleRepository;
import com.insy2s.keycloakauth.service.IRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for {@link Role} entity.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RoleService implements IRoleService {

    private final IRoleRepository roleRepository;
    private final RealmResource realmResource;

    /**
     * {@inheritDoc}
     */
    @Override
    public Role create(Role role) {
        log.debug("Request to create Role : {}", role);
        role.setId(null);
        Optional<Role> existingRole = roleRepository.findByName(role.getName());
        RoleRepresentation existingRoleInKeyCloak = realmResource.roles().get(role.getName()).toRepresentation();
        if (existingRole.isPresent() || existingRoleInKeyCloak != null) {
            throw new BadRequestException("Le rôle existe déjà");
        }
        RoleRepresentation newRole = new RoleRepresentation();
        newRole.setName(role.getName());
        newRole.setDescription(role.getDescription());
        realmResource.roles().create(newRole);
        return roleRepository.save(role);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Role> getRoles() {
        log.debug("Request to get all Roles");
        return roleRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRolesWithStatusFalse() {
        log.debug("Request to get all Roles with status false");
        return roleRepository.getAllRolesByStatusFalse();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Role findByName(String name) {
        log.debug("Request to get Role : {}", name);
        return roleRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Role non trouvé avec le nom : " + name));
    }

    @Override
    public void delete(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role " + id + " non trouvé avec l'ID"));
        role.setStatus(true);
        try {
            RoleResource roleResource = realmResource.roles().get(role.getName());
            roleResource.remove();
            roleRepository.save(role);
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du rôle : {}", e.getMessage());
            throw new BadRequestException("Erreur lors de la suppression du rôle : " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Role findById(Long id) {
        log.debug("Request to get Role : {}", id);
        return roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role non trouvé avec l'ID : " + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Role update(Long id, Role role) {
        log.debug("Request to update Role : {}", role);
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role non trouvé avec l'ID : " + id));

        RoleResource roleResource = realmResource.roles().get(role.getName());
        RoleRepresentation roleToUpdate = roleResource.toRepresentation();
        if (roleToUpdate == null) {
            throw new NotFoundException("Role non trouvé avec le nom : " + role.getName());
        }

        roleToUpdate.setDescription(role.getDescription());
        roleResource.update(roleToUpdate);
        existingRole.setDescription(role.getDescription());
        return roleRepository.save(existingRole);
    }

}
