package com.imcode.imcms.mapping;

import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;

@Service
public class TextDocumentInitializer {

    @Inject
    private TextDocumentContentSaver textDocMapper;

    /**
     * Initializes text document.
     */
    public void initialize(TextDocumentDomainObject document) {
        initContentLoops(document);
        initTexts(document);
        initImages(document);
        initMenus(document);
        initIncludes(document);
        initTemplateNames(document);
    }

    public void initTexts(TextDocumentDomainObject document) {
        for (Map.Entry<Integer, TextDomainObject> e : textDocMapper.getTexts(document.getRef()).entrySet()) {
            document.setText(e.getKey(), e.getValue());
        }

        for (Map.Entry<TextDocumentDomainObject.LoopItemRef, TextDomainObject> e : textDocMapper.getLoopTexts(document.getRef()).entrySet()) {
            document.setText(e.getKey(), e.getValue());
        }
    }



    public void initIncludes(TextDocumentDomainObject document) {
        document.setIncludesMap(textDocMapper.getIncludes(document.getId()));
    }


    public void initTemplateNames(TextDocumentDomainObject document) {
        TextDocumentDomainObject.TemplateNames templateNames = textDocMapper.getTemplateNames(document.getMeta().getId());

        if (templateNames == null) {
            templateNames = new TextDocumentDomainObject.TemplateNames();
        }

        document.setTemplateNames(templateNames);
    }


    public void initImages(TextDocumentDomainObject document) {
        for (Map.Entry<Integer, ImageDomainObject> e : textDocMapper.getImages(document.getRef()).entrySet()) {
            document.setImage(e.getKey(), e.getValue());
        }

        for (Map.Entry<TextDocumentDomainObject.LoopItemRef, ImageDomainObject> e : textDocMapper.getLoopImages(document.getRef()).entrySet()) {
            document.setImage(e.getKey(), e.getValue());
        }
    }


    public void initMenus(TextDocumentDomainObject document) {
        document.setMenus(textDocMapper.getMenus(document.getVersionRef()));
    }

    public void initContentLoops(TextDocumentDomainObject document) {
        document.setLoops(textDocMapper.getLoops(document.getVersionRef()));
    }
}