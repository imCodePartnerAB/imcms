package imcode.properties;

import imcode.util.FileFinder;
import imcode.util.FileStringReplacer;
import imcode.util.LineReader;
import imcode.util.MultiTreeMap;
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.*;

/**
 * @author kreiger
 */
public class PropertyInsert {

    public static void main( String[] args ) throws IOException {
        PropertyInsert theInstance = new PropertyInsert();
        theInstance.work( args );
    }

    private void work( String[] args ) throws IOException {
        Properties[] propertieses = loadPropertieses( args );
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( System.in ) );

        MultiHashMap allProperties = new MultiHashMap();
        MultiTreeMap allPropertyKeysInValueLengthOrder = new MultiTreeMap( Collections.reverseOrder() );
        populatePropertiesMaps( propertieses, allProperties, allPropertyKeysInValueLengthOrder );

        Iterator propertyKeysIterator = null;
        if ( false ) {
            propertyKeysIterator = allPropertyKeysInValueLengthOrder.values().iterator();
        } else {
            propertyKeysIterator = new AskForStringIterator("Next key to handle?", bufferedReader);
        }

        propertyKeysLoop: while ( propertyKeysIterator.hasNext() ) {
            String propertyKey = (String)propertyKeysIterator.next();

            if ("".equals( propertyKey )) {
                System.exit(0) ;
            }

            boolean removePropertyKey = false ;
            boolean keyHasAlreadyBeenHandledAndRemoved = !allProperties.containsKey( propertyKey );
            if ( keyHasAlreadyBeenHandledAndRemoved ) {
                continue;
            }

            Properties[] propertiesesToSet = new Properties[propertieses.length];
            for ( int i = 0; i < propertiesesToSet.length; i++ ) {
                propertiesesToSet[i] = new Properties();
            }
            handleKey: {
                outputPropertiesForKey( propertyKey, allProperties );
                Collection propertyValues = (Collection)allProperties.get( propertyKey );
                Iterator iterator = propertyValues.iterator();
                String previousPropertyValue = null;
                boolean valuesIdentical = true;
                int replacementStringsWanted = Integer.MAX_VALUE;
                for ( int propertyValueIndex = 0; iterator.hasNext(); propertyValueIndex++ ) {
                    String propertyValue = (String)iterator.next();

                    for ( int replacementStringIndex = 0; replacementStringIndex < replacementStringsWanted; ) {
                        int replacementStringsCount = ( replacementStringIndex + 1 );
                        askForReplacement: {
                            String replacement = askForReplacement( replacementStringsCount, propertyValueIndex, propertyKey,
                                                                    bufferedReader );
                            if ( null == replacement || "".equals( replacement ) ) {
                                if ( Integer.MAX_VALUE == replacementStringsWanted ) {
                                    replacementStringsWanted = replacementStringIndex;
                                    break;
                                } else {
                                    System.out.println( "You need to input as many strings (" + replacementStringsWanted
                                                        + ") as you did for value " + propertyValueIndex + "!" );
                                    break askForReplacement;
                                }
                            }
                            String replacementPropertyKey = propertyKey + "/" + replacementStringsCount;
                            String tag = "<? " + replacementPropertyKey + " ?>";
                            String replacementRegex = "\\Q" + replacement.replaceAll( "\\s+", "\\\\E\\\\s+\\\\Q" ) + "\\E";
                            String replacedPropertyValue = propertyValue.replaceAll( replacementRegex, tag );
                            if ( replacedPropertyValue.equals( propertyValue ) ) {
                                System.out.println( "The string " + replacement + " was not found in value " + ( propertyValueIndex
                                                                                                                 + 1 ) );
                                break askForReplacement;
                            }
                            propertyValue = replacedPropertyValue;
                            propertiesesToSet[propertyValueIndex].setProperty( replacementPropertyKey, replacement );
                            ++replacementStringIndex;
                        }
                    }
                    if ( null != previousPropertyValue && !previousPropertyValue.equals( propertyValue ) ) {
                        valuesIdentical = false;
                    }

                    previousPropertyValue = propertyValue;
                }
                if ( !valuesIdentical ) {
                    String line = null ;
                    do {
                        System.out.println( "The resulting values are not identical! Put back last one anyway?" );
                        line = bufferedReader.readLine();
                        if ( null == line ) {
                            System.exit( 0 );
                        } else if ( line.toLowerCase().startsWith( "n" ) ) {
                            continue propertyKeysLoop;
                        }
                    } while (!line.toLowerCase().startsWith("y")) ;
                }
                FileStringReplacer fileStringReplacer = new FileStringReplacer( "<? " + propertyKey + " ?>",
                                                                                previousPropertyValue );
                FileFinder fileFinder = new FileFinder();
                fileFinder.find( fileStringReplacer, new File( "." ) );
                removePropertyKey = true ;
            }
            for ( int i = 0; i < propertieses.length; i++ ) {
                Properties properties = propertieses[i];
                Properties propertiesToSet = propertiesesToSet[i];
                properties.putAll( propertiesToSet );
                if (removePropertyKey) {
                    properties.remove( propertyKey );
                    allProperties.remove( propertyKey );
                }
                String filename = args[i];
                saveProperties( properties, filename );
            }
        }
    }

    private void saveProperties( Properties properties, String filename ) throws IOException {
        OutputStream out = new BufferedOutputStream( new FileOutputStream( new File( filename ) ) );
        properties.store( out, null );
        out.flush();
        out.close();
    }

    private String askForReplacement( int replacementStringsCount, int propertyValueIndex, String propertyKey,
                                      BufferedReader bufferedReader ) throws IOException {
        System.out.println( "String " + replacementStringsCount + " to replace in value " + ( propertyValueIndex + 1 )
                            + " for key " + propertyKey + "?" );
        String line = bufferedReader.readLine();
        return line;
    }

    private void outputPropertiesForKey( String propertyKey, MultiHashMap allProperties ) throws IOException {
        System.out.println( "Key: " + propertyKey );
        System.out.println( "<<<<<<<" );
        Collection propertyValues = (Collection)allProperties.get( propertyKey );
        boolean propertyValuesIdentical = propertyValues.size() > 1
                                          ? true
                                          : false;
        String previousPropertyValue = null;
        for ( Iterator iterator = propertyValues.iterator(); iterator.hasNext(); ) {
            String propertyValue = (String)iterator.next();
            if ( propertyValuesIdentical && null != previousPropertyValue
                    && !previousPropertyValue.equals( propertyValue ) ) {
                propertyValuesIdentical = false;
            }
            LineReader lineReader = new LineReader( new StringReader( propertyValue ) );
            for ( String line; null != ( line = lineReader.readLine() ); ) {
                String lineNumberString = StringUtils.leftPad( "" + lineReader.getLinesRead(), 4 );
                System.out.print( lineNumberString + ": " + line );
            }
            System.out.println();
            if ( iterator.hasNext() ) {
                System.out.println( "=======" );
            }
            previousPropertyValue = propertyValue;
        }
        System.out.println( ">>>>>>>" );
        if ( propertyValuesIdentical ) {
            System.out.println( "The values are identical." );
        }
    }

    private void populatePropertiesMaps( Properties[] propertieses, MultiHashMap allProperties,
                                         MultiTreeMap allPropertyKeysInValueLengthOrder ) {
        for ( int i = 0; i < propertieses.length; i++ ) {
            Properties properties = propertieses[i];
            Set propertiesEntrySet = properties.entrySet();
            for ( Iterator iterator = propertiesEntrySet.iterator(); iterator.hasNext(); ) {
                Map.Entry propertiesEntry = (Map.Entry)iterator.next();

                String propertyKey = (String)propertiesEntry.getKey();
                String propertyValue = (String)propertiesEntry.getValue();
                allProperties.put( propertyKey, propertyValue );

                int propertiesValueLength = ( (String)propertiesEntry.getValue() ).length();
                allPropertyKeysInValueLengthOrder.put( new Integer( propertiesValueLength ), propertyKey );
            }
        }
    }

    private Properties[] loadPropertieses( String[] filenames ) {
        List propertiesList = new ArrayList();
        Iterator filenamesIterator = Arrays.asList( filenames ).iterator();
        while ( filenamesIterator.hasNext() ) {
            String filename = (String)filenamesIterator.next();
            propertiesList.add( loadProperties( new File( filename ) ) );
        }
        return (Properties[])propertiesList.toArray( new Properties[propertiesList.size()] );
    }

    private Properties loadProperties( File fileArg ) {
        try {
            Properties properties = new Properties();
            properties.load( new FileInputStream( fileArg ) );
            return properties;
        } catch ( IOException e ) {
            System.err.println( "Failed to open file " + fileArg.getPath() + ": " + e.toString() );
            System.exit( 1 );
            return null;
        }
    }

    private class AskForStringIterator implements Iterator {

        private String question;
        private BufferedReader reader;
        private String line = null;

        private AskForStringIterator( String question, BufferedReader reader ) {
            this.question = question;
            this.reader = reader;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext() {
            askForString();
            return null != line;
        }

        public Object next() {
            askForString();
            if ( null == line ) {
                throw new NoSuchElementException();
            }
            String result = line ;
            line = null ;
            return result;
        }

        private void askForString() {
            try {
                if ( null == line ) {
                    System.out.println( question );
                    line = reader.readLine();
                }
            } catch ( IOException ignored ) {
                // ignored
            }
        }
    }
}
