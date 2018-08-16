package com.imcode.imcms.model;

import com.imcode.imcms.domain.dto.Documentable;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public abstract class Text implements Documentable, Serializable {

    private static final long serialVersionUID = 9160535816003555084L;

    protected Text(Text from) {
        setIndex(from.getIndex());
        setType(from.getType());
        setText(from.getText());
        setLoopEntryRef(from.getLoopEntryRef());
    }

    public abstract Integer getIndex();

    public abstract void setIndex(Integer index);

    public abstract String getLangCode();

    public abstract Type getType();

    public abstract void setType(Type type);

    public abstract String getText();

    public abstract void setText(String text);

    public abstract LoopEntryRef getLoopEntryRef();

    public abstract void setLoopEntryRef(LoopEntryRef loopEntryRef);

    public enum Type {
        TEXT,
        TEXT_FROM_EDITOR,
        HTML,
        HTML_FROM_EDITOR,
        CLEAN_HTML
    }
}
