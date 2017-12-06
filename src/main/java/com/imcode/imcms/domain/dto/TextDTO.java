package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.entity.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextDTO extends Text {

    private Integer index;
    private Integer docId;
    private LoopEntryRefDTO loopEntryRef;
    private String langCode;
    private Type type;
    private String text;

    public TextDTO(Integer index, Integer docId, String langCode, LoopEntryRef loopEntryRef) {
        this.index = index;
        this.docId = docId;
        this.langCode = langCode;
        setLoopEntryRef(loopEntryRef);
    }

    public TextDTO(Text from, Version version, Language language) {
        super(from, from.getLoopEntryRef());
        this.langCode = language.getCode();
        this.docId = version.getDocId();
    }

    @Override
    public void setLoopEntryRef(LoopEntryRef loopEntryRef) {
        this.loopEntryRef = (loopEntryRef == null) ? null : new LoopEntryRefDTO(loopEntryRef);
    }
}
