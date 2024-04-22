package com.insy2s.mskeycloak.service.impl;

import com.insy2s.mskeycloak.dto.AccessDto;
import com.insy2s.mskeycloak.dto.CreateAccess;
import com.insy2s.mskeycloak.dto.mapper.IAccessMapper;
import com.insy2s.mskeycloak.error.exception.NotFoundException;
import com.insy2s.mskeycloak.model.Access;
import com.insy2s.mskeycloak.model.Role;
import com.insy2s.mskeycloak.model.User;
import com.insy2s.mskeycloak.repository.IAccessRepository;
import com.insy2s.mskeycloak.repository.IRoleRepository;
import com.insy2s.mskeycloak.repository.IUserRepository;
import com.insy2s.mskeycloak.service.IAccessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of {@link IAccessService}.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AccessService implements IAccessService {

    private static final String MENU = "Menu";
    private final IAccessRepository accessRepository;
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final IAccessMapper accessMapper;

    /**
     * Filter the sub-access of an access by the roles of the user.
     * Use recursive to filter the sub-access of the sub-access. etc.
     * Meaning if a Role has Access Menu1 and Menu1 has Access Page1,
     * but the Role doesn't have Access Page1,
     * then Page1 should not be in the result.
     *
     * @param accesses the list of Access to filter
     * @param roles    the list of Role to filter
     * @return the list of Access filtered
     */
    private List<Access> filterSubAccessOfAccessByRoles(List<Access> accesses, List<Role> roles) {
        List<Access> res = accesses;

        res = res.stream()
                .map(access -> {
                    List<Access> subAccesses = access.getSubAccess()
                            .stream()
                            .filter(subAccess -> roles.stream()
                                    .anyMatch(role -> role.getAccessList().contains(subAccess)))
                            .toList();
                    subAccesses = filterSubAccessOfAccessByRoles(subAccesses, roles);
                    access.setSubAccess(subAccesses);
                    return access;
                })
                .toList();

        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Access> findAllWithoutChildren() {
        log.debug("SERVICE to find all Access");
        return accessRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<AccessDto> findAllMenusAndChildren() {
        log.debug("SERVICE to find all Access DTO");
        List<Access> menus = accessRepository.findByType(MENU);
        return accessMapper.toDto(menus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<AccessDto> findAllMenusByRole(Long roleId) {
        log.debug("SERVICE to find all Access DTO by role");
        List<Access> menus = accessRepository.findAllByRoleAndType(roleId, MENU);
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new NotFoundException("The role is not found"));
        menus = filterSubAccessOfAccessByRoles(menus, List.of(role));
        return accessMapper.toDto(menus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<AccessDto> findAllMenusByUserId(String userId) {
        log.debug("SERVICE to find all Access DTO by user id {}", userId);
        User user = userRepository.findById(userId).orElse(null);
        List<Access> menus=null;
        List<Role> roles = null;
        if(user==null){
            menus=accessRepository.findByType(MENU);
            roles=new ArrayList<Role>();
            roles.add(roleRepository.findByName("ADMIN").orElse(null));
        }else
        if (user.getRoles().isEmpty()) {
            return new ArrayList<>();
        }
        else
        {
            menus = accessRepository.findByUserAndType(userId, MENU);

            roles = user.getRoles().stream().toList();
            menus = filterSubAccessOfAccessByRoles(menus, roles);
        }
        return accessMapper.toDto(menus);
    }

    @Override
    public List<String> findAllAccessCodeOfUserIdAndByType(String userId, String type) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (user.getRoles().isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> roleIds = user.getRoles().stream().map(Role::getId).toList();
        Set<Access> accessSet = accessRepository.findAllByRolesInAndType(roleIds, type);
        return accessSet.stream().map(Access::getCode).toList();
    }

    //TODO: see the usage of this method to refactor it
    @Override
    public List<String> refactorAccess(List<Access> access) {
        List<String> res = new ArrayList<>();
        for (Access a : access) {
            if (!res.contains(a.getCode())) {
                res.add(a.getCode());
            }
        }
        return res;
    }

    /**
     * {@inheritDoc}
     */
    //TODO: can children be created before parent? should they be created in the same time? both possible?
    @Override
    public AccessDto create(CreateAccess access) {
        log.debug("SERVICE to create Access : {}", access);
        Access entity = accessMapper.toEntity(access);
        entity.setId(null);

        if (access.parent() != null) {
            Optional<Access> parent = accessRepository.findById(access.parent().getId());
            if (parent.isEmpty()) {
                throw new NotFoundException("Access parent not found");
            }
            entity.setParent(parent.get());
        }

        entity = accessRepository.save(entity);
        return accessMapper.toDto(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) {
        log.debug("SERVICE to delete Access : {}", id);
        Optional<Access> access = accessRepository.findById(id);
        if (access.isEmpty()) {
            throw new NotFoundException("Access " + id + " not found");
        }
        accessRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public AccessDto findById(Long id) {
        log.debug("SERVICE to find Access by id : {}", id);
        Access access = accessRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Access " + id + " not found"));
        return accessMapper.toDto(access);
    }

    @Override
    @Transactional(readOnly = true)
    public Access findByIdForUpdate(Long id){
        log.debug("SERVICE to find Access by id : {}", id);
        Access access = accessRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Access " + id + " not found"));
        return access;
    }

    @Override
    public Access update(Long id, Access access) {

             access.setId(id);
            Access updateAccess = accessRepository.save( access);
            return updateAccess;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<AccessDto> findByParentId(Long id) {
        log.debug("SERVICE to find all Access by parent id : {}", id);
        List<Access> accesses = accessRepository.findByParentId(id);
        return accessMapper.toDto(accesses);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<AccessDto> findByType(String type) {
        log.debug("SERVICE to find all Access by type : {}", type);
        List<Access> accesses = accessRepository.findByType(type);
        return accessMapper.toDto(accesses);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<AccessDto> findByRoleAndType(Long roleId, String type) {
        log.debug("SERVICE to find all Access by role id : {} and type : {}", roleId, type);
        List<Access> accesses = accessRepository.findAllByRoleAndType(roleId, type);
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new NotFoundException("The role is not found"));
        accesses = filterSubAccessOfAccessByRoles(accesses, List.of(role));
        return accessMapper.toDto(accesses);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Access addAccessToRole(Long roleId, Long accessId) {
        log.debug("SERVICE to add Access to role id : {} and access id : {}", roleId, accessId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Role not found"));
        Access access = accessRepository.findById(accessId)
                .orElseThrow(() -> new NotFoundException("Access not found"));

        if (!role.getAccessList().contains(access)) {
            role.getAccessList().add(access);
            roleRepository.save(role);
        }

        return access;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Access removeAccessFromRole(Long roleId, Long accessId) {
        log.debug("SERVICE to remove Access from role id : {} and access id : {}", roleId, accessId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Role not found"));
        Access access = accessRepository.findById(accessId)
                .orElseThrow(() -> new NotFoundException("Access not found"));

        role.getAccessList().remove(access);
        roleRepository.save(role);
        return access;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public AccessDto findByCode(String code) {
        log.debug("SERVICE to find Access by id : {}", code);
        Access access = accessRepository.findByCode(code).orElse(null);
        return accessMapper.toDto(access);
    }

}
