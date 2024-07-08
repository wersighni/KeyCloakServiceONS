package com.resturant.mskeycloak.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resturant.mskeycloak.config.KeycloakConfig;
import com.resturant.mskeycloak.dto.ChangePasswordRequest;
import com.resturant.mskeycloak.dto.LoginRequest;
import com.resturant.mskeycloak.error.exception.BadRequestException;
import com.resturant.mskeycloak.error.exception.NotAuthorizedException;
import com.resturant.mskeycloak.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link LoginController}.
 */
@AutoConfigureMockMvc
@SpringBootTest
@WithMockUser
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private Keycloak keycloak;
    @MockBean
    private KeycloakConfig keycloakConfig;

    @BeforeEach
    void setUp() {
        when(keycloak.realm(keycloakConfig.getRealm())).thenReturn(Mockito.mock(RealmResource.class));
        when(keycloak.realm(keycloakConfig.getRealm()).users()).thenReturn(Mockito.mock(UsersResource.class));
    }

    //////////////////////////////////////////////////////////////////////
    // POST /api/keycloak/auth/login : login
    //////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void testLogin_success() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("insy2s");
        loginRequest.setPassword("insy2s");

        when(keycloakConfig.instantiateKeycloakUser(loginRequest.getUsername(), loginRequest.getPassword()))
                .thenReturn(Mockito.mock(Keycloak.class));
        when(keycloakConfig.instantiateKeycloakUser(loginRequest.getUsername(), loginRequest.getPassword())
                .tokenManager())
                .thenReturn(Mockito.mock(TokenManager.class));
        when(keycloakConfig.instantiateKeycloakUser(loginRequest.getUsername(), loginRequest.getPassword())
                .tokenManager().grantToken())
                .thenReturn(Mockito.mock(AccessTokenResponse.class));
        when(keycloakConfig.instantiateKeycloakUser(loginRequest.getUsername(), loginRequest.getPassword())
                .tokenManager().grantToken().getToken())
                .thenReturn("myAccessToken");
        when(keycloakConfig.instantiateKeycloakUser(loginRequest.getUsername(), loginRequest.getPassword())
                .tokenManager().grantToken().getRefreshToken())
                .thenReturn("myAccessToken");

        mockMvc.perform(post("/api/keycloak/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("myAccessToken"))
                .andExpect(jsonPath("$.refresh_token").value("myAccessToken"));
    }

    @Test
    @Transactional
    void testLogin_incorrectPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("insy2s");
        loginRequest.setPassword("wrongPassword");

        when(keycloakConfig.instantiateKeycloakUser(loginRequest.getUsername(), loginRequest.getPassword()))
                .thenReturn(Mockito.mock(Keycloak.class));

        when(keycloakConfig.instantiateKeycloakUser(loginRequest.getUsername(), loginRequest.getPassword())
                .tokenManager())
                .thenThrow(NotAuthorizedException.class);

        mockMvc.perform(post("/api/keycloak/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void testLogin_incorrectUsername() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("wrongUsername");
        loginRequest.setPassword("wrongPassword");
        mockMvc.perform(post("/api/keycloak/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    //////////////////////////////////////////////////////////////////////
    // POST /api/keycloak/auth/logout : logout
    //////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    @WithMockUser(username = "788d8106-852b-4c67-8364-680eadc628d0")
    void testLogout_success() throws Exception {
        String userId = "788d8106-852b-4c67-8364-680eadc628d0";


        when(keycloak.realm(keycloakConfig.getRealm()).users().get(userId)).thenReturn(
                Mockito.mock(UserResource.class));

        mockMvc.perform(post("/api/keycloak/auth/logout/" + userId))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @WithMockUser(username = "wrongUserId")
    void testLogout_incorrectUserId() throws Exception {
        String userId = "wrongUserId";

        when(keycloak.realm(keycloakConfig.getRealm()).users().get(userId)).thenThrow(BadRequestException.class);

        mockMvc.perform(post("/api/keycloak/auth/logout/" + userId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @WithMockUser(username = "wrongUserId")
    void testLogout_userIdNotEqualsCurrentUserId() throws Exception {
        String userId = "788d8106-852b-4c67-8364-680eadc628d0";

        when(keycloak.realm(keycloakConfig.getRealm()).users().get(userId)).thenReturn(
                Mockito.mock(UserResource.class));

        mockMvc.perform(post("/api/keycloak/auth/logout/" + userId))
                .andExpect(status().isBadRequest());
    }

    //////////////////////////////////////////////////////////////////////
    // POST /api/keycloak/auth/findAccount/verificationCode?email&code : checkVerificationCode
    //////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void testCheckVerificationCode_emailNotFound() throws Exception {
        final String email = "wrongemail@local.host";
        final String code = "goodCode";

        when(keycloak.realm(keycloakConfig.getRealm()).users().searchByEmail(email, true)).thenReturn(
                new ArrayList<>());

        mockMvc.perform(post("/api/keycloak/auth/findAccount/verificationCode")
                        .param("email", email)
                        .param("code", code))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Aucun utilisateur avec l'adresse e-mail"));
    }

    @Test
    @Transactional
    void testCheckVerificationCode_noVerificationCodeEmpty() throws Exception {
        final String email = "goodemail@local.host";
        final String code = "goodCode";

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail(email);
        Map<String, List<String>> attributes = new HashMap<>();
        userRepresentation.setAttributes(attributes);
        when(keycloak.realm(keycloakConfig.getRealm()).users().searchByEmail(email, true))
                .thenReturn(List.of(userRepresentation));

        mockMvc.perform(post("/api/keycloak/auth/findAccount/verificationCode")
                        .param("email", email)
                        .param("code", code))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("L'utilisateur a un attribut VerificationCode, mais sa valeur est vide."));
    }

    @Test
    @Transactional
    void testCheckVerificationCode_verificationCodeEmpty() throws Exception {
        final String email = "goodemail@local.host";
        final String code = "goodCode";

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail(email);
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("VerificationCode", new ArrayList<>());
        attributes.put("VerificationCodeDate", new ArrayList<>());
        userRepresentation.setAttributes(attributes);
        when(keycloak.realm(keycloakConfig.getRealm()).users().searchByEmail(email, true)).thenReturn(
                List.of(userRepresentation));

        mockMvc.perform(post("/api/keycloak/auth/findAccount/verificationCode")
                        .param("email", email)
                        .param("code", code))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("L'utilisateur n'a pas encore d'attribut VerificationCode."));
    }

    @Test
    @Transactional
    void testCheckVerificationCode_verificationCodeNotEquals() throws Exception {
        final String email = "goodemail@local.host";
        final String code = "goodCode";

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail(email);
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("VerificationCode", List.of("badCode"));
        attributes.put("VerificationCodeDate", List.of("Wed Oct 16 00:00:00 2100"));
        userRepresentation.setAttributes(attributes);
        when(keycloak.realm(keycloakConfig.getRealm()).users().searchByEmail(email, true)).thenReturn(
                List.of(userRepresentation));

        mockMvc.perform(post("/api/keycloak/auth/findAccount/verificationCode")
                        .param("email", email)
                        .param("code", code))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Le code de vérification invalide."));
    }

    @Test
    @Transactional
    void testCheckVerificationCode_verificationCodeExpired() throws Exception {
        final String email = "goodemail@local.host";
        final String code = "goodCode";

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail(email);
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("VerificationCode", List.of("goodCode"));
        attributes.put("VerificationCodeDate", List.of("Wed Oct 16 00:00:00 2013"));
        userRepresentation.setAttributes(attributes);
        when(keycloak.realm(keycloakConfig.getRealm()).users().searchByEmail(email, true))
                .thenReturn(List.of(userRepresentation));

        mockMvc.perform(post("/api/keycloak/auth/findAccount/verificationCode")
                        .param("email", email)
                        .param("code", code))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Le code de vérification a expiré."));
    }

    @Test
    @Transactional
    void testCheckVerificationCode_success() throws Exception {
        final String email = "goodemail@local.host";
        final String code = "goodCode";

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail(email);
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("VerificationCode", List.of("goodCode"));
        attributes.put("VerificationCodeDate", List.of("Wed Oct 16 00:00:00 2100"));
        userRepresentation.setAttributes(attributes);
        when(keycloak.realm(keycloakConfig.getRealm()).users().searchByEmail(email, true))
                .thenReturn(List.of(userRepresentation));

        mockMvc.perform(post("/api/keycloak/auth/findAccount/verificationCode")
                        .param("email", email)
                        .param("code", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Code valide"));
    }

    //////////////////////////////////////////////////////////////////////
    // POST /api/keycloak/auth/findAccount/restPassword?email&password : resetPassword
    //////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void testResetPassword_wrongEmail() throws Exception {
        final String email = "wrongemail@local.host";
        final String password = "goodPassword";

        when(keycloak.realm(keycloakConfig.getRealm()).users().searchByEmail(email, true)).thenReturn(
                new ArrayList<>());

        mockMvc.perform(post("/api/keycloak/auth/findAccount/restPassword")
                        .param("email", email)
                        .param("password", password))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Aucun utilisateur avec l'adresse e-mail : " + email));

    }

    @Test
    @Transactional
    void testResetPassword_success() throws Exception {
        final String email = "goodemail@local.host";
        final String password = "goodPassword";

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail(email);
        when(keycloak.realm(keycloakConfig.getRealm()).users().searchByEmail(email, true))
                .thenReturn(List.of(userRepresentation));
        when(keycloak.realm(keycloakConfig.getRealm()).users().get(userRepresentation.getId()))
                .thenReturn(Mockito.mock(UserResource.class));

        mockMvc.perform(post("/api/keycloak/auth/findAccount/restPassword")
                        .param("email", email)
                        .param("password", password))
                .andExpect(status().isOk());

    }

    //////////////////////////////////////////////////////////////////////
    // POST /api/keycloak/auth/changePassword : changePassword
    //////////////////////////////////////////////////////////////////////
    @Test
    @Transactional
    void changePassword_notSameUsername() throws Exception {
        final String username = "goodUsername";
        final ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .username(username)
                .currentPassword("goodPassword")
                .newPassword("newPassword")
                .build();

        try (MockedStatic<SecurityUtils> securityUtilsMockedStatic = Mockito.mockStatic(SecurityUtils.class)) {
            securityUtilsMockedStatic.when(SecurityUtils::getCurrentUserEmail).thenReturn("differentUsername");
            UserRepresentation userRepresentation = new UserRepresentation();
            userRepresentation.setEmail(username);
            when(keycloak.realm(keycloakConfig.getRealm()).users().searchByEmail(username, true))
                    .thenReturn(List.of(userRepresentation));
            when(keycloak.realm(keycloakConfig.getRealm()).users().get(userRepresentation.getId()))
                    .thenReturn(Mockito.mock(UserResource.class));

            mockMvc.perform(post("/api/keycloak/auth/changePassword")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message")
                            .value("Vous ne pouvez pas changer le mot de passe d'un autre utilisateur"));
        }
    }

    @Test
    @Transactional
    void changePassword_userNotFound() throws Exception {
        final String username = "wrongUsername";
        final ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .username(username)
                .currentPassword("goodPassword")
                .newPassword("newPassword")
                .build();

        try (MockedStatic<SecurityUtils> securityUtilsMockedStatic = Mockito.mockStatic(SecurityUtils.class)) {
            securityUtilsMockedStatic.when(SecurityUtils::getCurrentUserEmail).thenReturn(username);
            when(keycloak.realm(keycloakConfig.getRealm()).users().searchByEmail(username, true))
                    .thenReturn(new ArrayList<>());

            mockMvc.perform(post("/api/keycloak/auth/changePassword")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message")
                            .value("Aucun utilisateur avec le nom d'utilisateur : " + username));
        }
    }

    @Test
    @Transactional
    void changePassword_success() throws Exception {
        final String username = "insy2s";
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword("insy2s");
        final ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .username(username)
                .currentPassword(loginRequest.getPassword())
                .newPassword("newPassword")
                .build();

        try (MockedStatic<SecurityUtils> securityUtilsMockedStatic = Mockito.mockStatic(SecurityUtils.class)) {
            securityUtilsMockedStatic.when(SecurityUtils::getCurrentUserEmail).thenReturn(username);
            UserRepresentation userRepresentation = new UserRepresentation();
            userRepresentation.setEmail(username);
            when(keycloak.realm(keycloakConfig.getRealm()).users().search(username))
                    .thenReturn(List.of(userRepresentation));
            when(keycloak.realm(keycloakConfig.getRealm()).users().get(userRepresentation.getId()))
                    .thenReturn(Mockito.mock(UserResource.class));
            when(keycloakConfig.instantiateKeycloakUser(loginRequest.getUsername(), loginRequest.getPassword()))
                    .thenReturn(Mockito.mock(Keycloak.class));
            when(keycloakConfig.instantiateKeycloakUser(loginRequest.getUsername(), loginRequest.getPassword())
                    .tokenManager())
                    .thenReturn(Mockito.mock(TokenManager.class));
            when(keycloakConfig.instantiateKeycloakUser(loginRequest.getUsername(), loginRequest.getPassword())
                    .tokenManager().grantToken())
                    .thenReturn(Mockito.mock(AccessTokenResponse.class));
            when(keycloakConfig.instantiateKeycloakUser(loginRequest.getUsername(), loginRequest.getPassword())
                    .tokenManager().grantToken().getToken())
                    .thenReturn("myAccessToken");
            when(keycloakConfig.instantiateKeycloakUser(loginRequest.getUsername(), loginRequest.getPassword())
                    .tokenManager().grantToken().getRefreshToken())
                    .thenReturn("myAccessToken");

            mockMvc.perform(post("/api/keycloak/auth/changePassword")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isOk());
        }
    }

}
