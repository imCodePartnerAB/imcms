package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.DocVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface DocVersionedMultiContentRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    List<T> findByDocVersion(DocVersion version);

    T findByDocVersionAndNo(DocVersion version, int no);

//    T getByDocIdAndDocVersionNo(int docId, int versionNo);
}
