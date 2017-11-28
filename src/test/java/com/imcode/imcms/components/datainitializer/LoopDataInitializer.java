package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.persistence.entity.LoopJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.LoopRepository;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class LoopDataInitializer extends TestDataCleaner {
    public static final int TEST_VERSION_NO = 0;

    private final LoopRepository loopRepository;
    private final BiFunction<LoopDTO, Version, LoopJPA> loopDtoToLoop;
    private final VersionDataInitializer versionDataInitializer;

    public LoopDataInitializer(LoopRepository loopRepository,
                               BiFunction<LoopDTO, Version, LoopJPA> loopDtoToLoop,
                               VersionDataInitializer versionDataInitializer) {
        super(loopRepository);
        this.loopRepository = loopRepository;
        this.loopDtoToLoop = loopDtoToLoop;
        this.versionDataInitializer = versionDataInitializer;
    }

    public LoopJPA createData(LoopDTO loopDTO) {
        final Version testVersion = versionDataInitializer.createData(TEST_VERSION_NO, loopDTO.getDocId());

        final LoopJPA testLoop = loopDtoToLoop.apply(loopDTO, testVersion);
        return loopRepository.saveAndFlush(testLoop);
    }

    @Override
    public void cleanRepositories() {
        super.cleanRepositories();
        versionDataInitializer.cleanRepositories();
    }
}
