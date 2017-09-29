package com.imcode.imcms.util.datainitializer;

import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.mapping.jpa.UserRepository;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.util.RepositoryTestDataCleaner;
import com.imcode.imcms.util.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class VersionDataInitializer extends RepositoryTestDataCleaner {
    private final VersionRepository versionRepository;
    private final UserRepository userRepository;

    public VersionDataInitializer(VersionRepository versionRepository, UserRepository userRepository) {
        super(versionRepository);
        this.versionRepository = versionRepository;
        this.userRepository = userRepository;
    }

    public Version createData(int versionIndex, int docId) {
        final User user = userRepository.findById(1);

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
}
