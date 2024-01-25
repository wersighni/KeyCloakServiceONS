package com.insy2s.keycloakauth.repository;

import com.insy2s.keycloakauth.model.Access;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link Access} entity.
 */
@Repository
public interface AccessRepository extends JpaRepository<Access, Long> {

    List<Access> findByType(@Param("type") String type);

    List<Access> findByParentId(Long id);

    @Query(value = "SELECT a.* FROM Access a, role_access ra where ra.role_id=:roleId and ra.access_id=a.id and a.type=:type", nativeQuery = true)
    List<Access> findByRoleAndType(@Param("roleId") Long roleId, @Param("type") String type);

// TODO: test this query to see if it can replace the previous one
//    @Query("""
//        SELECT a FROM Role r
//        JOIN r.accessList a
//        WHERE r.id = :roleId AND a.type = :type
//    """)
//    List<Access> findByRoleAndType(@Param("roleId") Long roleId, @Param("type") String type);

}
