import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.imcode.imcms.api.Content;
import com.imcode.imcms.api.ContentLoop;
import com.imcode.imcms.dao.ContentLoopDao;


public class DaoTest {
	
	private static ApplicationContext context;
	
	private ContentLoopDao clDao;
	
	@BeforeClass
	public static void setUpClass() {
		context = new ClassPathXmlApplicationContext(
		        new String[] {"spring-hibernate.xml"});		
	}
	
	@Before 
	public void setUp() {
		clDao = (ContentLoopDao)context.getBean("contentLoopDao");
	}
	
	@Test public void cerateContentLoop() {
		ContentLoop loop = clDao.createContentLoop(1001, 3, 10000);
				
		Assert.assertTrue(loop.getId() > 2);
	}	
	

	@Test public void getContentLoop() {
		ContentLoop loop = clDao.getContentLoop(1001, 1);
				
		Assert.assertTrue(loop.getMetaId() == 1001);
		
		List<Content> contents = loop.getContents();
		
		for (int i = 1; i < contents.size(); i++) {
			Assert.assertTrue(contents.get(i - 1).getOrderIndex() < contents.get(i).getOrderIndex());
		}
	}

	@Test public void deleteContent() {
		ContentLoop loop = clDao.getContentLoop(1001, 1);
		int contentCount = loop.getContents().size(); 
		
		Content c = loop.getContents().get(2);
		
		clDao.deleteContent(loop, c.getId());
		
		loop = clDao.getContentLoop(1001, 1);
		int newContentCount = loop.getContents().size();
		
		Assert.assertTrue(contentCount == newContentCount + 1);		
	}
	
	@Test public void addFisrtContent() {
		ContentLoop loop = clDao.getContentLoop(1001, 1);
				
		Content c = clDao.addFisrtContent(loop);
		
		loop = clDao.getContentLoop(1001, 1);
		List<Content> contents = loop.getContents();
		
		Assert.assertEquals(c.getId(), contents.get(0).getId());
	}	
	
	@Test public void addLastContent() {
		ContentLoop loop = clDao.getContentLoop(1001, 1);
				
		Content c = clDao.addLastContent(loop);	
		loop = clDao.getContentLoop(1001, 1);
		List<Content> contents = loop.getContents();
		
		Assert.assertEquals(c.getId(), contents.get(contents.size() - 1).getId());		
	}	
	
	@Test public void moveContentUp() {
		ContentLoop loop = clDao.getContentLoop(1001, 1);		
		Content currentContent = loop.getContents().get(4);
		Content prevContent = loop.getContents().get(3);
		
		long oldCurrentContentId = currentContent.getId();
		long oldPrevContentId = prevContent.getId();		
		
		clDao.moveContentUp(loop, currentContent.getId());
		
		loop = clDao.getContentLoop(1001, 1);
		currentContent = loop.getContents().get(4);
		prevContent = loop.getContents().get(3);
		
		long newCurrentContentId = currentContent.getId();
		long newPrevContentId = prevContent.getId();		
		
		Assert.assertTrue(oldCurrentContentId == newPrevContentId);	
		Assert.assertTrue(oldPrevContentId == newCurrentContentId);
	}	
	
	@Test public void moveContentDown() {
		ContentLoop loop = clDao.getContentLoop(1001, 1);
		Content currentContent = loop.getContents().get(5);
		Content nextContent = loop.getContents().get(6);
		
		long oldCurrentContentId = currentContent.getId();
		long oldNextContentId = nextContent.getId();		
		
		clDao.moveContentDown(loop, currentContent.getId());
		
		loop = clDao.getContentLoop(1001, 1);
		currentContent = loop.getContents().get(5);
		nextContent = loop.getContents().get(6);
		
		long newCurrentContentId = currentContent.getId();
		long newNextContentId = nextContent.getId();		
		
		Assert.assertTrue(oldCurrentContentId == newNextContentId);	
		Assert.assertTrue(oldNextContentId == newCurrentContentId);		
	}	
	
	
	@Test public void insertNextContentBefore() {
		ContentLoop loop = clDao.getContentLoop(1001, 1);		
		Content content = loop.getContents().get(5);		
		long contentId = content.getId();
		
		clDao.insertNewContentBefore(loop, contentId);
	}
	
	@Test public void insertNextContentAfter() {
		ContentLoop loop = clDao.getContentLoop(1001, 1);		
		Content content = loop.getContents().get(5);		
		long contentId = content.getId();
		
		clDao.insertNewContentAfter(loop, contentId);
	}	
}
