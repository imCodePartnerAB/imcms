package com.imcode.imcms.imagearchive.service;

import com.imcode.imcms.document.text.TextContentFilter;
import com.imcode.imcms.mapping.TextDocumentContentInitializer;
import com.imcode.imcms.mapping.TextDocumentContentLoader;
import com.imcode.imcms.mapping.TextDocumentContentSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for work with text
 *
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 31.03.17.
 */
@Service
public class TextService {
    @Autowired
    private TextContentFilter textContentFilter;
    @Autowired
    private TextDocumentContentLoader contentLoader;
    @Autowired
    private TextDocumentContentInitializer contentInitializer;
    @Autowired
    private TextDocumentContentSaver contentSaver;

    public TextContentFilter getTextContentFilter() {
        return textContentFilter;
    }

    public TextDocumentContentLoader getContentLoader() {
        return contentLoader;
    }

    public TextDocumentContentInitializer getContentInitializer() {
        return contentInitializer;
    }

    public TextDocumentContentSaver getContentSaver() {
        return contentSaver;
    }
}
