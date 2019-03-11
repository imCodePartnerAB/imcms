package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.persistence.entity.CommonContentJPA;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.CommonContentRepository;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CommonContentDataInitializer extends TestDataCleaner {
    private static final String ENG_CODE = "en";
    private static final String SWE_CODE = "sv";

    private final LanguageRepository languageRepository;
    private final CommonContentRepository commonContentRepository;
    private final VersionDataInitializer versionDataInitializer;

    public CommonContentDataInitializer(LanguageRepository languageRepository,
                                        CommonContentRepository commonContentRepository,
                                        VersionDataInitializer versionDataInitializer) {
        super(commonContentRepository);
        this.languageRepository = languageRepository;
        this.commonContentRepository = commonContentRepository;
        this.versionDataInitializer = versionDataInitializer;
    }

    @Override
    public void cleanRepositories() {
        super.cleanRepositories();
        versionDataInitializer.cleanRepositories();
    }

    public List<CommonContent> createData(Integer docId, Integer versionIndex) {
        return createData(versionDataInitializer.createData(versionIndex, docId));
    }

    public List<CommonContent> createData(Version version) {
        LanguageJPA en = languageRepository.findByCode(ENG_CODE);
        LanguageJPA se = languageRepository.findByCode(SWE_CODE);
        // both langs should be already created

        return Arrays.asList(
                commonContentRepository.saveAndFlush(new CommonContentJPA(
                        version.getDocId(), en, "headline_en", "menuText_en", true, version.getNo()
                )),
                commonContentRepository.saveAndFlush(new CommonContentJPA(
                        version.getDocId(), se, "headline_se", "menuText_se", true, version.getNo()
                ))
        );
    }
}
