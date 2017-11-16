package com.imcode.imcms.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class LanguageDTO implements Serializable {

    private static final long serialVersionUID = -3433592782831228045L;

    private Integer id;

    /**
     * Two-letter ISO-639-1 code, like "en" or "sv"
     */
    private String code;

    private String name;

    private String nativeName;

    private boolean enabled;

}
