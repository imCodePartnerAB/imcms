//package com.imcode.imcms.servlet.tags;
//
//import com.imcode.imcms.mapping.container.LoopEntryRef;
//import com.imcode.imcms.servlet.tags.Editor.TextEditor;
//import imcode.server.DocumentRequest;
//import imcode.server.Imcms;
//import imcode.server.document.TextDocumentPermissionSetDomainObject;
//import imcode.server.document.textdocument.TextDocumentDomainObject;
//import imcode.server.document.textdocument.TextDomainObject;
//import imcode.server.parser.TagParser;
//import org.apache.commons.lang3.StringUtils;
//
///**
// * @deprecated use text.tag instead
// */
//@Deprecated
//public class TextTag extends SimpleImcmsTag {
//    public static final String SOURCE_FROM_HTML = "source-from-html";
//    public static final String CLEAN_SOURCE_FROM_HTML = "clean-source-from-html";
//    public static final String TEXT = "text";
//    public static final String HTML = "html";
//    public static final String CLEAN_HTML = "cleanhtml";
//
//    protected String getContent(TagParser tagParser) {
//        final LoopTag loopTag = (LoopTag) findAncestorWithClass(this, LoopTag.class);
//        final String documentProp = attributes.getProperty("document");
//        final DocumentRequest documentRequest = parserParameters.getDocumentRequest();
//
//        final TextDocumentDomainObject textDoc = StringUtils.isBlank(documentProp)
//                ? documentRequest.getDocument()
//                : Imcms.getServices().getDocumentMapper().getVersionedDocument(documentProp, pageContext.getRequest());
//
//        final LoopEntryRef loopEntryRef = (loopTag == null)
//                ? null
//                : loopTag.getLoopEntryRef();
//
//        final String content = tagParser.tagText(attributes, loopEntryRef, textDoc);
//        final TextDocumentPermissionSetDomainObject permissions = (TextDocumentPermissionSetDomainObject) documentRequest
//                .getUser()
//                .getPermissionSetFor(textDoc);
//
//        final boolean hasEditTexts = permissions.getEditTexts();
//
//        if (TagParser.isEditable(attributes, hasEditTexts)) {
//            final String locale = documentRequest.getDocument().getLanguage().getCode();
//            final int textNo = Integer.parseInt(attributes.getProperty("no"));
//            final String contentType = resolveContentType(textDoc, loopEntryRef, textNo);
//            final String label = attributes.getProperty("label", "");
//            final String showLabel = attributes.getProperty("showlabel", "false");
//
//            ((TextEditor) editor)
//                    .setDocumentId(textDoc.getId())
//                    .setContentType(contentType)
//                    .setLabel(label)
//                    .setShowlabel(showLabel)
//                    .setLocale(locale)
//                    .setLoopEntryRef(loopEntryRef)
//                    .setNo(textNo);
//        } else {
//            editor = null;
//        }
//
//        return content;
//    }
//
//    private String resolveContentType(TextDocumentDomainObject textDoc, LoopEntryRef loopEntryRef,
//                                      int textNo) {
//        String contentType = HTML; // default value
//        final String formats = attributes.getProperty("formats", "").toLowerCase();
//
//        if (formats.equals(TEXT)) {
//            contentType = TEXT;
//
//        } else {
//            final TextDomainObject textDO = (loopEntryRef == null)
//                    ? textDoc.getText(textNo)
//                    : textDoc.getText(TextDocumentDomainObject.LoopItemRef.of(loopEntryRef, textNo));
//
//            if ((textDO != null) && (textDO.getType() == TextDomainObject.TEXT_TYPE_PLAIN)) {
//                contentType = (formats.contains("clean")) ? CLEAN_SOURCE_FROM_HTML : SOURCE_FROM_HTML;
//
//            } else if (formats.contains("clean")) {
//                contentType = CLEAN_HTML;
//            }
//        }
//
//        return contentType;
//    }
//
//    @Override
//    public TextEditor createEditor() {
//        return new TextEditor();
//    }
//
//    public void setRows(int rows) {
//        attributes.setProperty("rows", "" + rows);
//    }
//
//    public void setMode(String mode) {
//        attributes.setProperty("mode", mode);
//    }
//
//    public void setFormats(String formats) {
//        attributes.setProperty("formats", formats);
//    }
//
//    public void setDocument(String documentName) {
//        attributes.setProperty("document", documentName);
//    }
//
//    public void setDocument(Integer documentName) {
//        attributes.setProperty("document", documentName.toString());
//    }
//
//    public void setPlaceholder(String placeholder) {
//        attributes.setProperty("placeholder", placeholder);
//    }
//
//    public void setShowlabel(boolean showLabel) {
//        attributes.setProperty("showlabel", "" + showLabel);
//    }
//}
