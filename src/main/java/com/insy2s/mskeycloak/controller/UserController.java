package com.insy2s.mskeycloak.controller;

import com.insy2s.mskeycloak.model.User;
import com.insy2s.mskeycloak.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for {@link User} entity.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/keycloak/users")
public class UserController {

    private final IUserService userService;

    /**
     * GET  /api/keycloak/users/find : get the user by username.
     *
     * @param username the username of the user to find.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the user,
     * or status {@code 404 (Not Found)} if the user couldn't be found.
     */
    @GetMapping("/find")
    public ResponseEntity<User> getByUsername(@RequestParam String username) {
        log.debug("REST request to get User : {}", username);
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    /**
     * GET  /api/keycloak/users/{id} : get the user by id.
     *
     * @param id the id of the user to find.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the user,
     * or status {@code 404 (Not Found)} if the user couldn't be found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable String id) {
        log.debug("REST request to get User : {}", id);
        User user = userService.findById(id);
        return ResponseEntity.ok().body(user);
    }

    //TODO: should use a Page to not send all users at once
    /**
     * GET  /api/keycloak/users : get all users.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of users in body.
     */
    @GetMapping
    public List<User> getAll() {
        log.debug("REST request to get all Users");
        return userService.findAll();
    }

    //TODO: should have two different endpoints:
    //  - one to create a user as an admin
    //  - one to register the current user (should not be able to set some fields like roles)
    /**
     * POST  /api/keycloak/users/create : create a new user.
     *
     * @param user the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user,
     * or status {@code 400 (Bad Request)} if the email or username is already in use.
     */
    @PostMapping("/create")
    public ResponseEntity<User> create(@RequestBody @Valid User user) {
        log.debug("REST request to save User : {}", user.getUsername());
        User userCreated = userService.create(user);
        return ResponseEntity.status(201).body(userCreated);
    }

    /**
     * POST  /api/keycloak/users/toggleUserEnabled/{id} : toggle user enabled status.
     *
     * @param id the id of the user to toggle.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated user,
     * or status {@code 404 (Not Found)} if the user couldn't be found.
     */
    @PostMapping(value = "/toggleUserEnabled/{id}")
    public ResponseEntity<String> toggleUserEnabled(@PathVariable String id) {
        log.debug("REST request to toggle user enabled : {}", id);
        userService.toggleUserEnabled(id);
        return ResponseEntity.ok().body("User enabled status toggled successfully.");
    }

    /**
     * DELETE  /api/keycloak/users/{id} : delete the user by id.
     *
     * @param id the id of the user to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteById(@PathVariable String id) {
        log.debug("REST request to delete User : {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    //TODO: should be a PUT
    //TODO: should have two different endpoints:
    //  - one to update a user as an admin
    //  - one to update the current user (should not be able to update some fields like roles)
    /**
     * POST  /api/keycloak/users/update : update the user.
     *
     * @param user the user to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated user,
     * or status {@code 404 (Not Found)} if the user couldn't be found.
     */
    @PostMapping("/update")
    public ResponseEntity<User> update(@RequestBody @Valid User user) {
        log.debug("REST request to update User : {}", user.getUsername());
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }

}
