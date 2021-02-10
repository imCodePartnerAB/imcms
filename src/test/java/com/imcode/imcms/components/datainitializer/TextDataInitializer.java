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

    public TextDataInitializer(TextRepository textRepository) {
        super(textRepository);
        this.textRepository = textRepository;
    }

    public void createText(int index, LanguageJPA language, Version version, String testText) {
        final TextJPA text = new TextJPA();
        text.setIndex(index);
        text.setLanguage(language);
        text.setText(testText);
        text.setType(TEXT);
        text.setVersion(version);

        textRepository.saveAndFlush(text);
    }

    public void createText(int index, LanguageJPA language, Version version, String testText, LoopEntryRef loopEntryRef) {
        final TextJPA text = new TextJPA();
        text.setIndex(index);
        text.setLanguage(language);
        text.setText(testText);
        text.setType(TEXT);
        text.setVersion(version);
        text.setLoopEntryRef(loopEntryRef);

        textRepository.saveAndFlush(text);
    }


    public void createLikePublishedText(int index, LanguageJPA language, Version version, String testText, LoopEntryRef loopEntryRef) {
        final TextJPA text = new TextJPA();
        text.setIndex(index);
        text.setLanguage(language);
        text.setText(testText);
        text.setType(TEXT);
        text.setVersion(version);
        text.setLoopEntryRef(loopEntryRef);
        text.setLikePublished(true);

        textRepository.saveAndFlush(text);
    }

}
