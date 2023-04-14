package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.domain.dto.LoopEntryRefDTO;
import com.imcode.imcms.domain.service.AbstractVersionedContentService;
import com.imcode.imcms.domain.service.LoopService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.Loop;
import com.imcode.imcms.model.LoopEntry;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.persistence.entity.*;
import com.imcode.imcms.persistence.repository.LoopRepository;
import imcode.server.Imcms;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service("loopService")
@Transactional
public class DefaultLoopService extends AbstractVersionedContentService<LoopJPA, LoopRepository> implements LoopService {

    private final VersionService versionService;

    DefaultLoopService(LoopRepository loopRepository, VersionService versionService) {
        super(loopRepository);
        this.versionService = versionService;
    }

    @Override
    public Set<Loop> getByDocId(int docId){
        boolean isNewVersion = versionService.hasNewerVersion(docId);

        final Version version = isNewVersion
                ? versionService.getDocumentWorkingVersion(docId)
                : versionService.getLatestVersion(docId);

        return getByVersion(version);
    }

    @Override
    public Loop getLoop(int loopIndex, int docId) {
        return getLoop(loopIndex, docId, versionService.getDocumentWorkingVersion(docId));
    }

    @Override
    public Loop getLoop(int loopIndex, int docId, int versionNo) {
        return getLoop(loopIndex, docId, versionService.findByDocIdAndNo(docId, versionNo));
    }

    @Override
    public Loop getLoopPublic(int loopIndex, int docId) {
        return getLoop(loopIndex, docId, versionService.getLatestVersion(docId));
    }

    @Override
    public Loop getLoop(int loopIndex, int docId, Version version) {
        final LoopJPA loop = repository.findByVersionAndIndex(version, loopIndex);

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
        if(Imcms.isVersioningAllowed()){
            super.updateVersionInIndex(docId);
        }else{
            Imcms.getServices().getDocumentMapper().invalidateDocument(docId);
        }
    }

    @Override
    public void setAsWorkingVersion(Version version) {
        final Version workingVersion = versionService.getDocumentWorkingVersion(version.getDocId());

        final List<LoopJPA> loopsByVersion = repository.findByVersion(version);

        final List<LoopJPA> saveLoops = new ArrayList<>();
        loopsByVersion.forEach(loopByVersion -> {
            List<LoopEntry> entriesCopy = new ArrayList<>();
            loopByVersion.getEntries().forEach(loopEntry ->
                entriesCopy.add(new LoopEntryJPA(loopEntry.getIndex(), loopEntry.isEnabled()))
            );

            LoopJPA loopCopy = new LoopJPA(loopByVersion, workingVersion);
            loopCopy.setId(null);
            loopByVersion.setEntries(entriesCopy);

            saveLoops.add(loopCopy);
        });

        repository.deleteByVersion(workingVersion);
        repository.flush();
        repository.saveAll(saveLoops);
    }

    @Override
    public LoopEntryRef buildLoopEntryRef(int loopIndex, int entryIndex) {
        return new LoopEntryRefDTO(loopIndex, entryIndex);
    }

    @Override
    public void deleteByDocId(Integer docIdToDelete) {
        repository.deleteAll(repository.findByDocId(docIdToDelete));
	    repository.flush();
    }

    @Override
    protected LoopJPA removeId(LoopJPA dto, Version version) {
        return new LoopJPA(dto, version);
    }

    public Set<Loop> getByVersion(Version version) {
        return repository.findByVersion(version)
                .stream()
                .map(LoopDTO::new)
                .collect(Collectors.toSet());
    }

    @Override
    public void createLoopEntryIfNotExists(Version version, LoopEntryRefJPA entryRef) {
        if (entryRef == null) return;

        LoopJPA loop = repository.findByVersionAndIndex(
                version, entryRef.getLoopIndex());
        int entryIndex = entryRef.getLoopEntryIndex();
        int loopIndex = entryRef.getLoopIndex();

        if (loop == null) {
            loop = new LoopJPA();
            loop.setVersion(version);
            loop.setIndex(loopIndex);
            loop.getEntries().add(new LoopEntryJPA(entryIndex, true));
        } else {
            if (!loop.containsEntry(entryRef.getLoopEntryIndex())) {
                loop.getEntries().add(new LoopEntryJPA(entryIndex, true));
            }
        }
        repository.save(loop);
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
