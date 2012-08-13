package imcode.server.document.textdocument;

import com.google.common.base.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ContentLoopRef implements Cloneable {

    @Column(name = "content_loop_no")
    private int loopNo;

    @Column(name = "content_no")
    private int contentNo;

    protected ContentLoopRef() {}

    public ContentLoopRef(int loopNo, int contentNo) {
        this.loopNo = loopNo;
        this.contentNo = contentNo;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ContentLoopRef)) {
            return false;
        }

        ContentLoopRef that = (ContentLoopRef)object;

        return this.hashCode() == that.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(loopNo, contentNo);
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
