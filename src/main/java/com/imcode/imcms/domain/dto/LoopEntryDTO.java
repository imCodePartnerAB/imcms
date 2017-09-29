package com.imcode.imcms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class LoopEntryDTO implements Serializable {
    private static final long serialVersionUID = 8928942908190412349L;

    private Integer no;
    private boolean enabled;

    public LoopEntryDTO() {
    }
}
