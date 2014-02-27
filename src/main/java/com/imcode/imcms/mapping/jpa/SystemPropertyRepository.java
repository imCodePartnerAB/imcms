package com.imcode.imcms.mapping.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemPropertyRepository extends JpaRepository<SystemProperty, Integer> {

    SystemProperty findByName(String name);
}





