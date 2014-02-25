package com.imcode.imcms.mapping;

import com.imcode.imcms.api.Loop;
import com.imcode.imcms.mapping.dao.*;
import com.imcode.imcms.mapping.orm.*;
import imcode.server.document.GetterDocumentReference;
import imcode.server.document.textdocument.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class TextDocumentInitializer {

    private DocumentGetter documentGetter;

    @Inject
    private DocDao metaDao;

    @Inject
    private TextDocLoopDao textDocLoopDao;

    @Inject
    private TextDocMapper textDocMapper;

    @Inject
    private DocVersionDao docVersionDao;

    @Inject
    private TextDocIncludeDao textDocIncludeDao;

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

        for (Map.Entry<LoopItemRef, TextDomainObject> e : textDocMapper.getLoopTexts(document.getRef()).entrySet()) {
            document.setText(e.getKey(), e.getValue());
        }
    }


    public void initIncludes(TextDocumentDomainObject document) {
        Collection<TextDocInclude> textDocIncludes = textDocIncludeDao.findByDocId(document.getMeta().getId());

        Map<Integer, Integer> includesMap = new HashMap<>();

        for (TextDocInclude textDocInclude : textDocIncludes) {
            includesMap.put(textDocInclude.getNo(), textDocInclude.getIncludedDocumentId());
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
        for (Map.Entry<Integer, ImageDomainObject> e : textDocMapper.getImages(document.getRef()).entrySet()) {
            document.setImage(e.getKey(), e.getValue());
        }

        for (Map.Entry<LoopItemRef, ImageDomainObject> e : textDocMapper.getLoopImages(document.getRef()).entrySet()) {
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


    /**
     * @throws IllegalStateException if a content loop is empty i.e. does not have a contents.
     */
    public void initContentLoops(TextDocumentDomainObject document) {
        DocRef docRef = document.getRef();
        DocVersion docVersion = docVersionDao.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());

        List<TextDocLoop> loops = textDocLoopDao.findByDocVersion(docVersion);
        Map<Integer, Loop> loopsMap = new HashMap<>();

        for (TextDocLoop loop : loops) {
            loopsMap.put(loop.getNo(), OrmToApi.toApi(loop));
        }

        document.setLoops(loopsMap);
    }

    public DocDao getMetaDao() {
        return metaDao;
    }

    public void setMetaDao(DocDao metaDao) {
        this.metaDao = metaDao;
    }


    public DocumentGetter getDocumentGetter() {
        return documentGetter;
    }

    public void setDocumentGetter(DocumentGetter documentGetter) {
        this.documentGetter = documentGetter;
    }
}