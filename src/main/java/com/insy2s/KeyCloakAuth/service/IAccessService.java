package com.insy2s.keycloakauth.service;

import com.insy2s.keycloakauth.dto.AccessDto;
import com.insy2s.keycloakauth.model.Access;

import java.util.List;

/**
 * Interface Service for {@link Access} entity.
 */
public interface IAccessService {

    List<Access> getAllAccess();

    Access create(Access access);

    void deleteAccess(Long id);

    Access findById(Long id);

    List<Access> findByParentId(Long id);

    List<Access> findByType(String type);

    List<Access> findByRoleAndType(Long roleId, String type);

    AccessDto refactorMenu(Access access, List<Access> pages, List<Access> actions);

    List<AccessDto> getAllAccessDto();

    List<AccessDto> findByRole(Long roleId);

    List<AccessDto> findByUser(String userId);

    List<String> refactorByUserAndType(String userId, String type);

    List<String> refactorAccess(List<Access> access);

    Access addAccessToRole(Long roleId, Long accessId);

    Access removeAccessRole(Long roleId, Long accessId);

}
