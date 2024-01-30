package com.insy2s.keycloakauth.service.impl;

import com.insy2s.keycloakauth.model.Role;
import com.insy2s.keycloakauth.repository.IRoleRepository;
import com.insy2s.keycloakauth.service.IRoleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
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

    @Override
    public Role createRole(Role role) {
        try {
            RoleRepresentation newRole = new RoleRepresentation();
            newRole.setName(role.getName());
            newRole.setDescription(role.getDescription());

            Optional<Role> existingRole = roleRepository.findByName(role.getName());
            if (existingRole.isPresent() && !existingRole.get().isStatus()) {
                //Vérifier si  Le rôle existe soit localement (dans la base de donnée) soit dans Keycloak
                // Le nom existe déjà, renvoyez une erreur avec le statut HTTP 400 et un message approprié
                return null;
            } else {
                // Le rôle n'existe ni localement ni dans Keycloak, vous pouvez l'ajouter
                realmResource.roles().create(newRole);
                // Obtenez l'ID Keycloak du rôle nouvellement créé
                RoleRepresentation createdRole = realmResource
                        .roles()
                        .get(role.getName())
                        .toRepresentation();
                createdRole.getId();
                return roleRepository.save(role);
            }
        } catch (Exception e) {
            log.error("Erreur lors de la création du rôle : {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public List<Role> getAllRolesStatusFalse() {
        return roleRepository.getAllRolesByStatusFalse();
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name).orElse(null);
    }

    @Override
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id).orElse(null);
        Role role1 = roleRepository.findById(id).orElseThrow((null));
        role1.setStatus(true); // Désactiver le role
        roleRepository.save(role1);
        // Récupérer la ressource des rôles
        RolesResource rolesResource = realmResource.roles();
        // Supprimer le rôle par son ID
        try {
            RoleResource roleResource = rolesResource.get(role.getName());
            // Supprimer le rôle de la keycloak
            roleResource.remove();
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du rôle : {}", e.getMessage());
        }
    }

    @Override
    public Role getRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    public Role updateRole(Long id, Role role) {
        // Récupérer le rolee existant depuis le repository par son ID
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role non trouvé avec l'ID : " + id));
        // Récupérer la ressource des rôles
        RolesResource rolesResource = realmResource.roles();

        // Mettre à jour les détails du problème existant avec les nouvelles données
        RoleRepresentation roleToUpdate = rolesResource.get(role.getName()).toRepresentation();
        roleToUpdate.setDescription(role.getDescription());

        RoleResource roleResource = rolesResource.get(role.getName());
        roleResource.update(roleToUpdate);

        existingRole.setDescription(role.getDescription());

        // Enregistrer les modifications dans la base de données et retourner le problème mis à jour
        return roleRepository.save(existingRole);
    }

}
