package com.insy2s.keycloakauth.controller;

import com.insy2s.keycloakauth.model.User;
import com.insy2s.keycloakauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/keycloak/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/find")
    ResponseEntity<User> getUser(@RequestParam String username) {
        return ResponseEntity.status(200).body(userService.getUser(username));
    }

    @GetMapping(value = "/{id}")
    public User getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @GetMapping("/")
    List<User> getUser() {
        return userService.getUsers();
    }

    @PostMapping(value = "/create")
    ResponseEntity<?> createUser(@RequestBody User user) {

        return userService.createUser(user);
    }

    @PostMapping(value = "/toggleUserEnabled/{id}")
    ResponseEntity toggleUserEnabled(@PathVariable String id) {

        return userService.toggleUserEnabled(id);
    }

    @DeleteMapping(value = "/{id}")
    ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        try {
            ResponseEntity updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la modification du user.");
        }
    }
}


