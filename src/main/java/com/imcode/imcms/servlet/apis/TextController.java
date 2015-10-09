package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.mapping.DocumentSaveException;
import com.imcode.imcms.mapping.TextDocumentContentLoader;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.container.LoopEntryRef;
import com.imcode.imcms.mapping.container.TextDocTextContainer;
import com.imcode.imcms.mapping.container.VersionRef;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.TextHistory;
import com.jcabi.w3c.Defect;
import com.jcabi.w3c.ValidationResponse;
import com.jcabi.w3c.ValidatorBuilder;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Shadowgun on 23.12.2014.
 */
/*
 todo: Since {@link RequestMapping} can get in request path parameters like "/{id}/{someparam1}/{someparam2}" it would be better to REPLACE  {@link RequestParam} with {@link PathParam}
 */
@RestController
@RequestMapping("/text")
public class TextController {

    private ImcmsServices imcmsServices;

    public TextController() {
        imcmsServices = Imcms.getServices();
    }

    /**
     * Provide API access to text history
     * @param docId {@link imcode.server.document.DocumentDomainObject} id
     * @param textNo text id
     * @param locale Content language. For more information about languages see {@link com.imcode.imcms.mapping.jpa.doc.Language} and {@link imcode.server.LanguageMapper}
     * @param loopEntryRef {@link LoopEntryRef} represented in text form
     * @return List of textHistory entities
     *
     * @see TextDocumentContentLoader#getTextHistory(DocRef, int)
     * @see TextDocumentContentLoader#getTextHistory(DocRef, com.imcode.imcms.mapping.jpa.doc.content.textdoc.LoopEntryRef, int)
     */
    @RequestMapping
    public Object getTextHistory(@RequestParam("meta") int docId,
                                 @RequestParam("no") int textNo,
                                 @RequestParam("locale") String locale,
                                 @RequestParam(value = "loopentryref", required = false) String loopEntryRef) {
        TextDocumentContentLoader contentLoader = Imcms.getServices().getManagedBean(TextDocumentContentLoader.class);
        DocRef docRef = DocRef.of(imcmsServices.getDocumentMapper().getDocument(docId).getVersionRef(), locale);
        LoopEntryRef loopEntryRefOpt = null;
        if (!StringUtils.isEmpty(loopEntryRef)) {
            String[] items = loopEntryRef.split("_", 2);
            if (items.length > 1)
                loopEntryRefOpt = LoopEntryRef.of(Integer.parseInt(items[0]), Integer.parseInt(items[1]));
        }
        Collection<TextHistory> textHistories = loopEntryRefOpt == null ?
                contentLoader.getTextHistory(docRef, textNo) :
                contentLoader.getTextHistory(docRef,
                        new com.imcode.imcms.mapping.jpa.doc.content.textdoc.LoopEntryRef(loopEntryRefOpt.getLoopNo(), loopEntryRefOpt.getEntryNo()),
                        textNo);

        return textHistories.stream().map(it -> new Object() {
            public String modifiedBy = it.getModifiedBy().getFirstName();
            public String modifiedDate = it.getModifiedDt().toString();
            public String text = it.getText();
        }).collect(Collectors.toList());
    }

    /**
     * Save passed text into database. Also passed text saved into {@link TextHistory} table
     * @param content Content to save
     * @param locale Content language. For more information about languages see {@link com.imcode.imcms.mapping.jpa.doc.Language} and {@link imcode.server.LanguageMapper}
     * @param docId {@link imcode.server.document.DocumentDomainObject} id
     * @param textNo text id
     * @param loopEntryRef {@link LoopEntryRef} represented in text form
     *
     * @see VersionRef
     *
     */
    @RequestMapping(method = RequestMethod.POST)
    protected void saveText(@RequestParam("content") String content,
                            @RequestParam("locale") String locale,
                            @RequestParam("meta") int docId,
                            @RequestParam("no") int textNo,
                            @RequestParam(value = "loopentryref", required = false) String loopEntryRef) {

        UserDomainObject user = Imcms.getUser();
        TextDocumentDomainObject doc = imcmsServices.getDocumentMapper().getWorkingDocument(docId);

        TextDocumentPermissionSetDomainObject permissionSet = (TextDocumentPermissionSetDomainObject)
                user.getPermissionSetFor(doc);

        // fixme: v4.
        if (!permissionSet.getEditTexts()) {
            //AdminDoc.adminDoc(documentId, user, request, res, getServletContext)
            return;
        }

        VersionRef versionRef = VersionRef.of(docId, DocumentVersion.WORKING_VERSION_NO);
        LoopEntryRef loopEntryRefOpt = null;
        if (!StringUtils.isEmpty(loopEntryRef)) {
            String[] items = loopEntryRef.split("_", 2);
            if (items.length > 1)
                loopEntryRefOpt = LoopEntryRef.of(Integer.parseInt(items[0]), Integer.parseInt(items[1]));
        }

        try {
            imcmsServices.getDocumentMapper().saveTextDocText(
                    TextDocTextContainer.of(
                            DocRef.of(versionRef, locale),
                            loopEntryRefOpt, textNo,
                            new TextDomainObject(content.trim(),
                                    TextDomainObject.TEXT_TYPE_HTML)
                    ), user);
        } catch (DocumentSaveException e) {
            e.printStackTrace();
        }
    }

    /**
     * Provide simple validation api.
     *
     * @param content Text, that should be validated by W3C
     * @return anonymous object entity
     * @throws IOException
     *
     * @see ValidatorBuilder
     * @see ValidationResponse
     * @see com.jcabi.w3c.Validator
     * @see Defect
     */
    @RequestMapping(method = RequestMethod.POST, value = "/validate")
    public Object validateText(@RequestParam("content") String content) throws IOException {
        content = String.format("<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>Test</title></head><body>%s</body></html>", content);
        ValidationResponse response = new ValidatorBuilder().html().validate(content);
        Function<Defect, Object> mapper = it -> new Object() {
            public String message = it.message();
            public String source = it.source().replace("&#60;/body&#62;&#60;/html&#62;", "");
            public String explanation = it.explanation();
            public int column = it.column();
            public int line = it.line();
        };

        return new Object() {
            public boolean result = response.valid();
            public String message = response.toString();
            public Object data = new Object() {
                public Set<Object> errors = response.errors().stream().map(mapper).collect(Collectors.toSet());
                public Set<Object> warnings = response.warnings().stream().map(mapper).collect(Collectors.toSet());
            };
        };
    }
}
