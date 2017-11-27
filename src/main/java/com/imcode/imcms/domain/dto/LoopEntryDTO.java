package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.LoopEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoopEntryDTO extends LoopEntry implements Serializable {
    private static final long serialVersionUID = 8928942908190412349L;

    private int index;
    private boolean enabled;

    public static LoopEntryDTO createEnabled(int index) {
        return new LoopEntryDTO(index, true);
    }

    public LoopEntryDTO(LoopEntry from) {
        super(from);
    }
}
