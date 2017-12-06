package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.UserService;
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
public class VersionService {

    private final VersionRepository versionRepository;
    private final UserService userService;

    VersionService(VersionRepository versionRepository, UserService userService) {
        this.versionRepository = versionRepository;
        this.userService = userService;
    }

    public Version getDocumentWorkingVersion(int docId) {
        return getVersion(docId, versionRepository::findWorking);
    }

    public Version getLatestVersion(int docId) {
        return getVersion(docId, versionRepository::findLatest);
    }

    public Version getVersion(int docId, Function<Integer, Version> versionReceiver) {
        return Optional.ofNullable(versionReceiver.apply(docId)).orElseThrow(DocumentNotExistException::new);
    }

    public Version create(int docId) {
        return create(docId, Imcms.getUser().getId());
    }

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

    public Version findByDocIdAndNo(int docId, int no) {
        return versionRepository.findByDocIdAndNo(docId, no);
    }

    public List<Version> findByDocId(int docId) {
        return versionRepository.findByDocId(docId);
    }

    public Version findDefault(int docId) {
        return versionRepository.findDefault(docId);
    }

    public Version findWorking(int docId) {
        return versionRepository.findWorking(docId);
    }

    public void deleteByDocId(Integer docId) {
        versionRepository.deleteByDocId(docId);
    }
}
