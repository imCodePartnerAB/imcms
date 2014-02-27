package com.imcode.imcms.mapping.jpa.doc;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "imcms_languages")
public class Language implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private String code;

    @NotNull
    @Column(nullable = false)
    private String name;

    @Column(name = "native_name")
    private String nativeName;

    @Column(nullable = false)
    private boolean enabled;

    public Language() {
    }

    public Language(String code, String name, String nativeName) {
        this(null, code, name, nativeName, true);
    }

    public Language(String code, String name, String nativeName, boolean enabled) {
        this(null, code, name, nativeName, enabled);
    }

    public Language(Integer id, String code, String name, String nativeName, boolean enabled) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.nativeName = nativeName;
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof Language && equals((Language) o));
    }

    private boolean equals(Language that) {
        return Objects.equals(id, that.id)
                && Objects.equals(code, that.code)
                && Objects.equals(name, that.name)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(nativeName, that.nativeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, nativeName, enabled);
    }

    @Override
    public Language clone() {
        try {
            return (Language) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNativeName() {
        return nativeName;
    }

    public void setNativeName(String nativeName) {
        this.nativeName = nativeName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}