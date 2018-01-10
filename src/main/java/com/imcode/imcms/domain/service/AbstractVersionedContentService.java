package com.imcode.imcms.domain.service;

import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.VersionedContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public abstract class AbstractVersionedContentService<JPA, DTO, Repository extends VersionedContentRepository<JPA> & JpaRepository<JPA, Integer>> implements VersionedContentService<DTO> {

    protected final Repository repository;

    @Autowired
    private VersionService versionService;

    protected AbstractVersionedContentService(Repository repository) {
        this.repository = repository;
    }

    protected abstract DTO mapToDTO(JPA jpa, Version version);

    protected abstract JPA mapToJpaWithoutId(DTO dto, Version version);

    @Override
    public Set<DTO> getByVersion(Version version) {
        return repository.findByVersion(version).stream()
                .map(jpa -> mapToDTO(jpa, version))
                .collect(Collectors.toSet());
    }

    @Override
    public void createVersionedContent(Version workingVersion, Version newVersion) {
        final List<JPA> forSave = getByVersion(workingVersion).stream()
                .map(jpa -> mapToJpaWithoutId(jpa, newVersion))
                .collect(toList());

        repository.save(forSave);
    }

    protected void updateWorkingVersion(int docId) {
        versionService.updateWorkingVersion(docId);
    }

}
