package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.domain.service.RoleService;
import com.imcode.imcms.persistence.entity.RoleJPA;
import com.imcode.imcms.persistence.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DefaultRoleService implements RoleService {

    private final RoleRepository roleRepository;

    DefaultRoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleDTO getById(int id) {
        return new RoleDTO(roleRepository.findOne(id));
    }

    @Override
    public List<RoleDTO> getAll() {
        return roleRepository.findAll().stream()
                .map(RoleDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Saves role by it's data holder.
     * <b>Note that {@link RoleDTO#permission} is * omitted while save and in
     * return value.</b>
     *
     * @return saved Role in DTO form.
     */
    @Override
    public RoleDTO save(RoleDTO saveMe) {
        return ((Function<RoleDTO, RoleJPA>) RoleJPA::new).andThen(roleRepository::save)
                .andThen(RoleDTO::new)
                .apply(saveMe);
    }

}
