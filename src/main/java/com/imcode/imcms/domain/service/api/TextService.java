package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.mapping.jpa.doc.content.textdoc.TextRepository;
import org.springframework.stereotype.Service;

@Service
public class TextService {

    private final TextRepository textRepository;

    public TextService(TextRepository textRepository) {
        this.textRepository = textRepository;
    }

}
