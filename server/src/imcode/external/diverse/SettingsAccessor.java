package imcode.external.diverse;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;

public class SettingsAccessor {

    private String fileName;
    private Properties settingsTable;
    private String delimiter;
    private UserDomainObject user;
    private String directory;

    public SettingsAccessor( String fileName, UserDomainObject user, String directory ) {
        this.fileName = fileName;
        this.user = user;
        this.directory = directory;
        settingsTable = new Properties();
        delimiter = "||";
    }

    /**
     * Sets the delimiter which should be used
     */
    public void setDelimiter( String newDelim ) {
        delimiter = newDelim;
    }

    /**
     * Loads the data from a storage device.
     */
    public synchronized void loadSettings() throws IOException {

        // Lets open the settingsfile
        ImcmsServices imcref = Imcms.getServices();
        BufferedReader inputFromFile = new BufferedReader( new StringReader( imcref.getTemplateFromDirectory( fileName, user, null, directory ) ) );

        // Lets get the settings from file into settingsTable, its a property
        Properties result;
        synchronized ( this ) {
            Properties table = new Properties();
            StringTokenizer st;
            String aLine;
            String propName, propVal;
            while ( ( aLine = inputFromFile.readLine() ) != null ) {
                propName = "";
                propVal = "";
                st = new StringTokenizer( aLine, delimiter );
                if ( st.hasMoreTokens() ) {
                    propName = convertArgument( st.nextToken().trim() );
                }
                if ( st.hasMoreTokens() ) {
                    propVal = st.nextToken().trim();
                }

                table.put( propName, propVal );
            }
            result = table;
        }
        settingsTable = result;

        inputFromFile.close();

    }

    public String toString() {
        return settingsTable.toString();
    }

    /**
     * returns a setting
     */
    public String getSetting( String wantedProp ) {
        String retStr = null;
        String wantedStr = this.convertArgument( wantedProp );
        retStr = (String)settingsTable.get( wantedStr );
        return retStr;
    }

    private String convertArgument( String theProp ) {
        return theProp.toUpperCase();
    }

}
