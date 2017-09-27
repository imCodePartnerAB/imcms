package com.imcode.imcms.service;

import com.imcode.imcms.mapping.dto.LoopDTO;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.LoopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.function.Function;

@Service
@Transactional(propagation = Propagation.SUPPORTS)
public class LoopService {

    private final LoopRepository loopRepository;
    private final Function<Loop, LoopDTO> loopToDtoMapper;
    private final VersionService versionService;

    @Autowired
    public LoopService(LoopRepository loopRepository, Function<Loop, LoopDTO> loopToDtoMapper,
                       VersionService versionService) {
        this.loopRepository = loopRepository;
        this.loopToDtoMapper = loopToDtoMapper;
        this.versionService = versionService;
    }

    public LoopDTO getLoop(int loopId, int docId) {
        final Version documentWorkingVersion = versionService.getDocumentWorkingVersion(docId);
        return loopToDtoMapper.apply(loopRepository.findByVersionAndNo(documentWorkingVersion, loopId));
    }

    public void saveLoop(LoopDTO loopDTO) {
        loopDTO.setEntries(Collections.emptyList());
    }
}
