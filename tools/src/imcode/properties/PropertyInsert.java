package imcode.properties;

import imcode.util.MultiTreeMap;
import imcode.util.FileStringReplacer;
import imcode.util.FileFinder;
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
        Properties[] propertieses = getPropertiesesFromArgs( args );

        MultiHashMap allProperties = new MultiHashMap();
        MultiTreeMap allPropertyKeysInValueLengthOrder = new MultiTreeMap( Collections.reverseOrder() );

        populatePropertiesMaps( propertieses, allProperties, allPropertyKeysInValueLengthOrder );

        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( System.in ) ) ;
        Iterator propertyKeysByValueLengthIterator = allPropertyKeysInValueLengthOrder.values().iterator();
        boolean oneDone = false ;
        while ( !oneDone && propertyKeysByValueLengthIterator.hasNext() ) {
            String propertyKey = (String)propertyKeysByValueLengthIterator.next();

            boolean keyHasAlreadyBeenHandledAndRemoved = !allProperties.containsKey(propertyKey);
            if (keyHasAlreadyBeenHandledAndRemoved) {
                continue ;
            }

            handleKey: {
                outputPropertiesForKey( propertyKey, allProperties );
                Collection propertyValues = (Collection)allProperties.get( propertyKey );
                Iterator iterator = propertyValues.iterator();
                String previousPropertyValue = null;
                for ( int propertyValueIndex = 0; iterator.hasNext(); propertyValueIndex++ ) {
                    String propertyValue = (String)iterator.next();

                    propertyValue = replaceStringsInPropertyValue( propertyValueIndex, propertyKey, bufferedReader, propertyValue, propertieses );
                    if ( null != previousPropertyValue && !previousPropertyValue.equals( propertyValue ) ) {
                        System.out.println( "The resulting values are not identical!" );
                        break handleKey;
                    }

                    previousPropertyValue = propertyValue;
                }
                FileStringReplacer fileStringReplacer = new FileStringReplacer("<? "+propertyKey+" ?>",previousPropertyValue );
                FileFinder fileFinder = new FileFinder() ;
                fileFinder.find(fileStringReplacer,new File(".")) ;
            }
            allProperties.remove( propertyKey ) ;
            oneDone = true ;
        }

        for ( int i = 0; i < propertieses.length; i++ ) {
            Properties properties = propertieses[i];
            properties.store(new FileOutputStream(new File(args[i])), null);
        }

    }

    private String replaceStringsInPropertyValue( int propertyValueIndex, String propertyKey, BufferedReader bufferedReader,
                                                  String propertyValue, Properties[] propertieses ) throws IOException {
        int replacementStringsWanted = Integer.MAX_VALUE;
        for ( int replacementStringIndex = 0; replacementStringIndex < replacementStringsWanted; ++replacementStringIndex ) {
            int replacementStringsCount = ( replacementStringIndex + 1 );
            askForReplacement: {
                String replacement = askForReplacement( replacementStringsCount, propertyValueIndex,
                                                        propertyKey, bufferedReader );
                if ( null == replacement || "".equals( replacement ) ) {
                    if ( Integer.MAX_VALUE == replacementStringsWanted ) {
                        replacementStringsWanted = replacementStringIndex ;
                        break;
                    } else {
                        System.out.println(
                                "You need to input as many strings (" + replacementStringsWanted
                                + ") as you did for value "
                                + propertyValueIndex
                                + "!" );
                        break askForReplacement;
                    }
                }
                String replacementPropertyKey = propertyKey + "/" + replacementStringsCount;
                String tag = "<? " + replacementPropertyKey + " ?>";
                propertyValue = StringUtils.replace( propertyValue, replacement, tag );
                propertieses[propertyValueIndex].setProperty( replacementPropertyKey, replacement );
            }
        }
        for (int i = 0; i < propertieses.length; ++i) {
            propertieses[i].remove(propertyKey) ;
        }
        return propertyValue;
    }

    private String askForReplacement( int replacementStringsCount, int propertyValueIndex, String propertyKey,
                                      BufferedReader bufferedReader ) throws IOException {
        System.out.println( "String " + replacementStringsCount + " to replace in value " + ( propertyValueIndex + 1 ) + " for key " + propertyKey + "?" );
        String line = bufferedReader.readLine();
        return line;
    }

    private void outputPropertiesForKey( String propertyKey, MultiHashMap allProperties ) {
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
            System.out.println( propertyValue );
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

    private Properties[] getPropertiesesFromArgs( String[] args ) {
        List propertiesList = new ArrayList();
        Iterator argsIterator = Arrays.asList( args ).iterator();
        while ( argsIterator.hasNext() ) {
            String arg = (String)argsIterator.next();
            propertiesList.add( loadPropertiesFromFile( new File( arg ) ) );
        }
        return (Properties[])propertiesList.toArray( new Properties[propertiesList.size()] );
    }

    private Properties loadPropertiesFromFile( File fileArg ) {
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

}
