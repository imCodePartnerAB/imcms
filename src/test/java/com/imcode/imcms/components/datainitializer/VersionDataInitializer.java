package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.util.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class VersionDataInitializer extends AbstractTestDataInitializer<Integer, Version> {
    private final VersionRepository versionRepository;
    private final UserDataInitializer userDataInitializer;

    private User user;

    public VersionDataInitializer(VersionRepository versionRepository,
                                  UserDataInitializer userDataInitializer) {
        super(versionRepository);
        this.versionRepository = versionRepository;
        this.userDataInitializer = userDataInitializer;
    }

    @Override
    public Version createData(Integer versionIndex, Integer docId) {
        Version testVersion = versionRepository.findByDocIdAndNo(docId, versionIndex);

        if (testVersion != null) {
            return testVersion;
        }

        final int adminUserId = 1;
        user = userDataInitializer.createData(adminUserId).get(0);

        testVersion = Value.with(new Version(), version -> {
            version.setNo(versionIndex);
            version.setDocId(docId);
            version.setCreatedBy(user);
            version.setCreatedDt(new Date());
            version.setModifiedBy(user);
            version.setModifiedDt(new Date());
        });
        versionRepository.saveAndFlush(testVersion);

        return testVersion;
    }

    public User getUser() {
        return user;
    }
}
