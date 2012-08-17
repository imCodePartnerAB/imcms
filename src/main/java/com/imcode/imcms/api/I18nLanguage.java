package com.imcode.imcms.api;

import java.io.Serializable;

import javax.persistence.*;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Entity
@Table(name = "imcms_languages")
public final class I18nLanguage implements Serializable, Cloneable {
                    //
    public static final class Builder {
        private I18nLanguage i18nLanguage = new I18nLanguage();

        public Builder() {}

        public Builder(I18nLanguage language) {
            id(language.id);
            enabled(language.enabled);
            code(language.code);
            name(language.name);
            nativeName(language.nativeName);
        }

        public Builder id(Integer id) {
            i18nLanguage.id = id;
            return this;
        }

        public Builder enabled(boolean enabled) {
            i18nLanguage.enabled = enabled;
            return this;
        }

        public Builder code(String code) {
            i18nLanguage.code = code;
            return this;
        }

        public Builder name(String name) {
            i18nLanguage.name = name;
            return this;
        }

        public Builder nativeName(String nativeName) {
            i18nLanguage.nativeName = nativeName;
            return this;
        }

        public I18nLanguage build() {
            I18nLanguage newI18nLanguage = new I18nLanguage();

            newI18nLanguage.id = i18nLanguage.id;
            newI18nLanguage.code = i18nLanguage.code;
            newI18nLanguage.name = i18nLanguage.name;
            newI18nLanguage.nativeName = i18nLanguage.nativeName;
            newI18nLanguage.enabled = i18nLanguage.enabled;

            return newI18nLanguage;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(I18nLanguage i18nLanguage) {
        return new Builder(i18nLanguage);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;

    private String name;

    private boolean enabled;

    @Column(name = "native_name")
    private String nativeName;

    protected I18nLanguage() {
    }


    @Override
    public boolean equals(Object object) {
        if (!(object instanceof I18nLanguage)) {
            return false;
        }

        if (this == object) {
            return true;
        }

        I18nLanguage that = (I18nLanguage) object;

        return new EqualsBuilder()
                .append(code, that.code).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(11, 31)
                .append(code).toHashCode();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public I18nLanguage clone() {
        try {
            return (I18nLanguage)super.clone();
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