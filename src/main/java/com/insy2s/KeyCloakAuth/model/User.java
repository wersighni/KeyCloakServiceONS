package com.insy2s.KeyCloakAuth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users")
public class User   {
    @Id
    private String id;
    @Size(max = 50)
    @Column(unique = true)
    private  String username;

    private String email;
    private String firstname;
    private String lastname;
    //private String titre;
    private String docProfileId;

    private  String password;
    @Temporal(TemporalType.TIMESTAMP) // Ajoutez cette annotation pour la date d'inscription
    private Date dateInscription;
    private  boolean enabled=true;
    private boolean status = false;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles = new ArrayList<>();


    public User(String username, String id, String email, String lastname, String firstname,  String password, Collection<Role> roles) {
        this.setUsername(username);
        this.setEmail(email);
        this.setId(id);
        this.setLastname(lastname);
        this.setFirstname(firstname);
        this.setPassword(password);
        this.setRoles(roles);

    }


    public User(String username, String firstname, String lastname, String id, String email, Collection<Role> roles) {
        this.setUsername(username);
        this.setEmail(email);
        this.setId(id);
        this.setLastname(lastname);
        this.setFirstname(firstname);
        this.setRoles(roles);
    }
}

