package com.insy2s.keycloakauth.service.impl;

import com.insy2s.keycloakauth.dto.AccessDto;
import com.insy2s.keycloakauth.model.Access;
import com.insy2s.keycloakauth.model.Role;
import com.insy2s.keycloakauth.model.User;
import com.insy2s.keycloakauth.repository.AccessRepository;
import com.insy2s.keycloakauth.repository.RoleRepository;
import com.insy2s.keycloakauth.repository.UserRepository;
import com.insy2s.keycloakauth.service.IAccessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link IAccessService}.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AccessService implements IAccessService {

    private final AccessRepository accessRepository;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;

    public List<Access> getAllAccess() {
        log.debug("SERVICE request to get all Access");
        return accessRepository.findAll();
    }

    public List<AccessDto> getAllAccessDto() {
        List<Access> menus = accessRepository.findByType("Menu");
        List<Access> actions = accessRepository.findByType("Action");
        List<AccessDto> res = new ArrayList<>();
        for (Access m : menus) {
            AccessDto mdto = refactorMenu(m, accessRepository.findByParentId(m.getId()), actions);
            res.add(mdto);
        }
        return res;
    }

    public List<AccessDto> findByRole(Long roleId) {
        List<Access> menus = accessRepository.findByRoleAndType(roleId, "Menu");
        List<Access> pages = accessRepository.findByRoleAndType(roleId, "Page");
        List<Access> actions = accessRepository.findByRoleAndType(roleId, "Action");
        List<AccessDto> res = new ArrayList<>();
        for (Access m : menus) {
            AccessDto mdto = refactorMenu(m, pages, actions);
            res.add(mdto);
        }
        return res;
    }

    public List<AccessDto> findByUser(String userId) {
        List<Access> menus = new ArrayList<>();
        List<Access> pages = new ArrayList<>();
        List<Access> actions = new ArrayList<>();
        List<AccessDto> res = new ArrayList<>();
        User user = userRepo.findById(userId).orElse(null);
        if (user != null) {
            for (Role r : user.getRoles()) {
                menus.addAll(accessRepository.findByRoleAndType(r.getId(), "Menu"));
                pages.addAll(accessRepository.findByRoleAndType(r.getId(), "Page"));
                actions.addAll(accessRepository.findByRoleAndType(r.getId(), "Action"));
            }
            List<String> names = new ArrayList<>();
            for (Access m : menus) {
                if (!names.contains(m.getCode())) {
                    names.add(m.getCode());
                    AccessDto mdto = refactorMenu(m, pages, actions);
                    res.add(mdto);
                }
            }
        }
        return res;
    }

    public List<String> refactorByUserAndType(String userId, String type) {
        List<Access> access = new ArrayList<Access>();
        List<String> names = new ArrayList<String>();
        User user = userRepo.findById(userId).orElse(null);
        if (user != null) {
            for (Role r : user.getRoles()) {
                access.addAll(accessRepository.findByRoleAndType(r.getId(), "type"));
            }
            for (Access m : access) {
                if (!names.contains(m.getCode())) {
                    names.add(m.getCode());
                }
            }
        }
        return names;
    }

    public List<String> refactorAccess(List<Access> access) {
        List<String> res = new ArrayList<String>();
        for (Access a : access) {
            if (!res.contains(a.getCode())) {
                res.add(a.getCode());
            }
        }
        return res;
    }

    public Access create(Access access) {
        access.setId(null);
        access = accessRepository.save(access);
        if (!access.getSubAccess().isEmpty()) {
            for (Access accessChildren : access.getSubAccess()) {
                accessChildren.setParent(access);
                create(accessChildren);
            }
        }
        return accessRepository.save(access);
    }

    public void deleteAccess(Long id) {
        accessRepository.deleteById(id);
    }

    public Access findById(Long id) {
        return accessRepository.findById(id).orElse(null);
    }

    public List<Access> findByParentId(Long id) {
        return accessRepository.findByParentId(id);
    }

    public List<Access> findByType(String type) {
        return accessRepository.findByType(type);
    }

    public List<Access> findByRoleAndType(Long roleId, String type) {
        return accessRepository.findByRoleAndType(roleId, type);
    }

    //TODO: should unit test this method before refactoring it
    public AccessDto refactorMenu(Access m, List<Access> pages, List<Access> actions) {
        AccessDto mDto = new AccessDto(m.getId(), m.getName(), m.getCode(), m.getType(), m.getPath());

        for (Access p : pages) {
            if (p.getParent() != null && p.getParent().getId().equals(m.getId())) {
                AccessDto pDto = new AccessDto(p.getId(), p.getName(), p.getCode(), p.getType(), p.getPath());
                if (!mDto.getSubAccess().contains(pDto)) {
                    List<AccessDto> lstP = mDto.getSubAccess();
                    for (Access a : actions) {
                        if (a.getParent() != null && a.getParent().getId().equals(p.getId())) {
                            AccessDto aDto = new AccessDto(a.getId(), a.getName(), a.getCode(), a.getType(), a.getPath());
                            if (!pDto.getSubAccess().contains(aDto)) {
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

    public Access addAccessToRole(Long roleId, Long accessId) {
        Role role = roleRepo.findById(roleId).orElse(null);
        Access access = null;
        if (role != null) {
            access = accessRepository.findById(accessId).orElse(null);
            if (access != null) {
                List<Access> lst = role.getAccessList();
                if (!lst.contains(access)) {
                    lst.add(access);
                    role.setAccessList(lst);
                    roleRepo.save(role);
                }
            }
        }
        return access;
    }

    public Access removeAccessRole(Long roleId, Long accessId) {
        Role role = roleRepo.findById(roleId).orElse(null);
        Access access = null;
        if (role != null) {
            access = accessRepository.findById(accessId).orElse(null);
            if (access != null) {
                List<Access> lst = role.getAccessList();
                lst.remove(access);
                role.getAccessList().remove(access);
                roleRepo.save(role);
            }
        }
        return access;
    }

}
