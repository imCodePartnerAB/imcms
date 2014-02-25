package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.DocLanguage;
import com.imcode.imcms.mapping.orm.DocVersion;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface DocVersionedMultiI18nContenRepository<T, ID extends Serializable> extends DocVersionedMultiContentRepository<T, ID> {

    List<T> findByDocVersionAndDocLanguage(DocVersion version, DocLanguage language);

    T findByDocVersionAndLanguageAndNo(DocVersion version, DocLanguage language, int no);

    //T getByDocVersionAndLanguage(DocVersion version, DocLanguage language);
}
