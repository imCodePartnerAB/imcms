package com.imcode.imcms.service;

import com.imcode.imcms.mapping.dto.LoopDTO;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.LoopRepository;
import com.imcode.imcms.mapping.mapper.Mappable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 22.09.17.
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS)
public class LoopService {

    private final LoopRepository loopRepository;
    private final Mappable<Loop, LoopDTO> loopToDtoMapper;

    @Autowired
    public LoopService(LoopRepository loopRepository, Mappable<Loop, LoopDTO> loopToDtoMapper) {
        this.loopRepository = loopRepository;
        this.loopToDtoMapper = loopToDtoMapper;
    }

    public LoopDTO getLoop(Version version, int loopId) {
        return loopToDtoMapper.map(loopRepository.findByVersionAndNo(version, loopId));
    }
}
