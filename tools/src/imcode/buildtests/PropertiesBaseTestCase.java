package imcode.buildtests;

import junit.framework.TestCase;

import java.util.Properties;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;

/**
 * @author kreiger
 */
public class PropertiesBaseTestCase extends TestCase {

    protected Properties[] getPropertieses( File dir, String[] propertiesFilenames ) throws IOException {
        Properties[] propertieses = new Properties[propertiesFilenames.length] ;
        for ( int i = 0; i < propertiesFilenames.length; i++ ) {
            String propertiesFilename = propertiesFilenames[i];
            Properties properties = new Properties() ;
            properties.load(new FileInputStream(new File(dir,propertiesFilename)));
            propertieses[i] = properties ;
        }
        return propertieses ;
    }

}
