package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import org.apache.commons.lang3.builder.EqualsBuilder;

import javax.persistence.*;
import java.util.Objects;

/**
 * Text document include.
 */
@Entity
@Table(name = "includes")
public class Include implements Cloneable {

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
        if (!(obj instanceof Include)) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        final Include o = (Include) obj;

        return new EqualsBuilder()
                .append(no, o.getNo()).isEquals();
    }

    @Override
    public Include clone() {
        try {
            return (Include) super.clone();
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
