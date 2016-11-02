package com.imcode.imcms.imagearchive.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "archive_image_keywords")
@NamedQueries({
        @NamedQuery(name = "keywordsUsedByImages",
                query = "SELECT DISTINCT k.id AS id, k.keywordNm AS keywordNm FROM ImageKeywords ik " +
                        "INNER JOIN ik.keyword k ORDER BY k.keywordNm")
})
@IdClass(ImageKeywordsPK.class)
public class ImageKeywords implements Serializable {
    private static final long serialVersionUID = 3659080044661718290L;

    @Id
    @Column(name = "image_id", nullable = false)
    private long imageId;

    @ManyToOne
    @JoinColumn(name = "image_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Images image;

    @Id
    @Column(name = "keyword_id", nullable = false)
    private long keywordId;

    @ManyToOne
    @JoinColumn(name = "keyword_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Keywords keyword;

    @Column(name = "created_dt", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDt = new Date();


    public ImageKeywords() {
    }


    public Date getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(Date createdDt) {
        this.createdDt = createdDt;
    }

    public Images getImage() {
        return image;
    }

    public void setImage(Images image) {
        this.image = image;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public Keywords getKeyword() {
        return keyword;
    }

    public void setKeyword(Keywords keyword) {
        this.keyword = keyword;
    }

    public long getKeywordId() {
        return keywordId;
    }

    public void setKeywordId(long keywordId) {
        this.keywordId = keywordId;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final ImageKeywords other = (ImageKeywords) obj;
        if (this.imageId != other.imageId) {
            return false;
        }

        if (this.keywordId != other.keywordId) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (int) (this.imageId ^ (this.imageId >>> 32));
        hash = 79 * hash + (int) (this.keywordId ^ (this.keywordId >>> 32));

        return hash;
    }

    @Override
    public String toString() {
        return String.format("ImageKeywords[imageId: %d, keywordId: %d]",
                imageId, keywordId);
    }
}
