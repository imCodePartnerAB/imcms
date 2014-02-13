package com.imcode.imcms.api;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reference to text document loop item.
 * <p>
 * In the current implementation a loop can contain texts ans images.
 * </p>
 */
public class LoopItemRef {

    public static LoopItemRef of(int loopNo, int contentNo, int itemNo) {
        return new LoopItemRef(loopNo, contentNo, itemNo);
    }

    private final int loopNo;
    private final int contentNo;
    private final int itemNo;
    private final int cachedHashCode;

    public LoopItemRef(int loopNo, int contentNo, int itemNo) {
        this.loopNo = loopNo;
        this.contentNo = contentNo;
        this.itemNo = itemNo;
        this.cachedHashCode = Objects.hash(loopNo, contentNo, itemNo);
    }

    public static Optional<LoopItemRef> of(String loopNo, String contentNo, String itemNo) {
        Integer loopNoInt = Ints.tryParse(loopNo);
        Integer contentNoInt = Ints.tryParse(contentNo);
        Integer itemNoInt = Ints.tryParse(itemNo);

        return Optional.fromNullable(
                loopNoInt != null && contentNoInt != null && itemNo != null
                        ? LoopItemRef.of(loopNoInt, contentNoInt, itemNoInt)
                        : null
        );
    }

    public static Optional<LoopItemRef> of(String ref) {
        Matcher matcher = Pattern.compile("(\\d+)_(\\d+)_(\\d+)").matcher(Strings.nullToEmpty(ref).trim());

        return Optional.fromNullable(
                matcher.find()
                        ? LoopItemRef.of(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)))
                        : null
        );
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof LoopItemRef && equals((LoopItemRef) o));
    }

    private boolean equals(LoopItemRef that) {
        return loopNo == that.loopNo && contentNo == that.contentNo && itemNo == that.itemNo;
    }

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("contentNo", contentNo)
                .add("loopNo", loopNo)
                .add("itemNo", itemNo)
                .toString();
    }

    public int getLoopNo() {
        return loopNo;
    }

    public int getContentNo() {
        return contentNo;
    }

    public int getItemNo() {
        return itemNo;
    }
}
