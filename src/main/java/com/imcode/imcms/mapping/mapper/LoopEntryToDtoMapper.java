package com.imcode.imcms.mapping.mapper;

import com.imcode.imcms.mapping.dto.LoopEntryDTO;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop;
import org.springframework.stereotype.Component;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 22.09.17.
 */
@Component
public class LoopEntryToDtoMapper implements Mappable<Loop.Entry, LoopEntryDTO> {
    @Override
    public LoopEntryDTO map(Loop.Entry entry) {
        final String fakeContent = "Lorem ipsum... "; // todo: fill correct content, maybe on client side

        return new LoopEntryDTO(entry.getNo(), entry.isEnabled(), fakeContent);
    }
}
