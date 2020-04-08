package com.imcode.imcms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ExternalRole implements Serializable {
    protected String providerId;
    private String id;

    public ExternalRole() {
    }
}
