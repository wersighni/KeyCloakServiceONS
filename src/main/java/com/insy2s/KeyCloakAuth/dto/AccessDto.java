package com.insy2s.KeyCloakAuth.dto;

import com.insy2s.KeyCloakAuth.model.Access;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessDto {

    private Long id;
    private String name;
    private String code;
    private String type;
    private String path;

    private List<AccessDto> subAccess;

    public AccessDto(Long id, String name, String code, String type, String path) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.type = type;
        this.path = path;
    }
}
