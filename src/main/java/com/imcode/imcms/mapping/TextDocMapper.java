package com.imcode.imcms.mapping;

import com.google.common.base.Optional;
import com.imcode.imcms.api.*;
import com.imcode.imcms.mapping.dao.TextDocDao;
import imcode.server.document.GetterDocumentReference;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
@Transactional
// fixme: implment
public class TextDocMapper {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private TextDocDao textDocDao;

    // -----------------------------------------------------------------------------------------------------------------
    public List<TextDocumentTextWrapper> getAllTexts(DocVersionRef docVersionRef) {
        throw new NotImplementedException();
    }

    // -----------------------------------------------------------------------------------------------------------------
    public Map<DocumentLanguage, Map<Integer, TextDomainObject>> getTexts(DocVersionRef docVersionRef) {
        throw new NotImplementedException();
    }

    public Map<DocumentLanguage, Optional<TextDomainObject>> getTexts(DocVersionRef docVersionRef, int textNo) {
        throw new NotImplementedException();
    }

    public Map<Integer, TextDomainObject> getTexts(DocRef docRef) {
        throw new NotImplementedException();
    }

    public Optional<TextDomainObject> getText(DocRef docRef, int textNo) {
        throw new NotImplementedException();
    }

    // -----------------------------------------------------------------------------------------------------------------
    public Map<DocumentLanguage, Map<LoopItemRef, TextDomainObject>> getLoopTexts(DocVersionRef docVersionRef) {
        throw new NotImplementedException();
    }

    public Map<DocumentLanguage, Optional<TextDomainObject>> getLoopTexts(DocVersionRef docVersionRef, LoopItemRef loopItemRef) {
        throw new NotImplementedException();
    }

    public Map<LoopItemRef, TextDomainObject> getLoopTexts(DocRef docRef) {
        throw new NotImplementedException();
    }

    public Optional<TextDomainObject> getLoopText(DocRef docRef, LoopItemRef loopItemRef) {
        throw new NotImplementedException();
    }

    // -----------------------------------------------------------------------------------------------------------------
    public List<TextDocumentImageWrapper> getAllImages(DocVersionRef docVersionRef) {
        throw new NotImplementedException();
    }

    // -----------------------------------------------------------------------------------------------------------------
    public Map<DocumentLanguage, Map<Integer, ImageDomainObject>> getImages(DocVersionRef docVersionRef) {
        throw new NotImplementedException();
    }

    public Map<DocumentLanguage, Optional<ImageDomainObject>> getImages(DocVersionRef docVersionRef, int textNo) {
        throw new NotImplementedException();
    }

    public Map<Integer, ImageDomainObject> getImages(DocRef docRef) {
        throw new NotImplementedException();
    }

    public Optional<ImageDomainObject> getImage(DocRef docRef, int textNo) {
        throw new NotImplementedException();
    }

    // -----------------------------------------------------------------------------------------------------------------
    public Map<DocumentLanguage, Map<LoopItemRef, ImageDomainObject>> getLoopImages(DocVersionRef docVersionRef) {
        throw new NotImplementedException();
    }

    public Map<DocumentLanguage, Optional<ImageDomainObject>> getLoopImages(DocVersionRef docVersionRef, LoopItemRef loopItemRef) {
        throw new NotImplementedException();
    }

    public Map<LoopItemRef, ImageDomainObject> getLoopImages(DocRef docRef) {
        throw new NotImplementedException();
    }

    public Optional<ImageDomainObject> getLoopImage(DocRef docRef, LoopItemRef loopItemRef) {
        throw new NotImplementedException();
    }

    // -----------------------------------------------------------------------------------------------------------------
    public Map<Integer, Loop> getLoops(DocVersionRef docVersionRef) {
        throw new NotImplementedException();
    }

    public Optional<Loop> getLoop(DocVersionRef docVersionRef, int no) {
        throw new NotImplementedException();
    }


    // -----------------------------------------------------------------------------------------------------------------
    public Map<Integer, MenuDomainObject> getMenus(DocVersionRef docVersionRef) {
        throw new NotImplementedException();
    }

    private void initMenuItems(MenuDomainObject menu, DocumentGetter documentGetter) {

        for (Map.Entry<Integer, MenuItemDomainObject> entry : menu.getItemsMap().entrySet()) {
            Integer referencedDocumentId = entry.getKey();
            MenuItemDomainObject menuItem = entry.getValue();
            GetterDocumentReference gtr = new GetterDocumentReference(referencedDocumentId, documentGetter);

            menuItem.setDocumentReference(gtr);
        }
    }


    // get menu
    // get template
    // get include
}
