package com.imcode.imcms.mapping.orm;

import com.google.common.base.Optional;

import java.util.List;

public class ContentLoopOps {

    public static abstract class ContentIndex {
        public abstract Content content();
        public abstract int index();

        public static ContentIndex of(final Content content, final int index) {
            return new ContentIndex() {
                @Override
                public Content content() {
                    return content;
                }

                @Override
                public int index() {
                    return index;
                }
            };
        }
    }

    public static abstract class UpdatedLoopAndNewContent {
        public abstract ContentLoop loop();
        public abstract Content content();

        public static UpdatedLoopAndNewContent of(final ContentLoop loop, final Content content) {
            return new UpdatedLoopAndNewContent() {
                @Override
                public ContentLoop loop() {
                    return loop;
                }

                @Override
                public Content content() {
                    return content;
                }
            };
        }
    }
    
    private final ContentLoop loop;

    public ContentLoopOps(ContentLoop loop) {
        this.loop = loop;
    }    

    public UpdatedLoopAndNewContent addContentFirst() {
        ContentLoop newLoop = ContentLoop.builder(loop).addContent(0).build();

        return UpdatedLoopAndNewContent.of(newLoop, newLoop.getContents().get(0));
    }

    public UpdatedLoopAndNewContent addContentLast() {
        ContentLoop newLoop = ContentLoop.builder(loop).addContent(loop.getContents().size()).build();
        List<Content> contents = newLoop.getContents();

        return UpdatedLoopAndNewContent.of(newLoop, contents.get(contents.size() - 1));

    }

    public UpdatedLoopAndNewContent addContentBefore(int contentIndex) {
        Content throwExIfNotExists = loop.getContents().get(contentIndex);
        ContentLoop newLoop = ContentLoop.builder(loop).addContent(contentIndex).build();

        return UpdatedLoopAndNewContent.of(newLoop, newLoop.getContents().get(contentIndex));
    }

    public UpdatedLoopAndNewContent addContentAfter(int contentIndex) {
        int newContentIndex = contentIndex + 1;
        ContentLoop newLoop = ContentLoop.builder(loop).addContent(newContentIndex).build();

        return UpdatedLoopAndNewContent.of(newLoop, newLoop.getContents().get(newContentIndex));
    }

    public ContentLoop moveContentFirst(int contentIndex) {
        Content throwExIfNotExists = loop.getContents().get(contentIndex);

        return contentIndex == 0
                ? loop
                : ContentLoop.builder(loop).moveContent(contentIndex, 0).build();
    }

    public ContentLoop moveContentLast(int contentIndex) {
        Content throwExIfNotExists = loop.getContents().get(contentIndex);
        int lastIndex = loop.getContents().size() - 1;

        return contentIndex == lastIndex
                ? loop
                : ContentLoop.builder(loop).moveContent(contentIndex, lastIndex).build();
    }

    public ContentLoop deleteContent(int contentIndex) {
        return ContentLoop.builder(loop).deleteContent(contentIndex).build();
    }

    public ContentLoop enableContent(int contentIndex) {
        return ContentLoop.builder(loop).enableContent(contentIndex).build();
    }

    public ContentLoop disableContent(int contentIndex) {
        return ContentLoop.builder(loop).disableContent(contentIndex).build();
    }


    /**
     * @throws IndexOutOfBoundsException
     */
    public ContentLoop moveContentBackward(int contentIndex, boolean skipDisabled) {
        Content throwExIfNotExists = loop.getContents().get(contentIndex);

        for (int i = contentIndex - 1; i >= 0; i--) {
            Content nextContent = loop.getContents().get(i);
            if (skipDisabled && !nextContent.isEnabled()) continue;

            return ContentLoop.builder(loop).moveContent(contentIndex, i).build();
        }

        return loop;
    }

    /**
     * @throws IndexOutOfBoundsException
     */
    public ContentLoop moveContentBackward(int contentIndex) {
        return moveContentBackward(contentIndex, true);
    }

    /**
     * @throws IndexOutOfBoundsException
     */
    public ContentLoop moveContentForward(int contentIndex) {
        return moveContentForward(contentIndex, true);
    }

    /**
     * @throws IndexOutOfBoundsException
     */
    public ContentLoop moveContentForward(int contentIndex, boolean skipDisabled) {
        Content throwExIfNotExists = loop.getContents().get(contentIndex);
        for (int i = contentIndex + 1; i < loop.getContents().size(); i++) {
            Content nextContent = loop.getContents().get(i);
            if (skipDisabled && !nextContent.isEnabled()) continue;

            return ContentLoop.builder(loop).moveContent(contentIndex, i).build();
        }

        return loop;
    }

    public Optional<ContentIndex> findContent(int contentNo) {
        for (int i = 0, k = loop.getContents().size(); i < k; i++) {
            Content content = loop.getContents().get(i);
            if (content.getNo() == contentNo) return Optional.of(ContentIndex.of(content, i));
        }

        return Optional.absent();
    }
}