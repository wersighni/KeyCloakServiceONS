package com.insy2s.KeyCloakAuth.dto;

import jakarta.persistence.Id;
import lombok.Data;

@Data
public class RoleDto {
    @Id
    private String id;
    private  String name;
    private String description;
}
