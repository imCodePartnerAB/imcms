package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.Loop;
import com.imcode.imcms.model.LoopEntry;
import com.imcode.imcms.persistence.entity.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoopDTO extends Loop implements Serializable {

    private static final long serialVersionUID = 7251620455605095203L;

    private Integer docId;
    private Integer index;
    private List<LoopEntryDTO> entries;

    public LoopDTO(Loop from, Version version) {
        super(from);
        this.docId = version.getDocId();
    }

    public static LoopDTO empty(int docId, int index) {
        return new LoopDTO(docId, index, Collections.emptyList());
    }

    @Override
    public List<LoopEntry> getEntries() {
        return (entries == null) ? null : new ArrayList<>(entries);
    }

    @Override
    public void setEntries(List<LoopEntry> entries) {
        this.entries = (entries == null) ? null
                : entries.stream().map(LoopEntryDTO::new).collect(Collectors.toList());
    }
}
