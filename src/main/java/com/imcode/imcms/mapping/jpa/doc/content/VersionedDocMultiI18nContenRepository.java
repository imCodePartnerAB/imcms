package com.imcode.imcms.mapping.jpa.doc.content;

import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.Language;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface VersionedDocMultiI18nContenRepository<T, ID extends Serializable> extends VersionedDocMultiContentRepository<T, ID> {

    List<T> findByDocVersionAndDocLanguage(Version version, Language language);

    T findByDocVersionAndLanguageAndNo(Version version, Language language, int no);

    //T getByDocVersionAndLanguage(DocVersion version, DocLanguage language);
}
