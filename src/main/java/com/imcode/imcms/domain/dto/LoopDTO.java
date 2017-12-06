package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.Loop;
import com.imcode.imcms.model.LoopEntry;
import com.imcode.imcms.persistence.entity.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoopDTO extends Loop<LoopEntryDTO> implements Serializable {

    private static final long serialVersionUID = 7251620455605095203L;

    private Integer docId;
    private Integer index;
    private List<LoopEntryDTO> entries;

    public <LE2 extends LoopEntry, L extends Loop<LE2>> LoopDTO(L from, Version version) {
        super(from, LoopEntryDTO::new);
        this.docId = version.getDocId();
    }

    public static LoopDTO empty(int docId, int index) {
        return new LoopDTO(docId, index, Collections.emptyList());
    }
}
