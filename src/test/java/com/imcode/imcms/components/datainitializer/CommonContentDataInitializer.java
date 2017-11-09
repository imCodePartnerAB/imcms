package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.persistence.entity.CommonContent;
import com.imcode.imcms.persistence.entity.Language;
import com.imcode.imcms.persistence.repository.CommonContentRepository;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CommonContentDataInitializer extends AbstractTestDataInitializer<Integer, List<CommonContent>> {
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

    @Override
    public List<CommonContent> createData(Integer docId, Integer versionIndex) {
        Language en = languageRepository.findByCode(ENG_CODE);
        Language se = languageRepository.findByCode(SWE_CODE);
        // both langs should be already created

        versionDataInitializer.createData(versionIndex, docId);

        return Arrays.asList(
                commonContentRepository.saveAndFlush(new CommonContent(
                        docId, en, "headline_en", "menuText_en", "menuImageUrl_en", true, versionIndex
                )),
                commonContentRepository.saveAndFlush(new CommonContent(
                        docId, se, "headline_se", "menuText_se", "menuImageUrl_se", true, versionIndex
                ))
        );
    }
}
