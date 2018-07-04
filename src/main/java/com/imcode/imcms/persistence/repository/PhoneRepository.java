package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.PhoneJPA;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneRepository extends JpaRepository<PhoneJPA, Integer> {
    void deleteByUserId(int userId);
}
