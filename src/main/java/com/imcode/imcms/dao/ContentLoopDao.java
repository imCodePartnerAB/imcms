package com.imcode.imcms.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.Content;
import com.imcode.imcms.api.ContentLoop;

// TODO: Refactor and optimize
/**
 * Content loop DAO.
 * 
 * @author Anton Josua
 */
public class ContentLoopDao extends HibernateTemplate {

	/**
	 * Content indexes immutable structure.
	 */
	private static final class ContentIndexes {
		final Integer lower;
		final Integer higher;
		final Integer sequence;
		
		public ContentIndexes(Integer lower, Integer higher, Integer sequence) {
			this.lower = lower;
			this.higher = higher;
			this.sequence = sequence;
		}
	}
	
	/**
	 * Creates and returns next context indexes.
	 * 
	 * @param loopId loop id.
	 * @return new instance of ContentIndexes. 
	 */
	private ContentIndexes getNextContentIndexes(long loopId) {
		List<Object[]> nextIndexesList = findByNamedQueryAndNamedParam(
				"Content.getNextIndexes", "loopId", loopId); 

		Object[] indexes = nextIndexesList.get(0);
		Integer lower = indexes[0] == null ? 0 : (Integer)indexes[0];
		Integer higher = indexes[1] == null ? 0 : (Integer)indexes[1];		
		Integer sequence = indexes[2] == null ? 0 : (Integer)indexes[2];

		return new ContentIndexes(lower, higher, sequence); 
	} 

	/**
	 * Creates and adds new Content at the fist position of the loop.
	 *  
	 * @param loopId loop id
	 * @return new Content added at the fist position of the loop.
	 */
	@Transactional
	public synchronized Content addFisrtContent(Long loopId) {
		ContentIndexes indexes = getNextContentIndexes(loopId);
				
		return createContent(loopId, indexes.sequence, indexes.lower);
	}

	/**
	 * Creates and adds new Content at the last position of the loop.
	 *  
	 * @param loopId loop id
	 * @return new Content added at the last position of the loop.
	 */	
	@Transactional
	public synchronized Content addLastContent(Long loopId) {
		ContentIndexes indexes = getNextContentIndexes(loopId);
		
		return createContent(loopId, indexes.sequence, indexes.higher);
	}
	
	/**
	 * Creates and saves new content.
	 * 
	 * @param loopId content's loop id
	 * @param sequenceIndex content's sequence index.
	 * @param orderIndex content's order index. 
	 * @return new instance of the Content.
	 */
	private Content createContent(Long loopId, Integer sequenceIndex, Integer orderIndex) {
		Content content = new Content();
		content.setLoopId(loopId);		
		content.setSequenceIndex(sequenceIndex);		
		content.setOrderIndex(orderIndex);		
		
		save(content);
		
		return content;		
	}	

	@Transactional
	public ContentLoop createContentLoop(Long metaId, Integer loopIndex, Integer baseIndex) {
		ContentLoop loop = new ContentLoop();
		loop.setMetaId(metaId);
		loop.setNo(loopIndex);
		loop.setBaseIndex(baseIndex);
		
		save(loop);
		
		return loop;
	}

	@Transactional
	public void deleteContent(Long id) {
		getSession().getNamedQuery("Content.delete")
			.setParameter("id", id)
			.executeUpdate();
	}

	/**
	 * Returns loop or null if loop can not be found. 
	 * 
	 * @param metaId meta id.
	 * @param index loop index.
	 * 
	 * @return loop or null if loop can not be found. 
	 */
	@Transactional
	public ContentLoop getContentLoop(Long metaId, Integer index) {
		return (ContentLoop)getSession().getNamedQuery("ContentLoop.getByMetaIdAndIndex")
			.setParameter("metaId", metaId)
			.setParameter("index", index)
			.uniqueResult();
	}

	@Transactional
	public synchronized void moveContentUp(ContentLoop loop, Long contentId) {
		List<Content> contents = loop.getContents();
		int size = contents.size();		
		Query hql = getSession().getNamedQuery("Content.updateOrderIndex");
		
		for (int i = 0; i < size; i++) {
			Content content = contents.get(i);
			
			if (content.getId().equals(contentId)) {
				if (i != 0) {
					ContentIndexes contentIndexes = getNextContentIndexes(loop.getId());
					Content prevContent = contents.get(i - 1);
					
					Integer prevIndex = prevContent.getOrderIndex();
					Integer currentIndex = content.getOrderIndex();
					Integer tempIndex = contentIndexes.higher;
										
					hql.setParameter("orderIndex", tempIndex);
					hql.setLong("id", prevContent.getId());							
					int n = hql.executeUpdate();
					
					hql.setParameter("orderIndex", prevIndex);
					hql.setLong("id", content.getId());							
					n = hql.executeUpdate();
					
					hql.setParameter("orderIndex", currentIndex);
					hql.setLong("id", prevContent.getId());							
					n = hql.executeUpdate();
				}
				
				break;				
			}			
		}		
	}
	
