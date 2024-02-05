package com.insy2s.mskeycloak.dto;

import lombok.*;

@Builder
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
