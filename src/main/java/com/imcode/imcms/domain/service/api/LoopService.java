package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.domain.dto.LoopEntryRefDTO;
import com.imcode.imcms.domain.service.core.VersionService;
import com.imcode.imcms.persistence.entity.LoopJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.LoopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.SUPPORTS)
public class LoopService {

    private final LoopRepository loopRepository;
    private final VersionService versionService;

    @Autowired
    LoopService(LoopRepository loopRepository, VersionService versionService) {
        this.loopRepository = loopRepository;
        this.versionService = versionService;
    }

    public LoopDTO getLoop(int loopIndex, int docId) {
        return getLoop(loopIndex, docId, versionService::getDocumentWorkingVersion);
    }


    public LoopDTO getLoopPublic(int loopIndex, int docId) {
        return getLoop(loopIndex, docId, versionService::getLatestVersion);

    }

    public LoopDTO getLoop(int loopIndex, int docId, Function<Integer, Version> versionGetter) {
        final Version documentWorkingVersion = versionGetter.apply(docId);
        final LoopJPA loop = loopRepository.findByVersionAndIndex(documentWorkingVersion, loopIndex);

        return Optional.ofNullable(loop)
                .map(loop1 -> new LoopDTO(loop1, loop1.getVersion()))
                .orElse(LoopDTO.empty(docId, loopIndex));
    }

    public void saveLoop(LoopDTO loopDTO) {
        final Version documentWorkingVersion = versionService.getDocumentWorkingVersion(loopDTO.getDocId());
        final LoopJPA loopForSave = new LoopJPA(loopDTO, documentWorkingVersion);
        final Integer loopId = getLoopId(documentWorkingVersion, loopDTO.getIndex());

        loopForSave.setId(loopId);
        loopRepository.save(loopForSave);
    }

    private Integer getLoopId(Version version, Integer loopIndex) {
        final LoopJPA loop = loopRepository.findByVersionAndIndex(version, loopIndex);

        if (loop == null) {
            return null;
        }

        return loop.getId();
    }

    public LoopEntryRefDTO buildLoopEntryRef(int loopIndex, int entryIndex) {
        return new LoopEntryRefDTO(loopIndex, entryIndex);
    }

    public Collection<LoopDTO> findAllByVersion(Version version) {
        return loopRepository.findByVersion(version).stream()
                .map(loop1 -> new LoopDTO(loop1, loop1.getVersion()))
                .collect(Collectors.toSet());
    }
}
