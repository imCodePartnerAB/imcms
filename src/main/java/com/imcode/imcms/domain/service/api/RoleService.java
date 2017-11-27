package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.persistence.entity.RoleJPA;
import com.imcode.imcms.persistence.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final Function<RoleDTO, RoleJPA> roleDtoToRole;
    private final Function<RoleJPA, RoleDTO> roleToRoleDTO;

    RoleService(RoleRepository roleRepository,
                Function<RoleDTO, RoleJPA> roleDtoToRole,
                Function<RoleJPA, RoleDTO> roleToRoleDTO) {

        this.roleRepository = roleRepository;
        this.roleDtoToRole = roleDtoToRole;
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

    /**
     * Saves role by it's data holder.
     * <b>Note that {@link RoleDTO#permission} is * omitted while save and in
     * return value.</b>
     *
     * @return saved Role in DTO form.
     */
    public RoleDTO save(RoleDTO saveMe) {
        return roleDtoToRole.andThen(roleRepository::save)
                .andThen(roleToRoleDTO)
                .apply(saveMe);
    }

}
