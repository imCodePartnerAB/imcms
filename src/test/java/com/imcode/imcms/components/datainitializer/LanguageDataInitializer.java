package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static imcode.server.ImcmsConstants.ENG_CODE;
import static imcode.server.ImcmsConstants.SWE_CODE;

@Component
public class LanguageDataInitializer extends TestDataCleaner {

    @Autowired
    private LanguageRepository languageRepository;

    public LanguageDataInitializer() {
        super();
    }

    public List<LanguageDTO> createData() {
        final LanguageDTO englishLanguage = new LanguageDTO(languageRepository.findByCode(ENG_CODE));
        final LanguageDTO swedishLanguage = new LanguageDTO(languageRepository.findByCode(SWE_CODE));

        englishLanguage.setEnabled(true);
        swedishLanguage.setEnabled(true);

        return Arrays.asList(englishLanguage, swedishLanguage);
    }

    public List<LanguageDTO> createData(List<String> availableLanguagesCodes) {
        return availableLanguagesCodes.stream()
                .map(code -> {
                    LanguageDTO language = new LanguageDTO(languageRepository.findByCode(code));
                    language.setEnabled(true);
                    return language;
                })
                .collect(Collectors.toList());
    }
}
