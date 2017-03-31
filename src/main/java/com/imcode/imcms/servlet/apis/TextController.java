package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.document.text.TextContentFilter;
import com.imcode.imcms.mapping.TextDocumentContentLoader;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.container.TextDocTextContainer;
import com.imcode.imcms.mapping.container.VersionRef;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.LoopEntryRef;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.TextHistory;
import com.imcode.imcms.servlet.tags.TextTag;
import com.jcabi.w3c.Defect;
import com.jcabi.w3c.ValidationResponse;
import com.jcabi.w3c.ValidatorBuilder;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Shadowgun on 23.12.2014.
 * <p>
 * Refactored by Serhii Maksymchuk in 2017
 */
/*
 todo: Since {@link RequestMapping} can get in request path parameters like "/{id}/{someparam1}/{someparam2}" it would be better to REPLACE  {@link RequestParam} with {@link PathParam}
 */
@RestController
@RequestMapping("/text")
public class TextController {
    private static final Logger log = Logger.getLogger(TextController.class);

    private ImcmsServices imcmsServices;

    @Autowired
    private TextContentFilter textContentFilter;

    @PostConstruct
    public void init() {
        imcmsServices = Imcms.getServices();
    }

    /**
     * Provide API access to text history
     *
     * @param docId           {@link imcode.server.document.DocumentDomainObject} id
     * @param textNo          text id
     * @param locale          Content language. For more information about languages see {@link com.imcode.imcms.mapping.jpa.doc.Language} and {@link imcode.server.LanguageMapper}
     * @param loopEntryRefStr {@link com.imcode.imcms.mapping.container.LoopEntryRef} represented in text form
     * @return List of textHistory entities
     * @see TextDocumentContentLoader#getTextHistory(DocRef, int)
     * @see TextDocumentContentLoader#getTextHistory(DocRef, LoopEntryRef, int)
     */
    @RequestMapping
    public List<TextHistoryWebEntity> getTextHistory(@RequestParam("meta") int docId,
                                                     @RequestParam("no") int textNo,
                                                     @RequestParam("locale") String locale,
                                                     @RequestParam(value = "loopentryref", required = false) String loopEntryRefStr) {

        final com.imcode.imcms.mapping.container.LoopEntryRef loopEntryRefOpt;
        LoopEntryRef loopEntryRef = null;

        if (!StringUtils.isEmpty(loopEntryRefStr)) {
            final String[] items = loopEntryRefStr.split("_", 2);

            if (items.length > 1) {
                loopEntryRefOpt = com.imcode.imcms.mapping.container.LoopEntryRef.of(
                        Integer.parseInt(items[0]),
                        Integer.parseInt(items[1])
                );

                loopEntryRef = new LoopEntryRef(
                        loopEntryRefOpt.getLoopNo(),
                        loopEntryRefOpt.getEntryNo()
                );
            }
        }

        final VersionRef versionRef = imcmsServices.getDocumentMapper()
                .getWorkingDocument(docId)
                .getVersionRef();

        return Imcms.getServices()
                .getManagedBean(TextDocumentContentLoader.class)
                .getTextHistory(DocRef.of(versionRef, locale), loopEntryRef, textNo)
                .stream()
                .map(TextHistoryWebEntity::new)
                .collect(Collectors.toList());
    }

