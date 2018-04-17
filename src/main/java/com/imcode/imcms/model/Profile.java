package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public abstract class Profile implements Serializable {

    private static final long serialVersionUID = -9147976485333808065L;

    public Profile(Profile from) {
        setId(from.getId());
        setDocumentName(from.getDocumentName());
        setName(from.getName());
    }

    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getDocumentName();

    public abstract void setDocumentName(String documentName);

    public abstract Integer getId();

    public abstract void setId(Integer id);

}
