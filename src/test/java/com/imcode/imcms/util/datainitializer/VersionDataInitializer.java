package com.imcode.imcms.util.datainitializer;

import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.util.RepositoryTestDataCleaner;
import com.imcode.imcms.util.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class VersionDataInitializer extends RepositoryTestDataCleaner {
    private final VersionRepository versionRepository;
    private final UserDataInitializer userDataInitializer;

    public VersionDataInitializer(VersionRepository versionRepository,
                                  UserDataInitializer userDataInitializer) {
        super(versionRepository);
        this.versionRepository = versionRepository;
        this.userDataInitializer = userDataInitializer;
    }

    public Version createData(int versionIndex, int docId) {
        final int adminUserId = 1;
        final User user = userDataInitializer.createData(adminUserId);

        final Version testVersion = Value.with(new Version(), version -> {
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

    @Override
    public void cleanRepositories() {
        super.cleanRepositories();
    }
}
