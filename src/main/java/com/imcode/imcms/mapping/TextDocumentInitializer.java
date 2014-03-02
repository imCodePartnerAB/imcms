package com.imcode.imcms.mapping;

import com.imcode.imcms.mapping.jpa.doc.DocRepository;
import imcode.server.document.GetterDocumentReference;
import imcode.server.document.textdocument.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;

@Service
public class TextDocumentInitializer {

    private DocumentGetter documentGetter;

    @Inject
    private DocRepository metaRepository;

    @Inject
    private TextDocumentContentMapper textDocMapper;

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
        for (Map.Entry<Integer, MenuDomainObject> e : textDocMapper.getMenus(document.getVersionRef()).entrySet()) {
            document.setMenu(e.getKey(), initMenuItems(e.getValue(), documentGetter));
        }
    }

    @Deprecated
    private MenuDomainObject initMenuItems(MenuDomainObject menu, DocumentGetter documentGetter) {

        for (Map.Entry<Integer, MenuItemDomainObject> entry : menu.getItemsMap().entrySet()) {
            Integer referencedDocumentId = entry.getKey();
            MenuItemDomainObject menuItem = entry.getValue();
            GetterDocumentReference gtr = new GetterDocumentReference(referencedDocumentId, documentGetter);

            menuItem.setDocumentReference(gtr);
        }

        return menu;
    }

    public void initContentLoops(TextDocumentDomainObject document) {
        document.setLoops(textDocMapper.getLoops(document.getVersionRef()));
    }

    public DocRepository getMetaRepository() {
        return metaRepository;
    }

    public void setMetaRepository(DocRepository metaRepository) {
        this.metaRepository = metaRepository;
    }


    public DocumentGetter getDocumentGetter() {
        return documentGetter;
    }

    public void setDocumentGetter(DocumentGetter documentGetter) {
        this.documentGetter = documentGetter;
    }
}