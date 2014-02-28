package com.imcode.imcms.mapping;

import com.imcode.imcms.api.Loop;
import com.imcode.imcms.mapping.container.*;
import com.imcode.imcms.mapping.jpa.doc.DocRepository;
import com.imcode.imcms.mapping.jpa.doc.DocVersion;
import com.imcode.imcms.mapping.jpa.doc.DocVersionRepository;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Include;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.IncludeRepository;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.LoopRepository;
import imcode.server.document.GetterDocumentReference;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TextDocumentInitializer {

    private DocumentGetter documentGetter;

    @Inject
    private DocRepository metaRepository;

    @Inject
    private LoopRepository loopRepository;

    @Inject
    private TextDocumentContentMapper textDocMapper;

    @Inject
    private DocVersionRepository docVersionRepository;

    @Inject
    private IncludeRepository includeRepository;

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
        for (TextDocTextContainer textContainer : textDocMapper.getAllTexts(document.getRef())) {
            LoopEntryRef loopEntryRef = textContainer.getLoopEntryRef();

            if (loopEntryRef == null) {
                document.setText(textContainer.getTextNo(), textContainer.getText());
            } else {
                document.setText(
                        LoopItemRef.of(loopEntryRef.getLoopNo(), loopEntryRef.getEntryNo(), textContainer.getTextNo()),
                        textContainer.getText()
                );
            }
        }
    }



    public void initIncludes(TextDocumentDomainObject document) {
        Collection<Include> includes = includeRepository.findByDocId(document.getMeta().getId());

        Map<Integer, Integer> includesMap = new HashMap<>();

        for (Include include : includes) {
            includesMap.put(include.getNo(), include.getIncludedDocumentId());
        }

        document.setIncludesMap(includesMap);
    }


    public void initTemplateNames(TextDocumentDomainObject document) {
        TextDocumentDomainObject.TemplateNames templateNames = textDocMapper.getTemplateNames(document.getMeta().getId());

        if (templateNames == null) {
            templateNames = new TextDocumentDomainObject.TemplateNames();
        }

        document.setTemplateNames(templateNames);
    }


    public void initImages(TextDocumentDomainObject document) {
        for (TextDocImageContainer imageContainer : textDocMapper.getAllImages(document.getRef())) {
            LoopEntryRef loopEntryRef = imageContainer.getLoopEntryRef();

            if (loopEntryRef == null) {
                document.setImage(imageContainer.getImageNo(), imageContainer.getImage());
            } else {
                document.setImage(
                        LoopItemRef.of(loopEntryRef.getLoopNo(), loopEntryRef.getEntryNo(), imageContainer.getImageNo()),
                        imageContainer.getImage()
                );
            }
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


    /**
     * @throws IllegalStateException if a content loop is empty i.e. does not have a contents.
     */
    public void initContentLoops(TextDocumentDomainObject document) {
        DocRef docRef = document.getRef();
        DocVersion docVersion = docVersionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());

        List<com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop> loops = loopRepository.findByDocVersion(docVersion);
        Map<Integer, Loop> loopsMap = new HashMap<>();

        for (com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop loop : loops) {
            loopsMap.put(loop.getNo(), EntityConverter.fromEntity(loop));
        }

        document.setLoops(loopsMap);
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