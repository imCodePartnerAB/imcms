package com.imcode.imcms.servlet.tags.Editor;

import com.imcode.imcms.mapping.container.LoopEntryRef;

/**
 * Created by Shadowgun on 24.12.2014.
 */
public class TextEditor extends BaseEditor {
    private int no;
    private String locale;
    private LoopEntryRef loopEntryRef;
    private int documentId;

    @Override
    public String wrap(String content) {
        super.builder
                .addClass("text")
                .addParam("contenteditable", true, false)
                .addParam("no", no)
                .addParam("meta", documentId)
                .addParam("locale", locale)
                .addParam("LoopEntryRef",
                        loopEntryRef == null ? "" :
                                String.format("%s_%s", loopEntryRef.getLoopNo(), loopEntryRef.getEntryNo()));
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

    public int getDocumentId() {
        return documentId;
    }

    public TextEditor setDocumentId(int documentId) {
        this.documentId = documentId;
        return this;
    }
}
