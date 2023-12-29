package com.insy2s.KeyCloakAuth.service;

import com.insy2s.KeyCloakAuth.dto.ErrorResponse;
import com.insy2s.KeyCloakAuth.model.*;
import com.insy2s.KeyCloakAuth.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
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
    public ResponseEntity <?>createRole(Role role) {
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
                ErrorResponse errorResponse = new ErrorResponse("Nom déjà existant");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nom déjà existant");
            }
            else {
                    // Le rôle n'existe ni localement ni dans Keycloak, vous pouvez l'ajouter
                    keycloak.realm(realm).roles().create(newRole);
                    // Obtenez l'ID Keycloak du rôle nouvellement créé
                    RoleRepresentation createdRole = keycloak.realm(realm).roles().get(role.getName()).toRepresentation();
                    String keycloakRoleId = createdRole.getId();
                    // Associez l'ID Keycloak à l'ID de la base de données pour le rôle créé
                    //role.setKeycloakId(keycloakRoleId);
                    System.out.println(role);
                    Role roleSaved = roleRepository.save(role);
                    return ResponseEntity.status(201).body(roleSaved);
                }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    public List<Role>getRoles(){
        return roleRepository.findAll();}
    public List<Role> getAllRolesStatusFalse() {

        return roleRepository.getAllRolesByStatusFalse();
    }

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
    public Role getRoleById(Long id) {
        return roleRepository.findById(id).get();
    }

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