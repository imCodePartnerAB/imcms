package com.imcode.imcms.dao;

import static com.imcode.imcms.dao.Utils.contentLoopDao;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.testng.annotations.Test;

import com.imcode.imcms.api.ContentLoop;

public class ContentLoopDaoTest extends DaoTestG {

	@Test
	public void getExistingLoop() {
		ContentLoop loop = getPredefinedLoop();

		assertNotNull("Existing content loop", loop);
	}

	@Test
	public void updateLoop() {
		ContentLoop loop = getPredefinedLoop();
		Long loopId = loop.getId();

		int count = loop.getContents().size();
		loop.getContents().remove(0);
		ContentLoop newLoop = contentLoopDao.saveContentLoop(loop);
		assertTrue(count == newLoop.getContents().size() + 1);
		
		// Assert previos loop was removed
		assertFalse(loopId.equals(newLoop.getId()));
		assertNotNull(contentLoopDao.get(ContentLoop.class, newLoop.getId()));
		assertNull(contentLoopDao.get(ContentLoop.class, loopId));
	}

	@Test
	public void createLoop() {
		ContentLoop loop = getPredefinedLoop();
	}

	@Override
	protected String getDataSetFileName() {
		return "dbunit-content_loop-data.xml";
	}

	private ContentLoop getPredefinedLoop() {
		return contentLoopDao.getContentLoop(1001, 1, 1);
	}
}
