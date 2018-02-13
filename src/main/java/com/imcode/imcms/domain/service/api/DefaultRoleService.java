package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.domain.service.RoleService;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.RoleJPA;
import com.imcode.imcms.persistence.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
class DefaultRoleService implements RoleService {

    private final RoleRepository roleRepository;
    private Function<Role, Role> roleSaver;

    DefaultRoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        this.roleSaver = ((Function<Role, RoleJPA>) RoleJPA::new).andThen(roleRepository::save).andThen(RoleDTO::new);
    }

    @Override
    public Role getById(int id) {
        return new RoleDTO(roleRepository.findOne(id));
    }

    @Override
    public List<Role> getAll() {
        return roleRepository.findAll().stream()
                .map(RoleDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Saves role by it's data holder.
     *
     * @return saved Role in DTO form.
     */
    @Override
    public Role save(Role saveMe) {
        return roleSaver.apply(saveMe);
    }

}
