package com.imcode.imcms.mapping;

import com.google.common.base.Optional;
import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.Loop;
import com.imcode.imcms.mapping.container.*;
import com.imcode.imcms.mapping.jpa.doc.DocVersion;
import com.imcode.imcms.mapping.jpa.doc.DocVersionRepository;
import com.imcode.imcms.mapping.jpa.doc.LanguageRepository;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.*;
import imcode.server.document.GetterDocumentReference;
import imcode.server.document.textdocument.*;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
// fixme: implment
// fixme: images: TextDocumentUtils.initImagesSources
public class TextDocumentContentMapper {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private DocVersionRepository docVersionRepository;

    @Inject
    private TextRepository textRepository;

    @Inject
    private ImageRepository imageRepository;

    @Inject
    private MenuRepository menuRepository;

    @Inject
    TemplateNamesRepository templateNamesRepository;

    @Inject
    private LoopRepository loopRepository;

    @Inject
    private LanguageRepository languageRepository;

    //fixme: init to doc mapper
    private DocumentGetter menuItemDocumentGetter;

    public List<TextDocTextContainer> getAllTexts(DocRef docRef) {
        throw new NotImplementedException();
    }

    public Map<DocumentLanguage, Optional<TextDomainObject>> getTexts(DocVersionRef docVersionRef, int textNo) {
        throw new NotImplementedException();
    }

    public TextDomainObject getText(DocRef docRef, int textNo) {
        throw new NotImplementedException();
    }

    public Map<DocumentLanguage, Optional<TextDomainObject>> getLoopTexts(DocVersionRef docVersionRef, LoopItemRef loopItemRef) {
        throw new NotImplementedException();
    }

    public TextDomainObject getLoopText(DocRef docRef, LoopItemRef loopItemRef) {
        throw new NotImplementedException();
    }


    public List<TextDocImageContainer> getAllImages(DocRef docRef) {
        throw new NotImplementedException();
    }

    public Map<DocumentLanguage, Optional<ImageDomainObject>> getImages(DocVersionRef docVersionRef, int textNo) {
        throw new NotImplementedException();
    }

    public ImageDomainObject getImage(DocRef docRef, int textNo) {
        throw new NotImplementedException();
    }

    public Map<DocumentLanguage, Optional<ImageDomainObject>> getLoopImages(DocVersionRef docVersionRef, LoopItemRef loopItemRef) {
        throw new NotImplementedException();
    }

    public ImageDomainObject getLoopImage(DocRef docRef, LoopItemRef loopItemRef) {
        throw new NotImplementedException();
    }


    public Map<Integer, Loop> getLoops(DocVersionRef docVersionRef) {
        throw new NotImplementedException();
    }

    public Loop getLoop(DocVersionRef docVersionRef, int no) {
        throw new NotImplementedException();
    }


    public Map<Integer, MenuDomainObject> getMenus(DocVersionRef docVersionRef) {
        DocVersion docVersion = docVersionRepository.findByDocIdAndNo(docVersionRef.getDocId(), docVersionRef.getDocVersionNo());
        List<Menu> textDocMenus = menuRepository.getByDocVersion(docVersion);
        Map<Integer, MenuDomainObject> menus = new HashMap<>();

        for (Menu menu : textDocMenus) {
            menus.put(menu.getNo(), initMenuItems(EntityConverter.fromEntity(menu)));
        }

        return menus;
    }

    private MenuDomainObject initMenuItems(MenuDomainObject menu) {
        for (Map.Entry<Integer, MenuItemDomainObject> entry : menu.getItemsMap().entrySet()) {
            Integer referencedDocumentId = entry.getKey();
            MenuItemDomainObject menuItem = entry.getValue();
            GetterDocumentReference gtr = new GetterDocumentReference(referencedDocumentId, menuItemDocumentGetter);

            menuItem.setDocumentReference(gtr);
        }

        return menu;
    }

    public TextDocumentDomainObject.TemplateNames getTemplateNames(int docId) {
        return EntityConverter.fromEntity(templateNamesRepository.findOne(docId));
    }

    // get include
}
