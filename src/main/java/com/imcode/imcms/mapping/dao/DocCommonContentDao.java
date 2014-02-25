package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.DocCommonContent;
import com.imcode.imcms.mapping.orm.DocLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface DocCommonContentDao extends JpaRepository<DocCommonContent, Integer> {

    List<DocCommonContent> findByDocId(int docId);

    DocCommonContent findByDocIdAndDocLanguage(int docId, DocLanguage language);

    DocCommonContent findByDocIdAndDocLanguageCode(int docId, String code);
}
