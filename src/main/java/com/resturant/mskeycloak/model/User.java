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


    private String username;


    private String email;


    private String firstname;


    private String lastname;


    private String password;

    private boolean enabled = true;

    private boolean status = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles = new ArrayList<>();



}
