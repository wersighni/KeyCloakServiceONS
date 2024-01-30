package com.insy2s.keycloakauth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Access entity.
 * It is used to represent the different functionalities that can be accessed by the users/roles.
 * Like create a publication, delete a publication, read all users, etc.
 * A role in the app can have multiple accesses.
 * It is used to block access to some functionalities if the user who is trying to access it doesn't have the right role / access
 */
@Entity
@Table(name = "access")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Access {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    //TODO: add data verification on database and using spring-boot-starter-validation
    @Size(min = 2)
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    //TODO: add data verification on database and using spring-boot-starter-validation
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    //TODO: add data verification on database and using spring-boot-starter-validation
    @Column(name = "type")
    private String type;

    //TODO: add data verification on database and using spring-boot-starter-validation
    @Column(name = "path")
    private String path;

    //TODO: add data verification on database and using spring-boot-starter-validation
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Access parent;

    @JsonIgnore
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Access> subAccess = new ArrayList<>();

    public Access(String name, String code, String type, String path, Access parent) {
        this.name = name;
        this.code = code;
        this.type = type;
        this.path = path;
        this.parent = parent;
    }

}
