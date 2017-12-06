package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.Text;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "imcms_text_doc_texts")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TextJPA extends TextJPABase {

    public TextJPA(Text from, Version version, LanguageJPA language) {
        super(from, (from.getLoopEntryRef() == null) ? null : new LoopEntryRefJPA(from.getLoopEntryRef()));
        setVersion(version);
        setLanguage(language);
    }

}
