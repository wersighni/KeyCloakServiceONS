package com.insy2s.keycloakauth.repository;

import com.insy2s.keycloakauth.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Role} entity.
 */
@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {

    List<Role> getAllRolesByStatusFalse();

    Optional<Role> findByName(String name);

}
