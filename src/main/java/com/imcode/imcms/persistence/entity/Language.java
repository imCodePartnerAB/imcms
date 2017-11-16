package com.imcode.imcms.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "imcms_languages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Language implements Serializable, Cloneable {

    private static final long serialVersionUID = -7712182762931242124L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Two-letter ISO-639-1 code, like "en" or "sv"
     */
    @NotNull
    @Column(nullable = false)
    private String code;

    @NotNull
    @Column(nullable = false)
    private String name;

    @Column(name = "native_name")
    private String nativeName;

    @Column(nullable = false, columnDefinition = "tinyint")
    private boolean enabled;

    public Language(String code, String name, String nativeName) {
        this(null, code, name, nativeName, true);
    }

    public Language(String code, String name, String nativeName, boolean enabled) {
        this(null, code, name, nativeName, enabled);
    }

    @Override
    public Language clone() {
        try {
            return (Language) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}