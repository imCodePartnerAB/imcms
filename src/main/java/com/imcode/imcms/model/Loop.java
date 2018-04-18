package com.imcode.imcms.model;

import com.imcode.imcms.domain.dto.Documentable;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
public abstract class Loop implements Documentable, Serializable {

    private static final long serialVersionUID = -119166244375182973L;

    protected Loop(Loop from) {
        setIndex(from.getIndex());
        setEntries(from.getEntries());
    }

    public abstract Integer getIndex();

    public abstract void setIndex(Integer index);

    public abstract List<LoopEntry> getEntries();

    public abstract void setEntries(List<LoopEntry> entries);

}
