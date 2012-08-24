package imcode.server.document.textdocument;

import com.google.common.base.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ContentRef implements Serializable, Cloneable {

    @Column(name = "content_loop_no")
    private int loopNo;

    @Column(name = "content_no")
    private int contentNo;

    protected ContentRef() {}

    public ContentRef(int loopNo, int contentNo) {
        this.loopNo = loopNo;
        this.contentNo = contentNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContentRef)) return false;

        ContentRef that = (ContentRef) o;

        if (contentNo != that.contentNo) return false;
        if (loopNo != that.loopNo) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = loopNo;
        result = 31 * result + contentNo;
        return result;
    }

    @Override
    public String toString() {
        return "ContentRef{" +
                "contentNo=" + contentNo +
                ", loopNo=" + loopNo +
                '}';
    }

    public int getLoopNo() {
        return loopNo;
    }

    public int getContentNo() {
        return contentNo;
    }
}
