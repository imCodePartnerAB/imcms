package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.components.cleaner.RepositoryTestDataCleaner;
import com.imcode.imcms.mapping.jpa.doc.Language;
import com.imcode.imcms.mapping.jpa.doc.LanguageRepository;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContent;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContentRepository;
import org.springframework.stereotype.Component;

@Component
public class CommonContentDataInitializer extends RepositoryTestDataCleaner {
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

    public void createData(int docId, int versionIndex) {
        Language en = languageRepository.findByCode(ENG_CODE);
        Language se = languageRepository.findByCode(SWE_CODE);
        // both langs should be already created

        versionDataInitializer.createData(versionIndex, docId);

        commonContentRepository.saveAndFlush(new CommonContent(
                docId, en, "headline_en", "menuText_en", "menuImageUrl_en", true, versionIndex
        ));
        commonContentRepository.saveAndFlush(new CommonContent(
                docId, se, "headline_se", "menuText_se", "menuImageUrl_se", true, versionIndex
        ));
    }
}
