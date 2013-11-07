package imcode.server.document.textdocument;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Embeddable
public class ContentRef implements Serializable, Cloneable {

    @Column(name = "content_loop_no")
    private volatile int loopNo;

    @Column(name = "content_no")
    private volatile int contentNo;

    protected ContentRef() {}

    private ContentRef(int loopNo, int contentNo) {
        this.loopNo = loopNo;
        this.contentNo = contentNo;
    }

    public static ContentRef of(int loopNo, int contentNo) {
        return new ContentRef(loopNo, contentNo);
    }

    public static Optional<ContentRef> of(String loopNo, String contentNo) {
        Integer loopNoInt = Ints.tryParse(loopNo);
        Integer contentNoInt = Ints.tryParse(contentNo);

        return Optional.fromNullable(
            loopNoInt != null && contentNoInt != null
                    ? ContentRef.of(loopNoInt, contentNoInt)
                    : null
        );
    }

    public static Optional<ContentRef> of(String ref) {
        Matcher matcher = Pattern.compile("(\\d+)_(\\d+)").matcher(Strings.nullToEmpty(ref).trim());

        return Optional.fromNullable(
                matcher.find()
                    ? ContentRef.of(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)))
                    : null
        );
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof ContentRef && equals((ContentRef) o));
    }

    private boolean equals(ContentRef that) {
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

    public int loopNo() {
        return loopNo;
    }

    public int contentNo() {
        return contentNo;
    }
}
