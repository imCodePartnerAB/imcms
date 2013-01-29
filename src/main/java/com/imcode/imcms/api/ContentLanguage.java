package com.imcode.imcms.api;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "imcms_languages")
public final class ContentLanguage implements Serializable, Cloneable {
                    //
    public static final class Builder {
        private ContentLanguage contentLanguage = new ContentLanguage();

        public Builder() {}

        public Builder(ContentLanguage language) {
            id(language.id);
            enabled(language.enabled);
            code(language.code);
            name(language.name);
            nativeName(language.nativeName);
        }

        public Builder id(Integer id) {
            contentLanguage.id = id;
            return this;
        }

        public Builder enabled(boolean enabled) {
            contentLanguage.enabled = enabled;
            return this;
        }

        public Builder code(String code) {
            contentLanguage.code = code;
            return this;
        }

        public Builder name(String name) {
            contentLanguage.name = name;
            return this;
        }

        public Builder nativeName(String nativeName) {
            contentLanguage.nativeName = nativeName;
            return this;
        }

        public ContentLanguage build() {
            ContentLanguage newContentLanguage = new ContentLanguage();

            newContentLanguage.id = contentLanguage.id;
            newContentLanguage.code = contentLanguage.code;
            newContentLanguage.name = contentLanguage.name;
            newContentLanguage.nativeName = contentLanguage.nativeName;
            newContentLanguage.enabled = contentLanguage.enabled;

            return newContentLanguage;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ContentLanguage contentLanguage) {
        return new Builder(contentLanguage);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;

    private String name;

    private boolean enabled;

    @Column(name = "native_name")
    private String nativeName;

    protected ContentLanguage() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContentLanguage)) return false;

        ContentLanguage language = (ContentLanguage) o;

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
    public ContentLanguage clone() {
        try {
            return (ContentLanguage)super.clone();
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