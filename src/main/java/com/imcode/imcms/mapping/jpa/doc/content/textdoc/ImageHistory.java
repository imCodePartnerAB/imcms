package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.User;

import javax.persistence.*;
import java.util.Date;

/**
 *
 */
@Entity
@Table(name = "imcms_text_doc_images_history")
public class ImageHistory extends ImageBase {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User modifiedBy;

    @Column(name = "modified_dt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDt;

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
