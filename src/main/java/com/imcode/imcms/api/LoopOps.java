package com.imcode.imcms.api;

import com.google.common.base.Optional;

import java.util.LinkedList;

public class LoopOps {

    private final Loop loop;
    private final LinkedList<LoopContent> items;

    public LoopOps(Loop loop) {
        this.loop = loop;
        this.items = new LinkedList<>(loop.getItems());
    }

    private Loop addContentAtIndex(int index) {
        items.add(index, createContent());

        return new Loop(items);
    }

    public Loop addContentFirst() {
        return addContentAtIndex(0);
    }

    public Loop addContentLast() {
        return addContentAtIndex(items.size());
    }

    public Loop addContentAfter(int contentNo) {
        return addContentAtIndex(loop.findContentByNo(contentNo).get().getIndex() + 1);
    }

    public Loop addContentBefore(int contentNo) {
        return addContentAtIndex(loop.findContentByNo(contentNo).get().getIndex());
    }

    public Loop enableContent(int contentNo) {
        items.set(loop.findContentByNo(contentNo).get().getIndex(), LoopContent.of(contentNo));

        return new Loop(items);
    }

    public Loop disableContent(int contentNo) {
        items.set(loop.findContentByNo(contentNo).get().getIndex(), LoopContent.of(contentNo, false));

        return new Loop(items);
    }

    public Loop deleteContent(int contentNo) {
        items.remove(loop.findContentByNo(contentNo).get().getIndex());

        return new Loop(items);
    }

    public Loop restoreContent(int contentNo) {
        if (loop.findContentByNo(contentNo).isPresent()) {
            return loop;
        }

        items.add(LoopContent.of(contentNo));

        return new Loop(items);
    }

    private LoopContent createContent() {
        Optional<Loop.ContentAndIndex> contentAndIndexOpt = loop.findContentWithMaxNo();
        int contentNo = !contentAndIndexOpt.isPresent() ? 1 : contentAndIndexOpt.get().getContent().getNo() + 1;

        return LoopContent.of(contentNo);
    }
}