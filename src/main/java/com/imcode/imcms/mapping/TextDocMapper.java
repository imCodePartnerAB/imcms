package com.imcode.imcms.mapping;

import com.google.common.base.Optional;
import com.imcode.imcms.api.ContentLoopItemRef;
import com.imcode.imcms.api.DocVersionRef;
import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.dao.TextDocDao;
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

    public Map<DocumentLanguage, TextDomainObject> getTexts(DocVersionRef ref, int textNo, Optional<ContentLoopItemRef> loopItemRefOpt, boolean createIfNotExists) {
        throw new NotImplementedException();
    }

    public Map<DocumentLanguage, TextDomainObject> getImages(DocVersionRef ref, int textNo, Optional<ContentLoopItemRef> loopItemRefOpt, boolean createIfNotExists) {
        throw new NotImplementedException();
    }
}
