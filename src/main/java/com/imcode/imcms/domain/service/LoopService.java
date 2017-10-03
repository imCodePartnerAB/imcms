package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.domain.service.exception.DocumentNotExistException;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.LoopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
@Transactional(propagation = Propagation.SUPPORTS)
public class LoopService {

    private final LoopRepository loopRepository;
    private final Function<Loop, LoopDTO> loopToDtoMapper;
    private final BiFunction<LoopDTO, Version, Loop> loopDtoToLoop;
    private final VersionService versionService;

    @Autowired
    public LoopService(LoopRepository loopRepository, Function<Loop, LoopDTO> loopToDtoMapper,
                       BiFunction<LoopDTO, Version, Loop> loopDtoToLoop, VersionService versionService) {
        this.loopRepository = loopRepository;
        this.loopToDtoMapper = loopToDtoMapper;
        this.loopDtoToLoop = loopDtoToLoop;
        this.versionService = versionService;
    }

    public LoopDTO getLoop(int loopIndex, int docId) throws DocumentNotExistException {
        final Version documentWorkingVersion = versionService.getDocumentWorkingVersion(docId);
        final Loop loop = getOrCreateLoop(documentWorkingVersion, loopIndex);
        return loopToDtoMapper.apply(loop);
    }

    private Loop getOrCreateLoop(Version documentWorkingVersion, int loopIndex) {
        return Optional.ofNullable(loopRepository.findByVersionAndNo(documentWorkingVersion, loopIndex))
                .orElseGet(() -> createLoop(documentWorkingVersion, loopIndex));
    }

    public void saveLoop(LoopDTO loopDTO) throws DocumentNotExistException {
        final Version documentWorkingVersion = versionService.getDocumentWorkingVersion(loopDTO.getDocId());
        final Loop loopForSave = loopDtoToLoop.apply(loopDTO, documentWorkingVersion);
        final Loop prevLoop = getOrCreateLoop(documentWorkingVersion, loopDTO.getLoopIndex());

        loopForSave.setId(prevLoop.getId());
        loopRepository.save(loopForSave);
    }

    private Loop createLoop(Version version, Integer loopIndex) {
        final Loop loop = Loop.emptyLoop(version, loopIndex);
        return loopRepository.save(loop);
    }
}
