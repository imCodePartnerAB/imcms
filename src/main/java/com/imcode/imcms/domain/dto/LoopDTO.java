package com.imcode.imcms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoopDTO implements Serializable {

    private static final long serialVersionUID = 7251620455605095203L;

    private Integer docId;
    private Integer index;
    private List<LoopEntryDTO> entries;

    public static LoopDTO empty(int docId, int index) {
        return new LoopDTO(docId, index, Collections.emptyList());
    }
}
