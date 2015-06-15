package com.imcode.imcms.servlet.tags.Editor;

/**
 * Created by Shadowgun on 30.12.2014.
 */
public class MenuEditor extends BaseEditor {
    private int no;
    private int documentId;

    @Override
    public String wrap(String content) {
        super.builder
                .addClass("menu")
                .addParam("no", no)
                .addParam("meta", documentId);
        return super.wrap(content);
    }

    public MenuEditor setNo(int no) {
        this.no = no;
        return this;
    }

    public MenuEditor setDocumentId(int documentId) {
        this.documentId = documentId;
        return this;
    }
}
