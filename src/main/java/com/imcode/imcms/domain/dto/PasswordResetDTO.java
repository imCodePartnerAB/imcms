package com.imcode.imcms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordResetDTO {
    private final String id;
    private final long time;
}