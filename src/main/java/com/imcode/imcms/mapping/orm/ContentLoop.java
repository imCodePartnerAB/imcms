package com.imcode.imcms.mapping.orm;

import javax.persistence.*;
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

    /** Non Thread Safe */
    public static final class Builder {

        private ContentLoop contentLoop;

        public Builder() {
            contentLoop = new ContentLoop();
        }

        public Builder(ContentLoop contentLoop) {
            this.contentLoop = contentLoop.clone();
        }

        public Builder id(Long id) {
            contentLoop.id = id;
            return this;
        }

        public Builder docRef(DocRef docRef) {
            contentLoop.docRef = docRef;

            return this;
        }

        public Builder no(Integer no) {
            contentLoop.no = no;
            return this;
        }

        public Builder addContent(int contentIndex) {
            Content content = Content.builder().no(contentLoop.nextContentNo++).build();
            contentLoop.content.add(contentIndex, content);

            return this;
        }

        public Builder enableContent(int contentIndex) {
            contentLoop.content.set(contentIndex, Content.builder(contentLoop.content.get(contentIndex)).enabled(true).build());
            return this;
        }

        public Builder disableContent(int contentIndex) {
            contentLoop.content.set(contentIndex, Content.builder(contentLoop.content.get(contentIndex)).enabled(false).build());
            return this;
        }

        public Builder moveContent(int fromIndex, int toIndex) {
            Content content = contentLoop.content.remove(fromIndex);
            contentLoop.content.add(toIndex, content);

            return this;
        }

        public Builder deleteContent(int contentIndex) {
            contentLoop.content.remove(contentIndex);

            return this;
        }

        public ContentLoop build() {
            return contentLoop.clone();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ContentLoop contentLoop) {
        return new Builder(contentLoop);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private volatile Long id;

    private volatile Integer no;

    private volatile DocRef docRef;

    @Column(name = "next_content_no")
    private volatile int nextContentNo;

    @Version
    private volatile int version;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "imcms_text_doc_contents",
            joinColumns = @JoinColumn(name = "content_id")
    )
    @OrderColumn(name = "ix")
    private List<Content> content = new LinkedList<>();

    protected ContentLoop() {
    }

    public Long getId() {
        return id;
    }

    public Integer getNo() {
        return no;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return String.format("ContentLoop{id: %s, docRef: %s, no: %s, contents: [%s]}",
                id, getDocRef(), no, "..."); // causes hibernate stack overflow: StringUtils.join(contents, ", "));
    }

    @Override
    public ContentLoop clone() {
        ContentLoop clone;

        try {
            clone = (ContentLoop) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        clone.content = new LinkedList<>(content);

        return clone;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof ContentLoop && equals((ContentLoop) o));
    }

    private boolean equals(ContentLoop that) {
        return Objects.equals(content, that.content) &&
               Objects.equals(docRef, that.docRef) &&
               Objects.equals(id, that.id) &&
               Objects.equals(no, that.no);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, no, docRef, content);
    }

    public DocRef getDocRef() {
        return docRef;
    }

    public List<Content> getContents() {
        return Collections.unmodifiableList(content);
    }

    public ContentLoopOps ops() {
        return new ContentLoopOps(this);
    }
}