package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static imcode.server.ImcmsConstants.ENG_CODE;
import static imcode.server.ImcmsConstants.SWE_CODE;

@Component
public class LanguageDataInitializer extends TestDataCleaner {

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private Function<LanguageJPA, LanguageDTO> languageMapper;

    public LanguageDataInitializer() {
        super();
    }

    public List<LanguageDTO> createData() {
        final Function<String, LanguageDTO> languageGetter = languageMapper.compose(languageRepository::findByCode);
        final LanguageDTO englishLanguage = languageGetter.apply(ENG_CODE);
        final LanguageDTO swedishLanguage = languageGetter.apply(SWE_CODE);

        englishLanguage.setEnabled(true);
        swedishLanguage.setEnabled(true);

        return Arrays.asList(englishLanguage, swedishLanguage);
    }

}
