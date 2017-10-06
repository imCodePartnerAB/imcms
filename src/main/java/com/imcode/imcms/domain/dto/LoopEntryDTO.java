package com.imcode.imcms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoopEntryDTO implements Serializable {
    private static final long serialVersionUID = 8928942908190412349L;

    private Integer index;
    private boolean enabled;
}
