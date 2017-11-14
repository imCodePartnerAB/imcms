package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.persistence.entity.Role;
import com.imcode.imcms.persistence.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final Function<Role, RoleDTO> roleToRoleDTO;

    public RoleService(RoleRepository roleRepository,
                       Function<Role, RoleDTO> roleToRoleDTO) {
        this.roleRepository = roleRepository;
        this.roleToRoleDTO = roleToRoleDTO;
    }

    public RoleDTO getById(int id) {
        return roleToRoleDTO.apply(roleRepository.findOne(id));
    }

    public List<RoleDTO> getAll() {
        return roleRepository.findAll().stream()
                .map(roleToRoleDTO)
                .collect(Collectors.toList());
    }

}
