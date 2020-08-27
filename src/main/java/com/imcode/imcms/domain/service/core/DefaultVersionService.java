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
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@Transactional
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

    @Override
    public Version getDocumentWorkingVersion(int docId) throws DocumentNotExistException {
        return getVersion(docId, versionRepository::findWorking);
    }

    @Override
    public Version getLatestVersion(int docId) throws DocumentNotExistException {
        Function<Integer, Version> versionFunction = isVersioningAllowed
                ? versionRepository::findLatest
                : versionRepository::findWorking;

        return getVersion(docId, versionFunction);
    }

    @Override
    public Version getVersion(int docId, Function<Integer, Version> versionReceiver) throws DocumentNotExistException {
        return Optional.ofNullable(versionReceiver.apply(docId)).orElseThrow(DocumentNotExistException::new);
    }

    @Override
    public Version create(int docId) {
        return create(docId, Imcms.getUser().getId());
    }

    @Override
    public Version create(int docId, int userId) {
        final User creator = userService.getUser(userId);
        final Version latestVersion = versionRepository.findLatest(docId);
        final int no = (latestVersion == null) ? 0 : latestVersion.getNo() + 1;
        final Date now = new Date();

        log.error("createVersion: prepare to save version");
        log.error("createVersion user id - {}", creator.getId());
        Version version = versionRepository.save(new Version(docId, no, creator, now, creator, now));
        log.error("createVersion: saved version and return this version no - {}", version.getNo());
        versionRepository.flush();
        return version;
    }

    @Override
    public Version findByDocIdAndNo(int docId, int no) {
        return versionRepository.findByDocIdAndNo(docId, no);
    }

    @Override
    public List<Version> findByDocId(int docId) {
        return isVersioningAllowed
                ? versionRepository.findByDocId(docId)
                : Collections.singletonList(versionRepository.findWorking(docId));
    }

    @Override
    public Version findDefault(int docId) {
        return isVersioningAllowed
                ? versionRepository.findDefault(docId)
                : versionRepository.findWorking(docId);
    }

    @Override
    public Version findWorking(int docId) {
        return versionRepository.findWorking(docId);
    }

    @Override
    @Transactional
    public void deleteByDocId(Integer docId) {
        versionRepository.deleteByDocId(docId);
    }

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

    @Override
    public void updateWorkingVersion(int docId) {
        final Version workingVersion = getDocumentWorkingVersion(docId);
        workingVersion.setModifiedDt(new Date());
        versionRepository.save(workingVersion);
    }
}
