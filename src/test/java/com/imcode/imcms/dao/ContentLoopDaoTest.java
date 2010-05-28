package com.imcode.imcms.dao;

import com.imcode.imcms.Script;
import com.imcode.imcms.api.Content;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.*;
import static org.junit.Assert.*;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.imcode.imcms.api.ContentLoop;

import java.sql.SQLException;
import java.util.List;

public class ContentLoopDaoTest  {


    // loops predefined in src/test/resources/dbunit-content_loop.xml: loop_<contents-count>_[sort-order]_id
    final int loop_0_id = 0;

    final int loop_1_id = 1;

    final int loop_3_asc_id = 2;

    final int loop_3_desc_id = 3;


    static ContentLoopDao contentLoopDao;



    @BeforeClass
    public static void recreateDB() {
        Script.recreateDB();
    }


    @Before
    public void resetDBData() {
        SessionFactory sf = Script.createHibernateSessionFactory(
                new Class[] {ContentLoop.class},
                "src/main/resources/ContentLoop.hbm.xml");

        Script.runDBScripts("content_loop_dao.sql");
        
        contentLoopDao = new ContentLoopDao();
        contentLoopDao.setSessionFactory(sf);
    }


    @Test 
    public void getLoops() {
        List<ContentLoop> loops = contentLoopDao.getLoops(1001, 0);

        assertEquals("Loops count in the doc.", loops.size(), 4);
    }

    
	@Test
	public void getLoop() {
		ContentLoop[] loops = {
            getLoop(loop_0_id, true),
            getLoop(loop_1_id, true),
            getLoop(loop_3_asc_id, true),
            getLoop(loop_3_desc_id, true)};

        assertEquals("Contents count.", loops[0].getContents().size(), 0);
        assertEquals("Contents count.", loops[1].getContents().size(), 1);
        assertEquals("Contents count.", loops[2].getContents().size(), 3);
        assertEquals("Contents count.", loops[3].getContents().size(), 3);
	}

    
	@Test
	public void loopContensOrder() {
		List<Content> ascSortedContens = getLoop(loop_3_asc_id, true).getContents();
        List<Content> descSortedContens = getLoop(loop_3_desc_id, true).getContents();

        for (int i = 0; i <= 2 ; i++) {
            assertEquals("Content order no.", new Integer(i), ascSortedContens.get(i).getNo());
        }


        for (int i = 0; i <= 2; i++) {
            assertEquals("Content order no.", new Integer(2 - i), descSortedContens.get(i).getNo());
        }        
	}

    @Test
    public void createEmptyLoop() {
        ContentLoop loop = new ContentLoop();
        loop.setDocId(1001);
        loop.setDocVersionNo(0);
        loop.setNo(getNextLoopNo());

        contentLoopDao.saveLoop(loop);
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

	@Test
	public void deleteLoop() {
		ContentLoop loop = getLoop(0, true);

        assertNotNull("Loop exists", loop);
        
		Long loopId = loop.getId();

		contentLoopDao.deleteLoop(loopId);
        
		assertNull(contentLoopDao.getLoop(loopId));
	}

	@Test
	public void createNonEmptyLoop() {
        ContentLoop loop = new ContentLoop();
        loop.setDocId(1001);
        loop.setDocVersionNo(0);
        loop.setNo(getNextLoopNo());

        int contentsCount = 5;

        for (int i = 0; i < contentsCount; i++) {
            loop.addFirstContent();
        }

        loop = contentLoopDao.saveLoop(loop);

        ContentLoop savedLoop = contentLoopDao.getLoop(loop.getId());

        assertNotNull("Loop exists", savedLoop);

        List<Content> contents = loop.getContents();
        List<Content> savedContents = savedLoop.getContents();

        assertEquals("Content count matches", contentsCount, savedContents.size());

        for (int i = 0; i < contentsCount; i++) {
            Content content = contents.get(i);
            Content savedContent = savedContents.get(i);

            assertEquals("Contents no-s mathces.", content.getNo(), savedContent.getNo());
        }

        

	}

	private ContentLoop getLoop(int no) {
		return getLoop(no, false);
	}

	private ContentLoop getLoop(int no, boolean assertNotNull) {
        ContentLoop loop = contentLoopDao.getLoop(1001, 0, no);

        if (assertNotNull) {
            assertNotNull(String.format("Loop exists - docId: %s, docVersionNo: %s, no: %s.", 1001, 0, no), loop);
        }

        return loop;
	}


    public Integer getNextLoopNo() {
        Integer loopNo = (Integer)contentLoopDao.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery(
                        "select max(l.no) from ContentLoop l where l.docId = 1001 and l.docVersionNo = 0").uniqueResult();
            }
        });

        return loopNo == null ? 0 : loopNo + 1;
    }
}
