package com.imcode.imcms.dao;

import static com.imcode.imcms.dao.Utils.contentLoopDao;

import com.imcode.imcms.api.Content;
import imcode.server.Imcms;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import com.imcode.imcms.api.ContentLoop;

import java.util.List;

public class ContentLoopDaoTest extends DaoTest {

    ContentLoopDao loopDao;

    @BeforeClass
    public void setUpClass() {
        loopDao = Utils.contentLoopDao; //(ContentLoopDao)Imcms.getSpringBean("contentLoopDao");
    }

    @Test
    public void createEmptyLoop() {
        ContentLoop loop = new ContentLoop();
        loop.setDocId(1001);
        loop.setDocVersionNo(0);
        loop.setNo(1);

        loopDao.saveLoop(loop);
    }
    

	@Test
	public void getLoop() {
		ContentLoop loop = getPredefinedLoop();

		assertNotNull(loop, "Loop exists");
	}

	@Test
	public void updateLoop() {
		ContentLoop loop = getPredefinedLoop();
		Long loopId = loop.getId();

		int count = loop.getContents().size();

        loop.addLastContent();
		ContentLoop newLoop = contentLoopDao.saveLoop(loop);
		assertEquals(count + 1, newLoop.getContents().size());
		
		assertNotNull(contentLoopDao.getLoop(newLoop.getId()));
	}

	@Test(dependsOnMethods = "createEmptyLoop")
	public void deleteLoop() {
		ContentLoop loop = loopDao.getLoop(1001, 0, 0);

        assertNotNull(loop, "Loop exists");
        
		Long loopId = loop.getId();

		loopDao.deleteLoop(loopId);
        
		assertNull(contentLoopDao.getLoop(loopId));
	}

	@Test
	public void createNonEmptyLoop() {
        ContentLoop loop = new ContentLoop();
        loop.setDocId(1001);
        loop.setDocVersionNo(0);
        loop.setNo(3);

        for (int i = 0; i < 5; i++) {
            loop.addFirstContent();
        }

        loop = loopDao.saveLoop(loop);

        ContentLoop savedLoop = contentLoopDao.getLoop(loop.getId());

        assertNotNull(savedLoop, "Loop exists");

        List<Content> contents = loop.getContents();
        List<Content> savedContents = savedLoop.getContents();

        assertEquals(contents.size(), savedContents.size(), "Content count matches");

        for (int i = 0; i < 5; i++) {
            Content content = contents.get(i);
            Content savedContent = savedContents.get(i);

            assertEquals(content.getNo(), savedContent.getNo(), "Contents no-s mathces.");
        }

        

	}

	@Override
	protected String getDataSetFileName() {
		return "dbunit-content_loop.xml";
	}

	private ContentLoop getPredefinedLoop() {
		return contentLoopDao.getLoop(1001, 0, 0);
	}
}
