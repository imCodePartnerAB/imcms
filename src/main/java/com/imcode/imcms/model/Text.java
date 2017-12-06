package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class Text {

    protected Text(Text from, LoopEntryRef loopEntryRef) {
        setIndex(from.getIndex());
        setType(from.getType());
        setText(from.getText());
        setLoopEntryRef(loopEntryRef);
    }

    public abstract Integer getIndex();

    public abstract void setIndex(Integer index);

    public abstract Integer getDocId();

    public abstract String getLangCode();

    public abstract Type getType();

    public abstract void setType(Type type);

    public abstract String getText();

    public abstract void setText(String text);

    public abstract LoopEntryRef getLoopEntryRef();

    public abstract void setLoopEntryRef(LoopEntryRef loopEntryRef);

    public enum Type {
        PLAIN_TEXT, HTML
    }
}
