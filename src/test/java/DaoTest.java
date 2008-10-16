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
		ContentLoop loop = clDao.createContentLoop(1001, 2, 10000);
				
		Assert.assertTrue(loop.getMetaId() == 1001);
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
				
		Content c = loop.getContents().get(2);
		int oldCount = loop.getContents().size();
		
		loop = clDao.deleteContent(loop, c.getId());
		
		Assert.assertTrue(oldCount == loop.getContents().size() + 1);
	}
	
	@Test public void addFisrtContent() {
		ContentLoop loop = clDao.getContentLoop(1001, 1);
				
		Content c = clDao.addFisrtContent(loop);
	}	
	
	@Test public void addLastContent() {
		ContentLoop loop = clDao.getContentLoop(1001, 1);
				
		Content c = clDao.addLastContent(loop);
	}	
}
