package com.insy2s.KeyCloakAuth.controller;

import com.insy2s.KeyCloakAuth.dto.AccessDto;
import com.insy2s.KeyCloakAuth.model.Access;
import com.insy2s.KeyCloakAuth.model.Role;
import com.insy2s.KeyCloakAuth.service.IAccessService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/keycloak/access")
public class AccessController {

    Logger logger = LoggerFactory.getLogger(AccessController.class);

    private final IAccessService accessService;

    @PostMapping("/")
    public ResponseEntity<Access> CreateAccess(@RequestBody Access access) {
        return ResponseEntity.ok(accessService.create(access));
    }

    @GetMapping("/all")
    public List<Access> getAllAccess() {
        return accessService.getAllAccess();
    }
    @GetMapping("/")
    public List<AccessDto> getAllAccessDto() {return accessService.getAllAccessDto();}

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
    @GetMapping("/byRole/{roleId}")
    public List<AccessDto> findByRole( @PathVariable  Long roleId){
        return accessService.findByRole(roleId);
    }
    @GetMapping("/byUser")
    public List<AccessDto> findByUser( @RequestParam  String userId){
        return accessService.findByUser(userId);
    }
    @GetMapping("/addAccessRole")
    public Access addAccessToRole(@RequestParam  Long roleId, @RequestParam Long accessId) {
       return accessService.addAccessToRole(roleId,accessId);
    }
    @DeleteMapping("/removeAccessRole")
    public Access removeAccessRole(@RequestParam  Long roleId, @RequestParam Long accessId) {
        return accessService.removeAccessRole(roleId,accessId);
    }


}
