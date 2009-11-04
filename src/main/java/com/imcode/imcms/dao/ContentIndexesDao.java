package com.imcode.imcms.dao;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;

import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import com.imcode.imcms.api.Content;
import com.imcode.imcms.api.ContentLoop;
import com.imcode.imcms.api.ContentIndexes;

/**
 * Content indexes dao.
 *
 * Intended to safe indexes update.
 */
public class ContentIndexesDao extends HibernateTemplate {


	/**
	 * Returns loop context indexes.
	 *
	 * @param loopId loop id.
	 * @return loop content indexes.
	 */
    @Transactional(propagation=Propagation.REQUIRES_NEW)
	public ContentIndexes getIndexes(Long loopId) {
        return (ContentIndexes)getSession().createQuery("select c.contentIndexes from ContentLoop  c where c.id = :id")
                .setParameter("id", loopId)
                .uniqueResult();
	}

    
    /**
     * Increments content loop's content sequence and higher order indexes.
     *
     * @return updated content loop.
     */
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public synchronized ContentLoop incSequenceIncHigherOrder(Long loopId) {
        ContentLoop loop = (ContentLoop)get(ContentLoop.class, loopId);
        ContentIndexes indexes = loop.getContentIndexes();

        indexes.setSequence(indexes.getSequence() + 1);
        indexes.setHigherOrder(indexes.getHigherOrder() + 1);


        save(loop);

        return loop;
    }


    /**
     * Increments content loop's content sequence and decrements lower order indexes.
     *
     * @return updated content loop.
     */
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public synchronized ContentLoop incSequenceDecLowerOrder(Long loopId) {
        ContentLoop loop = (ContentLoop)get(ContentLoop.class, loopId);
        ContentIndexes indexes = loop.getContentIndexes();

        indexes.setSequence(indexes.getSequence() + 1);
        indexes.setLowerOrder(indexes.getLowerOrder() - 1);


        save(loop);

        return loop;
    }
}