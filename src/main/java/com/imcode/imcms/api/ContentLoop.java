package com.imcode.imcms.api;

import com.google.common.base.Optional;

import java.util.*;

public final class ContentLoop {

    public static ContentLoop of(List<Content> items) {
        return new ContentLoop(items);
    }

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

    private final List<Content> items;

    private final int cachedHashCode;

    public ContentLoop() {
        this(Collections.<Content>emptyList());
    }

    public ContentLoop(Collection<Content> items) {
        Map<Integer, Content> itemsMap = new LinkedHashMap<>();

        for (Content content : items) {
            itemsMap.put(content.getNo(), content);
        }

        this.items = Collections.unmodifiableList(new ArrayList<>(itemsMap.values()));
        this.cachedHashCode = Objects.hash(items);
    }


    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this).add("items", items).toString();
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof ContentLoop && equals((ContentLoop) o));
    }

    private boolean equals(ContentLoop that) {
        return Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    public List<Content> getItems() {
        return items;
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
