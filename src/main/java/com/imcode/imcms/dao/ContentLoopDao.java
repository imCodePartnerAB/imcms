package com.imcode.imcms.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.Content;
import com.imcode.imcms.api.ContentLoop;

/**
 * Content loop DAO.
 */
public class ContentLoopDao extends HibernateTemplate {

    private static class ContentIndexes {
        Integer sequence;
        Integer lower;
        Integer higher;
    }

    private ContentIndexes getContentIndexes(Long loopId) {
        Integer[] indexes = (Integer[])getSession().createQuery("SELECT max(c.index) + 1, min(c.orderIndex) - 1, max(c.orderIndex) + 1  FROM Content c WHERE c.looId = ?")
                .setParameter(0, loopId)
                .uniqueResult();

        ContentIndexes contentIndexes = new ContentIndexes();

        contentIndexes.sequence = indexes[0];
        contentIndexes.lower = indexes[1];
        contentIndexes.higher = indexes[2];

        return contentIndexes;
    }

    /**
     * Returns content loop.
     */
    @Transactional
    public ContentLoop getContentLoop(Long loopId) {
        return (ContentLoop)get(ContentLoop.class, loopId);
    }
    
	
	/**
	 * Creates and adds new content at the fist position of the loop.
	 *  
	 * @param loopId loop id
	 * @return new Content added at the fist position of the loop.
	 */
	@Transactional
	public synchronized Content addFisrtContent(Long loopId) {
		ContentIndexes  indexes = getContentIndexes(loopId);
        
		return createContent(loopId, indexes.sequence, indexes.lower);
	}
    
    
	/**
	 * Creates and adds new content at the last position of the loop.
	 *  
	 * @param loopId loop id
	 * @return new Content added at the last position of the loop.
	 */	
	@Transactional
	public synchronized Content addLastContent(Long loopId) {
        ContentIndexes  indexes = getContentIndexes(loopId);

		return createContent(loopId, indexes.sequence, indexes.higher);
	}
        
	
	
	/**
	 * Saves conent loop.
     *
     * @param loop content loop.
     * @return saved content loop. 
	 */
	@Transactional
	public synchronized ContentLoop saveContentLoop(final ContentLoop loop) {
        ContentLoop loopClone = loop.clone();

        save(loop.clone());
        
        return loopClone;
	} 
	
	/**
	 * Creates and saves a new content.
	 * 
	 * @param loopId content's loop id
	 * @param sequenceIndex content's sequence index.
	 * @param orderIndex content's order index. 
	 * @return new instance of the Content.
	 */
	private Content createContent(Long loopId, Integer sequenceIndex, Integer orderIndex) {
		Content content = new Content();
		content.setLoopId(loopId);		
		content.setIndex(sequenceIndex);
		content.setOrderIndex(orderIndex);		
		
		save(content);
		
		return content;		
	}	

	/**
	 * Create content loop with single content.
	 * 
	 * @param metaId meta id.
	 * @param loopNo loop number.
	 * @return
	 */
	@Transactional
	public synchronized ContentLoop createContentLoop(Integer metaId, Integer documentVersion, Integer loopNo) {
		ContentLoop loop = new ContentLoop();
		loop.setMetaId(metaId);
		loop.setDocumentVersion(documentVersion);
		loop.setNo(loopNo);
		
		Content content = new Content();
		content.setOrderIndex(0);
		content.setIndex(0);
		
		loop.getContents().add(content);
		
		save(loop);		
		
		return loop;
	}

    
	@Transactional
    // rename to disable.
	public void deleteContent(Long contentId) {
        Content content = (Content)get(Content.class, contentId);

        if (content != null) {
            content.setEnabled(false);
            save(content);
        }
	}

    
	@Transactional
	public synchronized void deleteContent(Long loopId, int sequenceIndex) {
		Long contentId = getContentId(loopId, sequenceIndex);
		deleteContent(contentId);
	}
	


	/**
	 * Returns loop or null if loop can not be found. 
	 * 
	 * @param metaId meta id.
	 * @param no loop no.
	 * 
	 * @return loop or null if loop can not be found. 
	 */
	@Transactional
	public ContentLoop getContentLoop(Integer metaId, Integer documentVersion, Integer no) {
		return (ContentLoop)getSession().getNamedQuery("ContentLoop.getByMetaIdAndDocumentVersionAndNo")
			.setParameter("metaId", metaId)
			.setParameter("documentVersion", documentVersion)
			.setParameter("no", no)
			.uniqueResult();
	}
	
	
	/**
	 * Returns document content loops. 
	 * 
	 * @param metaId meta id.
	 * 
	 * @return document content loops. 
	 */
	@Transactional
	public List<ContentLoop> getContentLoops(Integer metaId, Integer documentVersion) {
		return findByNamedQueryAndNamedParam("ContentLoop.getByMetaIdAndDocumentVersion", 
				new String[] {"metaId", "documentVersion"}, new Object[] {metaId, documentVersion });
	}
	
