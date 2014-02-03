package com.imcode.imcms.mapping.orm;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;

/**
 * Content 'index' corresponds to absolute context position in a loop starting from zero.
 * Content 'no' is a unique content identifier in a loop which is assigned (incremented) automatically
 * when a new content is added into the loop.
 */
@Entity
@Table(name = "imcms_text_doc_content_loops")
public class ContentLoop implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Min(1)
    @Column(updatable = false)
    private Integer no;

    @NotNull
    private DocRef docRef;

    @Min(1)
    @Column(name = "next_content_no")
    private int nextContentNo;

    @Version
    private int version;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "imcms_text_doc_contents",
            joinColumns = @JoinColumn(name = "content_id")
    )
    @OrderColumn(name = "ix")
    private List<Content> items = new LinkedList<>();

    @Override
    public ContentLoop clone() {
        ContentLoop clone;

        try {
            clone = (ContentLoop) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        clone.items = new LinkedList<>(items);

        return clone;
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("id", id)
                .add("no", no)
                .add("docRef", docRef)
                .add("nextContentNo", nextContentNo)
                .add("version", version)
                .add("items", items).toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, no, docRef, items);
    }


    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof ContentLoop && equals((ContentLoop) o));
    }

    private boolean equals(ContentLoop that) {
        return Objects.equals(id, that.id)
                && Objects.equals(no, that.no)
                && Objects.equals(nextContentNo, that.nextContentNo)
                && Objects.equals(docRef, that.docRef)
                && Objects.equals(version, that.version)
                && Objects.equals(items, that.items);

    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public DocRef getDocRef() {
        return docRef;
    }

    public void setDocRef(DocRef docRef) {
        this.docRef = docRef;
    }

    public int getNextContentNo() {
        return nextContentNo;
    }

    public void setNextContentNo(int nextContentNo) {
        this.nextContentNo = nextContentNo;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<Content> getItems() {
        return items;
    }

    public void setItems(List<Content> items) {
        this.items = items;
    }
}