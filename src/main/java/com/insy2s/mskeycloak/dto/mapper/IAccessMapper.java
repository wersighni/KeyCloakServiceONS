package com.insy2s.mskeycloak.dto.mapper;

import com.insy2s.mskeycloak.dto.AccessDto;
import com.insy2s.mskeycloak.dto.CreateAccess;
import com.insy2s.mskeycloak.model.Access;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {IAccessMapper.class}
)
public interface IAccessMapper {

    Access toEntity(CreateAccess dto);

    Access toEntity(AccessDto dto);

    CreateAccess toCreateDto(Access entity);

    AccessDto toDto(Access access);

    List<AccessDto> toDto(List<Access> accessList);

    List<Access> toEntity(List<AccessDto> accessDtoList);

}