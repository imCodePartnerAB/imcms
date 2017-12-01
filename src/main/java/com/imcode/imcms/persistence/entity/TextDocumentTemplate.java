package com.imcode.imcms.persistence.entity;

import lombok.NoArgsConstructor;

/**
 * Text document to template.
 */
@NoArgsConstructor
public abstract class TextDocumentTemplate {

    public static final String DEFAULT_TEMPLATE_NAME = "demo";

    protected TextDocumentTemplate(TextDocumentTemplate createFrom) {
        setDocId(createFrom.getDocId());
        setTemplateName(createFrom.getTemplateName());
        setChildrenTemplateName(createFrom.getChildrenTemplateName());
        setTemplateGroupId(createFrom.getTemplateGroupId());
    }

    public abstract Integer getDocId();

    public abstract void setDocId(Integer docId);

    public abstract String getTemplateName();

    public abstract void setTemplateName(String templateName);

    public abstract int getTemplateGroupId();

    public abstract void setTemplateGroupId(int templateGroupId);

    public abstract String getChildrenTemplateName();

    public abstract void setChildrenTemplateName(String childrenTemplateName);

}
