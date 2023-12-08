package com.insy2s.KeyCloakAuth.service;

import com.insy2s.KeyCloakAuth.dto.ErrorResponse;
import com.insy2s.KeyCloakAuth.dto.UserDto;
import com.insy2s.KeyCloakAuth.model.*;
import com.insy2s.KeyCloakAuth.repository.RoleRepository;
import com.insy2s.KeyCloakAuth.repository.UserRepository;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;


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

    public ResponseEntity<String> deleteUser( String id) {
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
            System.out.println("id "+id);
        UserRepresentation user = usersResource.get(id).toRepresentation();
        usersResource.delete(id);
        userRepository.deleteById(id);

        return ResponseEntity.status(200).body("L'utilisateur " + user.getUsername() + " a été supprimé avec succès.");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Échec de la suppression de l'utilisateur.");
    }
}

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
    public ResponseEntity<String> toggleUserEnabled( String userId) {
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
                User user=userRepository.findById(userRepresentation.getId()).get();
            user.setEnabled(userRepresentation.isEnabled());
            userRepository.save(user);
                return ResponseEntity.ok().body("User enabled status toggled successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
   public ResponseEntity <?>createUser(UserDto user) {
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
           System.out.println("password    "+password);
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
               ErrorResponse errorResponse = new ErrorResponse("Email déjà existant");
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email déjà existant");
           }
           else {
               Response response = keycloak.realm(realm).users().create(newUser);
                if(response.getStatus()==201){
                    String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                    UserRepresentation createdUser = keycloak.realm(realm).users().get(userId).toRepresentation();

                    // Assign the desired role to the user
                    RoleRepresentation roleS = keycloak.realm(realm).roles().get(user.getRoles()).toRepresentation();
                    keycloak.realm(realm).users().get(userId).roles().realmLevel().add(Arrays.asList(roleS));
                    Role role=roleRepository.findByName(user.getRoles()).get();
                    ArrayList<Role> roles=new ArrayList<>();
                    roles.add(role);
                    User userSavedToBdLocal=new User(user.getUsername(),createdUser.getId(),user.getEmail(),user.getLastname(),user.getFirstname(),user.getPassword());
                    userSavedToBdLocal.setRoles(roles);
                    user.setId(userId);

                    User userSaved= userRepository.save(userSavedToBdLocal);
                    return ResponseEntity.status(201).body(user);
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

    public ResponseEntity listUsers() {
        List<User> usersWithAdminOrRhManagerRoles = userRepository.findByRoles_NameIn(Arrays.asList("ADMIN", "RHManager"));
        return ResponseEntity.status(200).body(usersWithAdminOrRhManagerRoles);
    }


}
