package imcode.buildtests;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Iterator;
import java.util.Set;

/**
 * @author kreiger
 */
public class LanguagePropertyFilesInSyncTest extends TestCase {

    public void testTemplatesLanguagePropertyFiles() throws IOException {
        String dir = "templates";
        compareLanguagePropertyFiles( dir, "en", "sv" );
        compareLanguagePropertyFiles( dir, "sv", "en" );
    }

    public void testHtdocsLanguagePropertyFiles() throws IOException {
        String dir = "install/htdocs";
        compareLanguagePropertyFiles( dir, "en", "sv" );
        compareLanguagePropertyFiles( dir, "sv", "en" );
    }

    private void compareLanguagePropertyFiles( String dir, String lang1, String lang2 ) throws IOException {
        String testedFileName = "imcms_" + lang1 + ".properties";
        Properties lang1Properties = loadPropertyFile( dir, testedFileName );
        Properties lang2Properties = loadPropertyFile( dir, "imcms_" + lang2 + ".properties" );

        Properties testedProperties = new Properties() ;
        testedProperties.putAll( lang1Properties );

        removeKeysFromProperties( testedProperties, lang2Properties.keySet() );

        assertEquals( "In file " + dir + "/" + testedFileName + ": " + testedProperties.toString(), 0, testedProperties.size() );
    }

    private Properties loadPropertyFile( String dir, String fileName ) throws IOException {
        Properties swedishProperties = new Properties() ;
        swedishProperties.load( new FileInputStream(new File(dir, fileName ))) ;
        return swedishProperties;
    }

    private void removeKeysFromProperties( Properties propertiesToRemoveFrom, Set keysToRemove ) {
        for(Iterator iterator = keysToRemove.iterator(); iterator.hasNext() ;) {
            propertiesToRemoveFrom.remove(iterator.next()) ;
        }
    }

}
