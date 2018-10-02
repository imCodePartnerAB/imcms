package com.imcode.imcms.mapping;

import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TextDocumentContentInitializer {

    private final TextDocumentContentLoader contentLoader;

    public TextDocumentContentInitializer(TextDocumentContentLoader contentLoader) {
        this.contentLoader = contentLoader;
    }

    /**
     * Initializes text document.
     */
    public void initialize(TextDocumentDomainObject document) {
        initContentLoops(document);
        initTexts(document);
        initImages(document);
//        initMenus(document);
        initTemplateNames(document);
    }

    private void initTexts(TextDocumentDomainObject document) {
        for (Map.Entry<Integer, TextDomainObject> e : contentLoader.getTexts(document.getRef()).entrySet()) {
            document.setText(e.getKey(), e.getValue());
        }

        for (Map.Entry<TextDocumentDomainObject.LoopItemRef, TextDomainObject> e : contentLoader.getLoopTexts(document.getRef()).entrySet()) {
            document.setText(e.getKey(), e.getValue());
        }
    }


    private void initTemplateNames(TextDocumentDomainObject document) {
        TextDocumentDomainObject.TemplateNames templateNames = contentLoader.getTemplateNames(document.getMeta().getId());

        if (templateNames == null) {
            templateNames = new TextDocumentDomainObject.TemplateNames();
        }

        document.setTemplateNames(templateNames);
    }


    private void initImages(TextDocumentDomainObject document) {
        for (Map.Entry<Integer, ImageDomainObject> e : contentLoader.getImages(document.getRef()).entrySet()) {
            document.setImage(e.getKey(), e.getValue());
        }

        for (Map.Entry<TextDocumentDomainObject.LoopItemRef, ImageDomainObject> e : contentLoader.getLoopImages(document.getRef()).entrySet()) {
            document.setImage(e.getKey(), e.getValue());
        }
    }


    private void initContentLoops(TextDocumentDomainObject document) {
        document.setLoops(contentLoader.getLoops(document.getVersionRef()));
    }
}