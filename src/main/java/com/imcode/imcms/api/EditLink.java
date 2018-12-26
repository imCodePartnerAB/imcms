package com.imcode.imcms.api;

import com.imcode.imcms.model.LoopEntryRef;
import lombok.Data;

@Data
public class EditLink {
    private Integer metaId;
    private String title;
    private Integer index;
    private LoopEntryRef loopEntryRef;
}
