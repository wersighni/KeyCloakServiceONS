package com.resturant.mskeycloak.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resturant.mskeycloak.config.KeycloakConfig;
import com.resturant.mskeycloak.client.IMailClient;
import com.resturant.mskeycloak.model.User;
import com.resturant.mskeycloak.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.sql.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link UserController}.
 */
@AutoConfigureMockMvc
@SpringBootTest
@WithMockUser
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IUserRepository userRepository;
    @MockBean
    private Keycloak keycloak;
    @MockBean
    private KeycloakConfig keycloakConfig;
    @MockBean
    private IMailClient mailClient;

    private User user;

    @BeforeEach
    void setUp() {
        when(keycloak.realm(keycloakConfig.getRealm())).thenReturn(Mockito.mock(RealmResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).users()).thenReturn(Mockito.mock(UsersResource.class));
        user = User.builder()
                .id("id")
                .username("email@local.host")
                .email("email@local.host")
                .firstname("firstname")
                .lastname("lastname")
                .docProfileId("docProfileId")
                .password("password")
                .enabled(true)
                .status(true)
                .dateInscription(Date.valueOf("2021-01-01"))
                .build();

        when(mailClient.sendEmail(any())).thenReturn(ResponseEntity.ok(true));

    }

    //////////////////////////////////////////////////////////////////////
    // GET /api/keycloak/users/find : getByUsername
    //////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void testGetByUsername_notFound() throws Exception {
        mockMvc.perform(get("/api/keycloak/users/find")
                        .param("username", "wrongUsername"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void testGetByUsername_success() throws Exception {
        userRepository.saveAndFlush(user);

        mockMvc.perform(get("/api/keycloak/users/find")
                        .param("username", user.getUsername()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.firstname").value(user.getFirstname()))
                .andExpect(jsonPath("$.lastname").value(user.getLastname()))
                .andExpect(jsonPath("$.docProfileId").value(user.getDocProfileId()))
                .andExpect(jsonPath("$.enabled").value(user.isEnabled()))
                .andExpect(jsonPath("$.status").value(user.isStatus()));
    }

    //////////////////////////////////////////////////////////////////////
    // GET  /api/keycloak/users/{id} : getById
    //////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void testGetById_notFound() throws Exception {
        mockMvc.perform(get("/api/keycloak/users/{id}", "wrongId"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void testGetById_success() throws Exception {
        userRepository.saveAndFlush(user);

        mockMvc.perform(get("/api/keycloak/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.firstname").value(user.getFirstname()))
                .andExpect(jsonPath("$.lastname").value(user.getLastname()))
                .andExpect(jsonPath("$.docProfileId").value(user.getDocProfileId()))
                .andExpect(jsonPath("$.enabled").value(user.isEnabled()))
                .andExpect(jsonPath("$.status").value(user.isStatus()));
    }

    //////////////////////////////////////////////////////////////////////
    // GET  /api/keycloak/users : getAll
    //////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void testGetAll_noUser() throws Exception {
        mockMvc.perform(get("/api/keycloak/users/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    void testGetAll_oneUser() throws Exception {
        userRepository.saveAndFlush(user);

        mockMvc.perform(get("/api/keycloak/users/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(user.getId()))
                .andExpect(jsonPath("$[0].username").value(user.getUsername()))
                .andExpect(jsonPath("$[0].email").value(user.getEmail()))
                .andExpect(jsonPath("$[0].firstname").value(user.getFirstname()))
                .andExpect(jsonPath("$[0].lastname").value(user.getLastname()))
                .andExpect(jsonPath("$[0].docProfileId").value(user.getDocProfileId()))
                .andExpect(jsonPath("$[0].enabled").value(user.isEnabled()))
                .andExpect(jsonPath("$[0].status").value(user.isStatus()));
    }

    //////////////////////////////////////////////////////////////////////
    // POST  /api/keycloak/create : create
    //////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void testCreate_duplicateEmail() throws Exception {
        userRepository.saveAndFlush(user);

        mockMvc.perform(post("/api/keycloak/users/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email déjà existant"));
    }

    @Test
    @Transactional
    void testCreate_errorFromKeycloak() throws Exception {
        when(keycloak.realm(keycloakConfig.getRealm()).users().create(any())).thenReturn(mock(Response.class));
        when(keycloak.realm(keycloakConfig.getRealm()).users().create(any()).getStatus()).thenReturn(400);

        mockMvc.perform(post("/api/keycloak/users/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Erreur lors de la création de l'utilisateur"));
    }

    @Test
    @Transactional
    void testCreate_successWithoutRole() throws Exception {
        final String fakeIdFromKeycloak = "fakeIdFromKeycloak";
        String location = "http://localhost:8080/auth/admin/realms/insy2s/users/" + fakeIdFromKeycloak;
        Response response = Response.created(new URI(location)).build();
        when(keycloak.realm(keycloakConfig.getRealm()).users().create(any()))
                .thenReturn(response);
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(fakeIdFromKeycloak))
                .thenReturn(mock(UserResource.class));
        UserRepresentation userRepresentation = mock(UserRepresentation.class);
        userRepresentation.setId(fakeIdFromKeycloak);
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(fakeIdFromKeycloak).toRepresentation())
                .thenReturn(userRepresentation);
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(fakeIdFromKeycloak).toRepresentation().getId())
                .thenReturn(fakeIdFromKeycloak);

        mockMvc.perform(post("/api/keycloak/users/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(fakeIdFromKeycloak));
    }

    @Test
    @Transactional
    void testCreate_successWithRoleAdmin() throws Exception {
        final String fakeIdFromKeycloak = "fakeIdFromKeycloak";
        String location = "http://localhost:8080/auth/admin/realms/insy2s/users/" + fakeIdFromKeycloak;
        Response response = Response.created(new URI(location)).build();
        when(keycloak.realm(keycloakConfig.getRealm()).users().create(any()))
                .thenReturn(response);
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(fakeIdFromKeycloak))
                .thenReturn(mock(UserResource.class));
        UserRepresentation userRepresentation = mock(UserRepresentation.class);
        userRepresentation.setId(fakeIdFromKeycloak);
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(fakeIdFromKeycloak).toRepresentation())
                .thenReturn(userRepresentation);
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(fakeIdFromKeycloak).toRepresentation().getId())
                .thenReturn(fakeIdFromKeycloak);
        when(keycloak.realm(keycloakConfig.getRealm()).roles())
                .thenReturn(mock(RolesResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).roles().get("ADMIN"))
                .thenReturn(mock(RoleResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).roles().get("ADMIN").toRepresentation())
                .thenReturn(mock(RoleRepresentation.class));
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(fakeIdFromKeycloak).roles())
                .thenReturn(mock(RoleMappingResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(fakeIdFromKeycloak).roles().realmLevel())
                .thenReturn(mock(RoleScopeResource.class));

        user.setLstRole("ADMIN");

        mockMvc.perform(post("/api/keycloak/users/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(fakeIdFromKeycloak))
                .andExpect(jsonPath("$.roles[0].name").value("ADMIN"));
    }

    @Test
    @Transactional
    void testCreate_roleDoesntExist() throws Exception {
        final String fakeIdFromKeycloak = "fakeIdFromKeycloak";
        String location = "http://localhost:8080/auth/admin/realms/insy2s/users/" + fakeIdFromKeycloak;
        Response response = Response.created(new URI(location)).build();
        when(keycloak.realm(keycloakConfig.getRealm()).users().create(any()))
                .thenReturn(response);
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(fakeIdFromKeycloak))
                .thenReturn(mock(UserResource.class));
        UserRepresentation userRepresentation = mock(UserRepresentation.class);
        userRepresentation.setId(fakeIdFromKeycloak);
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(fakeIdFromKeycloak).toRepresentation())
                .thenReturn(userRepresentation);
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(fakeIdFromKeycloak).toRepresentation().getId())
                .thenReturn(fakeIdFromKeycloak);
        when(keycloak.realm(keycloakConfig.getRealm()).roles())
                .thenReturn(mock(RolesResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).roles().get("ADMIN"))
                .thenReturn(mock(RoleResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).roles().get("ADMIN").toRepresentation())
                .thenReturn(mock(RoleRepresentation.class));
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(fakeIdFromKeycloak).roles())
                .thenReturn(mock(RoleMappingResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(fakeIdFromKeycloak).roles().realmLevel())
                .thenReturn(mock(RoleScopeResource.class));

        user.setLstRole("WRONG_ROLE");

        mockMvc.perform(post("/api/keycloak/users/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roles").isEmpty());
    }

    //////////////////////////////////////////////////////////////////////
    // POST  /api/keycloak/users/toggleUserEnabled/{id} : toggleUserEnabled
    //////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void toggleUserEnabled_idDoesntExist() throws Exception {
        mockMvc.perform(post("/api/keycloak/users/toggleUserEnabled/{id}", "wrongId"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void toggleUserEnabled_success() throws Exception {
        userRepository.saveAndFlush(user);

        when(keycloak.realm(keycloakConfig.getRealm()).users().get(user.getId()))
                .thenReturn(mock(UserResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(user.getId()).toRepresentation())
                .thenReturn(mock(UserRepresentation.class));
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(user.getId()).toRepresentation().isEnabled())
                .thenReturn(true);

        mockMvc.perform(post("/api/keycloak/users/toggleUserEnabled/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(jsonPath("$").value("User enabled status toggled successfully."));
    }

    //////////////////////////////////////////////////////////////////////
    // DELETE  /api/keycloak/users/{id} : deleteById
    //////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void deleteById_idDoesntExist() throws Exception {
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(user.getId()))
                .thenReturn(mock(UserResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(user.getId()).toRepresentation())
                .thenReturn(null);

        mockMvc.perform(delete("/api/keycloak/users/{id}", user.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Utilisateur non trouvé"));
    }

    @Test
    @Transactional
    void deleteById_errorFromKeycloak() throws Exception {
        userRepository.saveAndFlush(user);
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(user.getId()))
                .thenReturn(mock(UserResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(user.getId()).toRepresentation())
                .thenReturn(mock(UserRepresentation.class));
        when(keycloak.realm(keycloakConfig.getRealm()).users().delete(user.getId()))
                .thenReturn(mock(Response.class));
        when(keycloak.realm(keycloakConfig.getRealm()).users().delete(user.getId()).getStatus())
                .thenReturn(400);

        mockMvc.perform(delete("/api/keycloak/users/{id}", user.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Erreur lors de la suppression de l'utilisateur"));
    }

    @Test
    @Transactional
    void deleteById_success() throws Exception {
        userRepository.saveAndFlush(user);
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(user.getId()))
                .thenReturn(mock(UserResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(user.getId()).toRepresentation())
                .thenReturn(mock(UserRepresentation.class));
        when(keycloak.realm(keycloakConfig.getRealm()).users().delete(user.getId()))
                .thenReturn(mock(Response.class));
        when(keycloak.realm(keycloakConfig.getRealm()).users().delete(user.getId()).getStatus())
                .thenReturn(204);

        mockMvc.perform(delete("/api/keycloak/users/{id}", user.getId()))
                .andExpect(status().isNoContent());
    }

    //////////////////////////////////////////////////////////////////////
    // PUT  /api/keycloak/users/update : update
    //////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void update_userDoesntExist() throws Exception {
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(user.getId()))
                .thenReturn(mock(UserResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(user.getId()).toRepresentation())
                .thenReturn(null);

        mockMvc.perform(post("/api/keycloak/users/update")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Utilisateur non trouvé"));
    }

    @Test
    @Transactional
    void update_success() throws Exception {
        userRepository.saveAndFlush(user);
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(user.getId()))
                .thenReturn(mock(UserResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(user.getId()).toRepresentation())
                .thenReturn(mock(UserRepresentation.class));
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(user.getId()).roles())
                .thenReturn(mock(RoleMappingResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(user.getId()).roles().realmLevel())
                .thenReturn(mock(RoleScopeResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).roles())
                .thenReturn(mock(RolesResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).roles().get("ADMIN"))
                .thenReturn(mock(RoleResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).roles().get("ADMIN").toRepresentation())
                .thenReturn(mock(RoleRepresentation.class));

        mockMvc.perform(post("/api/keycloak/users/update")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(user.getId()));
    }

}
