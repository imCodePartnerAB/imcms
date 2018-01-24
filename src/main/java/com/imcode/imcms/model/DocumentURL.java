package com.imcode.imcms.model;

import com.imcode.imcms.persistence.entity.Version;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class DocumentURL {

    public DocumentURL(DocumentURL from) {
        setId(from.getId());
        setUrlFrameName(from.getUrlFrameName());
        setUrlTarget(from.getUrlTarget());
        setUrl(from.getUrl());
        setUrlText(from.getUrlText());
        setUrlLanguagePrefix(from.getUrlLanguagePrefix());
        setVersion(from.getVersion());
    }

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract String getUrlFrameName();

    public abstract void setUrlFrameName(String urlFrameName);

    public abstract String getUrlTarget();

    public abstract void setUrlTarget(String urlTarget);

    public abstract String getUrl();

    public abstract void setUrl(String url);

    public abstract String getUrlText();

    public abstract void setUrlText(String urlText);

    public abstract String getUrlLanguagePrefix();

    public abstract void setUrlLanguagePrefix(String urlLanguagePrefix);

    public abstract Version getVersion();

    public abstract void setVersion(Version version);
}
