package com.imcode.imcms.addon.imagearchive.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="archive_keywords")
public class Keywords implements Serializable {
    private static final long serialVersionUID = -5618267783578349726L;
    
    @Id
    @Column(name="id", nullable=false)
    @GeneratedValue
    private long id;
    
    @Column(name="keyword_nm", length=50, nullable=false)
    private String keywordNm;
    
    @Column(name="created_dt", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDt = new Date();

    
    public Keywords() {
    }

    public Date getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(Date createdDt) {
        this.createdDt = createdDt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKeywordNm() {
        return keywordNm;
    }

    public void setKeywordNm(String keywordNm) {
        this.keywordNm = keywordNm;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final Keywords other = (Keywords) obj;
        if (this.id != other.id) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (int) (this.id ^ (this.id >>> 32));
        
        return hash;
    }

    @Override
    public String toString() {
        return String.format("com.imcode.imcms.addon.imagearchive.entity.Keywords[id: %d, keywordNm: %s]", 
                id, keywordNm);
    }
}
