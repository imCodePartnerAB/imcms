package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.domain.dto.LoopEntryRefDTO;
import com.imcode.imcms.persistence.entity.Version;

import java.util.Collection;
import java.util.function.Function;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 07.12.17.
 */
public interface LoopService {
    LoopDTO getLoop(int loopIndex, int docId);

    LoopDTO getLoopPublic(int loopIndex, int docId);

    LoopDTO getLoop(int loopIndex, int docId, Function<Integer, Version> versionGetter);

    void saveLoop(LoopDTO loopDTO);

    LoopEntryRefDTO buildLoopEntryRef(int loopIndex, int entryIndex);

    Collection<LoopDTO> findAllByVersion(Version version);
}
