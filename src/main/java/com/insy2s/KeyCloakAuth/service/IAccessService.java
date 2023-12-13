package com.insy2s.KeyCloakAuth.service;

import com.insy2s.KeyCloakAuth.model.Access;

import java.util.List;

public interface IAccessService {

    public List<Access> getAllAccess();
    public Access create(Access access) ;

    public void deleteAccess(Long id);


    public Access findById(Long id) ;

    public List<Access> findByParentId(Long id) ;
    public List<Access>  findByType(String type) ;

}
