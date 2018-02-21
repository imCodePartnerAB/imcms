package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.ProfileJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileJPA, Integer> {
}
