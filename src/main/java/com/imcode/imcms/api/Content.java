package com.imcode.imcms.api;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="text_doc_contents")
@NamedQueries({
	@NamedQuery(name="Content.getByLoopIdAndOrderIndex", 
		query="SELECT c FROM Content c " +
				"WHERE c.loopId = :loopId AND c.orderIndex = :orderIndex"),
								
	@NamedQuery(name="Content.getNextIndexes", 
		query="SELECT " +
				 "min(c.orderIndex)    - 1, " +
				 "max(c.orderIndex)    + 1, " + 
			     "max(c.sequenceIndex) + 1  " + 
               "FROM " +
                 "ContentLoop l JOIN l.contents c " +
		       "WHERE l.id = :loopId"),

   	@NamedQuery(name="Content.updateOrderIndex", 
		query="UPDATE Content c " +
				"SET c.orderIndex = :orderIndex WHERE c.id = :id"),
				
   	@NamedQuery(name="Content.delete", 
   			query="DELETE FROM Content c WHERE c.id = :id")
})
public class Content {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="loop_id")
	private Long loopId;	
	
	@Column(name="order_index")
	private Integer orderIndex;
	
	@Column(name="sequence_index", updatable=false)
	private Integer sequenceIndex;	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

	public Long getLoopId() {
		return loopId;
	}

	public void setLoopId(Long contentId) {
		this.loopId = contentId;
	}

	public Integer getSequenceIndex() {
		return sequenceIndex;
	}

	public void setSequenceIndex(Integer sequenceIndex) {
		this.sequenceIndex = sequenceIndex;
	}		
}