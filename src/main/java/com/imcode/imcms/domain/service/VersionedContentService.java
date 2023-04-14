package com.imcode.imcms.domain.service;

import com.imcode.imcms.persistence.entity.Version;

public interface VersionedContentService {
    void createVersionedContent(Version workingVersion, Version newVersion);

    void setAsWorkingVersion(Version version);
}
