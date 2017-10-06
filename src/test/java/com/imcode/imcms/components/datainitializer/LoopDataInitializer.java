package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.components.cleaner.RepositoryTestDataCleaner;
import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.persistence.entity.Loop;
import com.imcode.imcms.persistence.repository.LoopRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.BiFunction;

@Component
public class LoopDataInitializer extends RepositoryTestDataCleaner {
    private static final int TEST_VERSION_NO = 0;

    private final LoopRepository loopRepository;
    private final VersionRepository versionRepository;
    private final BiFunction<LoopDTO, Version, Loop> loopDtoToLoop;
    private final VersionDataInitializer versionDataInitializer;

    public LoopDataInitializer(LoopRepository loopRepository,
                               VersionRepository versionRepository,
                               BiFunction<LoopDTO, Version, Loop> loopDtoToLoop,
                               VersionDataInitializer versionDataInitializer) {
        super(loopRepository);
        this.loopRepository = loopRepository;
        this.versionRepository = versionRepository;
        this.loopDtoToLoop = loopDtoToLoop;
        this.versionDataInitializer = versionDataInitializer;
    }

    public void createData(LoopDTO loopDTO) {
        final Version testVersion = Optional.ofNullable(versionRepository
                .findByDocIdAndNo(loopDTO.getDocId(), TEST_VERSION_NO)
        ).orElseGet(() -> versionDataInitializer.createData(TEST_VERSION_NO, loopDTO.getDocId()));

        final Loop testLoop = loopDtoToLoop.apply(loopDTO, testVersion);
        loopRepository.saveAndFlush(testLoop);
    }

    @Override
    public void cleanRepositories() {
        super.cleanRepositories();
        versionDataInitializer.cleanRepositories();
    }
}
