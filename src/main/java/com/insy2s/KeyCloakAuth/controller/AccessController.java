package com.insy2s.KeyCloakAuth.controller;

import com.insy2s.KeyCloakAuth.model.Access;
import com.insy2s.KeyCloakAuth.service.IAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/keycloak/access")
public class AccessController {

    Logger logger = LoggerFactory.getLogger(AccessController.class);
    @Autowired
    private IAccessService accessService;

    @PostMapping("/")
    public ResponseEntity<Access> CreateAccess(@RequestBody Access access) {
        return ResponseEntity.ok(accessService.create(access));
    }

    @GetMapping("/")
    public List<Access> getAllAccess() {
        return accessService.getAllAccess();
    }


    @DeleteMapping("/{id}")
    public void deleteAccess(@PathVariable Long id) {
        accessService.deleteAccess(id);
    }

    @GetMapping("/{id}")
    public Access getAccessById (@PathVariable Long id) {
        return accessService.findById(id);
    }

    @GetMapping("/byParentId/{id}")
    public List<Access> getAccessByParentId (@PathVariable Long id) {
        return accessService.findByParentId(id);
    }

    @GetMapping("/byType/{type}")
    public List<Access> getAccessByParentId (@PathVariable String type) {
        return accessService.findByType(type);
    }
    @GetMapping("/byRoleAndType")
    public List<Access> findByRoleAndType( @RequestParam  Long roleId, @RequestParam  String type){
        return accessService.findByRoleAndType(roleId,type);
    }
}
