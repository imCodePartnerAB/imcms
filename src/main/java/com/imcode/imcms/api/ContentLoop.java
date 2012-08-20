package com.imcode.imcms.api;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.imcode.imcms.util.P;
import com.imcode.imcms.util.P2;
import imcode.server.document.textdocument.DocRef;
import org.hibernate.annotations.IndexColumn;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.*;

/**
 * A content can not be removed from a content loop - it can only be disabled.
 */
@Entity
@Table(name = "imcms_text_doc_content_loops")
public class ContentLoop implements Serializable, Cloneable {

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
            contentLoop.docId = docRef == null ? null : docRef.getDocId();
            contentLoop.docVersionNo = docRef == null ? null : docRef.getDocVersionNo();

            return this;
        }

        public Builder no(Integer no) {
            contentLoop.no = no;
            return this;
        }

        public Builder insertContent(int contentIndex) {
            Content content = Content.builder().no(contentLoop.contents.size()).build();
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

        public Builder swapContents(int contentIndexA, int contentIndexB) {
            Content contentA = contentLoop.contents.get(contentIndexA);
            Content contentB = contentLoop.contents.get(contentIndexB);

            contentLoop.contents.set(contentIndexA, contentB);
            contentLoop.contents.set(contentIndexB, contentA);

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "imcms_text_doc_contents",
            joinColumns = {
                    @JoinColumn(name = "doc_id", referencedColumnName = "doc_id"),
                    @JoinColumn(name = "doc_version_no", referencedColumnName = "doc_version_no"),
                    @JoinColumn(name = "loop_no", referencedColumnName = "no")
            }
    )
    @IndexColumn(name = "order_no") // todo: rename to index?
    private List<Content> contents = new LinkedList<Content>();

    protected ContentLoop() {
    }

    public Long getId() {
        return id;
    }

    public Integer getNo() {
        return no;
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

    public DocRef getDocRef() {
        return docId == null ? null : DocRef.of(docId, docVersionNo);
    }

    /**
     * @return all contents
     */
    public List<Content> getAllContents() {
        return Collections.unmodifiableList(contents);
    }

    /**
     * @return enabled contents.
     */
    public List<Content> getEnabledContents() {
        List<Content> enabledContents = Lists.newLinkedList();

        for (Content content: contents) {
            if (content.isEnabled()) enabledContents.add(content);
        }

        return Collections.unmodifiableList(enabledContents);
    }

    public Optional<P2<Content, Integer>> findContentWithIndexByNo(int contentNo) {
        for (int i = 0; i < contents.size(); i++) {
            Content content = contents.get(i);
            if (contents.get(i).getNo() == contentNo) return Optional.of(P.of(content, i));
        }

        return Optional.absent();
    }

    public Optional<Content> findContentByNo(int contentNo) {
        return findContentWithIndexByNo(contentNo).transform(new Function<P2<Content, Integer>, Content>() {
            public Content apply(P2<Content, Integer> input) { return input._1(); }
        });
    }

    public P2<ContentLoop, Content> addFirstContent() {
        ContentLoop contentLoop = builder(this).insertContent(0).build();

        return P.of(contentLoop, contentLoop.contents.get(0));
    }

    public P2<ContentLoop, Content> addLastContent() {
        ContentLoop contentLoop = builder(this).insertContent(contents.size()).build();
        List<Content> contents = contentLoop.contents;

        return P.of(contentLoop, contents.get(contents.size() - 1));
    }

    public Optional<P2<ContentLoop, Content>> addContentAfter(Content content) {
        Optional<P2<Content, Integer>> contentAndIndex = findContentWithIndexByNo(content.getNo());

        if (!contentAndIndex.isPresent()) return Optional.absent();

        Integer contentIndex = contentAndIndex.get()._2() + 1;
        ContentLoop contentLoop = builder(this).insertContent(contentIndex).build();

        return Optional.of(P.of(contentLoop, contentLoop.contents.get(contentIndex)));
    }

    public Optional<P2<ContentLoop, Content>> addContentBefore(Content content) {
        Optional<P2<Content, Integer>> contentAndIndex = findContentWithIndexByNo(content.getNo());

        if (!contentAndIndex.isPresent()) return Optional.absent();

        Integer contentIndex = contentAndIndex.get()._2();
        ContentLoop contentLoop = builder(this).insertContent(contentIndex).build();

        return Optional.of(P.of(contentLoop, contentLoop.contents.get(contentIndex)));
    }

    public Optional<ContentLoop> moveContentBackward(Content content) {
        Optional<P2<Content, Integer>> contentAndIndex = findContentWithIndexByNo(content.getNo());

        if (contentAndIndex.isPresent() && contentAndIndex.get()._1().isEnabled()) {
            int contentIndex = contentAndIndex.get()._2();

            for (int i = contentIndex - 1; i >= 0; i--) {
                Content prevContent = contents.get(i);
                if (prevContent.isEnabled()) {
                    return Optional.of(builder(this).swapContents(contentIndex, i).build());
                }
            }
        }

        return Optional.absent();
    }

    public Optional<ContentLoop> moveContentForward(Content content) {
        Optional<P2<Content, Integer>> contentAndIndex = findContentWithIndexByNo(content.getNo());

        if (contentAndIndex.isPresent() && contentAndIndex.get()._1().isEnabled()) {
            int contentIndex = contentAndIndex.get()._2();

            for (int i = contentIndex + 1; i < contents.size(); i++) {
                Content nextContent = contents.get(i);
                if (nextContent.isEnabled()) {
                    return Optional.of(builder(this).swapContents(contentIndex, i).build());
                }
            }
        }

        return Optional.absent();
    }


    public Optional<ContentLoop> disableContent(Content content) {
        Optional<Content> contentOpt = findContentByNo(content.getNo());

        return contentOpt.isPresent()
                ? Optional.of(builder(this).disableContent(contentOpt.get().getNo()).build())
                : Optional.<ContentLoop>absent();
    }

    public Optional<ContentLoop> enableContent(Content content) {
        Optional<Content> contentOpt = findContentByNo(content.getNo());

        return contentOpt.isPresent()
                ? Optional.of(builder(this).enableContent(contentOpt.get().getNo()).build())
                : Optional.<ContentLoop>absent();
    }
}