package com.imcode.imcms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExternalRole {
    protected String providerId;
    private String id;

    public ExternalRole() {
    }
}
