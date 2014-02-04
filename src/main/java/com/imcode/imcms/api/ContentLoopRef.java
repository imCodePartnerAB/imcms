package com.imcode.imcms.api;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentLoopRef {

    public static ContentLoopRef of(int loopNo, int contentNo) {
        return new ContentLoopRef(loopNo, contentNo);
    }

    private final int loopNo;

    private final int contentNo;

    public ContentLoopRef(int loopNo, int contentNo) {
        this.loopNo = loopNo;
        this.contentNo = contentNo;
    }

    public static Optional<ContentLoopRef> of(String loopNo, String contentNo) {
        Integer loopNoInt = Integer.valueOf(loopNo);
        Integer contentNoInt = Integer.valueOf(contentNo);

        return Optional.fromNullable(
                loopNoInt != null && contentNoInt != null
                        ? ContentLoopRef.of(loopNoInt, contentNoInt)
                        : null
        );
    }

    public static Optional<ContentLoopRef> of(String ref) {
        Matcher matcher = Pattern.compile("(\\d+)_(\\d+)").matcher(Strings.nullToEmpty(ref).trim());

        return Optional.fromNullable(
                matcher.find()
                        ? ContentLoopRef.of(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)))
                        : null
        );
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof ContentLoopRef && equals((ContentLoopRef) o));
    }

    private boolean equals(ContentLoopRef that) {
        return loopNo == that.loopNo && contentNo == that.contentNo;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(loopNo, contentNo);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("contentNo", contentNo).add("loopNo", loopNo).toString();
    }

    public int getLoopNo() {
        return loopNo;
    }

    public int getContentNo() {
        return contentNo;
    }
}
