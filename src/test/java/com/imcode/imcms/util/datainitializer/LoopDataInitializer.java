package com.imcode.imcms.util.datainitializer;

import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop;
import com.imcode.imcms.persistence.repository.LoopRepository;
import com.imcode.imcms.util.RepositoryTestDataCleaner;
import com.imcode.imcms.util.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class LoopDataInitializer extends RepositoryTestDataCleaner {
    private static final int TEST_VERSION_NO = 0;

    private final LoopRepository loopRepository;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    public LoopDataInitializer(LoopRepository loopRepository) {
        super(loopRepository);
        this.loopRepository = loopRepository;
    }

    public void createData(LoopDTO loopDTO) {
        final Version testVersion = versionDataInitializer.createData(TEST_VERSION_NO, loopDTO.getDocId());
        final Loop testLoop = Value.with(new Loop(), loop -> {
            loop.setVersion(testVersion);
            loop.setNo(loopDTO.getLoopIndex());
            loop.setEntries(Collections.emptyList());
            loop.setNextEntryNo(1);
        });
        loopRepository.saveAndFlush(testLoop);
    }

    @Override
    public void cleanRepositories() {
        super.cleanRepositories();
        versionDataInitializer.cleanRepositories();
    }
}
