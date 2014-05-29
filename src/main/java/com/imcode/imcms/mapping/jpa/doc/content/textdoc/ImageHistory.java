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

    public ImageHistory() {
    }

    public ImageHistory(Image image, User modifiedBy) {
        setAlign(image.getAlign());
        setAlternateText(image.getAlternateText());
        setBorder(image.getBorder());
        setCropRegion(image.getCropRegion());
        setFormat(image.getFormat());
        setGeneratedFilename(image.getGeneratedFilename());
        setHeight(image.getHeight());
        setHorizontalSpace(image.getHorizontalSpace());
        setLinkUrl(image.getLinkUrl());
        setLoopEntryRef(image.getLoopEntryRef());
        setLowResolutionUrl(image.getLowResolutionUrl());
        setName(image.getName());
        setNo(image.getNo());
        setResize(image.getResize());
        setRotateAngle(image.getRotateAngle());
        setTarget(image.getTarget());
        setType(image.getType());
        setUrl(image.getUrl());
        setVerticalSpace(image.getVerticalSpace());
        setWidth(image.getWidth());
        setLanguage(image.getLanguage());
        setVersion(image.getVersion());
        setArchiveImageId(image.getArchiveImageId());

        setModifiedBy(modifiedBy);
        setModifiedDt(new Date());
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
