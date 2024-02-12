package com.insy2s.mskeycloak.repository;

import com.insy2s.mskeycloak.model.Access;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Repository for {@link Access} entity.
 */
@Repository
public interface IAccessRepository extends JpaRepository<Access, Long> {

    List<Access> findByType(@Param("type") String type);

    List<Access> findByParentId(Long id);

    @Query("""
                SELECT a FROM Role r
                JOIN r.accessList a
                WHERE r.id = :roleId AND a.type = :type
            """)
    List<Access> findAllByRoleAndType(@Param("roleId") Long roleId, @Param("type") String type);

    @Query("""
                SELECT a FROM User u
                JOIN u.roles r
                JOIN r.accessList a
                WHERE u.id = :userId AND a.type = :type
            """)
    List<Access> findByUserAndType(@Param("userId") String userId, @Param("type") String type);

    @Query("""
                SELECT a FROM Role r
                JOIN r.accessList a
                WHERE r.id IN :roles AND a.type = :type
            """)
    Set<Access> findAllByRolesInAndType(List<Long> roles, String type);

}
