package com.insy2s.KeyCloakAuth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Access {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String code;
    private String type;
    private String path;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Access parent;

    public Access(String name,String code, String type) {
        this.name = name;
        this.type = type;
    }

    public Access(String name,String code, String type, String path, Access parent) {
        this.name = name;
        this.type = type;
        this.path = path;
        this.parent = parent;
    }

    public Access(String name,String code, String type, Access parent) {
        this.name = name;
        this.type = type;
        this.parent = parent;
    }
}
