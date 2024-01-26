package com.insy2s.keycloakauth.dto.mapper;

import com.insy2s.keycloakauth.dto.AccessDto;
import com.insy2s.keycloakauth.model.Access;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface AccessMapper {

    AccessDto toDto(Access access);

}