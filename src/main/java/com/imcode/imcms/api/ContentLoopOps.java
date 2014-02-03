package com.imcode.imcms.api;

import java.util.LinkedList;

public class ContentLoopOps {

    private final ContentLoop contentLoop;
    private final LinkedList<Content> items;

    public ContentLoopOps(ContentLoop contentLoop) {
        this.contentLoop = contentLoop;
        this.items = new LinkedList<>(contentLoop.getItems());
    }

    private ContentLoop addContentAtIndex(int index) {
        items.add(index, Content.of(contentLoop.getNextContentNo()));

        return new ContentLoop(contentLoop.getNextContentNo() + 1, items);
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

        return new ContentLoop(contentLoop.getNextContentNo(), items);
    }

    public ContentLoop disableContent(int contentNo) {
        items.set(contentLoop.findContentByNo(contentNo).get().getIndex(), Content.of(contentNo, false));

        return new ContentLoop(contentLoop.getNextContentNo(), items);
    }

    public ContentLoop deleteContent(int contentNo) {
        items.remove(contentLoop.findContentByNo(contentNo).get().getIndex());

        return new ContentLoop(contentLoop.getNextContentNo(), items);
    }

    public ContentLoop restoreContent(int contentNo) {
        int lastContentNo = contentLoop.getNextContentNo() - 1;
        if (contentNo < 1 || contentNo > lastContentNo) {
            throw new IllegalStateException(String.format("Illegal content no: %d.", contentNo));
        }

        if (!contentLoop.findContentByNo(contentNo).isPresent()) {
            items.add(Content.of(contentNo));
        }

        return new ContentLoop(contentLoop.getNextContentNo(), items);
    }
}
