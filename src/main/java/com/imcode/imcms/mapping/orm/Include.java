package com.imcode.imcms.mapping.orm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Text document include. 
 */
@Entity
@Table(name="includes")
public class Include {

	@Id @Column(name="meta_id")
	private Integer metaId;
	
	@Column(name="included_meta_id")
	private Integer includedDocumentId;


    // Include no
	@Column(name="include_id")
	private Integer index;
	
	@Override 
    public boolean equals( Object obj ) {
        if ( !( obj instanceof Include ) ) {
            return false;
        }
        
        if (this == obj) {
        	return true;
        }
        
        final Include o = (Include)obj;
        
        return new EqualsBuilder()
        		.append(index, o.getIndex()).isEquals();
    }

	@Override 
    public int hashCode() {
        return new HashCodeBuilder()
        		.append(index).toHashCode();
    }
    

	public Integer getMetaId() {
		return metaId;
	}

	public void setMetaId(Integer metaId) {
		this.metaId = metaId;
	}

	public Integer getIncludedDocumentId() {
		return includedDocumentId;
	}

	public void setIncludedDocumentId(Integer includedMetaId) {
		this.includedDocumentId = includedMetaId;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}		
}
