package com.insy2s.keycloakauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    private String userId;
    private String currentPassword;
    private String newPassword;
    private String username;
    //TODO: correct the email field in front end before correcting this
    private String Email;

}
