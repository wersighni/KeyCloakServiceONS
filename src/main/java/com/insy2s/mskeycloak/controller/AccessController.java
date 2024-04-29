package com.insy2s.mskeycloak.controller;

import com.insy2s.mskeycloak.dto.AccessDto;
import com.insy2s.mskeycloak.dto.CreateAccess;
import com.insy2s.mskeycloak.model.Access;
import com.insy2s.mskeycloak.service.IAccessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for {@link Access} entity.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/keycloak/access")
public class AccessController {

    private final IAccessService accessService;

    /**
     * POST /api/keycloak/access : Create a new Access.
     *
     * @param access {@link Access} to create.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body the new Access,
     * or with status {@code 400 (Bad Request)} if the Access data are not valid.
     */
    @PostMapping("/")
    public ResponseEntity<AccessDto> create(@RequestBody @Valid CreateAccess access) {
        log.debug("REST request to save a new Access : {}", access);
        AccessDto result = accessService.create(access);
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/keycloak/access/all : get all the Access.
     *
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body all the Access.
     */
    @GetMapping("/all")
    public List<Access> getAllWithoutChildren() {
        log.debug("REST request to get all Access");
        return accessService.findAllWithoutChildren();
    }

    /**
     * GET /api/keycloak/access : get all the Access DTO.
     *
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body all the Access DTO.
     */
    @GetMapping("/")
    public List<AccessDto> getAllMenusWithChildren() {
        log.debug("REST request to get all Access DTO");
        return accessService.findAllMenusAndChildren();
    }

    /**
     * DELETE /api/keycloak/access/{id} : delete the "id" Access.
     *
     * @param id the id of the Access to delete.
     * @return {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        log.debug("REST request to delete Access : {}", id);
        accessService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/keycloak/access/{id} : get the "id" Access.
     *
     * @param id the id of the Access to retrieve.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body the Access,
     * or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public AccessDto getById(@PathVariable Long id) {
        log.debug("REST request to get Access : {}", id);
        return accessService.findById(id);
    }

    /**
     * GET /api/keycloak/access/{id} : get the "id" Access.
     *
     * @param id the id of the Access to retrieve.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body the Access,
     * or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/forUpdate/{id}")
    public Access getForUpdateById(@PathVariable Long id) {
        log.debug("REST request to get Access : {}", id);
        return accessService.findByIdForUpdate(id);
    }

    /**
     * GET /api/keycloak/access/byParentId/{id} : get all the Access by parent id.
     *
     * @param id the id of the parent Access.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body all the Access by parent id.
     */
    @GetMapping("/byParentId/{id}")
    public List<AccessDto> getAllByParentId(@PathVariable Long id) {
        log.debug("REST request to get all Access by parent id : {}", id);
        return accessService.findByParentId(id);
    }

    /**
     * GET /api/keycloak/access/byType/{type} : get all the Access by type.
     *
     * @param type the type of the Access.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body all the Access by type.
     */
    @GetMapping("/byType/{type}")
    public List<AccessDto> getAllByType(@PathVariable String type) {
        log.debug("REST request to get all Access by type : {}", type);
        return accessService.findByType(type);
    }

    /**
     * GET /api/keycloak/access/byRoleAndType : get all the Access by role id and type.
     *
     * @param roleId the id of the role.
     * @param type   the type of the Access.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body all the Access by role id and type.
     */
    @GetMapping("/byRoleAndType")
    public List<AccessDto> getAllByRoleAndType(@RequestParam Long roleId, @RequestParam String type) {
        log.debug("REST request to get all Access by role id : {} and type : {}", roleId, type);
        return accessService.findByRoleAndType(roleId, type);
    }

    /**
     * GET /api/keycloak/access/byRole/{roleId} : get all the Access by role id.
     *
     * @param roleId the id of the role.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body all the Access by role id.
     */
    @GetMapping("/byRole/{roleId}")
    public List<AccessDto> getAllMenusByRole(@PathVariable Long roleId) {
        log.debug("REST request to get all Access by role id : {}", roleId);
        return accessService.findAllMenusByRole(roleId);
    }

    /**
     * GET /api/keycloak/access/byUser : get all the Access by user id.
     *
     * @param userId the id of the user.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body all the Access by user id.
     */
    @GetMapping("/byUser")
    public List<AccessDto> getAllByUser(@RequestParam String userId) {
        log.debug("REST request to get all Access by user id : {}", userId);
        return accessService.findAllMenusByUserId(userId);
    }

    //TODO: change GET to PUT

    /**
     * GET /api/keycloak/access/addAccessRole : add the Access to role.
     *
     * @param roleId   the id of the user.
     * @param accessId the id of the Access.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body the Access added to role.
     */
    @GetMapping("/addAccessRole")
    public Access addAccessToRole(@RequestParam Long roleId, @RequestParam Long accessId) {
        log.debug("REST request to add Access : {} to role : {}", accessId, roleId);
        return accessService.addAccessToRole(roleId, accessId);
    }

    /**
     * GET /api/keycloak/access/removeAccessRole : remove the Access from role.
     *
     * @param roleId   the id of the role.
     * @param accessId the id of the Access.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body the Access from role.
     */
    @DeleteMapping("/removeAccessRole")
    public Access removeAccessToRole(@RequestParam Long roleId, @RequestParam Long accessId) {
        log.debug("REST request to remove Access : {} from role : {}", accessId, roleId);
        return accessService.removeAccessFromRole(roleId, accessId);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Access> update(@PathVariable Long id, @RequestBody Access access) {
        log.debug("REST request to update Access : {}", access);
        Access updateAccess = accessService.update(id, access);
        return ResponseEntity.ok(updateAccess);
    }

}
