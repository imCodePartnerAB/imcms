package com.imcode.imcms.domain.service;

import com.imcode.imcms.persistence.entity.Version;

import java.util.Set;

public interface VersionedContentService<T> {
    Set<T> getByVersion(Version version);

    void createVersionedContent(Version workingVersion, Version newVersion);
}
