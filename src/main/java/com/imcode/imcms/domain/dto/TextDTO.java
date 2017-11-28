package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.Language;
import com.imcode.imcms.persistence.entity.Text;
import com.imcode.imcms.persistence.entity.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextDTO extends Text<LoopEntryRefDTO> {

    private Integer index;
    private Integer docId;
    private LoopEntryRefDTO loopEntryRef;
    private String langCode;
    private Type type = Text.Type.HTML;
    private String text = "";

    public TextDTO(Integer index, Integer docId, String langCode, LoopEntryRefDTO loopEntryRef) {
        this.index = index;
        this.docId = docId;
        this.langCode = langCode;
        this.loopEntryRef = loopEntryRef;
    }

    public TextDTO(Text from, Version version, Language language) {
        super(from, (from.getLoopEntryRef() == null) ? null : new LoopEntryRefDTO(from.getLoopEntryRef()));
        this.langCode = language.getCode();
        this.docId = version.getDocId();
    }
}
