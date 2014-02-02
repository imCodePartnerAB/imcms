package com.imcode.imcms.mapping.orm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Objects;

/**
 * Text document include. 
 */
@Entity
@Table(name="includes")
public class Include implements Cloneable {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@Column(name="meta_id")
	private Integer docId;
	
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
    public Include clone() {
        try {                          
            return (Include)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

	@Override 
    public int hashCode() {
        return Objects.hash(index);
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

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}		
}
