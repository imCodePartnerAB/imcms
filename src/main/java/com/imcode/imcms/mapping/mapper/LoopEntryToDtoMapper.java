package com.imcode.imcms.mapping.mapper;

import com.imcode.imcms.mapping.dto.LoopEntryDTO;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop.Entry;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 22.09.17.
 */
@Component
public class LoopEntryToDtoMapper implements Function<Entry, LoopEntryDTO> {

    @Override
    public LoopEntryDTO apply(Entry entry) {
        final String fakeContent = "Lorem ipsum... "; // todo: fill correct content, maybe on client side

        return new LoopEntryDTO(entry.getNo(), entry.isEnabled(), fakeContent);
    }

}
