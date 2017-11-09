package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.TextBase.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextDTO {

    private Integer index;
    private Integer docId;
    private LoopEntryRefDTO loopEntryRef;
    private String langCode;
    private Type type = Type.HTML;
    private String text = "";

    public TextDTO(Integer index, Integer docId, String langCode, LoopEntryRefDTO loopEntryRef) {
        this.index = index;
        this.docId = docId;
        this.langCode = langCode;
        this.loopEntryRef = loopEntryRef;
    }
}
