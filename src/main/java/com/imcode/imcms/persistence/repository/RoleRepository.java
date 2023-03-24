package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.RoleJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleJPA, Integer> {
	Role findByName(String name);
}
