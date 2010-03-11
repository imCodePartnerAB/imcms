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

    /**
     * Returns content loop.
     */
    @Transactional
    public ContentLoop getContentLoop(Long loopId) {
        return (ContentLoop)get(ContentLoop.class, loopId);
    }

	/**
	 * Returns loop or null if loop can not be found. 
	 * 
	 * @param docId document id.
	 * @param no loop no.
	 * 
	 * @return loop or null if loop can not be found. 
	 */
	@Transactional
	public ContentLoop getContentLoop(Integer docId, Integer docVersionNo, Integer no) {
		return (ContentLoop)getSession().getNamedQuery("ContentLoop.getByDocIdAndDocVersionNoAndNo")
			.setParameter("docId", docId)
			.setParameter("docVersionNo", docVersionNo)
			.setParameter("no", no)
			.uniqueResult();
	}
	
	
	/**
	 * Returns document content loops. 
	 * 
	 * @param docId document id.
	 * 
	 * @return document content loops. 
	 */
	@Transactional
	public List<ContentLoop> getContentLoops(Integer docId, Integer docVersionNo) {
		return findByNamedQueryAndNamedParam("ContentLoop.getByDocIdAndDocVersionNo", 
				new String[] {"docId", "docVersionNo"}, new Object[] {docId, docVersionNo });
	}
    

	/**
	 * Saves content loop.
     *
     * @param loop content loop.
     * @return saved content loop.
	 */
	@Transactional
	public synchronized ContentLoop saveContentLoop(final ContentLoop loop) {
        ContentLoop loopClone = loop.clone();

        saveOrUpdate(loopClone);

        return loopClone;
	}
    
	
	@Transactional
	public synchronized int deleteLoops(Integer docId, Integer documentVersion) {
		List<ContentLoop> loops = getContentLoops(docId, documentVersion);
		
		for (ContentLoop loop: loops) {
			delete(loop);
		}

		return loops.size();
	}


	@Transactional
	public void deleteLoop(Long loopId) {
	    delete(getContentLoop(loopId));
	}
}