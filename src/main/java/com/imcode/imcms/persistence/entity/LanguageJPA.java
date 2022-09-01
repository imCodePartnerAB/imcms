package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.Language;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "imcms_languages")
@EqualsAndHashCode(callSuper=false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LanguageJPA extends Language {

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

    public LanguageJPA(String code, String name, String nativeName) {
        this(null, code, name, nativeName, true);
    }

    public LanguageJPA(String code, String name, String nativeName, boolean enabled) {
        this(null, code, name, nativeName, enabled);
    }

    public LanguageJPA(Language from) {
        super(from);
    }
}