package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class Profile {

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
