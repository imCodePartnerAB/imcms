package com.imcode.imcms.mapping.mapper;

import com.imcode.imcms.mapping.dto.LoopDTO;
import com.imcode.imcms.mapping.dto.LoopEntryDTO;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop.Entry;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 22.09.17.
 */
@Component
public class LoopToDtoMapper implements Function<Loop, LoopDTO> {

    private final Function<Entry, LoopEntryDTO> loopEntryToDtoMapper;

    public LoopToDtoMapper(Function<Entry, LoopEntryDTO> loopEntryToDtoMapper) {
        this.loopEntryToDtoMapper = loopEntryToDtoMapper;
    }

    @Override
    public LoopDTO apply(Loop loop) {
        final List<LoopEntryDTO> loopEntryDTOs = Objects.requireNonNull(loop)
                .getEntries()
                .stream()
                .map(loopEntryToDtoMapper)
                .collect(Collectors.toList());

        return new LoopDTO(loop.getDocumentId(), loop.getNo(), loopEntryDTOs);
    }
}
