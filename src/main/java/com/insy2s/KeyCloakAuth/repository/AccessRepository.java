package com.insy2s.KeyCloakAuth.repository;


import com.insy2s.KeyCloakAuth.model.Access;
import com.insy2s.KeyCloakAuth.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
    public interface AccessRepository extends JpaRepository<Access, Long> {

        List<Access> findByType(@Param("type") String type);
    List<Access> findByParentId(Long id);
    @Query(value = "SELECT   a.* FROM Access a , role_access ra where ra.role_id=:roleId and ra.access_id=a.id and a.type=:type",nativeQuery = true)
    List<Access> findByRoleAndType(@Param("roleId") Long roleId,@Param("type") String type);
}
