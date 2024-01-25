package com.insy2s.keycloakauth.service;

import com.insy2s.keycloakauth.model.Role;
import com.insy2s.keycloakauth.model.User;
import com.insy2s.keycloakauth.repository.RoleRepository;
import com.insy2s.keycloakauth.repository.UserRepository;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.security.SecureRandom;
import java.util.*;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
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

    public static String generateRandomPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();

        int length = 30; // Longueur du mot de passe souhaitée

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    public ResponseEntity<String> deleteUser(String id) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .password(passwordAdmin)
                .username(userNameAdmin)
                .clientSecret(clientSecret)
                .build();
        // Get the realm resource for the desired realm
        RealmResource realmResource = keycloak.realm(realm);
        // Get the users resource
        UsersResource usersResource = realmResource.users();
        // Delete the user by ID
        try {
            System.out.println("id " + id);
            UserRepresentation user = usersResource.get(id).toRepresentation();
            usersResource.delete(id);
            userRepository.deleteById(id);

            return ResponseEntity.status(200).body("{\"message\": \"L'utilisateur " + user.getUsername() + " a été supprimé avec succès.\"}");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Échec de la suppression de l'utilisateur.");
        }
    }

    public ResponseEntity<String> toggleUserEnabled(String userId) {
        try {

            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .password(passwordAdmin)
                    .username(userNameAdmin)
                    .clientSecret(clientSecret)
                    .build();
            // Get the realm resource for the desired realm
            RealmResource realmResource = keycloak.realm(realm);

            // Get the user's representation
            UserRepresentation userRepresentation = realmResource.users().get(userId).toRepresentation();

            // Toggle the enabled status
            userRepresentation.setEnabled(!userRepresentation.isEnabled());

            // Update the user
            realmResource.users().get(userId).update(userRepresentation);
            User user = userRepository.findById(userRepresentation.getId()).get();
            user.setEnabled(userRepresentation.isEnabled());
            userRepository.save(user);
            //ToDo : renvoyer boolean au lieu de response
            return ResponseEntity.ok().body("User enabled status toggled successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    public ResponseEntity<?> createUser(User user) {
        try {

            UserRepresentation newUser = new UserRepresentation();
            newUser.setUsername(user.getUsername());
            newUser.setEmail(user.getEmail());
            newUser.setFirstName(user.getFirstname());
            newUser.setLastName(user.getLastname());
            newUser.setEnabled(true);

            // Set user credentials (password)
            CredentialRepresentation credentials = new CredentialRepresentation();
            credentials.setTemporary(false);
            credentials.setType(CredentialRepresentation.PASSWORD);
            String password = generateRandomPassword();
            user.setPassword(password);
            credentials.setValue(password); // Set the desired password
            newUser.setCredentials(List.of(credentials));
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .password(passwordAdmin)
                    .username(userNameAdmin)
                    .clientSecret(clientSecret)
                    .build();
            // Create the user
           /*if(userRepository.findByUsername(user.getUsername()).isPresent()){
               ErrorResponse errorResponse = new ErrorResponse("Username déjà existant");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username déjà existant");
           }*/
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                // L'email existe déjà, renvoyez une erreur avec le statut HTTP 400 et un message approprié
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email déjà existant");
            } else {
                Response response = keycloak.realm(realm).users().create(newUser);
                System.out.println("reponse de save" + response.getStatus());
                if (response.getStatus() == 201) {
                    String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                    UserRepresentation createdUser = keycloak.realm(realm).users().get(userId).toRepresentation();
                    Collection<Role> roles = user.getRoles();
                    if ((roles == null || roles.isEmpty()) && user.getLstRole() != null && !user.getLstRole().isEmpty()) {
                        Role rol = roleRepository.findByName(user.getLstRole()).orElse(null);
                        if (rol != null) {
                            roles = new ArrayList<Role>();
                            roles.add(rol);
                        }
                        // Assign the desired role to the user
                        if (!roles.isEmpty()) {
                            for (Role r : roles) {
                                RoleRepresentation roleS = keycloak.realm(realm).roles().get(r.getName()).toRepresentation();
                                keycloak.realm(realm).users().get(userId).roles().realmLevel().add(Arrays.asList(roleS));
                            }
                        }
                    }
                    User userSavedToBdLocal = new User(user.getUsername(), user.getFirstname(), user.getLastname(), createdUser.getId(), user.getEmail(), roles);
                    userSavedToBdLocal.setDateInscription(new Date());  // Définition de la date d'inscription
                    userSavedToBdLocal.setPassword(password);
                    User userSaved = userRepository.save(userSavedToBdLocal);
                    return ResponseEntity.status(201).body(userSaved);
                }
                return ResponseEntity.status(204).body(null);

                // User created successfully

            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }

    }

    public User getUser(String username) {
        return userRepository.findByUsername(username).get();
    }

    public User getUserById(String id) {
        return userRepository.findById(id).get();
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public ResponseEntity updateUser(User user) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .password(passwordAdmin)
                .username(userNameAdmin)
                .clientSecret(clientSecret)
                .build();

        // Get the realm resource for the desired realm
        RealmResource realmResource = keycloak.realm(realm);
        // Get the users resource
        UsersResource usersResource = realmResource.users();
        // Find the user by ID or username (You may use an ID or username to identify the user)
        String userId = user.getId(); // Replace with your method of obtaining the user's ID

        UserRepresentation existingUser = usersResource.get(userId).toRepresentation();
        // Update user attributes
        existingUser.setFirstName(user.getFirstname());
        existingUser.setLastName(user.getLastname());
        existingUser.setEmail(user.getEmail());
        // Add more attribute updates as needed

        // Update the user
        usersResource.get(userId).update(existingUser);

        // Optionally, you can check if the update was successful and return an appropriate ResponseEntity
        UserRepresentation updatedUser = usersResource.get(userId).toRepresentation();
        if (updatedUser != null) {
            List<RoleRepresentation> existingRoles = keycloak.realm(realm).users().get(userId).roles().realmLevel().listAll();
            keycloak.realm(realm).users().get(userId).roles().realmLevel().remove(existingRoles);

            for (Role role : user.getRoles()) {
                System.out.println(role.getName());
                RoleRepresentation roleS = keycloak.realm(realm).roles().get(role.getName()).toRepresentation();
                System.out.println(roleS.getName());
                keycloak.realm(realm).users().get(userId).roles().realmLevel().add(Arrays.asList(roleS));
            }
            return ResponseEntity.status(200).body(userRepository.save(user));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User update failed");
        }
    }


}
