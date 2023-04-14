package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Loop;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.Version;

import java.util.Set;

public interface LoopService extends VersionedContentService, DeleterByDocumentId {

    Set<Loop> getByDocId(int docId);

    Loop getLoop(int loopIndex, int docId);

    Loop getLoop(int loopIndex, int docId, int versionNo);

    Loop getLoopPublic(int loopIndex, int docId);

    Loop getLoop(int loopIndex, int docId, Version version);

    void saveLoop(Loop loopDTO);

    LoopEntryRef buildLoopEntryRef(int loopIndex, int entryIndex);

    Set<Loop> getByVersion(Version version);

    void createLoopEntryIfNotExists(Version version, LoopEntryRefJPA entryRef);

}
