package imcode.server;

import com.imcode.imcms.api.SystemProperty;
import com.imcode.imcms.dao.SystemDao;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.util.List;

/**
 * 
 */
public class SystemPropertyTest {

    private SystemDao systemDao;

    @BeforeClass
    public void setUpClass() {
        Imcms.start();
        systemDao = (SystemDao)Imcms.getSpringBean("systemDao");
    }


    @AfterClass
    public void afterClass() {
        Imcms.stop();
    }


    @Test
    public void getProperties() {
        List<SystemProperty> properties = systemDao.getProperties();

        assertTrue(properties.size() > 0);
    }


    @Test
    public void getProperty() {
        SystemProperty property = systemDao.getProperty("startDocument");

        assertNotNull(property);
        assertEquals(property.getValue(), "" + 1001);
    }


    @Test
    public void savePropery() {
        fail("NOT IMPLEMENTED");
    }    
}