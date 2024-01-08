package com.insy2s.KeyCloakAuth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.insy2s.KeyCloakAuth.model.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Data
public class UserDto {
    @Id
    private String id;
    private  String username;
    private String email;
    private String firstname;
    private String lastname;
    //private String titre;

    private  String password;
    @Temporal(TemporalType.TIMESTAMP) // Ajoutez cette annotation pour la date d'inscription
    private Date dateInscription;
    private  boolean enabled=true;
    private String roles;

}