	// TODO: Optimize
	private Long getContentId(Long loopId, Integer sequenceIndex) {
		return (Long)getSession().getNamedQuery("Content.getIdByLoopIdAndSequenceIndex")
			.setParameter("loopId", loopId)
			.setParameter("sequenceIndex", sequenceIndex)
			.uniqueResult();
	} 	

    
	@Transactional	
	public void moveContentUp(Long loopId, Integer sequenceIndex) {
		Long contentId = getContentId(loopId, sequenceIndex);
		if (contentId != null) {
			moveContentUp(loopId, contentId);
		}
	}

	
	@Transactional
	public void moveContentUp(Long loopId, Long contentId) {
        Content content = (Content)get(Content.class, contentId);

        if (content != null) {
            Content prevContent = (Content)getSession().getNamedQuery("Content.getPrevsByOrder")
                 .setParameter("loopId", loopId)
                 .setParameter("orderIndex", content.getOrderIndex())
                 .setMaxResults(1)
                 .uniqueResult();
                                   
            if (prevContent != null) {
                Integer ordIndex = content.getOrderIndex();
                Integer prevOrdIndex = prevContent.getOrderIndex();
                
                delete(prevContent);
                getSession().flush();

                content.setOrderIndex(prevOrdIndex);
                save(content);
                getSession().flush();

                prevContent.setOrderIndex(ordIndex);
                save(prevContent);
                getSession().flush();
            }
        }
	}
	
	
	@Transactional	
	public void moveContentDown(Long loopId, Integer sequenceIndex) {
		Long contentId = getContentId(loopId, sequenceIndex);
		if (contentId != null) {
			moveContentDown(loopId, contentId);
		}
	}
	
	@Transactional
	public void moveContentDown(Long loopId, Long contentId) {
        Content content = (Content)get(Content.class, contentId);

        if (content != null) {
            Content nextContent = (Content)getSession().getNamedQuery("Content.getNextsByOrder")
                 .setParameter("loopId", loopId)
                 .setParameter("orderIndex", content.getOrderIndex())
                 .setMaxResults(1)
                 .uniqueResult();

            if (nextContent != null) {
                Integer ordIndex = content.getOrderIndex();
                Integer nextOrdIndex = nextContent.getOrderIndex();

                delete(nextContent);
                getSession().flush();

                content.setOrderIndex(nextOrdIndex);
                save(content);
                getSession().flush();

                nextContent.setOrderIndex(ordIndex);
                save(nextContent);
                getSession().flush();
            }
        }
	}

	
	@Transactional	
	public Content insertNewContentAfter(Long loopId, Integer sequenceIndex) {
		Long contentId = getContentId(loopId, sequenceIndex);
		
		if (contentId == null) {
			throw new RuntimeException(String.format("Content with loop id=%s and sequence index=%s does not exist.", loopId, sequenceIndex));
		}
		
		return insertNewContentAfter(loopId, contentId);
	}

    
	@Transactional
	public synchronized Content insertNewContentAfter(Long loopId, Long contentId) {
		Query hql = getSession().getNamedQuery("Content.updateOrderIndex");

        ContentIndexes  indexes = getContentIndexes(loopId);
		final int sequenceIndex = indexes.sequence;
        int nextHigherOrderIndex = indexes.higher;
        ContentLoop loop = getContentLoop(loopId);
		
		Content newContent = null;
		List<Content> contents = loop.getContents();
		int size = contents.size();
		
		for (int i = size - 1; i >= 0; i--) {
			Content content = contents.get(i);
			
			if (content.getId().equals(contentId)) {
				newContent = new Content();
				newContent.setLoopId(loop.getId());
				newContent.setIndex(sequenceIndex);
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
				contentId + "] not found in loop id [" + loop.getId() + "].");
		}
		
		return newContent;
	}

	@Transactional	
	public synchronized Content insertNewContentBefore(Long loopId, Integer sequenceIndex) {
		Long contentId = getContentId(loopId, sequenceIndex);
		
		if (contentId == null) {
			throw new RuntimeException(String.format("Content with loop id=%s and sequence index=%s does not exist.", loopId, sequenceIndex));
		}
		
		return insertNewContentBefore(loopId, contentId);
	}

    
	@Transactional
	public synchronized Content insertNewContentBefore(Long loopId, Long contentId) {
		Query hql = getSession().getNamedQuery("Content.updateOrderIndex");

        ContentIndexes  indexes = getContentIndexes(loopId);

		final int sequenceIndex = indexes.sequence;
        int nextLowerOrderIndex = indexes.lower;

		Content newContent = null;
        ContentLoop loop = getContentLoop(loopId);
		
		for (Content content: loop.getContents()) {
			if (content.getId().equals(contentId)) {
				newContent = new Content();
				newContent.setLoopId(loop.getId());
				newContent.setIndex(sequenceIndex);
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
	

    // TODO: Optimize
	@Transactional
	public synchronized int deleteLoops(Integer metaId, Integer documentVersion) {
		List<ContentLoop> loops = getContentLoops(metaId, documentVersion);
		
		for (ContentLoop loop: loops) {
			delete(loop);
		}

		return loops.size();
	}


	@Transactional
	public int deleteLoop(Long loopId) {
		getSession().createQuery("delete from Contents c where c.loopId = ?")
                .setParameter(0, loopId)
                .executeUpdate();

		return getSession().createQuery("delete from ContentLoop l where l.id = ?")
                .setParameter(0, loopId)
                .executeUpdate();
	}
}