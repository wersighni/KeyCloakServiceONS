package com.insy2s.mskeycloak.controller;

import com.insy2s.mskeycloak.model.Role;
import com.insy2s.mskeycloak.service.IRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for {@link Role} entity.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/keycloak/roles")
public class RoleController {

    private final IRoleService roleService;

    /**
     * POST /api/keycloak/roles : create a new role.
     *
     * @param role the role to create.
     * @return the ResponseEntity with status 201 (Created) and with body the new role,
     * or with status 400 (Bad Request) if the role already exists.
     */
    @PostMapping("/")
    public ResponseEntity<Role> create(@RequestBody @Valid Role role) {
        log.debug("REST request to save Role : {}", role);
        Role roleCreated = roleService.create(role);
        return ResponseEntity.status(201).body(roleCreated);
    }

    /**
     * GET /api/keycloak/roles : get all roles.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of roles in body.
     */
    @GetMapping("/")
    public List<Role> getAll() {
        log.debug("REST request to get all Roles");
        return roleService.getRoles();
    }

    /**
     * GET /api/keycloak/roles/statusFalse : get all roles with status false.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of roles in body.
     */
    @GetMapping("/statusFalse")
    public List<Role> getAllWithStatusFalse() {
        log.debug("REST request to get all Roles with status false");
        return roleService.getAllRolesWithStatusFalse();
    }

    /**
     * GET /api/keycloak/roles/:id : get the "id" role.
     *
     * @param id the id of the role to retrieve.
     * @return the ResponseEntity with status 200 (OK) and with body the role,
     * or with status 404 (Not Found).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Role> getById(@PathVariable Long id) {
        log.debug("REST request to get Role : {}", id);
        Role role = roleService.findById(id);
        return ResponseEntity.ok(role);
    }

    /**
     * GET /api/keycloak/roles/byName/:name : get the "name" role.
     *
     * @param name the name of the role to retrieve.
     * @return the ResponseEntity with status 200 (OK) and with body the role,
     */
    @GetMapping("/byName/{name}")
    public ResponseEntity<Role> getByName(@PathVariable String name) {
        log.debug("REST request to get Role : {}", name);
        Role role = roleService.findByName(name);
        return ResponseEntity.ok(role);
    }

    //TODO: change Post to Put

    /**
     * POST /api/keycloak/roles/:id/update : update the "id" role.
     *
     * @param id   the id of the role to update.
     * @param role the role to update.
     * @return the ResponseEntity with status 200 (OK) and with body the updated role,
     * or with status 404 (Not Found) if the role is not found.
     */
    @PostMapping("/{id}/update")
    public ResponseEntity<Role> update(@PathVariable Long id, @RequestBody @Valid Role role) {
        log.debug("REST request to update Role : {}", role);
        Role updatedRole = roleService.update(id, role);
        return ResponseEntity.ok(updatedRole);
    }

    //TODO: change Put to Delete
    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
