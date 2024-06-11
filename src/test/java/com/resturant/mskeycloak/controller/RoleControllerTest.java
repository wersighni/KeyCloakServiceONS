package com.resturant.mskeycloak.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resturant.mskeycloak.config.KeycloakConfig;
import com.resturant.mskeycloak.model.Role;
import com.resturant.mskeycloak.repository.IRoleRepository;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link RoleController}.
 */
@AutoConfigureMockMvc
@SpringBootTest
@WithMockUser
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IRoleRepository roleRepository;
    @MockBean
    private Keycloak keycloak;
    @MockBean
    private KeycloakConfig keycloakConfig;

    @BeforeEach
    void setUp() {
        when(keycloak.realm(keycloakConfig.getRealm())).thenReturn(Mockito.mock(RealmResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).roles()).thenReturn(Mockito.mock(RolesResource.class));
    }

    //////////////////////////////////////////////////////////////////////
    // POST /api/keycloak/roles : create
    //////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void testCreate_roleExistInKeycloak_shouldReturn400() throws Exception {
        String roleName = "testRole";
        when(keycloak.realm(keycloakConfig.getRealm()).roles().get(roleName))
                .thenReturn(Mockito.mock(RoleResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).roles().get(roleName).toRepresentation())
                .thenReturn(new RoleRepresentation());

        Role role = new Role();
        role.setName(roleName);
        role.setDescription("testDescription");

        mockMvc.perform(post("/api/keycloak/roles/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void testCreate_roleExistInDB_shouldReturn400() throws Exception {
        String roleName = "ADMIN";
        when(keycloak.realm(keycloakConfig.getRealm()).roles().get(roleName))
                .thenThrow(new jakarta.ws.rs.NotFoundException());


        Role role = new Role();
        role.setName(roleName);
        role.setDescription("testDescription");

        mockMvc.perform(post("/api/keycloak/roles/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void testCreat_success() throws Exception {
        String roleName = "testRole";
        when(keycloak.realm(keycloakConfig.getRealm()).roles().get(roleName))
                .thenThrow(new jakarta.ws.rs.NotFoundException());
        Role role = new Role();
        role.setName(roleName);
        role.setDescription("testDescription");

        mockMvc.perform(post("/api/keycloak/roles/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(roleName))
                .andExpect(jsonPath("$.description").value("testDescription"));
    }

    //////////////////////////////////////////////////////////////////////
    // GET /api/keycloak/roles : getAll
    //////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void getAll() throws Exception {
        mockMvc.perform(get("/api/keycloak/roles/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[0].name").value("ADMIN"))
                .andExpect(jsonPath("$.[0].accessList", hasSize(54)))
                .andExpect(jsonPath("$.[1].id").value(2))
                .andExpect(jsonPath("$.[1].name").value("Tuteur Professionnel"))
                .andExpect(jsonPath("$.[1].accessList", hasSize(54)))
                .andExpect(jsonPath("$.[2].id").value(3))
                .andExpect(jsonPath("$.[2].name").value("Apprenant"))
                .andExpect(jsonPath("$.[2].accessList", hasSize(54)))
                .andExpect(jsonPath("$.[3].id").value(4))
                .andExpect(jsonPath("$.[3].name").value("Tuteur Academique"))
                .andExpect(jsonPath("$.[3].accessList", hasSize(54)))
                .andExpect(jsonPath("$.[4].id").value(5))
                .andExpect(jsonPath("$.[4].name").value("Apprenant d'Aide"))
                .andExpect(jsonPath("$.[4].accessList", hasSize(54)))
                .andExpect(jsonPath("$.[5].id").value(6))
                .andExpect(jsonPath("$.[5].name").value("Apprenant de Verif"))
                .andExpect(jsonPath("$.[5].accessList", hasSize(54)));
    }

    //////////////////////////////////////////////////////////////////////
    // GET /api/keycloak/roles/statusFalse : getAllWithStatusFalse
    //////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void testGetAllWithStatusFalse_noRoleStatusInDb() throws Exception {
        mockMvc.perform(get("/api/keycloak/roles/statusFalse"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Transactional
    void testGetAllWithStatusFalse_oneRoleStatusInDb() throws Exception {
        Role role = new Role();
        role.setName("testRole");
        role.setDescription("testDescription");
        role.setStatus(false);
        roleRepository.saveAndFlush(role);
        mockMvc.perform(get("/api/keycloak/roles/statusFalse"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    //////////////////////////////////////////////////////////////////////
    // GET /api/keycloak/roles/:id : getById
    //////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void testGetById_idExist_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/keycloak/roles/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("ADMIN"))
                .andExpect(jsonPath("$.accessList", hasSize(54)));
    }

    @Test
    @Transactional
    void testGetById_idNotExist_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/keycloak/roles/1000000"))
                .andExpect(status().isNotFound());
    }

    //////////////////////////////////////////////////////////////////////
    // GET /api/keycloak/roles/byName/:name : getByName
    //////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void testGetByName_nameExist_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/keycloak/roles/byName/ADMIN"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("ADMIN"))
                .andExpect(jsonPath("$.accessList", hasSize(54)));
    }

    @Test
    @Transactional
    void testGetByName_nameNotExist_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/keycloak/roles/byName/testRole"))
                .andExpect(status().isNotFound());
    }

    //////////////////////////////////////////////////////////////////////
    // POST /api/keycloak/roles/:id/update : update
    //////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void testUpdate_idDoesntExist_shouldReturn404() throws Exception {
        String roleName = "ADMIN";
        when(keycloak.realm(keycloakConfig.getRealm()).roles().get(roleName))
                .thenReturn(Mockito.mock(RoleResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).roles().get(roleName).toRepresentation())
                .thenReturn(new RoleRepresentation());

        Role role = new Role();
        role.setName(roleName);
        role.setDescription("testDescription");

        mockMvc.perform(post("/api/keycloak/roles/1000000/update")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void testUpdate_roleNameDoesntExistInKeycloak_shouldReturn404() throws Exception {
        String roleName = "testRole";
        when(keycloak.realm(keycloakConfig.getRealm()).roles().get(roleName))
                .thenReturn(Mockito.mock(RoleResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).roles().get(roleName).toRepresentation())
                .thenReturn(null);

        Role role = new Role();
        role.setName(roleName);
        role.setDescription("testDescription");

        mockMvc.perform(post("/api/keycloak/roles/1/update")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void testUpdate_success() throws Exception {
        String roleName = "ADMIN";
        when(keycloak.realm(keycloakConfig.getRealm()).roles().get(roleName))
                .thenReturn(Mockito.mock(RoleResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).roles().get(roleName).toRepresentation())
                .thenReturn(new RoleRepresentation());

        Role role = new Role();
        role.setName(roleName);
        role.setDescription("testDescription");

        mockMvc.perform(post("/api/keycloak/roles/1/update")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(roleName))
                .andExpect(jsonPath("$.description").value("testDescription"));
    }

    //////////////////////////////////////////////////////////////////////
    // PUT /api/keycloak/roles/:id : delete
    //////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void testDelete_idDoesntExist_shouldReturn404() throws Exception {
        String roleName = "ADMIN";
        when(keycloak.realm(keycloakConfig.getRealm()).roles().get(roleName))
                .thenReturn(Mockito.mock(RoleResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).roles().get(roleName).toRepresentation())
                .thenReturn(null);
        mockMvc.perform(put("/api/keycloak/roles/1000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void testDelete_success() throws Exception {
        String roleName = "ADMIN";
        when(keycloak.realm(keycloakConfig.getRealm()).roles().get(roleName))
                .thenReturn(Mockito.mock(RoleResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).roles().get(roleName).toRepresentation())
                .thenReturn(new RoleRepresentation());
        mockMvc.perform(put("/api/keycloak/roles/1"))
                .andExpect(status().isNoContent());
    }

}
