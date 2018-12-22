package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.IpAccessRuleJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IpAccessRuleRepository extends JpaRepository<IpAccessRuleJPA, Integer> {
}
