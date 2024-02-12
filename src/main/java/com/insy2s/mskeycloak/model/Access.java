package com.insy2s.mskeycloak.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
@Builder
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

    @NotBlank
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[ A-zÀ-ÿ'-]+$")
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @NotBlank
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[_a-zA-Z'-]+$")
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @NotBlank
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[a-zA-Z'-]+$")
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "path")
    private String path;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Access parent;

    @JsonIgnore
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Access> subAccess = new ArrayList<>();

}
