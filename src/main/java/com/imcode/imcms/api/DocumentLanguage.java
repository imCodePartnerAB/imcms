package com.imcode.imcms.api;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "imcms_languages")
public final class DocumentLanguage implements Serializable, Cloneable {
                    //
    public static final class Builder {
        private DocumentLanguage documentLanguage = new DocumentLanguage();

        public Builder() {}

        public Builder(DocumentLanguage language) {
            id(language.id);
            enabled(language.enabled);
            code(language.code);
            name(language.name);
            nativeName(language.nativeName);
        }

        public Builder id(Integer id) {
            documentLanguage.id = id;
            return this;
        }

        public Builder enabled(boolean enabled) {
            documentLanguage.enabled = enabled;
            return this;
        }

        public Builder code(String code) {
            documentLanguage.code = code;
            return this;
        }

        public Builder name(String name) {
            documentLanguage.name = name;
            return this;
        }

        public Builder nativeName(String nativeName) {
            documentLanguage.nativeName = nativeName;
            return this;
        }

        public DocumentLanguage build() {
            DocumentLanguage newDocumentLanguage = new DocumentLanguage();

            newDocumentLanguage.id = documentLanguage.id;
            newDocumentLanguage.code = documentLanguage.code;
            newDocumentLanguage.name = documentLanguage.name;
            newDocumentLanguage.nativeName = documentLanguage.nativeName;
            newDocumentLanguage.enabled = documentLanguage.enabled;

            return newDocumentLanguage;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(DocumentLanguage documentLanguage) {
        return new Builder(documentLanguage);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private volatile Integer id;

    private volatile String code;

    private volatile String name;

    private volatile boolean enabled;

    @Column(name = "native_name")
    private volatile String nativeName;

    protected DocumentLanguage() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentLanguage)) return false;

        DocumentLanguage language = (DocumentLanguage) o;

        if (enabled != language.enabled) return false;
        if (code != null ? !code.equals(language.code) : language.code != null) return false;
        if (id != null ? !id.equals(language.id) : language.id != null) return false;
        if (name != null ? !name.equals(language.name) : language.name != null) return false;
        if (nativeName != null ? !nativeName.equals(language.nativeName) : language.nativeName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (enabled ? 1 : 0);
        result = 31 * result + (nativeName != null ? nativeName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public DocumentLanguage clone() {
        try {
            return (DocumentLanguage)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getNativeName() {
        return nativeName != null ? nativeName : name;
    }

    public boolean isEnabled() {
        return enabled;
    }
}