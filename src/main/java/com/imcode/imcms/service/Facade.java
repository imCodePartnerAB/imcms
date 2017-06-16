package com.imcode.imcms.service;

import com.imcode.imcms.imagearchive.service.TextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Should became a good facade. Implement access to services here.
 * <p>
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 31.03.17.
 */
@Service
public class Facade {
    private final TextService textService;

    @Autowired
    public Facade(TextService textService) {
        this.textService = textService;
    }

    public TextService getTextService() {
        return textService;
    }
}
