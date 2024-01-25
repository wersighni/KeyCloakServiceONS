package com.insy2s.keycloakauth.service;

import com.insy2s.keycloakauth.model.Role;
import com.insy2s.keycloakauth.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService implements IRoleService {
    @Autowired
    private RoleRepository roleRepository;
    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.admin-username}")
    private String userNameAdmin;

    @Value("${keycloak.admin-password}")
    private String passwordAdmin;

    @Override
    public Role createRole(Role role) {
        try {
            RoleRepresentation newRole = new RoleRepresentation();
            newRole.setName(role.getName());
            newRole.setDescription(role.getDescription());

            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .password(passwordAdmin)
                    .username(userNameAdmin)
                    .clientSecret(clientSecret)
                    .build();


            Optional<Role> existingRole = roleRepository.findByName(role.getName());
            if (existingRole.isPresent() && !existingRole.get().isStatus()
            ) {

                //Vérifier si  Le rôle existe soit localement (dans la base de donnée) soit dans Keycloak
                // Le nom existe déjà, renvoyez une erreur avec le statut HTTP 400 et un message approprié
                return null;
            } else {
                // Le rôle n'existe ni localement ni dans Keycloak, vous pouvez l'ajouter
                keycloak.realm(realm).roles().create(newRole);
                // Obtenez l'ID Keycloak du rôle nouvellement créé
                RoleRepresentation createdRole = keycloak.realm(realm).roles().get(role.getName()).toRepresentation();
                String keycloakRoleId = createdRole.getId();
                // Associez l'ID Keycloak à l'ID de la base de données pour le rôle créé
                //role.setKeycloakId(keycloakRoleId);
                System.out.println(role);
                Role roleSaved = roleRepository.save(role);
                return (roleSaved);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    public ResponseEntity<Role> deleteRole(Long id) {
        Role role = roleRepository.findById(id).orElse(null);
        Role role1 = roleRepository.findById(id).orElseThrow((null));
        role1.setStatus(true); // Désactiver le role
        Role updateRole = roleRepository.save(role1);
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .password(passwordAdmin)
                .username(userNameAdmin)
                .clientSecret(clientSecret)
                .build();
        // Récupérer la ressource du realm pour le realm désiré
        RealmResource realmResource = keycloak.realm(realm);
        // Récupérer la ressource des rôles
        RolesResource rolesResource = realmResource.roles();
        // Supprimer le rôle par son ID
        try {
            RoleResource roleResource = rolesResource.get(role.getName());
            // Supprimer le rôle de la keycloak
            roleResource.remove();
            //roleRepository.deleteById(id);
            return ResponseEntity.ok(updateRole);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(null);
        }
    }

    @Override
    public Role getRoleById(Long id) {
        return roleRepository.findById(id).get();
    }

    @Override
    public Role updateRole(Long id, Role role) {
        // Récupérer le rolee existant depuis le repository par son ID
        Role existingRole = roleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Role non trouvé avec l'ID : " + id));
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .password(passwordAdmin)
                .username(userNameAdmin)
                .clientSecret(clientSecret)
                .build();

        // Récupérer la ressource du realm pour le realm désiré
        RealmResource realmResource = keycloak.realm(realm);
        // Récupérer la ressource des rôles
        RolesResource rolesResource = realmResource.roles();

        // Mettre à jour les détails du problème existant avec les nouvelles données
        RoleRepresentation roleToUpdate = rolesResource.get(role.getName()).toRepresentation();
        System.out.println(roleToUpdate);
        roleToUpdate.setDescription(role.getDescription());

        RoleResource roleResource = rolesResource.get(role.getName());
        System.out.println(roleResource);
        roleResource.update(roleToUpdate);

        existingRole.setDescription(role.getDescription());

        // Enregistrer les modifications dans la base de données et retourner le problème mis à jour
        return roleRepository.save(existingRole);
    }

}
