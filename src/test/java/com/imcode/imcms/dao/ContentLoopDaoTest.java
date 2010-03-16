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


    // loops predefined in src/test/resources/dbunit-content_loop.xml: loop_<contents-count>_[sort-order]_id
    final int loop_0_id = 0;

    final int loop_1_id = 1;

    final int loop_3_asc_id = 2;

    final int loop_3_desc_id = 3;


    ContentLoopDao loopDao;

    @BeforeClass
    public void setUpClass() {
        loopDao = Utils.contentLoopDao; //(ContentLoopDao)Imcms.getSpringBean("contentLoopDao");
    }


    @Test 
    public void getLoops() {
        List<ContentLoop> loops = loopDao.getLoops(1001, 0);

        assertEquals(loops.size(), 4, "Loops count in the doc.");
    }

    
	@Test
	public void getLoop() {
		ContentLoop[] loops = {
            getLoop(loop_0_id, true),
            getLoop(loop_1_id, true),
            getLoop(loop_3_asc_id, true),
            getLoop(loop_3_desc_id, true)};

        assertEquals(loops[0].getContents().size(), 0, "Contents count.");
        assertEquals(loops[1].getContents().size(), 1, "Contents count.");
        assertEquals(loops[2].getContents().size(), 3, "Contents count.");
        assertEquals(loops[3].getContents().size(), 3, "Contents count.");
	}

    
	@Test
	public void loopContensOrder() {
		List<Content> ascSortedContens = getLoop(loop_3_asc_id, true).getContents();
        List<Content> descSortedContens = getLoop(loop_3_desc_id, true).getContents();

        for (int i = 0; i <= 2 ; i++) {
            assertEquals(new Integer(i), ascSortedContens.get(i).getNo(), "Content order no.");
        }


        for (int i = 0; i <= 2; i++) {
            assertEquals(new Integer(2 - i), descSortedContens.get(i).getNo(), "Content order no.");
        }        
	}

    @Test
    public void createEmptyLoop() {
        ContentLoop loop = new ContentLoop();
        loop.setDocId(1001);
        loop.setDocVersionNo(0);
        loop.setNo(4);

        loopDao.saveLoop(loop);
    }
    



	@Test
	public void updateLoop() {
		ContentLoop loop = getLoop(0, true);
		Long loopId = loop.getId();

		int count = loop.getContents().size();

        loop.addLastContent();
		ContentLoop newLoop = contentLoopDao.saveLoop(loop);
		assertEquals(count + 1, newLoop.getContents().size());
		
		assertNotNull(contentLoopDao.getLoop(newLoop.getId()));
	}

	@Test(dependsOnMethods = "createEmptyLoop")
	public void deleteLoop() {
		ContentLoop loop = getLoop(0, true);

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
        loop.setNo(4);

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

	private ContentLoop getLoop(int no) {
		return getLoop(no, false);
	}

	private ContentLoop getLoop(int no, boolean assertNotNull) {
        ContentLoop loop = contentLoopDao.getLoop(1001, 0, no);

        if (assertNotNull) {
            assertNotNull(loop, String.format("Loop exists - docId: %s, docVersionNo: %s, no: %s.", 1001, 0, no));
        }

        return loop;
	}
}
