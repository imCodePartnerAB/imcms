package com.imcode.imcms.mapping.jpa.doc;

import com.google.common.base.Objects;
import com.imcode.imcms.mapping.jpa.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "imcms_doc_versions")
public class Version implements Cloneable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "doc_id", updatable = false, nullable = false)
    private Integer docId;

    private int no;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_dt", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDt;

    @ManyToOne
    @JoinColumn(name = "modified_by")
    private User modifiedBy;

    @Column(name = "modified_dt", updatable = true, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDt;

    public Version() {
    }

    public Version(Integer docId, int no, User createdBy, Date createdDt, User modifiedBy, Date modifiedDt) {
        this.docId = docId;
        this.no = no;
        this.createdBy = createdBy;
        this.createdDt = createdDt;
        this.modifiedBy = modifiedBy;
        this.modifiedDt = modifiedDt;
    }

    @Override
    public Version clone() {
        try {
            return (Version) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof Version && (equals((Version) o)));
    }

    private boolean equals(Version that) {
        return Objects.equal(this.id, that.id)
                && Objects.equal(this.no, that.no)
                && Objects.equal(this.docId, that.docId)
                && Objects.equal(this.createdBy, that.createdBy)
                && Objects.equal(this.createdDt, that.createdDt)
                && Objects.equal(this.modifiedBy, that.modifiedBy)
                && Objects.equal(this.modifiedDt, that.modifiedDt);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, no, docId, createdBy, createdDt, modifiedBy, modifiedDt);
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

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(Date createdDt) {
        this.createdDt = createdDt;
    }

    public User getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(User modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedDt() {
        return modifiedDt;
    }

    public void setModifiedDt(Date modifiedDt) {
        this.modifiedDt = modifiedDt;
    }
}