package com.imcode.imcms.dao;

import com.imcode.imcms.api.Content;
import com.imcode.imcms.api.ContentLoop;

public interface ContentLoopDao {

	ContentLoop getContentLoop(int metaId, int no);
	
	ContentLoop createContentLoop(int metaId, int no, int baseIndex);

	Content addFisrtContent(ContentLoop loop);
	
	Content addLastContent(ContentLoop loop);
	
	ContentLoop deleteContent(ContentLoop loop, long contentId);

	
	/*
	void deleteContent(Content content);
	
	Content moveContentUp(Content content);
	
	Content moveContentDown(Content content);
	*/
}
