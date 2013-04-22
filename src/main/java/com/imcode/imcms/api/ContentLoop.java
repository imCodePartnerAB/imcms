package com.imcode.imcms.api;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.imcode.imcms.util.P;
import com.imcode.imcms.util.P2;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.*;

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
            contentLoop.docId = docRef == null ? null : docRef.docId();
            contentLoop.docVersionNo = docRef == null ? null : docRef.docVersionNo();

            return this;
        }

        public Builder no(Integer no) {
            contentLoop.no = no;
            return this;
        }

        public Builder addContent(int contentIndex) {
            Content content = Content.builder().no(contentLoop.nextContentNo++).build();
            contentLoop.contents.add(contentIndex, content);

            return this;
        }

        public Builder enableContent(int contentIndex) {
            contentLoop.contents.set(contentIndex, Content.builder(contentLoop.contents.get(contentIndex)).enabled(true).build());
            return this;
        }

        public Builder disableContent(int contentIndex) {
            contentLoop.contents.set(contentIndex, Content.builder(contentLoop.contents.get(contentIndex)).enabled(false).build());
            return this;
        }

        public Builder moveContent(int fromIndex, int toIndex) {
            Content content = contentLoop.contents.remove(fromIndex);
            contentLoop.contents.add(toIndex, content);

            return this;
        }

        public Builder deleteContent(int contentIndex) {
            contentLoop.contents.remove(contentIndex);

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
    private Long id;

    private Integer no;

    @Column(name = "doc_id")
    private Integer docId;

    @Column(name = "doc_version_no")
    private Integer docVersionNo;

    @Column(name = "next_content_no")
    private int nextContentNo;

    @Version
    private int version;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "imcms_text_doc_contents",
            joinColumns = {
                    @JoinColumn(name = "doc_id", referencedColumnName = "doc_id"),
                    @JoinColumn(name = "doc_version_no", referencedColumnName = "doc_version_no"),
                    @JoinColumn(name = "loop_no", referencedColumnName = "no")
            }
    )
    @OrderColumn(name = "ix")
    private List<Content> contents = new LinkedList<Content>();

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

        clone.contents = new LinkedList<Content>(contents);

        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContentLoop)) return false;

        ContentLoop that = (ContentLoop) o;

        if (contents != null ? !contents.equals(that.contents) : that.contents != null) return false;
        if (docId != null ? !docId.equals(that.docId) : that.docId != null) return false;
        if (docVersionNo != null ? !docVersionNo.equals(that.docVersionNo) : that.docVersionNo != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (no != null ? !no.equals(that.no) : that.no != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, no, docId, docVersionNo, contents);
    }

    public DocRef getDocRef() {
        return docId == null ? null : DocRef.of(docId, docVersionNo);
    }

    public Optional<P2<Content, Integer>> findContent(int contentNo) {
        for (int i = 0, k = contents.size(); i < k; i++) {
            Content content = contents.get(i);
            if (content.getNo() == contentNo) return Optional.of(P.of(content, i));
        }

        return Optional.absent();
    }

    public List<Content> getContents() {
        return Collections.unmodifiableList(contents);
    }

    public P2<ContentLoop, Content> addFirstContent() {
        ContentLoop contentLoop = builder(this).addContent(0).build();

        return P.of(contentLoop, contentLoop.contents.get(0));
    }

    public P2<ContentLoop, Content> addLastContent() {
        ContentLoop contentLoop = builder(this).addContent(contents.size()).build();
        List<Content> contents = contentLoop.contents;

        return P.of(contentLoop, contents.get(contents.size() - 1));
    }

    /**
     * @throws IndexOutOfBoundsException
     */
    public ContentLoop deleteContent(int contentIndex) {
        return builder(this).deleteContent(contentIndex).build();
    }

    /**
     * @throws IndexOutOfBoundsException
     */
    public P2<ContentLoop, Content> addContentAfter(int contentIndex) {
        int newContentIndex = contentIndex + 1;
        ContentLoop contentLoop = builder(this).addContent(newContentIndex).build();

        return P.of(contentLoop, contentLoop.contents.get(newContentIndex));
    }

    /**
     * @throws IndexOutOfBoundsException
     */
    public P2<ContentLoop, Content> addContentBefore(int contentIndex) {
        Content throwExIfNotExists = contents.get(contentIndex);
        ContentLoop contentLoop = builder(this).addContent(contentIndex).build();

        return P.of(contentLoop, contentLoop.contents.get(contentIndex));
    }

    /**
     * @throws IndexOutOfBoundsException
     */
    public ContentLoop moveContentBackward(int contentIndex, boolean skipDisabled) {
        Content throwExIfNotExists = contents.get(contentIndex);

        for (int i = contentIndex - 1; i >= 0; i--) {
            Content nextContent = contents.get(i);
            if (skipDisabled && !nextContent.isEnabled()) continue;

            return builder(this).moveContent(contentIndex, i).build();
        }

        return this;
    }

    /**
     * @throws IndexOutOfBoundsException
     */
    public ContentLoop moveContentBackward(int contentIndex) {
        return moveContentBackward(contentIndex, true);
    }

    /**
     * @throws IndexOutOfBoundsException
     */
    public ContentLoop moveContentForward(int contentIndex) {
        return moveContentForward(contentIndex, true);
    }

    /**
     * @throws IndexOutOfBoundsException
     */
    public ContentLoop moveContentForward(int contentIndex, boolean skipDisabled) {
        Content throwExIfNotExists = contents.get(contentIndex);
        for (int i = contentIndex + 1; i < contents.size(); i++) {
            Content nextContent = contents.get(i);
            if (skipDisabled && !nextContent.isEnabled()) continue;

            return builder(this).moveContent(contentIndex, i).build();
        }

        return this;
    }

    /**
     * @throws IndexOutOfBoundsException
     */
    public ContentLoop moveContentTop(int contentIndex) {
        Content throwExIfNotExists = contents.get(contentIndex);

        return contentIndex == 0
                ? this
                : builder(this).moveContent(contentIndex, 0).build();
    }

    /**
     * @throws IndexOutOfBoundsException
     */
    public ContentLoop moveContentBottom(int contentIndex) {
        Content throwExIfNotExists = contents.get(contentIndex);
        int lastIndex = contents.size() - 1;

        return contentIndex == lastIndex
                ? this
                : builder(this).moveContent(contentIndex, lastIndex).build();
    }


    /**
     * @throws IndexOutOfBoundsException
     */
    public ContentLoop disableContent(int contentIndex) {
        return ContentLoop.builder(this).disableContent(contentIndex).build();
    }

    /**
     * @throws IndexOutOfBoundsException
     */
    public ContentLoop enableContent(int contentIndex) {
        return ContentLoop.builder(this).enableContent(contentIndex).build();
    }
}