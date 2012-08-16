package com.imcode.imcms.api;

import com.google.common.collect.Lists;
import imcode.server.document.textdocument.DocRef;
import org.hibernate.annotations.IndexColumn;
import scala.Option;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.*;

/**
 * @see com.imcode.imcms.dao.ContentLoopDao
 */
@Entity
@Table(name = "imcms_text_doc_content_loops")
public class ContentLoop implements Serializable, Cloneable {

    public static final class Builder {

        private ContentLoop vo = new ContentLoop();

        public Builder() {
        }

        public Builder id(Long id) {
            vo.id = id;
            return this;
        }

        public Builder docRef(DocRef docRef) {
            vo.docId = docRef == null ? null : docRef.getDocId();
            vo.docVersionNo = docRef == null ? null : docRef.getDocVersionNo();

            return this;
        }

        public Builder no(Integer no) {
            vo.no = no;
            return this;
        }

        public Builder addContent(int contentIndex) {
            Content content = new Content();
            content.setNo(vo.contents.size());

            vo.contents.add(contentIndex, content);

            return this;
        }

        public Builder toggleContent(int contentIndex, boolean enabled) {
            vo.contents.set(contentIndex, Content.builder(vo.contents.get(contentIndex)).enabled(enabled).build());

            return this;
        }

        public Builder swapContents(int contentIndexA, int contentIndexB) {
            Content contentA = vo.contents.get(contentIndexA);
            Content contentB = vo.contents.get(contentIndexB);

            vo.contents.set(contentIndexA, contentB);
            vo.contents.set(contentIndexB, contentA);

            return this;
        }

        public ContentLoop build() {
            return vo.clone();
        }


    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ContentLoop contentLoop) {
        Builder builder = new Builder();

        builder.vo = contentLoop.clone();

        return builder;
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
    @IndexColumn(name = "order_no") // todo: rename to index
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

        List<Content> contentsClone = new LinkedList<Content>();

        for (Content content : contents) {
            contentsClone.add(content.clone());
        }

        clone.contents = contentsClone;

        return clone;
    }

    public DocRef getDocRef() {
        return docId == null ? null : new DocRef(docId, docVersionNo);
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

    public Option<Tuple2<Content, Integer>> findContent(int contentNo) {
        for (int i = 0; i < contents.size(); i++) {
            Content content = contents.get(i);
            if (contents.get(i).getNo().equals(contentNo)) return Option.apply(Tuple2.apply(content, i));
        }

        return Option.empty();
    }

    public Tuple2<ContentLoop, Content> addFirstContent() {
        ContentLoop contentLoop = builder(this).addContent(0).build();

        return Tuple2.apply(contentLoop, contents.get(0));
    }

    public Tuple2<ContentLoop, Content> addLastContent() {
        ContentLoop contentLoop = builder(this).addContent(contents.size()).build();

        return Tuple2.apply(contentLoop, contents.get(contents.size() - 1));
    }

    public Option<Tuple2<ContentLoop, Content>> insertContentAfter(int contentNo) {
        Option<Tuple2<Content, Integer>> contentAndIndex = findContent(contentNo);

        if (contentAndIndex.isEmpty()) return Option.empty();

        Integer contentIndex = contentAndIndex.get()._2() + 1;
        ContentLoop contentLoop = builder(this).addContent(contentIndex).build();

        return Option.apply(Tuple2.apply(contentLoop, contentLoop.contents.get(contentIndex)));
    }

    public Option<Tuple2<ContentLoop, Content>> insertContentBefore(int contentNo) {
        Option<Tuple2<Content, Integer>> contentAndIndex = findContent(contentNo);

        if (contentAndIndex.isEmpty()) return Option.empty();

        Integer contentIndex = contentAndIndex.get()._2();
        ContentLoop contentLoop = builder(this).addContent(contentIndex).build();

        return Option.apply(Tuple2.apply(contentLoop, contentLoop.contents.get(contentIndex)));
    }

    public Tuple2<ContentLoop, Boolean> moveContentBackward(int contentNo) {
        Option<Tuple2<Content, Integer>> contentAndIndex = findContent(contentNo);

        if (contentAndIndex.isDefined() && contentAndIndex.get()._1().isEnabled()) {
            int contentIndex = contentAndIndex.get()._2();

            for (int i = contentIndex - 1; i >= 0; i--) {
                Content prevContent = contents.get(i);
                if (prevContent.isEnabled()) {
                    ContentLoop contentLoop = builder(this).swapContents(contentIndex, i).build();
                    return Tuple2.apply(contentLoop, true);
                }
            }
        }

        return Tuple2.apply(this, false);
    }

    public Tuple2<ContentLoop, Boolean> moveContentForward(int contentNo) {
        Option<Tuple2<Content, Integer>> contentAndIndex = findContent(contentNo);

        if (contentAndIndex.isDefined() && contentAndIndex.get()._1().isEnabled()) {
            int contentIndex = contentAndIndex.get()._2();

            for (int i = contentIndex + 1; i < contents.size(); i++) {
                Content nextContent = contents.get(i);
                if (nextContent.isEnabled()) {
                    ContentLoop contentLoop = builder(this).swapContents(contentIndex, i).build();
                    return Tuple2.apply(contentLoop, true);
                }
            }
        }

        return Tuple2.apply(this, false);
    }


    public Tuple2<ContentLoop, Boolean> disableContent(int contentNo) {
        Option<Tuple2<Content, Integer>> contentAndIndex = findContent(contentNo);

        return contentAndIndex.isEmpty()
                ? Tuple2.apply(this, false)
                : Tuple2.apply(builder(this).toggleContent(contentAndIndex.get()._2(), false).build(), true);
    }

    public Tuple2<ContentLoop, Boolean> enableContent(int contentNo) {
        Option<Tuple2<Content, Integer>> contentAndIndex = findContent(contentNo);

        return contentAndIndex.isEmpty()
                ? Tuple2.apply(this, false)
                : Tuple2.apply(builder(this).toggleContent(contentAndIndex.get()._2(), true).build(), true);
    }
}