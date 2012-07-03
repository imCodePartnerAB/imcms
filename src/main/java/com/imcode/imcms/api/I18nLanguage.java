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
        private Integer id;
        private Boolean enabled;
        private String code;
        private String name;
        private String nativeName;

        public Builder() {}

        public Builder(I18nLanguage language) {
            id(language.id);
            enabled(language.enabled);
            code(language.code);
            name(language.name);
            nativeName(language.nativeName);
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder nativeName(String nativeName) {
            this.nativeName = nativeName;
            return this;
        }

        public I18nLanguage build() {
            I18nLanguage language = new I18nLanguage();

            language.id = id;
            language.code = code;
            language.name = name;
            language.nativeName = nativeName;
            language.enabled = enabled;

            return language;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Reserved for future use
    private Boolean enabled;

    private String code;

    private String name;

    @Column(name = "native_name")
    private String nativeName;


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
    public I18nLanguage clone() {
        try {
            return (I18nLanguage)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
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

    public Boolean isEnabled() {
        return enabled;
    }
}