package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.PhoneJPA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhoneRepository extends JpaRepository<PhoneJPA, Integer> {

    void deleteByUserId(int userId);

    List<PhoneJPA> findByUserId(int userId);
}
