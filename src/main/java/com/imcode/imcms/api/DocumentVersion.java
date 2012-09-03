package com.imcode.imcms.api;

import com.google.common.base.Objects;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Document content version.
 */
@Entity
@Table(name = "imcms_doc_versions")
public class DocumentVersion implements Cloneable {

    public static final int WORKING_VERSION_NO = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doc_id", updatable = false, nullable = false)
    private Integer docId;

    private Integer no;

    @Column(name = "created_by", updatable = false)
    private Integer createdBy;

    @Column(name = "created_dt", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDt;

    @Column(name = "modified_by", updatable = true, nullable = false)
    private Integer modifiedBy;

    @Column(name = "modified_dt", updatable = true, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDt;

    public DocumentVersion() {}

    public DocumentVersion(Integer docId, Integer no, Integer createdBy, Date createdDt) {
        this.docId = docId;
        this.no = no;
        this.createdBy = createdBy;
        this.createdDt = createdDt;
        this.modifiedBy = createdBy;
        this.modifiedDt = createdDt;
    }

    @Override
    public DocumentVersion clone() {
        try {
            return (DocumentVersion) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof DocumentVersion)) return false;

        DocumentVersion that = (DocumentVersion)other;

        return that.canEqual(this)
                && Objects.equal(this.id, that.id)
                && Objects.equal(this.no, that.no)
                && Objects.equal(this.docId, that.docId)
                && Objects.equal(this.createdBy, that.createdBy)
                && Objects.equal(this.createdDt, that.createdDt)
                && Objects.equal(this.modifiedBy, that.modifiedBy)
                && Objects.equal(this.modifiedDt, that.modifiedDt);
    }

    protected boolean canEqual(Object other) { return other instanceof DocumentVersion; }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, no, docId, createdBy, createdDt, modifiedBy, modifiedDt);

    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("docId", docId)
                .add("no", no)
                .add("createdBy", createdBy)
                .add("createdDt", createdDt)
                .add("modifiedBy", modifiedBy)
                .add("modifiedDt", modifiedDt)
                .toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }


    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }


    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(Date createdDt) {
        this.createdDt = createdDt;
    }

    public Integer getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Integer modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedDt() {
        return modifiedDt;
    }

    public void setModifiedDt(Date modifiedDt) {
        this.modifiedDt = modifiedDt;
    }
}