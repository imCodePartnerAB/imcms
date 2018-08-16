package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.model.Text;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class TextDTO extends Text {

    private static final long serialVersionUID = 972530393516788304L;

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

    public TextDTO(Text from) {
        super(from);
        this.langCode = from.getLangCode();
        this.docId = from.getDocId();
    }

    @Override
    public void setLoopEntryRef(LoopEntryRef loopEntryRef) {
        this.loopEntryRef = (loopEntryRef == null) ? null : new LoopEntryRefDTO(loopEntryRef);
    }

    @Override
    public Type getType() {
        return Optional.ofNullable(type).orElse(Type.EDITOR);
    }
}
