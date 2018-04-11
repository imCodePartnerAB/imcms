package com.imcode.imcms.persistence.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "imcms_doc_versions")
@NoArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Version implements Cloneable, Serializable {

    public static final int WORKING_VERSION_INDEX = 0;

    private static final long serialVersionUID = 9090936463043750021L;
    /**
     * @deprecated need to make composite primary key from docId and no
     */
    @Id
    @Deprecated
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "doc_id", updatable = false, nullable = false)
    private Integer docId;

    private int no;

    @ManyToOne
    @JoinColumn(name = "created_by")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private User createdBy;

    @Column(name = "created_dt", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDt;

    @ManyToOne
    @JoinColumn(name = "modified_by")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private User modifiedBy;

    @Column(name = "modified_dt", updatable = true, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDt;

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
}
