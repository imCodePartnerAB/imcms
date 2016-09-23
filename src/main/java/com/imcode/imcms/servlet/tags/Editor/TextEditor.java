package com.imcode.imcms.servlet.tags.Editor;

import com.imcode.imcms.mapping.container.LoopEntryRef;

public class TextEditor extends BaseEditor {
    private int no;
    private String locale;
    private String contentType;
    private String label;
    private LoopEntryRef loopEntryRef;
    private int documentId;
    private String showlabel;

    @Override
    public String wrap(String content) {
        super.builder
                .addClass("text")
                .addParam("contenteditable", true, false)
                .addParam("contentType", contentType)
                .addParam("no", no)
                .addParam("meta", documentId)
                .addParam("locale", locale)
                .addParam("label", label)
                .addParam("showlabel", showlabel)
                .addParam("LoopEntryRef", (loopEntryRef != null)
                        ? String.format("%s_%s", loopEntryRef.getLoopNo(), loopEntryRef.getEntryNo())
                        : "");
        return super.wrap(content);
    }

    public TextEditor setNo(int no) {
        this.no = no;
        return this;
    }

    public TextEditor setLocale(String locale) {
        this.locale = locale;
        return this;
    }

    public TextEditor setLoopEntryRef(LoopEntryRef loopEntryRef) {
        this.loopEntryRef = loopEntryRef;
        return this;
    }

    public TextEditor setDocumentId(int documentId) {
        this.documentId = documentId;
        return this;
    }

    public TextEditor setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public TextEditor setLabel(String label) {
        this.label = label;
        return this;
    }

    public TextEditor setShowlabel(String showLabel) {
        this.showlabel = showLabel;
        return this;
    }
}
