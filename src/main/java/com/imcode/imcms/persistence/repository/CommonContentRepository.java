package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.CommonContent;
import com.imcode.imcms.persistence.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonContentRepository extends JpaRepository<CommonContent, Integer> {

    List<CommonContent> findByDocIdAndVersionNo(int docId, int versionNo);

    CommonContent findByDocIdAndVersionNoAndLanguage(int docId, int versionNo, Language language);

    CommonContent findByDocIdAndVersionNoAndLanguageCode(int docId, int versionNo, String code);
}
