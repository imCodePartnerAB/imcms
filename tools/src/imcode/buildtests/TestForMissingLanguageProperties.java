package imcode.buildtests;

import junit.framework.TestCase;

import java.io.*;
import java.util.Properties;
import java.util.HashSet;
import java.util.Set;

/**
 * @author kreiger
 */
public class TestForMissingLanguageProperties extends TestCase {

    private final static String START_TOKEN = "<? ";
    private final static String END_TOKEN = " ?>";
    private String[] propertyFilenames = { "imcms_sv.properties", "imcms_en.properties" } ;

    public void testHtdocs() throws IOException {
        File dir = new File( "install/htdocs" );
        Set missingProperties = new HashSet() ;

        Properties[] propertieses = getPropertieses( dir, propertyFilenames );
        recurse( dir, propertieses, missingProperties );

        assertTrue( "Missing properties: " + missingProperties.toString(), missingProperties.isEmpty() ) ;
    }

    public void testSql() throws IOException {
        File dir = new File( "sql" );
        Set missingProperties = new HashSet() ;

        Properties[] propertieses = getPropertieses( dir, propertyFilenames );
        recurse( dir, propertieses, missingProperties  );

        assertTrue( "Missing properties: " + missingProperties.toString(), missingProperties.isEmpty() ) ;
    }

    public void testTemplates() throws IOException {
        File dir = new File( "templates" );
        Set missingProperties = new HashSet() ;

        Properties[] propertieses = getPropertieses( dir, propertyFilenames );
        recurse( dir, propertieses, missingProperties  );

        assertTrue( "Missing properties: " + missingProperties.toString(), missingProperties.isEmpty() ) ;
    }

    private Properties[] getPropertieses( File dir, String[] propertiesFilenames ) throws IOException {
        Properties[] propertieses = new Properties[propertiesFilenames.length] ;
        for ( int i = 0; i < propertiesFilenames.length; i++ ) {
            String propertiesFilename = propertiesFilenames[i];
            Properties properties = new Properties() ;
            properties.load(new FileInputStream(new File(dir,propertiesFilename)));
            propertieses[i] = properties ;
        }
        return propertieses ;
    }

    private void recurse( File dir, Properties[] propertieses, Set missingProperties  ) throws IOException {
        File[] files = dir.listFiles();
        for ( int i = 0; i < files.length; i++ ) {
            File file = files[i];
            if (file.getName().startsWith( "." )) {
                continue ;
            }
            if ( file.isDirectory() ) {
                recurse( file, propertieses, missingProperties );
            } else {
                parseFile( file, propertieses, missingProperties );
            }
        }
    }

    private void parseFile( File file, Properties[] propertieses, Set missingProperties ) throws IOException {
        BufferedReader in = new BufferedReader( new FileReader( file ) );
        for ( String line; null != ( line = in.readLine() ); ) {
            for ( int startTokenIndex = 0; -1 != ( startTokenIndex = line.indexOf( START_TOKEN, startTokenIndex ) ); ) {
                int endTokenIndex = line.indexOf( END_TOKEN, startTokenIndex + START_TOKEN.length() );
                if ( -1 != endTokenIndex ) {
                    String propertyKey = line.substring( startTokenIndex + START_TOKEN.length(), endTokenIndex );
                    for ( int i = 0; i < propertieses.length; i++ ) {
                        Properties properties = propertieses[i];
                        if (null == properties.getProperty( propertyKey ))  {
                            missingProperties.add(propertyKey) ;
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