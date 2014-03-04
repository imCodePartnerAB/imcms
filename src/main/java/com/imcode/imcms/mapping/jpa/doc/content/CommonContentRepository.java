package com.imcode.imcms.mapping.jpa.doc.content;

import com.imcode.imcms.mapping.jpa.doc.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonContentRepository extends JpaRepository<CommonContent, Integer>, CommonContentRepositoryCustom {

    List<CommonContent> findByDocId(int docId);

    CommonContent findByDocIdAndLanguage(int docId, Language language);

    CommonContent findByDocIdAndLanguageCode(int docId, String code);
}

