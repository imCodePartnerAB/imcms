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
        SystemProperty property = getExistingProperty("startDocument");

        assertEquals(property.getValue(), "" + 1001);
    }


    @Test
    public void savePropery() {
        SystemProperty property = getExistingProperty("languageId");

        property.setValue("" + 0);

        systemDao.saveProperty(property);

        SystemProperty property2 = getExistingProperty("languageId");

        assertEquals(property2.getValue(), "" + 0);

        property2.setValue("" + 1);

        systemDao.saveProperty(property2);

        SystemProperty property3 = getExistingProperty("languageId");

        assertEquals(property3.getValue(), "" + 1);
    }


    public SystemProperty getExistingProperty(String name) {
        SystemProperty property = systemDao.getProperty(name);

        assertNotNull(property);

        return property;
    }
}