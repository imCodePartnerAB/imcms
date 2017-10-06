package com.imcode.imcms.util.datainitializer;

import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.persistence.entity.Loop;
import com.imcode.imcms.persistence.repository.LoopRepository;
import com.imcode.imcms.util.RepositoryTestDataCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class LoopDataInitializer extends RepositoryTestDataCleaner {
    private static final int TEST_VERSION_NO = 0;

    private final LoopRepository loopRepository;
    private final BiFunction<LoopDTO, Version, Loop> loopDtoToLoop;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    public LoopDataInitializer(LoopRepository loopRepository, BiFunction<LoopDTO, Version, Loop> loopDtoToLoop) {
        super(loopRepository);
        this.loopRepository = loopRepository;
        this.loopDtoToLoop = loopDtoToLoop;
    }

    public void createData(LoopDTO loopDTO) {
        final Version testVersion = versionDataInitializer.createData(TEST_VERSION_NO, loopDTO.getDocId());
        final Loop testLoop = loopDtoToLoop.apply(loopDTO, testVersion);
        loopRepository.saveAndFlush(testLoop);
    }

    @Override
    public void cleanRepositories() {
        super.cleanRepositories();
        versionDataInitializer.cleanRepositories();
    }
}
