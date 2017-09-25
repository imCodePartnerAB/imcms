package com.imcode.imcms.mapping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 22.09.17.
 */
@Data
@AllArgsConstructor
public class LoopDTO implements Serializable {

    private static final long serialVersionUID = 7251620455605095203L;

    private Integer docId;
    private Integer loopId;
    private Collection<LoopEntryDTO> entries;
}
