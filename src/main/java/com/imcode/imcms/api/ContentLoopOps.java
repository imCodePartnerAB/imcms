package com.imcode.imcms.api;

import com.google.common.base.Optional;

import java.util.LinkedList;

public class ContentLoopOps {

    private final ContentLoop contentLoop;
    private final LinkedList<Content> items;

    public ContentLoopOps(ContentLoop contentLoop) {
        this.contentLoop = contentLoop;
        this.items = new LinkedList<>(contentLoop.getItems());
    }

    private ContentLoop addContentAtIndex(int index) {
        items.add(index, createContent());

        return new ContentLoop(items);
    }

    public ContentLoop addContentFirst() {
        return addContentAtIndex(0);
    }

    public ContentLoop addContentLast() {
        return addContentAtIndex(items.size());
    }

    public ContentLoop addContentAfter(int contentNo) {
        return addContentAtIndex(contentLoop.findContentByNo(contentNo).get().getIndex() + 1);
    }

    public ContentLoop addContentBefore(int contentNo) {
        return addContentAtIndex(contentLoop.findContentByNo(contentNo).get().getIndex());
    }

    public ContentLoop enableContent(int contentNo) {
        items.set(contentLoop.findContentByNo(contentNo).get().getIndex(), Content.of(contentNo));

        return new ContentLoop(items);
    }

    public ContentLoop disableContent(int contentNo) {
        items.set(contentLoop.findContentByNo(contentNo).get().getIndex(), Content.of(contentNo, false));

        return new ContentLoop(items);
    }

    public ContentLoop deleteContent(int contentNo) {
        items.remove(contentLoop.findContentByNo(contentNo).get().getIndex());

        return new ContentLoop(items);
    }

    public ContentLoop restoreContent(int contentNo) {
        if (contentLoop.findContentByNo(contentNo).isPresent()) {
            return contentLoop;
        }

        items.add(Content.of(contentNo));

        return new ContentLoop(items);
    }

    private Content createContent() {
        Optional<ContentLoop.ContentAndIndex> contentAndIndexOpt = contentLoop.findContentWithMaxNo();
        int contentNo = !contentAndIndexOpt.isPresent() ? 1 : contentAndIndexOpt.get().getContent().getNo() + 1;

        return Content.of(contentNo);
    }
}