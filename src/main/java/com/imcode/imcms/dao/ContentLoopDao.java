package com.imcode.imcms.dao;

import java.util.List;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.ContentLoop;

/**
 * Content loop DAO.
 */
public class ContentLoopDao extends HibernateTemplate {

    /**
     * Returns content loop.
     */
    @Transactional
    public ContentLoop getLoop(Long loopId) {
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
	public ContentLoop getLoop(Integer docId, Integer docVersionNo, Integer no) {
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
	public List<ContentLoop> getLoops(Integer docId, Integer docVersionNo) {
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
	public synchronized ContentLoop saveLoop(final ContentLoop loop) {
        ContentLoop loopClone = loop.clone();

        saveOrUpdate(loopClone);
        flush();

        return loopClone;
	}
    
	
	@Transactional
	public synchronized int deleteLoops(Integer docId, Integer documentVersion) {
		List<ContentLoop> loops = getLoops(docId, documentVersion);
		
		for (ContentLoop loop: loops) {
			delete(loop);
		}

		return loops.size();
	}


	@Transactional
	public void deleteLoop(Long loopId) {
        ContentLoop loop = getLoop(loopId);

        if (loop != null) {
	        delete(loop);
        }
	}
}