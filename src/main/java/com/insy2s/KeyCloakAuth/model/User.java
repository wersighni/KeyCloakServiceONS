package com.insy2s.KeyCloakAuth.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    private  boolean enabled=true;
    private boolean status = false;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles = new ArrayList<>();


    public User(String username, String id, String email, String lastname, String firstname, String password) {
        this.setUsername(username);
        this.setEmail(email);
        this.setId(id);
        this.setLastname(lastname);
        this.setFirstname(firstname);
        this.setPassword(password);

    }


}

