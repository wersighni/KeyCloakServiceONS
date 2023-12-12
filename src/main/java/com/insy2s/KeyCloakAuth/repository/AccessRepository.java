package com.insy2s.KeyCloakAuth.repository;


import com.insy2s.KeyCloakAuth.model.Access;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
    public interface AccessRepository extends JpaRepository<Access, Long> {

        List<Access> findByType(@Param("type") String type);
    List<Access> findByParentId(Long id);
}
