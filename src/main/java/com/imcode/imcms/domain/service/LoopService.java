package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Loop;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.persistence.entity.Version;

import java.util.Collection;
import java.util.function.Function;

public interface LoopService extends VersionedContentService<Loop>, DeleterByDocumentId {

    Loop getLoop(int loopIndex, int docId);

    Loop getLoopPublic(int loopIndex, int docId);

    Loop getLoop(int loopIndex, int docId, Function<Integer, Version> versionGetter);

    void saveLoop(Loop loopDTO);

    LoopEntryRef buildLoopEntryRef(int loopIndex, int entryIndex);

    Collection<Loop> findAllByVersion(Version version);

}
