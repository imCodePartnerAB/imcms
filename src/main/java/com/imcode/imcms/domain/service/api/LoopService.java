package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.domain.dto.LoopEntryRefDTO;
import com.imcode.imcms.domain.service.core.VersionService;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.persistence.entity.Loop;
import com.imcode.imcms.persistence.repository.LoopRepository;
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

    public LoopDTO getLoop(int loopIndex, int docId) {
        final Version documentWorkingVersion = versionService.getDocumentWorkingVersion(docId);
        final Loop loop = loopRepository.findByVersionAndIndex(documentWorkingVersion, loopIndex);

        return Optional.ofNullable(loop)
                .map(loopToDtoMapper)
                .orElse(LoopDTO.empty(docId, loopIndex));
    }

    public void saveLoop(LoopDTO loopDTO) {
        final Version documentWorkingVersion = versionService.getDocumentWorkingVersion(loopDTO.getDocId());
        final Loop loopForSave = loopDtoToLoop.apply(loopDTO, documentWorkingVersion);
        final Integer loopId = getLoopId(documentWorkingVersion, loopDTO.getIndex());

        loopForSave.setId(loopId);
        loopRepository.save(loopForSave);
    }

    private Integer getLoopId(Version version, Integer loopIndex) {
        final Loop loop = loopRepository.findByVersionAndIndex(version, loopIndex);

        if (loop == null) {
            return null;
        }

        return loop.getId();
    }

    public LoopEntryRefDTO buildLoopEntryRef(int loopIndex, int entryIndex) {
        return new LoopEntryRefDTO(loopIndex, entryIndex);
    }
}
