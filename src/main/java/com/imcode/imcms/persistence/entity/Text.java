package com.imcode.imcms.persistence.entity;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class Text<LoopEntryReference extends LoopEntryRef> {

    protected Text(Text from, LoopEntryReference loopEntryRef) {
        setIndex(from.getIndex());
        setType(from.getType());
        setText(from.getText());
        setLoopEntryRef(loopEntryRef);
    }

    public abstract Integer getIndex();

    public abstract void setIndex(Integer index);

    public abstract Type getType();

    public abstract void setType(Type type);

    public abstract String getText();

    public abstract void setText(String text);

    public abstract LoopEntryReference getLoopEntryRef();

    public abstract void setLoopEntryRef(LoopEntryReference loopEntryRef);

    public enum Type {
        PLAIN_TEXT, HTML
    }
}
