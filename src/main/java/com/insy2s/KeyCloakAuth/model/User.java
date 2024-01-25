package com.insy2s.keycloakauth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * User entity.
 * It is used to represent the users of the application.
 * It is the account, used for the authentication and authorization.
 * To not be confused with the user's information from the Member microservice.
 */
@Entity
@Table(name = "users")
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * The id of the user from Keycloak.
     */
    @Id
    @Column(name = "id")
    private String id;

    //TODO: add data verification on database and using spring-boot-starter-validation
    @Size(max = 50)
    @Column(unique = true)
    private String username;

    //TODO: add data verification on database and using spring-boot-starter-validation
    @Column(name = "email")
    private String email;

    //TODO: add data verification on database and using spring-boot-starter-validation
    @Column(name = "firstname")
    private String firstname;

    //TODO: add data verification on database and using spring-boot-starter-validation
    @Column(name = "lastname")
    private String lastname;

    //TODO: add data verification on database and using spring-boot-starter-validation
    @Column(name = "doc_profile_id")
    private String docProfileId;

    @Transient
    private String lstRole;

    //TODO: add data verification on database and using spring-boot-starter-validation
    @Column(name = "password")
    private String password;

    //TODO: add data verification on database and using spring-boot-starter-validation
    @Column(name = "phone")
    @Temporal(TemporalType.TIMESTAMP) // Ajoutez cette annotation pour la date d'inscription
    private Date dateInscription;

    //TODO: add data verification on database and using spring-boot-starter-validation
    @Column(name = "enabled")
    private boolean enabled = true;

    //TODO: add data verification on database and using spring-boot-starter-validation
    @Column(name = "status")
    private boolean status = false;

    //TODO: add data verification on database and using spring-boot-starter-validation
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles = new ArrayList<>();

    public User(String username, String firstname, String lastname, String id, String email, Collection<Role> roles) {
        this.setUsername(username);
        this.setEmail(email);
        this.setId(id);
        this.setLastname(lastname);
        this.setFirstname(firstname);
        this.setRoles(roles);
    }

}
