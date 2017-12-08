package com.imcode.imcms.domain.service;

import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.VersionedContentRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public abstract class AbstractVersionedContentService<JPA, DTO, R extends VersionedContentRepository<JPA> & JpaRepository<JPA, Integer>> implements VersionedContentService<DTO> {

    protected final R repository;

    protected AbstractVersionedContentService(R repository) {
        this.repository = repository;
    }

    protected abstract DTO mapping(JPA jpa, Version version);

    protected abstract JPA mappingWithoutId(DTO dto, Version version);

    @Override
    public Set<DTO> getByVersion(Version version) {
        return repository.findByVersion(version).stream()
                .map(jpa -> mapping(jpa, version))
                .collect(Collectors.toSet());
    }

    @Override
    public void createVersionedContent(Version workingVersion, Version newVersion) {
        final List<JPA> forSave = getByVersion(workingVersion).stream()
                .map(jpa -> mappingWithoutId(jpa, newVersion))
                .collect(toList());

        repository.save(forSave);
    }

}
