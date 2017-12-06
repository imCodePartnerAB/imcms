package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class Language {

    protected Language(Language from) {
        setId(from.getId());
        setCode(from.getCode());
        setName(from.getName());
        setNativeName(from.getNativeName());
        setEnabled(from.isEnabled());
    }

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract String getCode();

    public abstract void setCode(String code);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getNativeName();

    public abstract void setNativeName(String nativeName);

    public abstract boolean isEnabled();

    public abstract void setEnabled(boolean enabled);
}
