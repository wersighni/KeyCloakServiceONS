package com.insy2s.keycloakauth.service.impl;

import com.insy2s.keycloakauth.config.KeycloakConfig;
import com.insy2s.keycloakauth.dto.LoginRequest;
import com.insy2s.keycloakauth.dto.LoginResponse;
import com.insy2s.keycloakauth.error.exception.BadRequestException;
import com.insy2s.keycloakauth.error.exception.NotAuthorizedException;
import com.insy2s.keycloakauth.error.exception.NotFoundException;
import com.insy2s.keycloakauth.model.User;
import com.insy2s.keycloakauth.service.IAccessService;
import com.insy2s.keycloakauth.service.ILoginService;
import com.insy2s.keycloakauth.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Service Implementation for Authentication.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LoginService implements ILoginService {

    private final IUserService userService;
    private final IAccessService accessService;
    private final RealmResource realmResource;
    private final KeycloakConfig keycloakConfig;

    private static long getDifferenceInMinutes(String codeVerifyTime) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US);
            Date verificationCodeDate = dateFormat.parse(codeVerifyTime);
            Date currentDate = new Date();
            long differenceInMillis = currentDate.getTime() - verificationCodeDate.getTime();
            return differenceInMillis / (60 * 1000);
        } catch (ParseException e) {
            throw new BadRequestException("Erreur lors de la conversion de la date.");
        }
    }

    private static Map<String, List<String>> getAttributesFromUser(UserRepresentation user) {
        Map<String, List<String>> attributes = user.getAttributes();
        if (!attributes.containsKey("VerificationCode") || !attributes.containsKey("VerificationCodeDate")) {
            throw new NotAuthorizedException(
                    "L'utilisateur a un attribut VerificationCode, mais sa valeur est vide."
            );
        }
        return attributes;
    }

    @Override
    public void testVerificationCode(String email, String code) {
        try {
            List<UserRepresentation> users = realmResource.users().searchByEmail(email, true);
            if (users.isEmpty()) {
                throw new NotAuthorizedException("Aucun utilisateur avec l'adresse e-mail : " + email);
            }
            Map<String, List<String>> attributes = getAttributesFromUser(users.get(0));
            List<String> codeVerify = attributes.get("VerificationCode");
            List<String> codeVerifyTime = attributes.get("VerificationCodeDate");
            if (CollectionUtils.isEmpty(codeVerify) || CollectionUtils.isEmpty(codeVerifyTime)) {
                throw new NotAuthorizedException("L'utilisateur n'a pas encore d'attribut VerificationCode.");
            }
            final String verificationCode = codeVerify.get(0);
            final String verificationCodeTime = codeVerifyTime.get(0);
            final long differenceInMinutes = getDifferenceInMinutes(verificationCodeTime);
            if (!verificationCode.equals(code)) {
                throw new NotAuthorizedException("Le code de vérification invalide.");
            }
            if (differenceInMinutes > 30) {
                throw new NotAuthorizedException("Le code de vérification a expiré.");
            }
        } catch (Exception e) {
            log.error("Erreur lors de la vérification du code de vérification : " + e.getMessage());
            throw new BadRequestException("Erreur : " + e.getMessage());
        }
    }

    @Override
    public void resetPassword(String email, String password) {
        try {
            List<UserRepresentation> users = realmResource.users().searchByEmail(email, true);
            if (users.isEmpty()) {
                throw new NotFoundException("Aucun utilisateur avec l'adresse e-mail : " + email);
            }
            UserRepresentation user = users.get(0);
            CredentialRepresentation newCredential = toCredentialRepresentation(password);
            user.setCredentials(Collections.singletonList(newCredential));
            realmResource.users().get(user.getId()).update(user);
        } catch (javax.ws.rs.NotFoundException notFoundEx) {
            throw new NotFoundException("Aucun utilisateur avec l'adresse e-mail : " + email);
        } catch (javax.ws.rs.BadRequestException badRequestEx) {
            throw new BadRequestException("Requête incorrecte lors de la mise à jour de l'utilisateur : " +
                    badRequestEx.getMessage());
        } catch (Exception e) {
            throw new BadRequestException("Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage());
        }
    }

    private CredentialRepresentation toCredentialRepresentation(String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        return credential;
    }

    @Override
    public void changePassword(String username, String currentPassword, String newPassword) {
        try {
            List<UserRepresentation> users = realmResource.users().search(username);
            if (users.isEmpty()) {
                throw new BadRequestException("Utilisateur non trouvé.");
            }
            LoginRequest loginRequest = new LoginRequest(username, currentPassword);
            login(loginRequest);
            UserResource userResource = realmResource.users().get(users.get(0).getId());
            CredentialRepresentation credentials = new CredentialRepresentation();
            credentials.setType(CredentialRepresentation.PASSWORD);
            credentials.setValue(newPassword);
            userResource.resetPassword(credentials);
        } catch (Exception e) {
            log.error("Erreur lors de la modification du mot de passe : " + e.getMessage());
            throw new BadRequestException("\"Erreur lors de la modification du mot de passe");
        }
    }

    @Override
    public LoginResponse login(LoginRequest loginrequest) {
        User user = null;

        if (!loginrequest.getUsername().equals("insy2s")) {
            user = userService.getUser(loginrequest.getUsername());
            if (user == null) {
                throw new BadRequestException("Le nom d'utilisateur ou le mot de passe est incorrect");
            }
        }

        try (Keycloak instanceKeycloakUser = keycloakConfig.instantiateKeycloakUser(
                loginrequest.getUsername(),
                loginrequest.getPassword()
        )) {
            LoginResponse loginResponse = new LoginResponse();
            AccessTokenResponse accessTokenResponse = instanceKeycloakUser.tokenManager().grantToken();
            loginResponse.setAccess_token(accessTokenResponse.getToken());
            loginResponse.setRefresh_token(accessTokenResponse.getRefreshToken());
            if (user != null) {
                loginResponse.setAccess(accessService.findByUser(user.getId()));
                loginResponse.setMenus(accessService.refactorByUserAndType(user.getId(), "Menu"));
                loginResponse.setPages(accessService.refactorByUserAndType(user.getId(), "Page"));
                loginResponse.setActions(accessService.refactorByUserAndType(user.getId(), "Action"));

            } else if (loginrequest.getUsername().equals("insy2s")) {
                loginResponse.setAccess(accessService.getAllAccessDto());
                loginResponse.setActions(accessService.refactorAccess(accessService.findByType("Action")));
            }
            return loginResponse;

        } catch (Exception e) {
            throw new BadRequestException("Le nom utilisateur ou le mot de passe est incorrect");
        }
    }

    @Override
    public void logout(String userId) {
        try {
            realmResource.users().get(userId).logout();
        } catch (Exception e) {
            log.error("Erreur lors de la déconnexion : " + e.getMessage());
            throw new BadRequestException("Échec de la déconnexion");
        }
    }

}
