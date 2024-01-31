package com.insy2s.keycloakauth.service.impl;

import com.insy2s.keycloakauth.error.exception.BadRequestException;
import com.insy2s.keycloakauth.error.exception.NotFoundException;
import com.insy2s.keycloakauth.model.Role;
import com.insy2s.keycloakauth.model.User;
import com.insy2s.keycloakauth.repository.IRoleRepository;
import com.insy2s.keycloakauth.repository.IUserRepository;
import com.insy2s.keycloakauth.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.core.Response;
import java.util.*;

/**
 * Service class for managing {@link User}.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements com.insy2s.keycloakauth.service.IUserService {

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final RealmResource realmResource;

    @Override
    public void deleteUser(String id) {
        UsersResource usersResource = realmResource.users();
        UserRepresentation user = usersResource.get(id).toRepresentation();
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        try (Response response = usersResource.delete(id)) {
            if (response.getStatus() != 204) {
                throw new BadRequestException("Erreur lors de la suppression de l'utilisateur");
            }
        }
        userRepository.deleteById(id);
    }

    @Override
    public void toggleUserEnabled(String userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
            UserRepresentation userRepresentation = realmResource.users().get(userId).toRepresentation();
            final boolean previousStatus = userRepresentation.isEnabled();
            user.setEnabled(!previousStatus);
            userRepository.save(user);
            userRepresentation.setEnabled(!previousStatus);
            realmResource.users().get(userId).update(userRepresentation);
        } catch (Exception e) {
            throw new BadRequestException("Erreur lors de la mise à jour du statut de l'utilisateur");
        }
    }

    @Override
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
            String password = RandomUtils.generateRandomString(30);
            user.setPassword(password);
            credentials.setValue(password); // Set the desired password
            newUser.setCredentials(List.of(credentials));
            // Create the user
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                // L'email existe déjà, renvoyez une erreur avec le statut HTTP 400 et un message approprié
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email déjà existant");
            } else {
                Response response = realmResource.users().create(newUser);
                if (response.getStatus() == 201) {
                    String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                    UserRepresentation createdUser = realmResource.users().get(userId).toRepresentation();
                    Collection<Role> roles = user.getRoles();
                    if ((roles == null || roles.isEmpty()) && user.getLstRole() != null &&
                            !user.getLstRole().isEmpty()) {
                        Role rol = roleRepository.findByName(user.getLstRole()).orElse(null);
                        if (rol != null) {
                            roles = new ArrayList<>();
                            roles.add(rol);
                        }
                        // Assign the desired role to the user
                        if (roles != null && !roles.isEmpty()) {
                            for (Role r : roles) {
                                RoleRepresentation roleS = realmResource.roles().get(r.getName()).toRepresentation();
                                realmResource.users().get(userId).roles().realmLevel().add(Arrays.asList(roleS));
                            }
                        }
                    }
                    User userSavedToBdLocal =
                            new User(user.getUsername(), user.getFirstname(), user.getLastname(), createdUser.getId(),
                                    user.getEmail(), roles);
                    userSavedToBdLocal.setDateInscription(new Date());  // Définition de la date d'inscription
                    userSavedToBdLocal.setPassword(password);
                    User userSaved = userRepository.save(userSavedToBdLocal);
                    return ResponseEntity.status(201).body(userSaved);
                }
                return ResponseEntity.status(204).body(null);

                // User created successfully

            }
        } catch (Exception e) {
            log.error("Erreur lors de la création de l'utilisateur : {}", e.getMessage());
            return ResponseEntity.status(500).body(null);
        }

    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(String username) {
        log.debug("SERVICE : getUser : {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(String id) {
        log.debug("SERVICE : getUserById : {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers() {
        log.debug("SERVICE : getUsers");
        return userRepository.findAll();
    }

    @Override
    public User updateUser(User user) {
        UsersResource usersResource = realmResource.users();
        final String userId = user.getId();
        UserRepresentation existingUser = usersResource.get(userId).toRepresentation();
        existingUser.setFirstName(user.getFirstname());
        existingUser.setLastName(user.getLastname());
        existingUser.setEmail(user.getEmail());
        usersResource.get(userId).update(existingUser);
        UserRepresentation updatedUser = usersResource.get(userId).toRepresentation();
        if (updatedUser == null) {
            throw new BadRequestException("Erreur lors de la mise à jour de l'utilisateur");
        }
        List<RoleRepresentation> existingRoles = realmResource.users().get(userId).roles().realmLevel().listAll();
        realmResource.users().get(userId).roles().realmLevel().remove(existingRoles);
        for (Role role : user.getRoles()) {
            RoleRepresentation roleS = realmResource.roles().get(role.getName()).toRepresentation();
            realmResource.users().get(userId).roles().realmLevel().add(Collections.singletonList(roleS));
        }
        return userRepository.save(user);
    }

}
