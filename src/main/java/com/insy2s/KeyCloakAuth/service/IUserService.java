package com.insy2s.keycloakauth.service;

import com.insy2s.keycloakauth.model.User;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    void deleteUser(String id);

    void toggleUserEnabled(String userId);

    ResponseEntity<?> createUser(User user);

    User getUser(String username);

    User getUserById(String id);

    List<User> getUsers();

    User updateUser(User user);
}
