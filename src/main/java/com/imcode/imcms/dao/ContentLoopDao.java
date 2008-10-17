package com.imcode.imcms.dao;

import com.imcode.imcms.api.Content;
import com.imcode.imcms.api.ContentLoop;

public interface ContentLoopDao {

	ContentLoop getContentLoop(int metaId, int no);
	
	ContentLoop createContentLoop(int metaId, int no, int baseIndex);

	Content addFisrtContent(ContentLoop loop);
	
	Content addLastContent(ContentLoop loop);
	
	void deleteContent(ContentLoop loop, long contentId);

	void moveContentUp(ContentLoop loop, long contentId);
	
	void moveContentDown(ContentLoop loop, long contentId);
	
	Content insertNewContentBefore(ContentLoop loop, long contentId);
	
	Content insertNewContentAfter(ContentLoop loop, long contentId);	
}