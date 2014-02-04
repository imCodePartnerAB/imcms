package com.imcode.imcms.api;

import com.google.common.base.Optional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ContentLoop {

    public static abstract class ContentAndIndex {
        public abstract Content getContent();
        public abstract int getIndex();

        public static ContentAndIndex of(final Content content, final int index) {
            return new ContentAndIndex() {
                @Override
                public Content getContent() {
                    return content;
                }

                @Override
                public int getIndex() {
                    return index;
                }
            };
        }
    }

    private final int nextContentNo;

    private final List<Content> items;

    private final int cachedHashCode;

    public ContentLoop() {
        this(1, Collections.<Content>emptyList());
    }

    public ContentLoop(int nextContentNo, List<Content> items) {
        if (nextContentNo < 1) {
            throw new IllegalArgumentException(String.format("nextContentNo must be >= 1, but was %d.", nextContentNo));
        }

        for (Content content : items) {
            int no = content.getNo();
            if (no < 1 || no >= nextContentNo) {
                throw new IllegalArgumentException(String.format(
                        "A content no must be in range [1..nextContentNo - 1], but was %d.", no));
            }
        }

        this.nextContentNo = nextContentNo;
        this.items = Collections.unmodifiableList(items);
        this.cachedHashCode = Objects.hash(nextContentNo, items);
    }


    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("nextContentNo", nextContentNo)
                .add("items", items).toString();
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof ContentLoop && equals((ContentLoop) o));
    }

    private boolean equals(ContentLoop that) {
        return Objects.equals(items, that.items) && Objects.equals(nextContentNo, that.nextContentNo);
    }

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    public List<Content> getItems() {
        return items;
    }

    public int getNextContentNo() {
        return nextContentNo;
    }

    public ContentLoopOps ops() {
        return new ContentLoopOps(this);
    }

    public Optional<ContentAndIndex> findContentByNo(int contentNo) {
        for (int i = 0, k = items.size(); i < k; i++) {
            Content content = items.get(i);
            if (content.getNo() == contentNo) return Optional.of(ContentAndIndex.of(content, i));
        }

        return Optional.absent();
    }

    public Optional<ContentAndIndex> findContentWithMaxNo() {
        if (items.size() == 0) {
            return Optional.absent();
        }

        int index = 0;
        Content content = items.get(index);

        for (int i = 1, n = items.size(); i < n; i++) {
            Content currentContent = items.get(i);

            if (content.getNo() < currentContent.getNo()) {
                content = currentContent;
                index = i;
            }
        }

        return Optional.of(ContentAndIndex.of(content, index));
    }
}
