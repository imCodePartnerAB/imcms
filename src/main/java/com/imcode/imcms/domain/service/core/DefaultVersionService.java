package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.persistence.entity.Version;
import imcode.server.Imcms;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@Transactional
public class DefaultVersionService implements VersionService {

    private final VersionRepository versionRepository;
    private final UserService userService;

    DefaultVersionService(VersionRepository versionRepository, UserService userService) {
        this.versionRepository = versionRepository;
        this.userService = userService;
    }

    @Override
    public Version getDocumentWorkingVersion(int docId) {
        return getVersion(docId, versionRepository::findWorking);
    }

    @Override
    public Version getLatestVersion(int docId) {
        return getVersion(docId, versionRepository::findLatest);
    }

    @Override
    public Version getVersion(int docId, Function<Integer, Version> versionReceiver) {
        return Optional.ofNullable(versionReceiver.apply(docId)).orElseThrow(DocumentNotExistException::new);
    }

    @Override
    public Version create(int docId) {
        return create(docId, Imcms.getUser().getId());
    }

    @Override
    public Version create(int docId, int userId) {
        final User creator = userService.getUser(userId);
        final Integer latestNo = versionRepository.findLatestNoForUpdate(docId);
        final int no = (latestNo == null) ? 0 : latestNo + 1;
        final Date now = new Date();
        final Version version = new Version();

        version.setDocId(docId);
        version.setNo(no);
        version.setCreatedDt(now);
        version.setCreatedBy(creator);
        version.setModifiedDt(now);
        version.setModifiedBy(creator);

        versionRepository.saveAndFlush(version);

        return version;
    }

    @Override
    public Version findByDocIdAndNo(int docId, int no) {
        return versionRepository.findByDocIdAndNo(docId, no);
    }

    @Override
    public List<Version> findByDocId(int docId) {
        return versionRepository.findByDocId(docId);
    }

    @Override
    public Version findDefault(int docId) {
        return versionRepository.findDefault(docId);
    }

    @Override
    public Version findWorking(int docId) {
        return versionRepository.findWorking(docId);
    }

    @Override
    public void deleteByDocId(Integer docId) {
        versionRepository.deleteByDocId(docId);
    }

    @Override
    public boolean hasNewerVersion(int docId) {
        final Version latestVersion = getLatestVersion(docId);
        final Version workingVersion = getDocumentWorkingVersion(docId);
        return latestVersion.equals(workingVersion)
                || latestVersion.getCreatedDt().before(workingVersion.getModifiedDt());
    }
}
