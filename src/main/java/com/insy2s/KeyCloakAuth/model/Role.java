package com.insy2s.KeyCloakAuth.model;

import jakarta.persistence.*;

import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(length = 20)
    private String name;
    @Column(length = 20)
    private String description;
    private boolean status = false;
    @ManyToMany()
    @JoinTable(
            name = "role_access",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "access_id"))
    List<Access> accessList;

    public Role(String name, String id, String description) {
    }

    public Role() {

    }

    public Role(String name, String description) {
    }
}