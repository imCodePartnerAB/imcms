package com.imcode.imcms.api;

import com.google.common.base.Optional;

import java.util.*;

public final class Loop {

    public static Loop of(List<LoopContent> items) {
        return new Loop(items);
    }

    public static abstract class ContentAndIndex {
        public abstract LoopContent getContent();
        public abstract int getIndex();

        public static ContentAndIndex of(final LoopContent loopContent, final int index) {
            return new ContentAndIndex() {
                @Override
                public LoopContent getContent() {
                    return loopContent;
                }

                @Override
                public int getIndex() {
                    return index;
                }
            };
        }
    }

    private final List<LoopContent> items;

    private final int cachedHashCode;

    public Loop() {
        this(Collections.<LoopContent>emptyList());
    }

    public Loop(Collection<LoopContent> items) {
        Map<Integer, LoopContent> itemsMap = new LinkedHashMap<>();

        for (LoopContent loopContent : items) {
            itemsMap.put(loopContent.getNo(), loopContent);
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
        return o == this || (o instanceof Loop && equals((Loop) o));
    }

    private boolean equals(Loop that) {
        return Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    public List<LoopContent> getItems() {
        return items;
    }


    public LoopOps ops() {
        return new LoopOps(this);
    }

    public Optional<ContentAndIndex> findContentByNo(int contentNo) {
        for (int i = 0, k = items.size(); i < k; i++) {
            LoopContent loopContent = items.get(i);
            if (loopContent.getNo() == contentNo) return Optional.of(ContentAndIndex.of(loopContent, i));
        }

        return Optional.absent();
    }

    public Optional<ContentAndIndex> findContentWithMaxNo() {
        if (items.size() == 0) {
            return Optional.absent();
        }

        int index = 0;
        LoopContent loopContent = items.get(index);

        for (int i = 1, n = items.size(); i < n; i++) {
            LoopContent currentLoopContent = items.get(i);

            if (loopContent.getNo() < currentLoopContent.getNo()) {
                loopContent = currentLoopContent;
                index = i;
            }
        }

        return Optional.of(ContentAndIndex.of(loopContent, index));
    }
}
