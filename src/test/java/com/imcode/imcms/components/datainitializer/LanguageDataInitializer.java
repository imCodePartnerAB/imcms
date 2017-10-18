package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.LanguageDTO;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class LanguageDataInitializer extends AbstractTestDataInitializer<Void, List<LanguageDTO>> {

    public LanguageDataInitializer() {
        super();
    }

    @Override
    public List<LanguageDTO> createData() {
        final LanguageDTO englishLanguage = new LanguageDTO();
        englishLanguage.setCode("en");
        englishLanguage.setName("English");
        englishLanguage.setNativeName("English");
        englishLanguage.setEnabled(true);

        final LanguageDTO swedishLanguage = new LanguageDTO();
        swedishLanguage.setCode("sv");
        swedishLanguage.setName("Swedish");
        swedishLanguage.setNativeName("Svenska");
        swedishLanguage.setEnabled(true);

        return Arrays.asList(englishLanguage, swedishLanguage);
    }

}
