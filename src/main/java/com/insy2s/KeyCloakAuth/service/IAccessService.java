package com.insy2s.keycloakauth.service;

import com.insy2s.keycloakauth.dto.AccessDto;
import com.insy2s.keycloakauth.dto.CreateAccess;
import com.insy2s.keycloakauth.error.exception.NotFoundException;
import com.insy2s.keycloakauth.model.Access;
import com.insy2s.keycloakauth.model.Role;
import com.insy2s.keycloakauth.model.User;

import java.util.List;

/**
 * Interface Service for {@link Access} entity.
 */
public interface IAccessService {

    /**
     * Find all {@link Access} without children.
     *
     * @return List of {@link Access}
     */
    List<Access> findAllWithoutChildren();

    /**
     * Create {@link Access}.
     *
     * @param access {@link CreateAccess} to create.
     * @return {@link AccessDto}
     * @throws NotFoundException if parent {@link Access} not found.
     */
    AccessDto create(CreateAccess access);

    /**
     * Delete {@link Access} by id.
     *
     * @param id {@link Access} id.
     * @throws NotFoundException if {@link Access} not found.
     */
    void delete(Long id);

    /**
     * Find {@link Access} by id.
     *
     * @param id {@link Access} id.
     * @return {@link AccessDto}
     * @throws NotFoundException if {@link Access} not found.
     */
    AccessDto findById(Long id);

    /**
     * Find {@link Access} by parent id.
     *
     * @param id {@link Access} id.
     * @return List of {@link AccessDto}
     */
    List<AccessDto> findByParentId(Long id);

    /**
     * Find {@link Access} by type.
     *
     * @param type {@link Access} type.
     * @return List of {@link AccessDto}
     */
    List<AccessDto> findByType(String type);

    /**
     * Find {@link Access} by role id and type.
     *
     * @param roleId {@link Role} id.
     * @param type   {@link Access} type.
     * @return List of {@link AccessDto}
     */
    List<AccessDto> findByRoleAndType(Long roleId, String type);

    /**
     * Find all {@link Access} and their children.
     *
     * @return List of {@link AccessDto}
     */
    List<AccessDto> findAllMenusAndChildren();

    /**
     * Find all {@link Access} by role id.
     *
     * @param roleId {@link Access} id.
     * @return List of {@link AccessDto}
     */
    List<AccessDto> findAllMenusByRole(Long roleId);

    /**
     * Find all {@link Access} by user id.
     *
     * @param userId {@link Access} id.
     * @return List of {@link AccessDto}
     * @throws NotFoundException if {@link User} not found.
     */
    List<AccessDto> findAllMenusByUserId(String userId);

    List<String> findAllAccessCodeOfUserIdAndByType(String userId, String type);

    List<String> refactorAccess(List<Access> access);

    /**
     * Add {@link Access} to {@link Role}.
     *
     * @param roleId   {@link Role} id.
     * @param accessId {@link Access} id.
     * @return {@link Access} added.
     * @throws NotFoundException if {@link Role} or {@link Access} not found.
     */
    Access addAccessToRole(Long roleId, Long accessId);

    /**
     * Remove {@link Access} from {@link Role}.
     *
     * @param roleId   {@link Role} id.
     * @param accessId {@link Access} id.
     * @return {@link Access} removed.
     * @throws NotFoundException if {@link Role} or {@link Access} not found.
     */
    Access removeAccessFromRole(Long roleId, Long accessId);

}
