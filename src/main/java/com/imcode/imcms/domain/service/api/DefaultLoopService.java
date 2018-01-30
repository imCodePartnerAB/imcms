package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.domain.dto.LoopEntryRefDTO;
import com.imcode.imcms.domain.service.AbstractVersionedContentService;
import com.imcode.imcms.domain.service.LoopService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.Loop;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.persistence.entity.LoopJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.LoopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Transactional
@Service("loopService")
class DefaultLoopService extends AbstractVersionedContentService<LoopJPA, Loop, LoopRepository> implements LoopService {

    private final VersionService versionService;

    @Autowired
    DefaultLoopService(LoopRepository loopRepository, VersionService versionService) {
        super(loopRepository);
        this.versionService = versionService;
    }

    @Override
    public Loop getLoop(int loopIndex, int docId) {
        return getLoop(loopIndex, docId, versionService::getDocumentWorkingVersion);
    }

    @Override
    public Loop getLoopPublic(int loopIndex, int docId) {
        return getLoop(loopIndex, docId, versionService::getLatestVersion);
    }

    @Override
    public Loop getLoop(int loopIndex, int docId, Function<Integer, Version> versionGetter) {
        final Version documentWorkingVersion = versionGetter.apply(docId);
        final LoopJPA loop = repository.findByVersionAndIndex(documentWorkingVersion, loopIndex);

        return Optional.ofNullable(loop)
                .map(LoopDTO::new)
                .orElse(LoopDTO.empty(docId, loopIndex));
    }

    @Override
    public void saveLoop(Loop loopDTO) {
        final Integer docId = loopDTO.getDocId();
        final Version documentWorkingVersion = versionService.getDocumentWorkingVersion(docId);
        final LoopJPA loopForSave = new LoopJPA(loopDTO, documentWorkingVersion);
        final Integer loopId = getLoopId(documentWorkingVersion, loopDTO.getIndex());

        loopForSave.setId(loopId);
        repository.save(loopForSave);
        super.updateWorkingVersion(docId);
    }

    @Override
    public LoopEntryRef buildLoopEntryRef(int loopIndex, int entryIndex) {
        return new LoopEntryRefDTO(loopIndex, entryIndex);
    }

    @Override
    public void deleteByDocId(Integer docIdToDelete) {
        repository.delete(repository.findByDocId(docIdToDelete));
        repository.flush();
    }

    @Override
    protected Loop mapToDTO(LoopJPA jpa) {
        return new LoopDTO(jpa);
    }

    @Override
    protected LoopJPA mapToJpaWithoutId(Loop dto, Version version) {
        return new LoopJPA(dto, version);
    }

    @Override
    public Set<Loop> getByVersion(Version version) {
        return super.getByVersion(version); // note: to make method transactional
    }

    @Override
    public void createVersionedContent(Version workingVersion, Version newVersion) {
        super.createVersionedContent(workingVersion, newVersion); // note: to make method transactional
    }

    private Integer getLoopId(Version version, Integer loopIndex) {
        final LoopJPA loop = repository.findByVersionAndIndex(version, loopIndex);

        if (loop == null) {
            return null;
        }

        return loop.getId();
    }
}
