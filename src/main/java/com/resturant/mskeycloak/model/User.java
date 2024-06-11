package com.resturant.mskeycloak.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {


    @Id
    @Column(name = "id")
    private String id;

    @Size(min = 4, max = 50)
    @Email
    @NotBlank
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Size(min = 4, max = 50)
    @Email
    @NotBlank
    @Column(name = "email", unique = true, nullable = false, length = 50)
    private String email;

    @Size(min = 2)
    @Pattern(regexp = "^[ A-zÀ-ÿ'-]+$")
    @NotBlank
    @Column(name = "firstname", nullable = false, length = 50)
    private String firstname;

    @Size(min = 2)
    @Pattern(regexp = "^[ A-zÀ-ÿ'-]+$")
    @NotBlank
    @Column(name = "lastname", nullable = false, length = 50)
    private String lastname;

    //TODO: add data verification on database and using spring-boot-starter-validation
    // or delete this field?
    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Column(name = "doc_profile_id")
    private String docProfileId;

    // TODO: delete this field?
/*    @Transient
    private String lstRole;*/

    @Column(name = "date_inscription")
    @Temporal(TemporalType.TIMESTAMP) // Ajoutez cette annotation pour la date d'inscription
    private Date dateInscription;

    @Column(name = "enabled")
    private boolean enabled = true;

    @Column(name = "status")
    private boolean status = false;

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
