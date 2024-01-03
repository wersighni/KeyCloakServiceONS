package com.insy2s.KeyCloakAuth.service;

import com.insy2s.KeyCloakAuth.dto.AccessDto;
import com.insy2s.KeyCloakAuth.model.Access;
import com.insy2s.KeyCloakAuth.model.Role;
import com.insy2s.KeyCloakAuth.model.User;
import com.insy2s.KeyCloakAuth.repository.AccessRepository;
import com.insy2s.KeyCloakAuth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccessService implements IAccessService{

    @Autowired
    private AccessRepository accessRepository;
    @Autowired
    private UserRepository userRepo;
    public List<Access> getAllAccess() {
        return accessRepository.findAll();
    }

    public List<AccessDto> getAllAccessDto() {
        List<Access> menus= accessRepository.findByType("Menu");
        List<Access> actions=accessRepository.findByType( "Action");
        List<AccessDto> res = new ArrayList<AccessDto>();
        for (Access m : menus) {
                AccessDto mdto=refarctorMenu(m,accessRepository.findByParentId(m.getId()),actions);
                res.add(mdto);
        }
        return res;
    }

    public List<AccessDto> findByRole(  Long roleId) {
        List<Access> menus= accessRepository.findByRoleAndType(roleId,"Menu");
        List<Access> pages=accessRepository.findByRoleAndType(roleId,"Page");
        List<Access> actions=accessRepository.findByRoleAndType(roleId, "Action");
        List<AccessDto> res = new ArrayList<AccessDto>();
        for (Access m : menus) {
            AccessDto mdto=refarctorMenu(m,pages,actions);
            res.add(mdto);
        }
        return res;
    }

    public List<AccessDto> findByUser(  String userId){

        List<Access> menus=new ArrayList<Access>();
        List<Access> pages=new ArrayList<Access>();
        List<Access> actions=new ArrayList<Access>();
        List<AccessDto> res = new ArrayList<AccessDto>();
        User user=userRepo.findById(userId).orElse(null);
        if(user!=null) {
            for (Role r : user.getRoles()) {

                menus.addAll(/*r.getAccessList());*/accessRepository.findByRoleAndType(r.getId(), "Menu"));
                pages.addAll(accessRepository.findByRoleAndType(r.getId(), "Page"));
                actions.addAll(accessRepository.findByRoleAndType(r.getId(), "Action"));
            }


            List<String> names = new ArrayList<String>();
            for (Access m : menus) {
                if (!names.contains(m.getCode())) {
                    names.add(m.getCode());
                    AccessDto mdto = refarctorMenu(m, pages, actions);
                    res.add(mdto);
                }
            }
        }
            return res;
    }
    public Access create(Access access) {
        access=accessRepository.save(access);
        List<Access> accList = new ArrayList<>();

        if (access !=null && access.getSubAccess()!=null) {
            for (Access ac : access.getSubAccess()) {
                ac.setParent(access);
                Access  acc= create(ac);
                accList.add(acc);
            }
        }
        //access.setSubAccess(accList);
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

    public List<Access> findByRoleAndType( Long roleId, String type){
        return accessRepository.findByRoleAndType(roleId,type);
    }

    public AccessDto refarctorMenu(Access m,List<Access> pages, List<Access> actions){
        AccessDto mDto=new AccessDto(m.getId(),m.getName(),m.getCode(),m.getType(),m.getPath());


            mDto.setSubAccess(new ArrayList<AccessDto>());

            for (Access p : pages) {
                if (p.getParent() != null && p.getParent().getId().equals(m.getId())) {
                    AccessDto pDto = new AccessDto(p.getId(), p.getName(), p.getCode(), p.getType(), p.getPath());
                    if (mDto.getSubAccess() != null && !mDto.getSubAccess().contains(pDto)) {
                        List<AccessDto> lstP = mDto.getSubAccess();
                        pDto.setSubAccess(new ArrayList<AccessDto>());
                        for (Access a : actions) {


                            if (a.getParent() != null && a.getParent().getId().equals(p.getId())) {
                                System.out.println("action" + a + " " + a.getParent());
                                AccessDto aDto = new AccessDto(a.getId(), a.getName(), a.getCode(), a.getType(), a.getPath());
                                if (pDto.getSubAccess() != null && !pDto.getSubAccess().contains(aDto)) {
                                    List<AccessDto> lsta = pDto.getSubAccess();
                                    lsta.add(aDto);
                                    pDto.setSubAccess(lsta);
                                }
                            }
                        }

                        lstP.add(pDto);
                        mDto.setSubAccess(lstP);
                    }

                }
            }
            return mDto;
    }

}
