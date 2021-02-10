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
    private HtmlFilteringPolicy htmlFilteringPolicy;
    private String text;
    private boolean likePublished;

    public TextDTO(Integer index, Integer docId, String langCode, LoopEntryRef loopEntryRef) {
        this.index = index;
        this.docId = docId;
        this.langCode = langCode;
        htmlFilteringPolicy = HtmlFilteringPolicy.RESTRICTED;
        setLoopEntryRef(loopEntryRef);
    }

    public TextDTO(Integer index, Integer docId, String langCode, LoopEntryRef loopEntryRef, boolean likePublished) {
        this.index = index;
        this.docId = docId;
        this.langCode = langCode;
        this.likePublished = likePublished;
        htmlFilteringPolicy = HtmlFilteringPolicy.RESTRICTED;
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

    @Override
    public HtmlFilteringPolicy getHtmlFilteringPolicy() {
        return Optional.ofNullable(htmlFilteringPolicy).orElse(HtmlFilteringPolicy.RESTRICTED);
    }

}
