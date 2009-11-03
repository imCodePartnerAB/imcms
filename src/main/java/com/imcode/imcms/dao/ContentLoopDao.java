package com.imcode.imcms.dao;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;

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
	 * Content indexes immutable structure.
	 */
    /*
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
	*/
	
	/**
	 * Creates and returns next context indexes.
	 * 
	 * @param loopId loop id.
	 * @return new instance of ContentIndexes. 
	 */
    /*
	private ContentIndexes getNextContentIndexes(Long loopId) {
		List<Object[]> nextIndexesList = findByNamedQueryAndNamedParam(
				"Content.getNextIndexes", "loopId", loopId); 

		Object[] indexes = nextIndexesList.get(0);
		Integer lower = indexes[0] == null ? 0 : (Integer)indexes[0];
		Integer higher = indexes[1] == null ? 0 : (Integer)indexes[1];		
		Integer sequence = indexes[2] == null ? 0 : (Integer)indexes[2];

		return new ContentIndexes(lower, higher, sequence);
	}
	*/


	/**
	 * Creates and returns next context indexes.
	 *
	 * @param metaId meta id.
	 * @return new instance of ContentIndexes.
	 */
	private com.imcode.imcms.api.ContentIndexes getContentIndexes(Long loopId) {
        return (com.imcode.imcms.api.ContentIndexes)getSession().createQuery("select c.contentIndexes from ContentLoop  c where c.id = :id")
                .setParameter("id", loopId)
                .uniqueResult();
	}

    /**
     *
     */
    @Transactional
    public ContentLoop getContentLoop(Long loopId) {
        return (ContentLoop)get(ContentLoop.class, loopId);
    }
	
	/**
	 * Creates and adds new Content at the fist position of the loop.
	 *  
	 * @param loopId loop id
	 * @return new Content added at the fist position of the loop.
	 */
	@Transactional
	public synchronized Content addFisrtContent(Long loopId) {
		//ContentIndexes indexes = getContentIndexes(loopId);
        ContentLoop loop = getContentLoop(loopId);
        ContentIndexes indexes = loop.getContentIndexes();

        Integer newSequenceIndex = indexes.setSequence(indexes.getSequence() + 1);
        Integer newOrderIndex = indexes.setLowerOrder(indexes.getLowerOrder() - 1);

        update(loop);
				
		return createContent(loopId, newSequenceIndex, newOrderIndex);
	}
	
	/**
	 * Creates and adds new Content at the fist position of the loop.
	 *  
	 * @param loop content loop
	 * @return new Content added at the fist position of the loop.
	 */
	@Transactional
	public Content addFisrtContent(ContentLoop loop) {
		return addFisrtContent(loop.getId());
	}	

	/**
	 * Creates and adds new Content at the last position of the loop.
	 *  
	 * @param loopId loop id
	 * @return new Content added at the last position of the loop.
	 */	
	@Transactional
	public synchronized Content addLastContent(Long loopId) {
        ContentLoop loop = getContentLoop(loopId);
        ContentIndexes indexes = loop.getContentIndexes();

        Integer newSequenceIndex = indexes.setSequence(indexes.getSequence() + 1);
        Integer newOrderIndex = indexes.setHigherOrder(indexes.getHigherOrder() + 1);

        update(loop);
		
		return createContent(loopId, newSequenceIndex, newOrderIndex);
	}
	
	/**
	 * Creates and adds new Content at the last position of the loop.
	 *  
	 * @param loop content loop
	 * @return new Content added at the last position of the loop.
	 */	
	@Transactional
	public Content addLastContent(ContentLoop loop) {
		return addLastContent(loop.getId());
	}
	
	
	/**
	 * Saves content loop.
	 * 
	 * If content loop already exists delete it fist along with contents. 
	 */
	// TODO: refactor
	@Transactional
	public synchronized ContentLoop saveContentLoop(Integer documentId, ContentLoop contentLoop) {	
		
		Long loopId = contentLoop.getId();
		
		// delete all contents and content
		if (loopId != null) {
			getSession().createQuery("DELETE FROM Content c WHERE c.loopId = :loopId")
				.setParameter("loopId", loopId)
				.executeUpdate();
			
			getSession().createQuery("DELETE FROM ContentLoop l WHERE l.id = :id")
				.setParameter("id", loopId)
				.executeUpdate();				
		}
		
		// Cleaun-up
		for (Content content: contentLoop.getContents()) {
			content.setId(null);
			content.setLoopId(null);
		}
		
		contentLoop.setId(null);
    	contentLoop.setMetaId(documentId);
    	
    	save(contentLoop);
    	
    	return contentLoop;
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

	/**
	 * Create content loop with single content.
	 * 
	 * @param document
	 * @param loopIndex
	 * @param baseIndex
	 * @return
	 */
	@Transactional
	public ContentLoop createContentLoop(Integer metaId, Integer metaVersion, Integer loopIndex, Integer baseIndex) {
		ContentLoop loop = new ContentLoop();
		loop.setMetaId(metaId);
		loop.setMetaVersion(metaVersion);
		loop.setIndex(loopIndex);
		loop.setBaseIndex(baseIndex);
		
		Content content = new Content();
		content.setOrderIndex(0);
		content.setSequenceIndex(0);
		
		loop.getContents().add(content);
		
		save(loop);		
		
		return loop;
	}

	@Transactional
	public void deleteContent(Long id) {
		getSession().getNamedQuery("Content.delete")
			.setParameter("id", id)
			.executeUpdate();
	}
	
	@Transactional
	public synchronized void deleteContent(ContentLoop loop, int sequenceIndex) {
		Long contentId = getContentId(loop.getId(), sequenceIndex);
		deleteContent(contentId);
	}
	
	
	@Transactional
	public void deleteContent(Content content) {
		deleteContent(content.getId());
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
	public ContentLoop getContentLoop(Integer documentId, Integer documentVersion, Integer index) {
		return (ContentLoop)getSession().getNamedQuery("ContentLoop.getByMetaIdAndMetaVersionAndIndex")
			.setParameter("metaId", documentId)
			.setParameter("metaVersion", documentVersion)
			.setParameter("index", index)
			.uniqueResult();
	}
	
	
	/**
	 * Returns document content loops. 
	 * 
	 * @param metaId document's id.
	 * 
	 * @return document content loops. 
	 */
	@Transactional
	public List<ContentLoop> getContentLoops(Integer documentId, Integer documentVersion) {
		return findByNamedQueryAndNamedParam("ContentLoop.getByMetaIdAndMetaVersion", 
				new String[] {"metaId", "metaVersion"}, new Object[] {documentId, documentVersion });
	}
	
	// TODO: Optimize
	private Long getContentId(Long loopId, Integer sequenceIndex) {
		return (Long)getSession().getNamedQuery("Content.getContentIdByLoopIdAndSequenceIndex")
			.setParameter("loopId", loopId)
			.setParameter("sequenceIndex", sequenceIndex)
			.uniqueResult();
	} 	
	
	@Transactional	
	public synchronized void moveContentUp(ContentLoop loop, Integer sequenceIndex) {
		Long contentId = getContentId(loop.getId(), sequenceIndex);
		if (contentId != null) {
			moveContentUp(loop, contentId);
		}
	}

	
	@Transactional
	public synchronized void moveContentUp(ContentLoop loop, Long contentId) {
        Content content = (Content)get(Content.class, contentId);

        if (content != null) {
            Content prevContent = (Content)getSession().createQuery(
                "select c from Content c where c.loopId = :loopId AND c.orderIndex < :orderIndex order by c.orderIndex"
                ).setParameter("loopId", loop.getId())
                 .setParameter("orderIndex", content.getOrderIndex())
                 .setMaxResults(1)
                 .uniqueResult();
                                   
            if (prevContent != null) {
                Integer ordIndex = content.getOrderIndex();
                Integer seqIndex = content.getSequenceIndex();
                Integer prevOrdIndex = prevContent.getOrderIndex();
                Integer prevSeqIndex = prevContent.getSequenceIndex();                

                prevContent.setOrderIndex(Integer.MAX_VALUE);
                prevContent.setSequenceIndex(Integer.MAX_VALUE);                
                save(prevContent);
                getSession().flush();

                content.setOrderIndex(prevOrdIndex);
                content.setSequenceIndex(prevSeqIndex);
                save(content);
                getSession().flush();

                prevContent.setOrderIndex(ordIndex);
                prevContent.setSequenceIndex(seqIndex);
                save(prevContent);
                getSession().flush();
            }
        }
	}
	
	
	@Transactional	
	public synchronized void moveContentDown(ContentLoop loop, Integer sequenceIndex) {
		Long contentId = getContentId(loop.getId(), sequenceIndex);
		if (contentId != null) {
			moveContentDown(loop, contentId);
		}
	}
	
	@Transactional
	public synchronized void moveContentDown(ContentLoop loop, Long contentId) {
        Content content = (Content)get(Content.class, contentId);

        if (content != null) {
            Content nextContent = (Content)getSession().createQuery(
                "select c from Content c where c.loopId = :loopId AND c.orderIndex > :orderIndex order by c.orderIndex"
                ).setParameter("loopId", loop.getId())
                 .setParameter("orderIndex", content.getOrderIndex())
                 .setMaxResults(1)
                 .uniqueResult();

            if (nextContent != null) {
                Integer ordIndex = content.getOrderIndex();
                Integer seqIndex = content.getSequenceIndex();
                Integer nextOrdIndex = nextContent.getOrderIndex();
                Integer nextSeqIndex = nextContent.getSequenceIndex();

                nextContent.setOrderIndex(Integer.MAX_VALUE);
                nextContent.setSequenceIndex(Integer.MAX_VALUE);
                save(nextContent);
                getSession().flush();

                content.setOrderIndex(nextOrdIndex);
                content.setSequenceIndex(nextSeqIndex);
                save(content);
                getSession().flush();

                nextContent.setOrderIndex(ordIndex);
                nextContent.setSequenceIndex(seqIndex);
                save(nextContent);
                getSession().flush();
            }
        }
	}

	
	@Transactional	
	public synchronized Content insertNewContentAfter(ContentLoop loop, Integer sequenceIndex) {
		Long contentId = getContentId(loop.getId(), sequenceIndex);
		
		if (contentId == null) {
			throw new RuntimeException(String.format("Content with loop id=%s and sequence index=%s does not exist.", loop.getId(), sequenceIndex));
		}
		
		return insertNewContentAfter(loop, contentId);
	}
	
	@Transactional
	public synchronized Content insertNewContentAfter(ContentLoop loop, Long contentId) {
        /*
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
		*/
        return null;
	}

	@Transactional	
	public synchronized Content insertNewContentBefore(ContentLoop loop, Integer sequenceIndex) {
		Long contentId = getContentId(loop.getId(), sequenceIndex);
		
		if (contentId == null) {
			throw new RuntimeException(String.format("Content with loop id=%s and sequence index=%s does not exist.", loop.getId(), sequenceIndex));
		}
		
		return insertNewContentBefore(loop, contentId);
	}
	
	@Transactional
	public synchronized Content insertNewContentBefore(ContentLoop loop, Long contentId) {
        /*
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
		*/
        return null;
	}
	
	// Very ineffective - optimize
	@Transactional
	public synchronized int deleteLoops(Integer documentId, Integer documentVersion) {
		List<ContentLoop> loops = getContentLoops(documentId, documentVersion);
		
		for (ContentLoop loop: loops) {
			delete(loop);
		}

		return loops.size();
	}


	@Transactional
	public synchronized int deleteLoop(Long loopId) {
		return getSession().createQuery("delete from ContentLoop l where l.id = :id")
                .setParameter("id", loopId)
                .executeUpdate();
	}


	@Transactional
	public synchronized int deleteAllLoops() {
		return getSession().createQuery("delete from ContentLoop l")
                .executeUpdate();
	}
}