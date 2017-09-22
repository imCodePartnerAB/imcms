package com.imcode.imcms.mapping.mapper;

import com.imcode.imcms.mapping.dto.LoopDTO;
import com.imcode.imcms.mapping.dto.LoopEntryDTO;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 22.09.17.
 */
@Component
public class LoopToDtoMapper implements Mappable<Loop, LoopDTO> {

    private final LoopEntryToDtoMapper loopEntryToDtoMapper;

    public LoopToDtoMapper(LoopEntryToDtoMapper loopEntryToDtoMapper) {
        this.loopEntryToDtoMapper = loopEntryToDtoMapper;
    }

    @Override
    public LoopDTO map(Loop loop) {
        final List<LoopEntryDTO> loopEntryDTOs = loop.getEntries()
                .stream()
                .map(loopEntryToDtoMapper::map)
                .collect(Collectors.toList());

        return new LoopDTO(loop.getDocumentId(), loop.getId(), loopEntryDTOs);
    }
}
