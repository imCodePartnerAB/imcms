package com.imcode.imcms.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LanguageDTO {

    private String code;

    private String name;

    private String nativeName;

    private boolean enabled;

}
