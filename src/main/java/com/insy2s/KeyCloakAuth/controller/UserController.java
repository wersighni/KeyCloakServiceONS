package com.insy2s.keycloakauth.controller;

import com.insy2s.keycloakauth.model.User;
import com.insy2s.keycloakauth.service.IUserService;
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
    public ResponseEntity<User> getUser(@RequestParam String username) {
        log.debug("REST request to get User : {}", username);
        User user = userService.getUser(username);
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
    public User getUserById(@PathVariable String id) {
        log.debug("REST request to get User : {}", id);
        return userService.getUserById(id);
    }

    /**
     * GET  /api/keycloak/users : get all users.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of users in body.
     */
    @GetMapping
    public List<User> getUser() {
        log.debug("REST request to get all Users");
        return userService.getUsers();
    }

    //TODO: refacto service
    @PostMapping(value = "/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        log.debug("REST request to save User : {}", user.getUsername());
        return userService.createUser(user);
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
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        log.debug("REST request to delete User : {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT  /api/keycloak/users/update : update the user.
     *
     * @param user the user to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated user,
     * or status {@code 404 (Not Found)} if the user couldn't be found.
     */
    @PostMapping("/update")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        log.debug("REST request to update User : {}", user.getUsername());
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }

}
