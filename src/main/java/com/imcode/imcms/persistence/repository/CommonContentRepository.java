package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.CommonContentJPA;
import com.imcode.imcms.persistence.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonContentRepository extends JpaRepository<CommonContentJPA, Integer> {

    List<CommonContentJPA> findByDocIdAndVersionNo(int docId, int versionNo);

    CommonContentJPA findByDocIdAndVersionNoAndLanguage(int docId, int versionNo, Language language);

    CommonContentJPA findByDocIdAndVersionNoAndLanguageCode(int docId, int versionNo, String code);
}
