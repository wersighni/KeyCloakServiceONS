package com.insy2s.keycloakauth.controller;

import com.insy2s.keycloakauth.dto.AccessDto;
import com.insy2s.keycloakauth.model.Access;
import com.insy2s.keycloakauth.service.IAccessService;
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
     * @param access {@link Access} to create.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body the new Access,
     * or with status {@code 400 (Bad Request)} if the Access data are not valid.
     */
    @PostMapping
    public ResponseEntity<Access> create(@RequestBody Access access) {
        log.debug("REST request to save a new Access : {}", access);
        return ResponseEntity.ok(accessService.create(access));
    }

    /**
     * GET /api/keycloak/access/all : get all the Access.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body all the Access.
     */
    @GetMapping("/all")
    public List<Access> getAll() {
        log.debug("REST request to get all Access");
        return accessService.getAllAccess();
    }

    /**
     * GET /api/keycloak/access : get all the Access DTO.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body all the Access DTO.
     */
    @GetMapping
    public List<AccessDto> getAllDto() {
        log.debug("REST request to get all Access DTO");
        return accessService.getAllAccessDto();
    }

    /**
     * DELETE /api/keycloak/access/{id} : delete the "id" Access.
     * @param id the id of the Access to delete.
     * @return {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccess(@PathVariable Long id) {
        log.debug("REST request to delete Access : {}", id);
        accessService.deleteAccess(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/keycloak/access/{id} : get the "id" Access.
     * @param id the id of the Access to retrieve.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body the Access,
     * or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Access getById(@PathVariable Long id) {
        log.debug("REST request to get Access : {}", id);
        return accessService.findById(id);
    }

    /**
     * GET /api/keycloak/access/byParentId/{id} : get all the Access by parent id.
     * @param id the id of the parent Access.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body all the Access by parent id.
     */
    @GetMapping("/byParentId/{id}")
    public List<Access> getAllByParentId(@PathVariable Long id) {
        log.debug("REST request to get all Access by parent id : {}", id);
        return accessService.findByParentId(id);
    }

    /**
     * GET /api/keycloak/access/byType/{type} : get all the Access by type.
     * @param type the type of the Access.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body all the Access by type.
     */
    @GetMapping("/byType/{type}")
    public List<Access> getAllByParentId(@PathVariable String type) {
        log.debug("REST request to get all Access by type : {}", type);
        return accessService.findByType(type);
    }

    /**
     * GET /api/keycloak/access/byRoleAndType : get all the Access by role id and type.
     * @param roleId the id of the role.
     * @param type the type of the Access.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body all the Access by role id and type.
     */
    @GetMapping("/byRoleAndType")
    public List<Access> getAllByRoleAndType(@RequestParam Long roleId, @RequestParam String type) {
        log.debug("REST request to get all Access by role id : {} and type : {}", roleId, type);
        return accessService.findByRoleAndType(roleId, type);
    }

    /**
     * GET /api/keycloak/access/byRole/{roleId} : get all the Access by role id.
     * @param roleId the id of the role.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body all the Access by role id.
     */
    @GetMapping("/byRole/{roleId}")
    public List<AccessDto> getAllByRole(@PathVariable Long roleId) {
        log.debug("REST request to get all Access by role id : {}", roleId);
        return accessService.findByRole(roleId);
    }

    /**
     * GET /api/keycloak/access/byUser : get all the Access by user id.
     * @param userId the id of the user.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body all the Access by user id.
     */
    @GetMapping("/byUser")
    public List<AccessDto> getAllByUser(@RequestParam String userId) {
        log.debug("REST request to get all Access by user id : {}", userId);
        return accessService.findByUser(userId);
    }

    /**
     * GET /api/keycloak/access/byUserAndType : get all the Access by user id and type.
     * @param roleId the id of the user.
     * @param accessId the id of the Access.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body all the Access by user id and type.
     */
    @GetMapping("/addAccessRole")
    public Access addAccessToRole(@RequestParam Long roleId, @RequestParam Long accessId) {
        log.debug("REST request to add Access : {} to role : {}", accessId, roleId);
        return accessService.addAccessToRole(roleId, accessId);
    }

    /**
     * GET /api/keycloak/access/removeAccessRole : remove the Access from role.
     * @param roleId the id of the role.
     * @param accessId the id of the Access.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body the Access from role.
     */
    @DeleteMapping("/removeAccessRole")
    public Access removeAccessToRole(@RequestParam Long roleId, @RequestParam Long accessId) {
        log.debug("REST request to remove Access : {} from role : {}", accessId, roleId);
        return accessService.removeAccessRole(roleId, accessId);
    }

}
