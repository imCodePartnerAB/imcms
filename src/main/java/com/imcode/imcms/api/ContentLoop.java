package com.imcode.imcms.api;

import imcode.server.document.textdocument.DocVersionItem;
import imcode.server.document.textdocument.DocOrderedItem;
import org.hibernate.annotations.IndexColumn;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.*;

/**
 * @see com.imcode.imcms.dao.ContentLoopDao
 */
@Entity
@Table(name="imcms_text_doc_content_loops")
public class ContentLoop implements Serializable, Cloneable, DocVersionItem, DocOrderedItem {
	
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private Integer no;

    @Column(name="doc_id")
    private Integer docId;

    @Column(name="doc_version_no")
    private Integer docVersionNo;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "imcms_text_doc_contents",
            joinColumns = {@JoinColumn(name="doc_id", referencedColumnName="doc_id"),
                           @JoinColumn(name="doc_version_no", referencedColumnName="doc_version_no"),
                           @JoinColumn(name="loop_no", referencedColumnName="no")})
    @IndexColumn(name = "order_no")
    private List<Content> contents = new LinkedList<Content>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNo() {
        return no;
    }


    public void setNo(Integer no) {
        this.no = no;
    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public Integer getDocVersionNo() {
        return docVersionNo;
    }

    public void setDocVersionNo(Integer docVersionNo) {
        this.docVersionNo = docVersionNo;
    }


    // synchronized methods

    @Override
    public synchronized String toString() {
        return String.format("{id: %s, docId: %s, docVersionNo: %s, no: %s, contents: [%s]}",
                id, docId, docVersionNo, no, "..."); // causes hibernate stack overflow: StringUtils.join(contents, ", "));
    }

    @Override
    public synchronized ContentLoop clone() {
        ContentLoop clone;

        try {
            clone = (ContentLoop)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        List<Content> contentsClone = new LinkedList<Content>();

        for (Content content: contents) {
            contentsClone.add(content.clone());
        }

        clone.contents  = contentsClone;

        return clone;
    }

    /**
     *
     * @param contentNo
     * @return content index
     * @throws RuntimeException if there is no such content.
     */
    private Integer getContentIndex(int contentNo) {
        int contentsCount = contents.size();

        for (int i = 0; i < contentsCount; i++) {
            Content content = contents.get(i);
            if (content.getNo() == contentNo) {
                return i;
            }
        }

        return null;
    }


    private int getCheckedContentIndex(int contentNo) {
        Integer index = getContentIndex(contentNo);

        if (index == null) {
            throw new RuntimeException(String.format("No such content - docId: %s, docVersionNo: %s, loop no: %s, contentNo: %s.", getDocId(), getDocVersionNo(), getNo(), contentNo));
        }

        return index;
    }


    private Content addContent(int contentIndex) {
        Content content = new Content();
        content.setNo(contents.size());

        contents.add(contentIndex, content);

        return content;
    }

    /**
     * @param contentNo content no.
     * @return Content with given no.
     * @throws RuntimeException if there is no such content.
     */
    public synchronized Content getContent(int contentNo) {
        return contents.get(getCheckedContentIndex(contentNo));
    }


    /**
     * @return contents sorted by order no.
     */
    public synchronized List<Content> getContents() {
        return Collections.unmodifiableList(contents);
    }

    public synchronized Content addFirstContent() {
        return addContent(0);
    }

    public synchronized Content addLastContent() {
        return addContent(contents.size());
    }

    public synchronized Content insertContentAfter(int contentNo) {
        return addContent(getCheckedContentIndex(contentNo) + 1);

    }

    public synchronized Content insertContentBefore(int contentNo) {
        return addContent(getCheckedContentIndex(contentNo));
    }

    public synchronized Content moveContentBackward(int contentNo) {
        int contentIndex = getCheckedContentIndex(contentNo);
        Content content = contents.get(contentIndex);

        if (!content.isEnabled()) {
            throw new RuntimeException(String.format("Can not move disabled content - docId: %s, docVersionNo: %s, no: %s, contentNo: %s.", getDocId(), getDocVersionNo(), getNo(), contentNo));
        }

        for (int i = contentIndex - 1; i >= 0; i--) {
            Content prevContent = contents.get(i);
            if (prevContent.isEnabled()) {
                contents.set(i, content);
                contents.set(contentIndex, prevContent);

                break;
            }
        }

        return content;
    }

    public synchronized Content moveContentForward(int contentNo) {
        int contentIndex = getCheckedContentIndex(contentNo);
        Content content = contents.get(contentIndex);

        if (!content.isEnabled()) {
            throw new RuntimeException(String.format("Can not move disabled content - docId: %s, docVersionNo: %s, no: %s, contentNo: %s.", getDocId(), getDocVersionNo(), getNo(), contentNo));
        }

        int contentCount = contents.size();

        for (int i = contentIndex + 1; i <  contentCount; i++) {
            Content nextContent = contents.get(i);
            if (nextContent.isEnabled()) {
                contents.set(i, content);
                contents.set(contentIndex, nextContent);

                break;
            }
        }

        return content;
    }

    public synchronized Content disableContent(int contentNo) {
        Content content = getContent(contentNo);

        content.setEnabled(false);

        return content;
    }

    public synchronized Content enableContent(int contentNo) {
        Content content = getContent(contentNo);

        content.setEnabled(true);

        return content;
    }

    public synchronized boolean contentExists(int contentNo) {
        return getContentIndex(contentNo) != null;
    }
}