package imcode.buildtests;

import imcode.util.LineReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;

/**
 * @author kreiger
 */
public class TestForMissingLanguageProperties extends PropertiesBaseTestCase {

    private final static String START_TOKEN = "<? ";
    private final static String END_TOKEN = " ?>";
    private String[] propertyFilenames = {"imcms_swe.properties", "imcms_eng.properties"};

    public void testWeb() throws IOException {
        testDir( new File( "web" ) );
    }

    public void testSql() throws IOException {
        testDir( new File( "sql" ) );
    }

    public void testTemplates() throws IOException {
        testDir( new File( "templates" ) );
    }

    private void testDir( File dir ) throws IOException {
        MultiMap mapOfPropertyKeysToFiles = new MultiHashMap();

        Properties[] propertieses = getPropertieses( new File("."), propertyFilenames );
        recurse( dir, propertieses, mapOfPropertyKeysToFiles );

        MultiMap filesWithMissingProperties = buildMapOfFileNamesToPropertyKeys( mapOfPropertyKeysToFiles );
        assertTrue( "Missing properties: " + filesWithMissingProperties.toString(), filesWithMissingProperties.isEmpty() );
    }

    private MultiMap buildMapOfFileNamesToPropertyKeys( MultiMap missingProperties ) throws IOException {
        Set missingPropertyKeys = missingProperties.keySet() ;
        MultiMap filesWithMissingProperties = new MultiHashMap() ;
        for ( Iterator iterator = missingPropertyKeys.iterator(); iterator.hasNext(); ) {
            String propertyKey = (String)iterator.next() ;
            Collection filesWithPropertyKey = (Collection)missingProperties.get( propertyKey ) ;
            Set uniqueFilesWithPropertyKey = new HashSet( filesWithPropertyKey) ;
            for ( Iterator iterator1 = uniqueFilesWithPropertyKey.iterator(); iterator1.hasNext(); ) {
                File fileWithPropertyKey = (File)iterator1.next();
                filesWithMissingProperties.put("\n"+fileWithPropertyKey.getCanonicalPath()+":0: \n", propertyKey) ;
            }
        }
        return filesWithMissingProperties;
    }

    private void recurse( File dir, Properties[] propertieses, MultiMap missingProperties ) throws IOException {
        File[] files = dir.listFiles();
        for ( int i = 0; i < files.length; i++ ) {
            File file = files[i];
            if ( file.getName().startsWith( "." ) ) {
                continue;
            }
            if ( file.isDirectory() ) {
                recurse( file, propertieses, missingProperties );
            } else {
                parseFile( file, propertieses, missingProperties );
            }
        }
    }

    private void parseFile( File file, Properties[] propertieses, MultiMap missingProperties ) throws IOException {
        LineReader in = new LineReader( new BufferedReader( new FileReader( file ) ) );
        for ( String line; null != ( line = in.readLine() ); ) {
            for ( int startTokenIndex = 0; -1 != ( startTokenIndex = line.indexOf( START_TOKEN, startTokenIndex ) ); ) {
                int endTokenIndex = line.indexOf( END_TOKEN, startTokenIndex + START_TOKEN.length() );
                if ( -1 != endTokenIndex ) {
                    String propertyKey = line.substring( startTokenIndex + START_TOKEN.length(), endTokenIndex );
                    for ( int i = 0; i < propertieses.length; i++ ) {
                        Properties properties = propertieses[i];
                        if ( null == properties.getProperty( propertyKey ) ) {
                            missingProperties.put( propertyKey, file );
                        }
                    }
                    startTokenIndex = endTokenIndex + END_TOKEN.length();
                } else {
                    break;
                }
            }
        }
    }

}