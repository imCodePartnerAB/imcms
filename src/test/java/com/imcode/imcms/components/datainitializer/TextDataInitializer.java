package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.TextJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.TextRepository;
import org.springframework.stereotype.Component;

import static com.imcode.imcms.model.Text.Type.TEXT;

@Component
public class TextDataInitializer extends TestDataCleaner {

    private final TextRepository textRepository;
    private final LanguageDataInitializer languageDataInitializer;

    public TextDataInitializer(TextRepository textRepository, LanguageDataInitializer languageDataInitializer) {
        super(textRepository);
        this.textRepository = textRepository;
        this.languageDataInitializer = languageDataInitializer;
    }

    public TextJPA createText(int index, Version version, String testText){
        LanguageJPA language = new LanguageJPA(languageDataInitializer.createData().get(0));
        return createText(index, language, version, testText);
    }

    public TextJPA createText(int index, LanguageJPA language, Version version, String testText) {
        final TextJPA text = new TextJPA();
        text.setIndex(index);
        text.setLanguage(language);
        text.setText(testText);
        text.setType(TEXT);
        text.setVersion(version);

        return textRepository.saveAndFlush(text);
    }

    public TextJPA createText(int index, LanguageJPA language, Version version, String testText, LoopEntryRef loopEntryRef) {
        final TextJPA text = new TextJPA();
        text.setIndex(index);
        text.setLanguage(language);
        text.setText(testText);
        text.setType(TEXT);
        text.setVersion(version);
        text.setLoopEntryRef(loopEntryRef);

        return textRepository.saveAndFlush(text);
    }

}
