package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.domain.service.RoleService;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.RoleJPA;
import com.imcode.imcms.persistence.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
class DefaultRoleService implements RoleService {

    private final RoleRepository roleRepository;

    DefaultRoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role getById(int id) {
	    return roleRepository.findById(id).map(RoleDTO::new).orElse(null);
    }

	@Override
	public Role getByName(String name) {
		return roleRepository.findByName(name);
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
        return new RoleDTO(roleRepository.save(new RoleJPA(saveMe)));
    }

    @Override
    public Role saveNewRole(Role role) {
        role.setId(null);
        return save(role);
    }

    @Override
    public void delete(int roleID) {
	    roleRepository.deleteById(roleID);
    }

}
