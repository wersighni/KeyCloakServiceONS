package com.insy2s.KeyCloakAuth.repository;

import com.insy2s.KeyCloakAuth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {


    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);


}
