package com.resturant.mskeycloak.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resturant.mskeycloak.model.Role;
import com.resturant.mskeycloak.model.User;
import com.resturant.mskeycloak.repository.IUserRepository;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link AccessController}.
 */
@AutoConfigureMockMvc
@SpringBootTest
@WithMockUser
class AccessControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IUserRepository userRepository;

    //////////////////////////////////////////////////////////////////////
    // POST /api/keycloak/access : create
    //////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void testCreate_noParent_noChildren() throws Exception {
        CreateAccess createAccess = new CreateAccess(
                "Test",
                "test",
                "Menu",
                "/test",
                null,
                new ArrayList<>()
        );

        mockMvc.perform(post("/api/keycloak/access/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAccess)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(createAccess.name()))
                .andExpect(jsonPath("$.code").value(createAccess.code()))
                .andExpect(jsonPath("$.type").value(createAccess.type()))
                .andExpect(jsonPath("$.path").value(createAccess.path()))
                .andExpect(jsonPath("$.subAccess", hasSize(0)));
    }

    @Test
    @Transactional
    void testCreate_withParent_withNewChildren() throws Exception {
        Access parent = new Access();
        parent.setId(1L);
        Access child = new Access();
        child.setCode("child");
        child.setName("child");
        CreateAccess createAccess = new CreateAccess(
                "Test",
                "test",
                "Menu",
                "/test",
                parent,
                List.of(child)
        );

        mockMvc.perform(post("/api/keycloak/access/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAccess)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(createAccess.name()))
                .andExpect(jsonPath("$.code").value(createAccess.code()))
                .andExpect(jsonPath("$.type").value(createAccess.type()))
                .andExpect(jsonPath("$.path").value(createAccess.path()))
                .andExpect(jsonPath("$.subAccess", hasSize(1)))
                .andExpect(jsonPath("$.subAccess[0].name").value(child.getName()))
                .andExpect(jsonPath("$.subAccess[0].code").value(child.getCode()))
                .andExpect(jsonPath("$.subAccess[0].type").value(child.getType()))
                .andExpect(jsonPath("$.subAccess[0].path").value(child.getPath()))
                .andExpect(jsonPath("$.subAccess[0].subAccess", hasSize(0)));
    }

    //////////////////////////////////////////////////////////////////////
    // DELETE /api/keycloak/access/:id : deleteById
    //////////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    void testDeleteById_idExist_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/keycloak/access/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    void testDeleteById_idDoesntExist_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/keycloak/access/10000"))
                .andExpect(status().isNotFound());
    }

    //////////////////////////////////////////////////////////////////////
    // GET /api/keycloak/access : getAllMenusWithChildren
    //////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void testGetAllMenusWithChildren() throws Exception {
        getAllMenusWithChildren("/api/keycloak/access/");
    }

    //////////////////////////////////////////////////////////////////////
    // GET /api/keycloak/access/all : getAllWithoutChildren
    //////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void testGetAllWithoutChildren() throws Exception {
        mockMvc.perform(get("/api/keycloak/access/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(54)));
    }

    //////////////////////////////////////////////////////////////////////
    // GET /api/keycloak/access/byRole/:id : getAllMenusWithChildren
    //////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void testGetAllMenusByRole_adminShouldHaveAll() throws Exception {
        getAllMenusWithChildren("/api/keycloak/access/byRole/1");
    }

    @Test
    @Transactional
    void testGetAllByUser_userIsAdminAndProfessionalTutor_shouldHaveAll() throws Exception {
        Role role = Role.builder()
                .id(1L)
                .build();
        User user = User.builder()
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
                .roles(List.of(role))
                .build();
        userRepository.saveAndFlush(user);
        getAllMenusWithChildren("/api/keycloak/access/byUser?userId=id");
    }


    //////////////////////////////////////////////////////////////////////
    // GET /api/keycloak/access/:id : getById
    /////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void testGetById_idExist_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/keycloak/access/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Administration"))
                .andExpect(jsonPath("$.code").value("Admin"))
                .andExpect(jsonPath("$.type").value("Menu"))
                .andExpect(jsonPath("$.subAccess", hasSize(4)));
    }

    @Test
    @Transactional
    void testGetById_idDoesntExist_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/keycloak/access/10000000"))
                .andExpect(status().isNotFound());
    }

    //////////////////////////////////////////////////////////////////////
    // GET /api/keycloak/access/byParentId/:id : getAllByParentId
    /////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void testGetAllByParentId_idExist() throws Exception {
        mockMvc.perform(get("/api/keycloak/access/byParentId/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].subAccess", hasSize(3)))
                .andExpect(jsonPath("$[0].subAccess[0].id").value(8))
                .andExpect(jsonPath("$[0].subAccess[0].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[0].subAccess[1].id").value(9))
                .andExpect(jsonPath("$[0].subAccess[1].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[0].subAccess[2].id").value(10))
                .andExpect(jsonPath("$[0].subAccess[2].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[1].id").value(3))
                .andExpect(jsonPath("$[1].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[2].id").value(4))
                .andExpect(jsonPath("$[2].subAccess", hasSize(3)))
                .andExpect(jsonPath("$[2].subAccess[0].id").value(5))
                .andExpect(jsonPath("$[2].subAccess[0].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[2].subAccess[1].id").value(6))
                .andExpect(jsonPath("$[2].subAccess[1].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[2].subAccess[2].id").value(7))
                .andExpect(jsonPath("$[2].subAccess[2].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[3].id").value(11))
                .andExpect(jsonPath("$[3].subAccess", hasSize(3)))
                .andExpect(jsonPath("$[3].subAccess[0].id").value(12))
                .andExpect(jsonPath("$[3].subAccess[0].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[3].subAccess[1].id").value(13))
                .andExpect(jsonPath("$[3].subAccess[1].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[3].subAccess[2].id").value(14))
                .andExpect(jsonPath("$[3].subAccess[2].subAccess", hasSize(0)));
    }

    @Test
    @Transactional
    void testGetAllByParentId_idDoesntExist_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/keycloak/access/byParentId/10000000"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    //////////////////////////////////////////////////////////////////////
    // GET /api/keycloak/access/byType/:type : getAllByType
    /////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void testGetAllByType_typeExist() throws Exception {
        getAllMenusWithChildren("/api/keycloak/access/byType/Menu");
    }

    @Test
    @Transactional
    void testGetAllByType_typeDoesntExist() throws Exception {
        mockMvc.perform(get("/api/keycloak/access/byType/NotExistingType"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    //////////////////////////////////////////////////////////////////////
    // GET /api/keycloak/access/byRoleAndType?roleId&type : getAllByRoleAndType
    /////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void getAllByRoleAndType() throws Exception {
        getAllMenusWithChildren("/api/keycloak/access/byRoleAndType?roleId=1&type=Menu");
    }

    @Test
    @Transactional
    void getAllByRoleAndType_roleIdDoesntExist() throws Exception {
        mockMvc.perform(get("/api/keycloak/access/byRoleAndType?roleId=10000000&type=Menu"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("The role is not found"));
    }

    @Test
    @Transactional
    void getAllByRoleAndType_typeDoesntExist() throws Exception {
        mockMvc.perform(get("/api/keycloak/access/byRoleAndType?roleId=1&type=NotExistingType"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    //////////////////////////////////////////////////////////////////////
    // GET /api/keycloak/access/addAccessRole?roleId&accessId : addAccessToRole
    /////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void testAddAccessToRole_bothIdsExist() throws Exception {
        mockMvc.perform(get("/api/keycloak/access/addAccessRole?roleId=1&accessId=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Administration"))
                .andExpect(jsonPath("$.code").value("Admin"))
                .andExpect(jsonPath("$.type").value("Menu"));
    }

    @Test
    @Transactional
    void testAddAccessToRole_roleIdDoesntExist_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/keycloak/access/addAccessRole?roleId=10000000&accessId=1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void testAddAccessToRole_accessIdDoesntExist_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/keycloak/access/addAccessRole?roleId=1&accessId=10000000"))
                .andExpect(status().isNotFound());
    }

    //////////////////////////////////////////////////////////////////////
    // DELETE /api/keycloak/access/removeAccessRole?roleId&accessId : removeAccessToRole
    /////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void removeAccessToRole_bothIdsExist() throws Exception {
        mockMvc.perform(delete("/api/keycloak/access/removeAccessRole?roleId=1&accessId=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Administration"))
                .andExpect(jsonPath("$.code").value("Admin"))
                .andExpect(jsonPath("$.type").value("Menu"));
    }

    @Test
    @Transactional
    void removeAccessToRole_roleIdDoesntExist_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/keycloak/access/removeAccessRole?roleId=10000000&accessId=1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void removeAccessToRole_accessIdDoesntExist_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/keycloak/access/removeAccessRole?roleId=1&accessId=10000000"))
                .andExpect(status().isNotFound());
    }

    //////////////////////////////////////////////////////////////////////
    // UTILS METHODS
    //////////////////////////////////////////////////////////////////////
    private void getAllMenusWithChildren(String path) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(9)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].subAccess", hasSize(4)))
                .andExpect(jsonPath("$[0].subAccess[0].id").value(2))
                .andExpect(jsonPath("$[0].subAccess[0].subAccess", hasSize(3)))
                .andExpect(jsonPath("$[0].subAccess[0].subAccess[0].id").value(8))
                .andExpect(jsonPath("$[0].subAccess[0].subAccess[0].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[0].subAccess[0].subAccess[1].id").value(9))
                .andExpect(jsonPath("$[0].subAccess[0].subAccess[1].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[0].subAccess[0].subAccess[2].id").value(10))
                .andExpect(jsonPath("$[0].subAccess[0].subAccess[2].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[0].subAccess[1].id").value(3))
                .andExpect(jsonPath("$[0].subAccess[1].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[0].subAccess[2].id").value(4))
                .andExpect(jsonPath("$[0].subAccess[2].subAccess", hasSize(3)))
                .andExpect(jsonPath("$[0].subAccess[2].subAccess[0].id").value(5))
                .andExpect(jsonPath("$[0].subAccess[2].subAccess[0].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[0].subAccess[2].subAccess[1].id").value(6))
                .andExpect(jsonPath("$[0].subAccess[2].subAccess[1].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[0].subAccess[2].subAccess[2].id").value(7))
                .andExpect(jsonPath("$[0].subAccess[2].subAccess[2].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[0].subAccess[3].id").value(11))
                .andExpect(jsonPath("$[0].subAccess[3].subAccess", hasSize(3)))
                .andExpect(jsonPath("$[0].subAccess[3].subAccess[0].id").value(12))
                .andExpect(jsonPath("$[0].subAccess[3].subAccess[0].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[0].subAccess[3].subAccess[1].id").value(13))
                .andExpect(jsonPath("$[0].subAccess[3].subAccess[1].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[0].subAccess[3].subAccess[2].id").value(14))
                .andExpect(jsonPath("$[0].subAccess[3].subAccess[2].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[1].id").value(15))
                .andExpect(jsonPath("$[1].subAccess", hasSize(7)))
                .andExpect(jsonPath("$[1].subAccess[0].id").value(16))
                .andExpect(jsonPath("$[1].subAccess[0].subAccess", hasSize(3)))
                .andExpect(jsonPath("$[1].subAccess[0].subAccess[0].id").value(18))
                .andExpect(jsonPath("$[1].subAccess[0].subAccess[0].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[1].subAccess[0].subAccess[1].id").value(19))
                .andExpect(jsonPath("$[1].subAccess[0].subAccess[1].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[1].subAccess[0].subAccess[2].id").value(20))
                .andExpect(jsonPath("$[1].subAccess[0].subAccess[2].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[1].subAccess[1].id").value(17))
                .andExpect(jsonPath("$[1].subAccess[1].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[1].subAccess[2].id").value(21))
                .andExpect(jsonPath("$[1].subAccess[2].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[1].subAccess[3].id").value(22))
                .andExpect(jsonPath("$[1].subAccess[3].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[1].subAccess[4].id").value(23))
                .andExpect(jsonPath("$[1].subAccess[4].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[1].subAccess[5].id").value(24))
                .andExpect(jsonPath("$[1].subAccess[5].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[1].subAccess[6].id").value(25))
                .andExpect(jsonPath("$[1].subAccess[6].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[2].id").value(26))
                .andExpect(jsonPath("$[2].subAccess", hasSize(3)))
                .andExpect(jsonPath("$[2].subAccess[0].id").value(27))
                .andExpect(jsonPath("$[2].subAccess[0].subAccess", hasSize(3)))
                .andExpect(jsonPath("$[2].subAccess[0].subAccess[0].id").value(30))
                .andExpect(jsonPath("$[2].subAccess[0].subAccess[0].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[2].subAccess[0].subAccess[1].id").value(31))
                .andExpect(jsonPath("$[2].subAccess[0].subAccess[1].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[2].subAccess[0].subAccess[2].id").value(32))
                .andExpect(jsonPath("$[2].subAccess[0].subAccess[2].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[2].subAccess[1].id").value(28))
                .andExpect(jsonPath("$[2].subAccess[1].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[2].subAccess[2].id").value(29))
                .andExpect(jsonPath("$[2].subAccess[2].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[3].id").value(33))
                .andExpect(jsonPath("$[3].subAccess", hasSize(3)))
                .andExpect(jsonPath("$[3].subAccess[0].id").value(34))
                .andExpect(jsonPath("$[3].subAccess[0].subAccess", hasSize(1)))
                .andExpect(jsonPath("$[3].subAccess[0].subAccess[0].id").value(36))
                .andExpect(jsonPath("$[3].subAccess[0].subAccess[0].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[3].subAccess[1].id").value(35))
                .andExpect(jsonPath("$[3].subAccess[1].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[3].subAccess[2].id").value(37))
                .andExpect(jsonPath("$[3].subAccess[2].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[4].id").value(38))
                .andExpect(jsonPath("$[4].subAccess", hasSize(4)))
                .andExpect(jsonPath("$[4].subAccess[0].id").value(39))
                .andExpect(jsonPath("$[4].subAccess[0].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[4].subAccess[1].id").value(40))
                .andExpect(jsonPath("$[4].subAccess[1].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[4].subAccess[2].id").value(41))
                .andExpect(jsonPath("$[4].subAccess[2].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[4].subAccess[3].id").value(42))
                .andExpect(jsonPath("$[4].subAccess[3].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[5].id").value(43))
                .andExpect(jsonPath("$[5].subAccess", hasSize(2)))
                .andExpect(jsonPath("$[5].subAccess[0].id").value(44))
                .andExpect(jsonPath("$[5].subAccess[0].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[5].subAccess[1].id").value(45))
                .andExpect(jsonPath("$[5].subAccess[1].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[6].id").value(46))
                .andExpect(jsonPath("$[6].subAccess", hasSize(2)))
                .andExpect(jsonPath("$[6].subAccess[0].id").value(47))
                .andExpect(jsonPath("$[6].subAccess[0].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[7].id").value(49))
                .andExpect(jsonPath("$[7].subAccess", hasSize(3)))
                .andExpect(jsonPath("$[7].subAccess[0].id").value(50))
                .andExpect(jsonPath("$[7].subAccess[0].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[7].subAccess[1].id").value(51))
                .andExpect(jsonPath("$[7].subAccess[1].subAccess", hasSize(0)))
                .andExpect(jsonPath("$[7].subAccess[2].id").value(52))
                .andExpect(jsonPath("$[7].subAccess[2].subAccess", hasSize(0)));
    }


}