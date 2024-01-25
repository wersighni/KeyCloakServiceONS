package com.insy2s.keycloakauth.service;

import com.insy2s.keycloakauth.dto.AccessDto;
import com.insy2s.keycloakauth.model.Access;

import java.util.List;

public interface IAccessService {

    public List<Access> getAllAccess();
    public Access create(Access access) ;

    public void deleteAccess(Long id);


    public Access findById(Long id) ;

    public List<Access> findByParentId(Long id) ;
    public List<Access>  findByType(String type) ;

    List<Access> findByRoleAndType(Long roleId,  String type);
    public AccessDto refarctorMenu(Access m, List<Access> pages, List<Access> actions);

    public List<AccessDto> getAllAccessDto() ;
    public List<AccessDto> findByRole(  Long roleId);
    public List<AccessDto> findByUser(  String userId);
    public List<String> refactorByUserAndType(  String userId,String type);
    public List<String> refactorAccess(List<Access> access);
    public Access addAccessToRole(  Long roleId,  Long accessId);
    public Access removeAccessRole(  Long roleId,  Long accessId);
}
