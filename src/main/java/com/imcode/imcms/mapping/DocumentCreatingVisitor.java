package com.imcode.imcms.mapping;

import com.imcode.imcms.mapping.orm.HtmlDocContent;
import com.imcode.imcms.mapping.orm.UrlDocContent;
import imcode.server.ImcmsServices;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;

import com.imcode.imcms.mapping.dao.MetaDao;
import org.springframework.transaction.annotation.Transactional;

/**
 * Not a public API. Must not be used directly.
 */
public class DocumentCreatingVisitor extends DocumentStoringVisitor {

    private UserDomainObject currentUser;

    public DocumentCreatingVisitor(ImcmsServices services, UserDomainObject currentUser) {
        super(services);
        this.currentUser = currentUser;
    }

    @Transactional
    public void visitHtmlDocument(HtmlDocumentDomainObject document) {
        HtmlDocContent reference = new HtmlDocContent();

        //fixme
        //reference.setDocRef(document.getRef());
        reference.setHtml(document.getHtml());

        MetaDao dao = services.getManagedBean(MetaDao.class);

        dao.saveHtmlReference(reference);
    }

    @Transactional
    public void visitUrlDocument(UrlDocumentDomainObject document) {
        UrlDocContent reference = new UrlDocContent();

        //fixme
        //reference.setDocRef(document.getRef());
        reference.setUrlTarget("");
        reference.setUrlText("");
        reference.setUrlLanguagePrefix("");
        reference.setUrlFrameName("");
        reference.setUrl(document.getUrl());

        MetaDao dao = services.getManagedBean(MetaDao.class);

        dao.saveUrlReference(reference);
    }

    @Transactional
    public void visitTextDocument(final TextDocumentDomainObject textDocument) {
        updateTextDocumentContentLoops(textDocument, currentUser);
        updateTextDocumentTemplateNames(textDocument, currentUser);
        updateTextDocumentTexts(textDocument, currentUser);
        updateTextDocumentImages(textDocument, currentUser);
        updateTextDocumentIncludes(textDocument);
        updateTextDocumentMenus(textDocument, currentUser);
    }
}
