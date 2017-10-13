package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Service
public class VersionService {

    private final VersionRepository versionRepository;
    private final UserService userService;

    public VersionService(VersionRepository versionRepository, UserService userService) {
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

    @Transactional
    public Version create(int docId, int userId) {
        User creator = userService.getUser(userId);
        Integer latestNo = versionRepository.findLatestNoForUpdate(docId);
        int no = latestNo == null ? 0 : latestNo + 1;
        Date now = new Date();
        Version version = new Version();

        version.setDocId(docId);
        version.setNo(no);
        version.setCreatedDt(now);
        version.setCreatedBy(creator);
        version.setModifiedDt(now);
        version.setModifiedBy(creator);

        versionRepository.saveAndFlush(version);

        return version;
    }

}
