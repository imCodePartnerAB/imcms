package com.imcode.imcms.mapping.jpa.doc.content;

import com.imcode.imcms.mapping.jpa.doc.DocVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface VersionedDocMultiContentRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    List<T> findByDocVersion(DocVersion docVersion);

    T findByDocVersionAndNo(DocVersion docVersion, int no);

//    T getByDocIdAndDocVersionNo(int docId, int versionNo);
}
