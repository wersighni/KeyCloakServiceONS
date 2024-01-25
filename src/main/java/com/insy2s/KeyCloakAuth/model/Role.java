package com.insy2s.keycloakauth.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Role entity.
 * It is used to represent the different roles that can be assigned by the users of the application.
 * Every user has a list of roles and every role has a list of accesses.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    //TODO: add data verification on database and using spring-boot-starter-validation
    @Column(name = "name")
    private String name;

    //TODO: add data verification on database and using spring-boot-starter-validation
    @Column(name = "description")
    private String description;

    //TODO: add data verification on database and using spring-boot-starter-validation
    @Column(name = "status")
    private boolean status = false;

    @ManyToMany()
    @JoinTable(
            name = "role_access",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "access_id")
    )
    @ToString.Exclude
    private List<Access> accessList = new ArrayList<>();

}
