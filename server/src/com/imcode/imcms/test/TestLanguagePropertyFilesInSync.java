package com.imcode.imcms.test;

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
public class TestLanguagePropertyFilesInSync extends TestCase {

    public void testLanguagePropertyFiles() throws IOException {
        String dir = "web/WEB-INF/conf";
        compareLanguagePropertyFiles( dir, "eng", "swe" );
        compareLanguagePropertyFiles( dir, "swe", "eng" );
    }

    private void compareLanguagePropertyFiles( String dir, String lang1, String lang2 ) throws IOException {
        String testedFileName = "imcms_" + lang1 + ".properties";
        Properties lang1Properties = loadPropertyFile( dir, testedFileName );
        Properties lang2Properties = loadPropertyFile( dir, "imcms_" + lang2 + ".properties" );

        Properties testedProperties = new Properties() ;
        testedProperties.putAll( lang1Properties );

        removeKeysFromProperties( testedProperties, lang2Properties.keySet() );
        assertTrue("Extra keys in file " + dir + '/' + testedFileName + ": " + testedProperties.keySet().toString(),testedProperties.isEmpty()) ;
    }

    private Properties loadPropertyFile( String dir, String fileName ) throws IOException {
        Properties properties = new Properties() ;
        properties.load( new FileInputStream(new File(dir, fileName ))) ;
        return properties;
    }

    private void removeKeysFromProperties( Properties propertiesToRemoveFrom, Set keysToRemove ) {
        for(Iterator iterator = keysToRemove.iterator(); iterator.hasNext() ;) {
            propertiesToRemoveFrom.remove(iterator.next()) ;
        }
    }

}
