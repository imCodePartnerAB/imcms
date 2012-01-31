package com.imcode.imcms.test.casual;

import com.imcode.util.FileTreeTraverser;
import junit.framework.TestCase;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.SystemUtils;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.PatternMatcherInput;

import java.io.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.Iterator;

public class TestLanguagePropertiesInUse extends TestCase {

    private Perl5Util perl5Util = new Perl5Util();
    
    public void testLanguagePropertiesInUse() throws IOException {
        File[] propertiesFiles = new File[]{new File("imcms_swe.properties"), new File("imcms_eng.properties")};

        Set propertyKeysInUse = getPropertyKeysInUse();

        checkPropertiesFiles( propertiesFiles, propertyKeysInUse );
    }

    private Set getPropertyKeysInUse() {
        StringCollectingFileFilter stringCollectingFilter = new StringCollectingFileFilter("m/<\\? (\\S+) \\?>/");
        FileTreeTraverser fileTreeTraverser = new FileTreeTraverser(stringCollectingFilter);
        fileTreeTraverser.traverseDirectory( new File( "sql" ) );
        fileTreeTraverser.traverseDirectory( new File( "web" ) );
        stringCollectingFilter.setStringPattern( "m/\"(\\S+)\"/" );
        fileTreeTraverser.traverseDirectory( new File( "server" ) );
        Set propertyKeysInUse = stringCollectingFilter.getCollectedStrings();
        return propertyKeysInUse;
    }

    private void checkPropertiesFiles( File[] propertiesFiles, Set propertyKeysInUse ) throws IOException {
        for ( int i = 0; i < propertiesFiles.length; i++ ) {
            File propertiesFile = propertiesFiles[i] ;
            checkPropertiesFile( propertiesFile, propertyKeysInUse );
        }
    }

    private void checkPropertiesFile( File propertiesFile, Set propertyKeysInUse ) throws IOException {
        Properties properties = loadPropertiesFile( propertiesFile );
        Set superfluousPropertyKeys = new HashSet( properties.keySet() );
        superfluousPropertyKeys.removeAll( propertyKeysInUse );
        if ( !superfluousPropertyKeys.isEmpty() ) {
            String listOfLineNumbersOfPropertyKeysInFile = createListOfLineNumbersOfPropertyKeysInFile( superfluousPropertyKeys, propertiesFile );
            fail( superfluousPropertyKeys.size() + " of " + properties.keySet().size() + " keys are superfluous: "+SystemUtils.LINE_SEPARATOR+listOfLineNumbersOfPropertyKeysInFile );
        }
    }

    private Properties loadPropertiesFile( File propertiesFile ) throws IOException {
        Properties properties = new Properties();
        properties.load( new BufferedInputStream( new FileInputStream( propertiesFile ) ) );
        return properties;
    }

    private String createListOfLineNumbersOfPropertyKeysInFile( Set propertyKeys, File propertiesFile ) throws IOException {
        propertyKeys = new HashSet(propertyKeys) ;
        String propertiesFileCanonicalPath = propertiesFile.getCanonicalPath();
        StringBuffer listBuffer = new StringBuffer();
        LineNumberReader reader = new LineNumberReader( new FileReader( propertiesFile ) );
        for ( String line; null != ( line = reader.readLine() ); ) {
            PatternMatcherInput lineToMatch = new PatternMatcherInput( line );
            while ( perl5Util.match( "m/^(\\S+)\\s*[:=]/", lineToMatch ) ) {
                String propertyKey = perl5Util.group( 1 );
                if ( propertyKeys.remove( propertyKey ) ) {
                    listBuffer.append( formatFileLineNumber( propertiesFileCanonicalPath, reader.getLineNumber(), propertyKey ) ) ;
                }
            }
        }
        for ( Iterator iterator = propertyKeys.iterator(); iterator.hasNext(); ) {
            String propertyKey = (String)iterator.next();
            listBuffer.append( formatFileLineNumber( propertiesFileCanonicalPath, 0, propertyKey ) ) ;

        }
        return listBuffer.toString() ;
    }

    private static String formatFileLineNumber( String canonicalFilePath, int lineNumber, String message ) {
        return canonicalFilePath + ':' + lineNumber + ": " + message + SystemUtils.LINE_SEPARATOR;
    }

    public static class StringCollectingFileFilter implements FileFilter {

        private Perl5Util perl5Util = new Perl5Util();
        private Set collectedStrings = new HashSet();

        private String stringPattern;

        public StringCollectingFileFilter(String stringPattern) {
            this.stringPattern = stringPattern ;
        }

        public boolean accept( File file ) {
            try {
                collectStringsInFile( file );
            } catch ( IOException e ) {
                throw new UnhandledException( e );
            }
            return true;
        }

        private void collectStringsInFile( File file ) throws IOException {
            if ( !file.isFile() ) {
                return;
            }
            LineNumberReader reader = new LineNumberReader( new FileReader( file ) );
            for ( String line; null != ( line = reader.readLine() ); ) {
                collectStringsInLine( line );
            }
        }

        private void collectStringsInLine( String line ) {
            PatternMatcherInput lineToMatch = new PatternMatcherInput( line );
            while ( perl5Util.match( stringPattern, lineToMatch ) ) {
                String propertyKey = perl5Util.group( 1 );
                collectedStrings.add( propertyKey );
            }
        }

        public Set getCollectedStrings() {
            return collectedStrings;
        }

        public void setStringPattern( String stringPattern ) {
            this.stringPattern = stringPattern;
        }

    }

}
