package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {
}