	@Transactional
	public synchronized void moveContentDown(ContentLoop loop, Long contentId) {
		List<Content> contents = loop.getContents();
		int size = contents.size();
		Query hql = getSession().getNamedQuery("Content.updateOrderIndex");
		
		for (int i = 0; i < size; i++) {
			Content content = contents.get(i);
			if (content.getId().equals(contentId)) {
				if (i != size - 1) {
					ContentIndexes contentIndexes = getNextContentIndexes(loop.getId());
					Content nextContent = contents.get(i + 1);
					
					Integer nextIndex = nextContent.getOrderIndex();
					Integer currentIndex = content.getOrderIndex();
					Integer tempIndex = contentIndexes.lower;
					
					hql.setParameter("orderIndex", tempIndex);
					hql.setLong("id", nextContent.getId());							
					int n = hql.executeUpdate();
					
					hql.setParameter("orderIndex", nextIndex);
					hql.setLong("id", content.getId());							
					n = hql.executeUpdate();
					
					hql.setParameter("orderIndex", currentIndex);
					hql.setLong("id", nextContent.getId());							
					n = hql.executeUpdate();
				}
				
				break;
			}
		}
	}

	
	@Transactional
	public synchronized Content insertNewContentAfter(ContentLoop loop, Long contentId) {
		Query hql = getSession().getNamedQuery("Content.updateOrderIndex");
						
		ContentIndexes contentIndexes = getNextContentIndexes(loop.getId());
		int nextHigherOrderIndex = contentIndexes.higher;
		
		Content newContent = null;
		List<Content> contents = loop.getContents();
		int size = contents.size();
		
		for (int i = size - 1; i >= 0; i--) {
			Content content = contents.get(i);
			
			if (content.getId().equals(contentId)) {
				newContent = new Content();
				newContent.setLoopId(loop.getId());
				newContent.setSequenceIndex(contentIndexes.sequence);
				newContent.setOrderIndex(nextHigherOrderIndex);
				
				save(newContent);
				
				break;
			}
			
		    int currentOrderIndex = content.getOrderIndex();
		    
		    hql.setParameter("orderIndex", nextHigherOrderIndex);
		    hql.setParameter("id", content.getId());
		    
		    hql.executeUpdate();
		    
		    nextHigherOrderIndex = currentOrderIndex;
		}
		
		if (newContent == null) {
			throw new RuntimeException("Content with id [" + 
				contentId + "] not found for loop id [" + loop.getId() + "].");
		}
		
		return newContent;
	}

	
	@Transactional
	public synchronized Content insertNewContentBefore(ContentLoop loop, Long contentId) {
		Query hql = getSession().getNamedQuery("Content.updateOrderIndex");				
		ContentIndexes contentIndexes = getNextContentIndexes(loop.getId());
		int nextLowerOrderIndex = contentIndexes.lower;				
		
		Content newContent = null;
		
		for (Content content: loop.getContents()) {
			if (content.getId().equals(contentId)) {
				newContent = new Content();
				newContent.setLoopId(loop.getId());
				newContent.setSequenceIndex(contentIndexes.sequence);
				newContent.setOrderIndex(nextLowerOrderIndex);
				
				save(newContent);						
				break;
			}
			
		    int currentOrderIndex = content.getOrderIndex();
		    
		    hql.setParameter("orderIndex", nextLowerOrderIndex);
		    hql.setParameter("id", content.getId());
		    
		    hql.executeUpdate();
		    
		    nextLowerOrderIndex = currentOrderIndex;
		}
		
		if (newContent == null) {
			throw new RuntimeException("Content with id [" + 
				contentId + "] not found for loop id [" + loop.getId() + "].");
		}
		
		return newContent;
	}
	
	/*
	private Content getContent(ContentLoop loop, Long contentId) {
		return (Content)getSession().getNamedQuery("Content.getByLoopIdAndCondentId")
			.setParameter("loopId", loop.getId())
			.setParameter("contentId", contentId)
			.uniqueResult();		
	}
	*/	
}