package com.insy2s.KeyCloakAuth.repository;

import com.insy2s.KeyCloakAuth.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("SELECT r FROM Role r WHERE r.name IN ('ADMIN', 'RHManager')")
    List<Role> findClientAndRH();

    Optional<Role> findByName(String name);
}
