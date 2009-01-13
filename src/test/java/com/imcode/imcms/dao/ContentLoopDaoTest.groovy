package com.imcode.imcms.dao;
import java.util.List;

import org.dbunit.dataset.xml.FlatXmlDataSet
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import org.testng.annotations.BeforeTest
import static org.testng.Assert.*
import org.testng.Assert
import imcode.server.document.textdocument.ImagesPathRelativePathImageSource

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.imcode.imcms.api.Content;
import com.imcode.imcms.api.ContentLoop;
import com.imcode.imcms.dao.ContentLoopDao;


public class ContentLoopDaoTest extends DaoTest {
    
    private ContentLoopDao clDao;
    
    @BeforeClass void init() {
    	clDao = Context.getBean("contentLoopDao")
    }       
        
    @Override
    def getDataSetFileName() {
        "dbunit-content_loop-data.xml"
    }
    
    @Test public void cerateContentLoop() {
        ContentLoop loop = clDao.createContentLoop(1001L, 2, 10000);
                
        Assert.assertTrue(loop.getId() != null);
    }   
    
    @Test public void getContentLoops() {
        def loops = clDao.getContentLoops(1001L);
                        
        Assert.assertTrue(loops.size() > 0);
    }
    
    @Test public void getContentLoop() {
        ContentLoop missingLoop = clDao.getContentLoop(-1L, -11);
        
        Assert.assertNull(missingLoop);
        
        ContentLoop loop = clDao.getContentLoop(1001L, 1);
                
        Assert.assertTrue(loop.getMetaId() == 1001);
        
        List<Content> contents = loop.getContents();
        
        for (int i = 1; i < contents.size(); i++) {
            Assert.assertTrue(contents.get(i - 1).getOrderIndex() < contents.get(i).getOrderIndex());
        }
    }

    @Test public void deleteContent() {
        ContentLoop loop = clDao.getContentLoop(1001L, 1);
        int contentCount = loop.getContents().size(); 
        
        Content c = loop.getContents().get(contentCount - 1);
        
        clDao.deleteContent(c.getId());
        
        loop = clDao.getContentLoop(1001L, 1);
        int newContentCount = loop.getContents().size();
        
        Assert.assertTrue(contentCount == newContentCount + 1);     
    }
    
    @Test public void addFisrtContent() {
        ContentLoop loop = clDao.getContentLoop(1001L, 1);
                
        Content c = clDao.addFisrtContent(loop.id);
        
        loop = clDao.getContentLoop(1001L, 1);
        List<Content> contents = loop.getContents();
        
        Assert.assertEquals(c.id, contents.get(0).id);
    }   
    
    @Test public void addLastContent() {
    	ContentLoop loop = clDao.getContentLoop(1001L, 1);
    	
        Content c = clDao.addLastContent(loop.id); 
        loop = clDao.getContentLoop(1001L, 1);
        List<Content> contents = loop.getContents();
        
        Assert.assertEquals(c.id, contents.get(contents.size() - 1).id);      
    }   
    
    @Test public void moveContentUp() {
        ContentLoop loop = clDao.getContentLoop(1001L, 1);      
        Content currentContent = loop.getContents().get(4);
        Content prevContent = loop.getContents().get(3);
        
        long oldCurrentContentId = currentContent.getId();
        long oldPrevContentId = prevContent.getId();        
        
        clDao.moveContentUp(loop, currentContent.getId());
        
        loop = clDao.getContentLoop(1001L, 1);
        currentContent = loop.getContents().get(4);
        prevContent = loop.getContents().get(3);
        
        long newCurrentContentId = currentContent.getId();
        long newPrevContentId = prevContent.getId();        
        
        Assert.assertTrue(oldCurrentContentId == newPrevContentId); 
        Assert.assertTrue(oldPrevContentId == newCurrentContentId);
    }   
    
    @Test public void moveContentDown() {
        ContentLoop loop = clDao.getContentLoop(1001L, 1);
        Content currentContent = loop.getContents().get(5);
        Content nextContent = loop.getContents().get(6);
        
        long oldCurrentContentId = currentContent.getId();
        long oldNextContentId = nextContent.getId();        
        
        clDao.moveContentDown(loop, currentContent.getId());
        
        loop = clDao.getContentLoop(1001L, 1);
        currentContent = loop.getContents().get(5);
        nextContent = loop.getContents().get(6);
        
        long newCurrentContentId = currentContent.getId();
        long newNextContentId = nextContent.getId();        
        
        Assert.assertTrue(oldCurrentContentId == newNextContentId); 
        Assert.assertTrue(oldNextContentId == newCurrentContentId);     
    }   
    
    
    @Test public void insertNextContentBefore() {
        ContentLoop loop = clDao.getContentLoop(1001L, 1);      
        Content content = loop.getContents().get(5);        
        long contentId = content.getId();
        
        clDao.insertNewContentBefore(loop, contentId);
    }
    
    @Test public void insertNextContentAfter() {
        ContentLoop loop = clDao.getContentLoop(1001L, 1);      
        Content content = loop.getContents().get(5);        
        long contentId = content.getId();
        
        clDao.insertNewContentAfter(loop, contentId);
    }   
}