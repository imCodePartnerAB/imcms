package com.imcode.imcms.api;

import java.io.Serializable;
import java.util.Objects;

public final class DocumentLanguage implements Serializable {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(DocumentLanguage documentLanguage) {
        return new Builder(documentLanguage);
    }

    public static final class Builder {
        private String code;
        private String name;
        private String nativeName;
        private boolean enabled;

        public Builder() {
        }

        public Builder(DocumentLanguage language) {
            this.code = language.code;
            this.name = language.name;
            this.nativeName = language.nativeName;
            this.enabled = language.enabled;
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

        public DocumentLanguage build() {
            return new DocumentLanguage(code, name, nativeName, enabled);
        }
    }

    private final String code;

    private final String name;

    private final String nativeName;

    private final boolean enabled;

    /**
     * @param code language ISO 639-1 code.
     * @param name language name
     * @param nativeName language native name
     * @param enabled language enabled status
     */
    public DocumentLanguage(String code, String name, String nativeName, boolean enabled) {
        this.code = code;
        this.name = name;
        this.nativeName = nativeName;
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof DocumentLanguage && equals((DocumentLanguage) o));
    }

    private boolean equals(DocumentLanguage that) {
        return Objects.equals(code, that.code)
                && Objects.equals(name, that.name)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(nativeName, that.nativeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, nativeName, enabled);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public DocumentLanguage clone() {
        try {
            return (DocumentLanguage) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return language ISO 639-1 code.
     */
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
