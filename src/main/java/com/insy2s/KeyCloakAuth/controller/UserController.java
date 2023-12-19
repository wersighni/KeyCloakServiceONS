package com.insy2s.KeyCloakAuth.controller;

import com.insy2s.KeyCloakAuth.dto.UserDto;
import com.insy2s.KeyCloakAuth.model.Role;
import com.insy2s.KeyCloakAuth.model.User;
import com.insy2s.KeyCloakAuth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/keycloak/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/find")
    ResponseEntity<User> getUser(@RequestParam String username )
    {
        return ResponseEntity.status(200).body(userService.getUser(username ));
    }
/*    @GetMapping("/")
    ResponseEntity getAllUsers( )
    {
        return userService.listUsers( );
    }*/

    @GetMapping("/")
    List<User> getUser( )
    {
        return userService.getUsers( );
    }

  @PostMapping(value = "/create")
  ResponseEntity <?> createUser(@RequestBody UserDto user){

       return userService.createUser( user);
   }
    @PostMapping(value = "/toggleUserEnabled/{id}")
    ResponseEntity toggleUserEnabled(@PathVariable String id){

        return userService.toggleUserEnabled( id);
    }

    @DeleteMapping(value = "/{id}")
    ResponseEntity deleteUser(@PathVariable String id ){
        return userService.deleteUser( id);
    }
    }


