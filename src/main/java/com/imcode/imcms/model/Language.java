package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public abstract class Language implements Serializable {

    private static final long serialVersionUID = -5874751837931922946L;

    protected Language(Language from) {
        setId(from.getId());
        setCode(from.getCode());
        setName(from.getName());
        setNativeName(from.getNativeName());
        setEnabled(from.isEnabled());
    }

    public abstract Integer getId();

    public abstract void setId(Integer id);

    /**
     * Two-letter ISO-639-1 code, like "en" or "sv"
     */
    public abstract String getCode();

    /**
     * Two-letter ISO-639-1 code, like "en" or "sv"
     */
    public abstract void setCode(String code);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getNativeName();

    public abstract void setNativeName(String nativeName);

    public abstract boolean isEnabled();

    public abstract void setEnabled(boolean enabled);
}
