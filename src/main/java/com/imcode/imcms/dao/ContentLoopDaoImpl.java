package com.imcode.imcms.dao;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.IteratorUtils;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.Content;
import com.imcode.imcms.api.ContentLoop;

public class ContentLoopDaoImpl extends HibernateTemplate implements ContentLoopDao {

	public final static String NEXT_SEQUENCE_INDEX = "NEXT_SEQUENCE_INDEX";
	public final static String NEXT_HIGHER_ORDER_INDEX = "NEXT_HIGHER_ORDER_INDEX";
	public final static String NEXT_LOWER_ORDER_INDEX = "NEXT_LOWER_ORDER_INDEX";
	
	public Map<String, Integer> getNextIndexes(long loopId) {
		List<Object[]> nextIndexesList = (List<Object[]>)findByNamedQueryAndNamedParam(
				"ContentLoop.getNextContentIndexes", "id", loopId); 

		Object[] nextIndexes = nextIndexesList.get(0);
		Map<String, Integer> nextIndexesMap = new HashMap<String, Integer>();
		
		nextIndexesMap.put(NEXT_SEQUENCE_INDEX, (Integer)nextIndexes[0]);
		nextIndexesMap.put(NEXT_HIGHER_ORDER_INDEX, (Integer)nextIndexes[1]);
		nextIndexesMap.put(NEXT_LOWER_ORDER_INDEX, (Integer)nextIndexes[2]);
		
		return nextIndexesMap;		
	} 

	@Transactional
	public synchronized Content addFisrtContent(ContentLoop contentLoop) {
		Map<String, Integer> nextIndexesMap = getNextIndexes(contentLoop.getId());
		
		Content content = new Content();		
		content.setSequenceIndex(nextIndexesMap.get(NEXT_SEQUENCE_INDEX));
		content.setOrderIndex(nextIndexesMap.get(NEXT_LOWER_ORDER_INDEX));		
		
		content.setLoopId(contentLoop.getId());
		
		save(content);
		
		return content;
	}

	@Transactional
	public synchronized Content addLastContent(ContentLoop contentLoop) {
		Map<String, Integer> nextIndexesMap = getNextIndexes(contentLoop.getId());
		
		Content content = new Content();
		content.setSequenceIndex(nextIndexesMap.get(NEXT_SEQUENCE_INDEX));
		content.setOrderIndex(nextIndexesMap.get(NEXT_HIGHER_ORDER_INDEX));
		content.setLoopId(contentLoop.getId());
		
		save(content);
		
		return content;
	}

	@Transactional
	public ContentLoop createContentLoop(int metaId, int loopNo, int baseIndex) {
		ContentLoop loop = new ContentLoop();
		loop.setMetaId(metaId);
		loop.setNo(loopNo);
		loop.setBaseIndex(baseIndex);
		
		save(loop);
		
		return loop;
	}

	@Transactional
	public void deleteContent(ContentLoop loop, long contentId) {
		List<Content> contents = loop.getContents();
		int size = contents.size();
		
		for (int i = 0; i < size; i++) {
			Content content = contents.get(i);
			if (content.getId() == contentId) {
				delete(content);
				break;
			}
		}				
	}

	public ContentLoop getContentLoop(int metaId, int loopNo) {
		List<ContentLoop> loops = findByNamedQueryAndNamedParam("ContentLoop.getByMetaAndNo", 
				new String[] {"metaId", "no"}, new Object[] {metaId, loopNo});
		
		return loops.isEmpty() ? null : (ContentLoop)loops.get(0);
	}

