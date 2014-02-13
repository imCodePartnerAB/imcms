package com.imcode.imcms.mapping;

import com.google.common.base.Optional;
import com.imcode.imcms.api.LoopItemRef;
import com.imcode.imcms.api.DocRef;
import com.imcode.imcms.api.DocVersionRef;
import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.dao.TextDocDao;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Map;

@Service
// fixme: implment
public class TextDocMapper {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private TextDocDao textDocDao;


    public Map<DocumentLanguage, Map<Integer, TextDomainObject>> getTexts(DocVersionRef ref, Optional<LoopItemRef> loopItemRefOpt, boolean createIfNotExists) {
        throw new NotImplementedException();
    }

    public Map<DocumentLanguage, TextDomainObject> getTexts(DocVersionRef ref, int textNo, Optional<LoopItemRef> loopItemRefOpt, boolean createIfNotExists) {
        throw new NotImplementedException();
    }

    public Map<Integer, TextDomainObject> getTexts(DocRef ref, Optional<LoopItemRef> loopItemRefOpt, boolean createIfNotExists) {
        throw new NotImplementedException();
    }

    public TextDomainObject getText(DocRef ref, int textNo, Optional<LoopItemRef> loopItemRefOpt, boolean createIfNotExists) {
        throw new NotImplementedException();
    }



    public Map<DocumentLanguage, ImageDomainObject> getImages(DocVersionRef ref, int textNo, Optional<LoopItemRef> loopItemRefOpt, boolean createIfNotExists) {
        throw new NotImplementedException();
    }

    public ImageDomainObject getImage(DocRef ref, int textNo, Optional<LoopItemRef> loopItemRefOpt, boolean createIfNotExists) {
        throw new NotImplementedException();
    }



    public Map<DocumentLanguage, TextDomainObject> getLoopTexts(DocVersionRef ref, int textNo, LoopItemRef loopItemRef, boolean createIfNotExists) {
        throw new NotImplementedException();
    }

    public Map<DocumentLanguage, TextDomainObject> getLoopText(DocRef ref, int textNo, LoopItemRef loopItemRef, boolean createIfNotExists) {
        throw new NotImplementedException();
    }

    public Map<DocumentLanguage, TextDomainObject> getLoopImages(DocVersionRef ref, int textNo, LoopItemRef loopItemRef, boolean createIfNotExists) {
        throw new NotImplementedException();
    }

    public Map<DocumentLanguage, TextDomainObject> getLoopImage(DocRef ref, int textNo, LoopItemRef loopItemRef, boolean createIfNotExists) {
        throw new NotImplementedException();
    }



    public Map<DocumentLanguage, TextDomainObject> getAllTexts(DocVersionRef ref, int textNo, boolean createIfNotExists) {
        throw new NotImplementedException();
    }

    public Map<DocumentLanguage, TextDomainObject> getAllImages(DocVersionRef ref, int textNo, boolean createIfNotExists) {
        throw new NotImplementedException();
    }

    // get loop
    // get menu
    // get templates


}
