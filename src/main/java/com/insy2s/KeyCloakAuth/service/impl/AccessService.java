package com.insy2s.keycloakauth.service.impl;

import com.insy2s.keycloakauth.dto.AccessDto;
import com.insy2s.keycloakauth.dto.CreateAccess;
import com.insy2s.keycloakauth.dto.mapper.IAccessMapper;
import com.insy2s.keycloakauth.error.exception.NotFoundException;
import com.insy2s.keycloakauth.model.Access;
import com.insy2s.keycloakauth.model.Role;
import com.insy2s.keycloakauth.model.User;
import com.insy2s.keycloakauth.repository.IAccessRepository;
import com.insy2s.keycloakauth.repository.IRoleRepository;
import com.insy2s.keycloakauth.repository.IUserRepository;
import com.insy2s.keycloakauth.service.IAccessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        return accessMapper.toDto(menus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<AccessDto> findAllMenusByUserId(String userId) {
        log.debug("SERVICE to find all Access DTO by user id {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (user.getRoles().isEmpty()) {
            return new ArrayList<>();
        }
        List<Access> menus = accessRepository.findByUserAndType(userId, MENU);
        return accessMapper.toDto(menus);
    }

    // TODO: see the usage of this method to refactor it
    @Override
    public List<String> refactorByUserAndType(String userId, String type) {
        List<Access> access = new ArrayList<>();
        List<String> names = new ArrayList<>();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (user.getRoles().isEmpty()) {
            return new ArrayList<>();
        }
        for (Role r : user.getRoles()) {
            access.addAll(accessRepository.findAllByRoleAndType(r.getId(), "type"));
        }
        for (Access m : access) {
            if (!names.contains(m.getCode())) {
                names.add(m.getCode());
            }
        }
        return names;
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
        Access access =
                accessRepository.findById(id).orElseThrow(() -> new NotFoundException("Access " + id + " not found"));
        return accessMapper.toDto(access);
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

}
