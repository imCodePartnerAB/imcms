package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.LoopEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class LoopEntryDTO extends LoopEntry implements Serializable {
    private static final long serialVersionUID = 8928942908190412349L;

    private int index;
    private boolean enabled;

    public LoopEntryDTO(LoopEntry from) {
        super(from);
    }

    public static LoopEntryDTO createEnabled(int index) {
        return new LoopEntryDTO(index, true);
    }
}
