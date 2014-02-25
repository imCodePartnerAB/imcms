package com.imcode.imcms.mapping.orm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;

import java.util.Objects;

/**
 * Text document include.
 */
@Entity
@Table(name = "includes")
public class TextDocInclude implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "meta_id")
    private Integer docId;

    @Column(name = "included_meta_id")
    private Integer includedDocumentId;

    // Include no
    @Column(name = "include_id")
    private Integer no;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TextDocInclude)) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        final TextDocInclude o = (TextDocInclude) obj;

        return new EqualsBuilder()
                .append(no, o.getNo()).isEquals();
    }

    @Override
    public TextDocInclude clone() {
        try {
            return (TextDocInclude) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(no);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public Integer getIncludedDocumentId() {
        return includedDocumentId;
    }

    public void setIncludedDocumentId(Integer includedDocId) {
        this.includedDocumentId = includedDocId;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer index) {
        this.no = index;
    }
}
