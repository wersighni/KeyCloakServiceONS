package com.insy2s.KeyCloakAuth.service;

import com.insy2s.KeyCloakAuth.model.Access;
import com.insy2s.KeyCloakAuth.repository.AccessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccessService implements IAccessService{

    @Autowired
    private AccessRepository accessRepository;
    public List<Access> getAllAccess() {
        return accessRepository.findAll();
    }

    public Access create(Access access) {
        return accessRepository.save(access);
    }

    public void deleteAccess(Long id) {
        accessRepository.deleteById(id);
    }


    public Access findById(Long id) {
        return accessRepository.findById(id).get();
    }

    public List<Access>  findByParentId(Long id) {
        return  accessRepository.findByParentId(id);
    }
    public List<Access>  findByType(String type) {
        return  accessRepository.findByType(type);
    }
}