	@Transactional
	public synchronized void moveContentUp(ContentLoop loop, long contentId) {
		List<Content> contents = loop.getContents();
		int size = contents.size();
		
		for (int i = 0; i < size; i++) {
			final Content content = contents.get(i);
			if (content.getId() == contentId) {
				if (i != 0) {
					Map<String, Integer> nextIndexesMap = getNextIndexes(loop.getId());
					final Content prevContent = contents.get(i - 1);
					
					final Integer prevIndex = prevContent.getOrderIndex();
					final Integer currentIndex = content.getOrderIndex();
					final Integer tempIndex = nextIndexesMap.get(NEXT_HIGHER_ORDER_INDEX);					
					
					execute(new HibernateCallback() {

						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							String query = "UPDATE content_loops SET order_index = ? WHERE loop_id = ?";
														
							SQLQuery sql = session.createSQLQuery(query);
							
							sql.setInteger(0, tempIndex);
							sql.setLong(1, prevContent.getId());							
							int n = sql.executeUpdate();
							
							sql = session.createSQLQuery(query);
							
							sql.setInteger(0, prevIndex);
							sql.setLong(1, content.getId());							
							n = sql.executeUpdate();
							
							sql.setInteger(0, currentIndex);
							sql.setLong(1, prevContent.getId());							
							n = sql.executeUpdate();
							
							return null;
						}						
					});
				}
				
				break;
			}
		}		
	}
	
	@Transactional
	public synchronized void moveContentDown(ContentLoop loop, long contentId) {
		List<Content> contents = loop.getContents();
		int size = contents.size();
		
		for (int i = 0; i < size; i++) {
			final Content content = contents.get(i);
			if (content.getId() == contentId) {
				if (i != size - 1) {
					Map<String, Integer> nextIndexesMap = getNextIndexes(loop.getId());
					final Content nextContent = contents.get(i + 1);
					
					final Integer nextIndex = nextContent.getOrderIndex();
					final Integer currentIndex = content.getOrderIndex();
					final Integer tempIndex = nextIndexesMap.get(NEXT_HIGHER_ORDER_INDEX);					
					
					execute(new HibernateCallback() {

						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							String query = "UPDATE content_loops SET order_index = ? WHERE loop_id = ?";
														
							SQLQuery sql = session.createSQLQuery(query);
							
							sql.setInteger(0, tempIndex);
							sql.setLong(1, nextContent.getId());							
							int n = sql.executeUpdate();
							
							sql = session.createSQLQuery(query);
							
							sql.setInteger(0, nextIndex);
							sql.setLong(1, content.getId());							
							n = sql.executeUpdate();
							
							sql.setInteger(0, currentIndex);
							sql.setLong(1, nextContent.getId());							
							n = sql.executeUpdate();
							
							return null;
						}						
					});
				}
				
				break;
			}
		}			
	}

	
	@Transactional
	public synchronized Content insertNewContentAfter(final ContentLoop loop, final long contentId) {
		return (Content)execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String query = "UPDATE content_loops SET order_index = ? WHERE loop_id = ?";
				SQLQuery sql = session.createSQLQuery(query);
				
				Map<String, Integer> nextIndexesMap = getNextIndexes(loop.getId());
				int nextSequenceIndex = nextIndexesMap.get(NEXT_SEQUENCE_INDEX);
				int nextHigherOrderIndex = nextIndexesMap.get(NEXT_HIGHER_ORDER_INDEX);
				
				Content newContent = null;
				List<Content> contents = loop.getContents();
				int size = contents.size();
				
				for (int i = size - 1; i >= 0; i--) {
					Content content = contents.get(i);
					
					if (content.getId() == contentId) {
						newContent = new Content();
						newContent.setLoopId(loop.getId());
						newContent.setSequenceIndex(nextSequenceIndex);
						newContent.setOrderIndex(nextHigherOrderIndex);
						
						save(newContent);						
						break;
					}
					
				    int currentOrderIndex = content.getOrderIndex();
				    
				    sql.setInteger(0, nextHigherOrderIndex);
				    sql.setLong(1, content.getId());
				    
				    sql.executeUpdate();
				    
				    nextHigherOrderIndex = currentOrderIndex;
				}
				
				if (newContent == null) {
					throw new RuntimeException("Content with id [" + 
							contentId + "] not found for loop id [" + loop.getId() + "].");
				}
				
				return newContent;
			}						
		});
	}

	
	@Transactional
	public synchronized Content insertNewContentBefore(final ContentLoop loop, final long contentId) {
		return (Content)execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String query = "UPDATE content_loops SET order_index = ? WHERE loop_id = ?";
				SQLQuery sql = session.createSQLQuery(query);
				
				Map<String, Integer> nextIndexesMap = getNextIndexes(loop.getId());
				int nextSequenceIndex = nextIndexesMap.get(NEXT_SEQUENCE_INDEX);
				int nextLowerOrderIndex = nextIndexesMap.get(NEXT_LOWER_ORDER_INDEX);
				
				Content newContent = null;
				
				for (Content content: loop.getContents()) {
					if (content.getId() == contentId) {
						newContent = new Content();
						newContent.setLoopId(loop.getId());
						newContent.setSequenceIndex(nextSequenceIndex);
						newContent.setOrderIndex(nextLowerOrderIndex);
						
						save(newContent);						
						break;
					}
					
				    int currentOrderIndex = content.getOrderIndex();
				    
				    sql.setInteger(0, nextLowerOrderIndex);
				    sql.setLong(1, content.getId());
				    
				    sql.executeUpdate();
				    
				    nextLowerOrderIndex = currentOrderIndex;
				}
				
				if (newContent == null) {
					throw new RuntimeException("Content with id [" + 
							contentId + "] not found for loop id [" + loop.getId() + "].");
				}
				
				return newContent;
			}						
		});
	}	
}
