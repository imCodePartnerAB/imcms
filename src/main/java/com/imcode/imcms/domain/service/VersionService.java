package com.imcode.imcms.domain.service;

import com.imcode.imcms.persistence.entity.Version;

import java.util.List;
import java.util.function.Function;

public interface VersionService extends DeleterByDocumentId {

    Version getDocumentWorkingVersion(int docId);

    Version getLatestVersion(int docId);

    Version getVersion(int docId, Function<Integer, Version> versionReceiver);

    Version create(int docId);

    Version create(int docId, int userId);

    Version findByDocIdAndNo(int docId, int no);

    List<Version> findByDocId(int docId);

    Version findDefault(int docId);

    Version findWorking(int docId);

}
