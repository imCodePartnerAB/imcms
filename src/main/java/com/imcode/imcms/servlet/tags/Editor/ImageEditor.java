package com.imcode.imcms.servlet.tags.Editor;

import com.imcode.imcms.mapping.container.LoopEntryRef;

/**
 * Created by Shadowgun on 30.03.2015.
 */
public class ImageEditor extends BaseEditor {
    private int no;
    private int documentId;
    private LoopEntryRef loopEntryRef;

    @Override
    public String wrap(String content) {
        super.builder
                .addClass("image")
                .addParam("no", no)
                .addParam("loop", loopEntryRef != null ? loopEntryRef.getLoopNo() : "")
                .addParam("entry", loopEntryRef != null ? loopEntryRef.getEntryNo() : "")
                .addParam("meta", documentId);
        return super.wrap(content);
    }

    public ImageEditor setNo(int no) {
        this.no = no;
        return this;
    }

    public ImageEditor setDocumentId(int documentId) {
        this.documentId = documentId;
        return this;
    }

    public ImageEditor setLoopEntryRef(LoopEntryRef loopEntryRef) {
        this.loopEntryRef = loopEntryRef;
        return this;
    }
}
