package com.imcode.imcms.mapping.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 22.09.17.
 */
@Data
public class LoopEntryDTO implements Serializable {
    private static final long serialVersionUID = 8928942908190412349L;

    private Integer no;
    private Boolean enabled;
    private String content;

    public LoopEntryDTO(int entryNo, boolean isEnabled, String content) {
        this.no = entryNo;
        this.enabled = isEnabled;
        this.content = content;
    }
}
