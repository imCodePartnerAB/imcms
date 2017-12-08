package com.imcode.imcms.domain.service;

import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.VersionedContentRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface VersionedContentService<T> {
    Set<T> getByVersion(Version version);

    void createVersionedContent(Version workingVersion, Version newVersion);
}
