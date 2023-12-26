package com.insy2s.KeyCloakAuth.service;

import com.insy2s.KeyCloakAuth.dto.ErrorResponse;
import com.insy2s.KeyCloakAuth.dto.RoleDto;
import com.insy2s.KeyCloakAuth.dto.UserDto;
import com.insy2s.KeyCloakAuth.model.*;
import com.insy2s.KeyCloakAuth.repository.RoleRepository;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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



            if (roleRepository.findByName(role.getName()).isPresent()
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
                    role.setKeycloakId(keycloakRoleId);
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

    public ResponseEntity<String> deleteRole(Long id) {
        Role role = roleRepository.findById(id).orElse(null);
        String roleKeycloakId = role.getKeycloakId();// Obtenez l'ID Keycloak correspondant à roleId depuis votre base de données ou une autre source
        System.out.println(roleKeycloakId);

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
            RoleRepresentation rolee = rolesResource.get(roleKeycloakId).toRepresentation();

            rolesResource.deleteRole(role.getName());

            // Supprimer le rôle de la base de données locale
            roleRepository.deleteById(id);
            return ResponseEntity.status(200).body("Le rôle a été supprimé avec succès.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Échec de la suppression du rôle.");
        }
    }


}