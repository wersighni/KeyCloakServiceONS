package com.insy2s.KeyCloakAuth.dto;

import lombok.Data;
import org.keycloak.representations.idm.CredentialRepresentation;

@Data
public class ChangePasswordRequest {
    private String userId;
    private String currentPassword;
    private String newPassword;
    private String username;
    private String Email;





}