package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.model.UserProperty;
import com.imcode.imcms.persistence.entity.UserPropertyJPA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPropertyRepository extends JpaRepository<UserPropertyJPA, Integer>{
    List<UserProperty> findByUserId(Integer userId);

    UserProperty findByUserIdAndKeyName(Integer userId, String keyName);

    List<UserProperty> findByUserIdAndValue(Integer userId, String value);
}