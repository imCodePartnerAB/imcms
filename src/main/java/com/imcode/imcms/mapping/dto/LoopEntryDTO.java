package com.imcode.imcms.mapping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 22.09.17.
 */
@Data
@AllArgsConstructor
public class LoopEntryDTO implements Serializable {
    private static final long serialVersionUID = 8928942908190412349L;

    private Integer no;
    private Boolean enabled;
    private String content;
}
