package com.insy2s.keycloakauth.dto;

import com.insy2s.keycloakauth.model.Access;

import java.util.List;

public record CreateAccess(
        String name,
        String code,
        String type,
        String path,
        Access parent,
        List<Access> subAccess
) {
}
