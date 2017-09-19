package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.mapping.container.LoopEntryRef;
import com.imcode.imcms.servlet.tags.Editor.ImageEditor;
import imcode.server.Imcms;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.parser.TagParser;
import org.apache.commons.lang3.StringUtils;

// todo: remove after new tag implementation
@Deprecated
public class ImageTag extends SimpleImcmsTag {

    protected String getContent(TagParser tagParser) {
        LoopTag loopTag = (LoopTag) findAncestorWithClass(this, LoopTag.class);

        LoopEntryRef loopEntryRef = loopTag == null ? null : loopTag.getLoopEntryRef();
        TextDocumentDomainObject doc = (TextDocumentDomainObject) (!StringUtils.isNotBlank(attributes.getProperty("document")) ?
                parserParameters.getDocumentRequest().getDocument() :
                Imcms.getServices().getDocumentMapper().getDocument(attributes.getProperty("document")));
        if (TagParser.isEditable(attributes,
                ((TextDocumentPermissionSetDomainObject) parserParameters.getDocumentRequest().getUser().getPermissionSetFor(doc)).getEditImages()))
        {
            ((ImageEditor) editor).setDocumentId(doc.getId())
                    .setLoopEntryRef(loopEntryRef)
                    .setNo(Integer.parseInt(attributes.getProperty("no")));
        } else {
            editor = null;
        }
        return tagParser.tagImage(attributes, loopEntryRef);
    }

    @Override
    public ImageEditor createEditor() {
        return new ImageEditor();
    }

    public void setMode(String mode) {
        attributes.setProperty("mode", mode);
    }

    public void setStyle(String style) {
        attributes.setProperty("style", style);
    }

    public void setStyleClass(String styleClass) {
        attributes.setProperty("class", styleClass);
    }

    public void setUsemap(String usemap) {
        attributes.setProperty("usemap", usemap);
    }

    public void setStyleId(String id) {
        attributes.setProperty("id", id);
    }

    public void setDocument(String documentName) {
        attributes.setProperty("document", documentName);
    }
}
