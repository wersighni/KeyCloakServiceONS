package com.insy2s.KeyCloakAuth.repository;

import com.insy2s.KeyCloakAuth.model.Role;
import com.insy2s.KeyCloakAuth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {


    Optional<User>  findByUsername(String username);
    Optional<User>  findByEmail(String email);
    List<User> findByRoles_NameIn(Collection<String> roleNames);

}
