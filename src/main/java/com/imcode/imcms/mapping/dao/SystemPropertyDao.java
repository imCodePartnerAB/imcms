package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.SystemProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemPropertyDao extends JpaRepository<SystemProperty, Integer> {

    SystemProperty findByName(String name);
}





