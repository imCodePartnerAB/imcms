package com.imcode.imcms.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.Content;
import com.imcode.imcms.api.ContentLoop;
import com.imcode.imcms.api.ContentIndexes;

/**
 * Content loop DAO.
 */
public class ContentLoopDao extends HibernateTemplate {

    /**
     * Updates indexes in nested trnsactions.
     */
    private ContentIndexesDao contentIndexesDao;


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
	public Content addFisrtContent(Long loopId) {
        ContentLoop loop = contentIndexesDao.updateForNextLowerOrder(loopId);
		ContentIndexes  indexes = loop.getContentIndexes();
        
		return createContent(loopId, indexes.getSequence(), indexes.getLowerOrder());
	}
    
    
	/**
	 * Creates and adds new content at the last position of the loop.
	 *  
	 * @param loopId loop id
	 * @return new Content added at the last position of the loop.
	 */	
	@Transactional
	public Content addLastContent(Long loopId) {
        ContentLoop loop = contentIndexesDao.updateForNextHigherOrder(loopId);
		ContentIndexes indexes = loop.getContentIndexes();

		return createContent(loopId, indexes.getSequence(), indexes.getHigherOrder());
	}
        
	
	
	/**
	 * Saves conent loop.
     *
     * @param loop content loop.
     * @return saved content loop. 
	 */
	@Transactional
	public synchronized ContentLoop saveContentLoop(final ContentLoop loop) {
        return (ContentLoop)save(loop.clone());
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
		content.setSequenceIndex(sequenceIndex);		
		content.setOrderIndex(orderIndex);		
		
		save(content);
		
		return content;		
	}	

	/**
	 * Create content loop with single content.
	 * 
	 * @param metaId meta id.
	 * @param loopNo loop number.
	 * @param baseIndex
	 * @return
	 */
	@Transactional
	public synchronized ContentLoop createContentLoop(Integer metaId, Integer documentVersion, Integer loopNo, Integer baseIndex) {
		ContentLoop loop = new ContentLoop();
		loop.setMetaId(metaId);
		loop.setDocumentVersion(documentVersion);
		loop.setNo(loopNo);
		loop.setBaseIndex(baseIndex);
		
		Content content = new Content();
		content.setOrderIndex(0);
		content.setSequenceIndex(0);
		
		loop.getContents().add(content);
		
		save(loop);		
		
		return loop;
	}

    
	@Transactional
	public void deleteContent(Long contentId) {
		getSession().getNamedQuery("Content.delete")
			.setParameter("id", contentId)
			.executeUpdate();
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

        ContentLoop loop = contentIndexesDao.updateForNextHigherOrder(loopId);
        ContentIndexes indexes = loop.getContentIndexes();
		final int sequenceIndex = indexes.getSequence();
        int nextHigherOrderIndex = indexes.getHigherOrder();     
		
		Content newContent = null;
		List<Content> contents = loop.getContents();
		int size = contents.size();
		
		for (int i = size - 1; i >= 0; i--) {
			Content content = contents.get(i);
			
			if (content.getId().equals(contentId)) {
				newContent = new Content();
				newContent.setLoopId(loop.getId());
				newContent.setSequenceIndex(sequenceIndex);
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

        ContentLoop loop = contentIndexesDao.updateForNextLowerOrder(loopId);
        ContentIndexes indexes = loop.getContentIndexes();

		final int sequenceIndex = indexes.getSequence();
        int nextLowerOrderIndex = indexes.getLowerOrder();

		Content newContent = null;
		
		for (Content content: loop.getContents()) {
			if (content.getId().equals(contentId)) {
				newContent = new Content();
				newContent.setLoopId(loop.getId());
				newContent.setSequenceIndex(sequenceIndex);
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

    
    public ContentIndexesDao getContentIndexesDao() {
        return contentIndexesDao;
    }

    public void setContentIndexesDao(ContentIndexesDao contentIndexesDao) {
        this.contentIndexesDao = contentIndexesDao;
    }
}