    /**
     * Save passed text into database. Also passed text saved into {@link TextHistory} table
     *
     * @param content      Content to save
     * @param locale       Content language. For more information about languages see {@link com.imcode.imcms.mapping.jpa.doc.Language} and {@link imcode.server.LanguageMapper}
     * @param docId        {@link imcode.server.document.DocumentDomainObject} id
     * @param textNo       text id
     * @param loopEntryRef {@link com.imcode.imcms.mapping.container.LoopEntryRef} represented in text form
     * @see VersionRef
     */
    @RequestMapping(method = RequestMethod.POST)
    public String saveText(@RequestParam("content") String content,
                           @RequestParam("locale") String locale,
                           @RequestParam("meta") int docId,
                           @RequestParam("no") int textNo,
                           @RequestParam(value = "loopentryref", required = false) String loopEntryRef,
                           @RequestParam(value = "contenttype", required = false) String contentType) {

        final UserDomainObject user = Imcms.getUser();
        final TextDocumentDomainObject doc = imcmsServices.getDocumentMapper().getWorkingDocument(docId);
        final TextDocumentPermissionSetDomainObject permissionSet = (TextDocumentPermissionSetDomainObject)
                user.getPermissionSetFor(doc);

        // fixme: v4.
        if (!permissionSet.getEditTexts()) {
            //AdminDoc.adminDoc(documentId, user, request, res, getServletContext)
            return "";
        }

        com.imcode.imcms.mapping.container.LoopEntryRef loopEntryRefOpt = null;

        if (!StringUtils.isEmpty(loopEntryRef)) {
            final String[] items = loopEntryRef.split("_", 2);

            if (items.length > 1)
                loopEntryRefOpt = com.imcode.imcms.mapping.container.LoopEntryRef.of(
                        Integer.parseInt(items[0]),
                        Integer.parseInt(items[1])
                );
        }

        if ((contentType != null) && (contentType.toLowerCase().contains("clean"))) {
            content = textContentFilter.cleanText(content);
        }

        try {
            final int contentTypeInt = Optional.ofNullable(contentType)
                    .map(type -> (type.contains(TextTag.TEXT) || type.contains(TextTag.SOURCE_FROM_HTML))
                            ? TextDomainObject.TEXT_TYPE_PLAIN
                            : TextDomainObject.TEXT_TYPE_HTML)
                    .orElse(TextDomainObject.TEXT_TYPE_HTML);

            final TextDomainObject textDomainObject = new TextDomainObject(
                    StringEscapeUtils.unescapeHtml4(content).trim(),
                    contentTypeInt
            );

            final VersionRef versionRef = VersionRef.of(docId, DocumentVersion.WORKING_VERSION_NO);
            final TextDocTextContainer container = TextDocTextContainer.of(
                    DocRef.of(versionRef, locale),
                    loopEntryRefOpt,
                    textNo,
                    textDomainObject
            );

            imcmsServices.getDocumentMapper().saveTextDocText(container, user);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error while saving text", e);
        }

        return content;
    }

    /**
     * Provide simple validation api.
     *
     * @param content Text, that should be validated by W3C
     * @return anonymous object entity
     * @throws IOException if content couldn't be validated
     * @see ValidatorBuilder
     * @see ValidationResponse
     * @see com.jcabi.w3c.Validator
     * @see Defect
     */
    @RequestMapping(method = RequestMethod.POST, value = "/validate")
    public TextValidationResultWebEntity validateText(@RequestParam("content") String content) throws IOException {
        final String contentWrapper = "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
                "<head><title>Test</title></head><body>%s</body></html>";
        content = String.format(contentWrapper, content);

        final ValidationResponse response = new ValidatorBuilder()
                .html()
                .validate(content);

        return new TextValidationResultWebEntity(response);
    }

    private class TextValidationResultWebEntity {
        public boolean result;
        public String message;
        public ValidationData data;

        TextValidationResultWebEntity(ValidationResponse response) {
            this.result = response.valid();
            this.message = response.toString();
            this.data = new ValidationData(response);
        }

        private class ValidationData {
            public Set<DefectWebEntity> errors;
            public Set<DefectWebEntity> warnings;

            ValidationData(ValidationResponse response) {
                this.errors = response.errors().stream().map(DefectWebEntity::new).collect(Collectors.toSet());
                this.warnings = response.warnings().stream().map(DefectWebEntity::new).collect(Collectors.toSet());
            }

            private class DefectWebEntity {
                public String message;
                public String source;
                public String explanation;
                public int column;
                public int line;

                DefectWebEntity(Defect defect) {
                    this.message = defect.message();
                    this.source = defect.source().replace("&#60;/body&#62;&#60;/html&#62;", "");
                    this.explanation = defect.explanation();
                    this.column = defect.column();
                    this.line = defect.line();
                }
            }
        }
    }

    private class TextHistoryWebEntity {
        public String modifiedBy;
        public long modifiedDate;
        public String text;
        public String type;

        TextHistoryWebEntity(TextHistory textHistory) {
            this.modifiedBy = textHistory.getModifiedBy().getFirstName();
            this.modifiedDate = textHistory.getModifiedDt().getTime();
            this.text = textHistory.getText();
            this.type = textHistory.getType().name().toLowerCase();
        }
    }
}
