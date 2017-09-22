package com.imcode.imcms.service;

import com.imcode.imcms.mapping.jpa.doc.content.textdoc.LoopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 22.09.17.
 */
@Service
public class LoopService {

    private final LoopRepository loopRepository;

    @Autowired
    public LoopService(LoopRepository loopRepository) {
        this.loopRepository = loopRepository;
    }
}
