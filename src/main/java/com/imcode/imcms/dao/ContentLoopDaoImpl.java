package com.imcode.imcms.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.Content;
import com.imcode.imcms.api.ContentLoop;

public class ContentLoopDaoImpl extends HibernateTemplate implements ContentLoopDao {

	public final static String nextSequenceIndex = "nextSequenceIndex";
	public final static String nextHigherOrderIndex = "nextHigherOrderIndex";
	public final static String nextLowerOrderIndex = "nextLowerOrderIndex";
	
	public Map<String, Integer> getNextIndexes(long loopId) {
		List<Object[]> nextIndexesList = (List<Object[]>)findByNamedQueryAndNamedParam(
				"ContentLoop.getNextContentIndexes", "id", loopId); 

		Object[] nextIndexes = nextIndexesList.get(0);
		Map<String, Integer> nextIndexesMap = new HashMap<String, Integer>();
		
		nextIndexesMap.put(nextSequenceIndex, (Integer)nextIndexes[0]);
		nextIndexesMap.put(nextHigherOrderIndex, (Integer)nextIndexes[1]);
		nextIndexesMap.put(nextLowerOrderIndex, (Integer)nextIndexes[2]);
		
		return nextIndexesMap;		
	} 

	@Transactional
	public synchronized Content addFisrtContent(ContentLoop contentLoop) {
		Map<String, Integer> nextIndexesMap = getNextIndexes(contentLoop.getId());
		
		Content content = new Content();
		content.setSequenceIndex(nextIndexesMap.get(nextSequenceIndex));
		content.setOrderIndex(nextIndexesMap.get(nextLowerOrderIndex));		
		
		content.setLoopId(contentLoop.getId());
		contentLoop.getContents().add(content);
		
		update(contentLoop);
		
		return content;
	}

	@Transactional
	public synchronized Content addLastContent(ContentLoop contentLoop) {
		Map<String, Integer> nextIndexesMap = getNextIndexes(contentLoop.getId());
		
		Content content = new Content();
		content.setSequenceIndex(nextIndexesMap.get(nextSequenceIndex));
		content.setOrderIndex(nextIndexesMap.get(nextHigherOrderIndex));

		content.setLoopId(contentLoop.getId());
		contentLoop.getContents().add(content);
		
		update(contentLoop);
		
		return content;
	}

	@Transactional
	public ContentLoop createContentLoop(int metaId, int loopNo, int baseIndex) {
		ContentLoop loop = new ContentLoop();
		loop.setMetaId(metaId);
		loop.setNo(loopNo);
		loop.setBaseIndex(baseIndex);
		//loop.setContents(new LinkedList<Content>());
		
		save(loop);
		
		return loop;
	}

	@Transactional
	public ContentLoop deleteContent(ContentLoop loop, long contentId) {		
		for (Content content: loop.getContents()) {
			if (content.getId() == contentId) {
				delete(content);
			}
		}
		
		return getContentLoop(loop.getMetaId(), loop.getNo());
	}

	public ContentLoop getContentLoop(int metaId, int loopNo) {
		List<ContentLoop> loops = findByNamedQueryAndNamedParam("ContentLoop.getByMetaAndNo", 
				new String[] {"metaId", "no"}, new Object[] {metaId, loopNo});
		
		return loops.isEmpty() ? null : (ContentLoop)loops.get(0);
	}
}
