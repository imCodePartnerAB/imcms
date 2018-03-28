package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.LanguageJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<LanguageJPA, Integer> {

    LanguageJPA findByCode(String code);

}
