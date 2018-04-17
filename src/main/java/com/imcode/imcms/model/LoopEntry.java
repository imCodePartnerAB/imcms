package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public abstract class LoopEntry implements Serializable {

    private static final long serialVersionUID = 8794764831568545929L;

    protected LoopEntry(LoopEntry from) {
        setIndex(from.getIndex());
        setEnabled(from.isEnabled());
    }

    public abstract int getIndex();

    public abstract void setIndex(int index);

    public abstract boolean isEnabled();

    public abstract void setEnabled(boolean enabled);

}
