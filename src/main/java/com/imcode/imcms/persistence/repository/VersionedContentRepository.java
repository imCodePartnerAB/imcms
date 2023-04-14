package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.Version;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface VersionedContentRepository<T> {

    List<T> findByVersion(Version version);

    void deleteByVersion(Version version);

}
