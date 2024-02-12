package com.insy2s.mskeycloak.dto;

import com.insy2s.mskeycloak.model.Access;

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
