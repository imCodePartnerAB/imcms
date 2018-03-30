package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.Language;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class LanguageDTO extends Language implements Serializable {

    private static final long serialVersionUID = -3433592782831228045L;

    private Integer id;

    /**
     * Two-letter ISO-639-1 code, like "en" or "sv"
     */
    private String code;

    private String name;

    private String nativeName;

    private boolean enabled;

    public LanguageDTO(Language from) {
        super(from);
    }
}
