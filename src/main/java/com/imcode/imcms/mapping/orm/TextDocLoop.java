package com.imcode.imcms.mapping.orm;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.*;

/**
 * Content 'index' corresponds to absolute context position in a loop starting from zero.
 * Content 'no' is a unique content identifier in a loop which is assigned (incremented) automatically
 * when a new content is added into the loop.
 */
@Entity
@Table(name = "imcms_text_doc_content_loops")
public class TextDocLoop extends DocVersionedContent {

    @Min(1)
    @Column(updatable = false)
    private Integer no;

    @Min(1)
    @Column(name = "next_content_no")
    private int nextItemNo;

    @Version
    private int version;

    @ElementCollection
    @CollectionTable(
            name = "imcms_text_doc_contents",
            joinColumns = @JoinColumn(name = "content_id")
    )
    @OrderColumn(name = "ix")
    private List<TextDocLoopItem> items = new LinkedList<>();

//    @Override
//    public String toString() {
//        return com.google.common.base.Objects.toStringHelper(this)
//                .add("id", id)
//                .add("no", no)
//                .add("docRef", docRef)
//                .add("nextContentNo", nextContentNo)
//                .add("version", version)
//                .add("items", items).toString();
//    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDocVersion(), no, nextItemNo, items, version);
    }


    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof TextDocLoop && equals((TextDocLoop) o));
    }

    private boolean equals(TextDocLoop that) {
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getDocVersion(), that.getDocVersion())
                && Objects.equals(no, that.no)
                && Objects.equals(nextItemNo, that.nextItemNo)
                && Objects.equals(version, that.version)
                && Objects.equals(items, that.items);
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public int getNextItemNo() {
        return nextItemNo;
    }

    public void setNextItemNo(int nextContentNo) {
        this.nextItemNo = nextContentNo;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<TextDocLoopItem> getItems() {
        return items;
    }

    public void setItems(List<TextDocLoopItem> items) {
        this.items = items;
    }
}