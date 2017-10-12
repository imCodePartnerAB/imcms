package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.mapping.jpa.doc.Meta;
import com.imcode.imcms.mapping.jpa.doc.MetaRepository;
import org.springframework.stereotype.Service;

@Service
public class MetaService {

    private final MetaRepository metaRepository;

    public MetaService(MetaRepository metaRepository) {
        this.metaRepository = metaRepository;
    }

    public Meta getOne(int docId) {
        return metaRepository.findOne(docId);
    }

}
