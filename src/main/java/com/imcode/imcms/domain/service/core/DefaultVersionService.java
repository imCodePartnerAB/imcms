package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.persistence.entity.Version;
import imcode.server.Imcms;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@Slf4j
public class DefaultVersionService implements VersionService {

    private final VersionRepository versionRepository;
    private final UserService userService;
    private final boolean isVersioningAllowed;

    DefaultVersionService(VersionRepository versionRepository,
                          UserService userService,
                          @Value("${document.versioning:true}") boolean isVersioningAllowed) {

        this.versionRepository = versionRepository;
        this.userService = userService;
        this.isVersioningAllowed = isVersioningAllowed;
    }

    @Transactional
    @Override
    public Version getDocumentWorkingVersion(int docId) throws DocumentNotExistException {
        return getVersion(docId, versionRepository::findWorking);
    }

    @Transactional
    @Override
    public Version getLatestVersion(int docId) throws DocumentNotExistException {
        Function<Integer, Version> versionFunction = isVersioningAllowed
                ? versionRepository::findLatest
                : versionRepository::findWorking;

        return getVersion(docId, versionFunction);
    }

    @Transactional
    @Override
    public Version getVersion(int docId, Function<Integer, Version> versionReceiver) throws DocumentNotExistException {
        return Optional.ofNullable(versionReceiver.apply(docId)).orElseThrow(DocumentNotExistException::new);
    }

    @Transactional
    @Override
    public Version create(int docId) {
        return create(docId, Imcms.getUser().getId());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Version create(int docId, int userId) {
        final User creator = userService.getUser(userId);
        final Version latestVersion = versionRepository.findLatest(docId);
        final int no = (latestVersion == null) ? 0 : latestVersion.getNo() + 1;
        final Date now = new Date();
        final Version version = new Version();

        version.setDocId(docId);
        version.setNo(no);
        version.setCreatedBy(creator);
        version.setCreatedDt(now);
        version.setModifiedBy(creator);
        version.setModifiedDt(now);

        log.error("createVersion: prepare to save version");
        versionRepository.saveAndFlush(version);
        log.error("createVersion: saved version and return this version no - {}", version.getNo());

        return version;
    }

    @Transactional
    @Override
    public Version findByDocIdAndNo(int docId, int no) {
        return versionRepository.findByDocIdAndNo(docId, no);
    }

    @Transactional
    @Override
    public List<Version> findByDocId(int docId) {
        return isVersioningAllowed
                ? versionRepository.findByDocId(docId)
                : Collections.singletonList(versionRepository.findWorking(docId));
    }

    @Transactional
    @Override
    public Version findDefault(int docId) {
        return isVersioningAllowed
                ? versionRepository.findDefault(docId)
                : versionRepository.findWorking(docId);
    }

    @Transactional
    @Override
    public Version findWorking(int docId) {
        return versionRepository.findWorking(docId);
    }

    @Override
    @Transactional
    public void deleteByDocId(Integer docId) {
        versionRepository.deleteByDocId(docId);
    }

    @Transactional
    @Override
    public boolean hasNewerVersion(int docId) {
        if (!isVersioningAllowed) {
            return false;
        }

        final Version latestVersion = getLatestVersion(docId);
        final Version workingVersion = getDocumentWorkingVersion(docId);

        return latestVersion.equals(workingVersion)
                || latestVersion.getCreatedDt().before(workingVersion.getModifiedDt());
    }

    @Transactional
    @Override
    public void updateWorkingVersion(int docId) {
        final Version workingVersion = getDocumentWorkingVersion(docId);
        workingVersion.setModifiedDt(new Date());
        versionRepository.save(workingVersion);
    }
}
