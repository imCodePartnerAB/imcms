package imcode.server;

import java.net.*;
import java.lang.reflect.Constructor;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Properties;
import java.util.Hashtable;
import imcode.util.log.*;
import imcode.util.Prefs;

/**
 *  Description of the Class
 *
 *@author     kreiger
 *@created    den 30 augusti 2001
 */
public class ApplicationServer {

    private final static String VERSION = "1.4.0 (2000-09-19 13:00)";
    private final static String CONFIG_FILE = "ImcServer.cfg";

    private final static int LOGINTERVAL = 10000;
    private final static int LOGSIZE = 16384;

    private final Hashtable serverObjects = new Hashtable();

    private final Log log = new Log( ApplicationServer.class.toString() );


    /**
     *  Constructor for the ApplicationServer object
     */
    public ApplicationServer() {

	// get list of servers
	StringTokenizer st = null;
	try {
	    String servers = Prefs.get("Servers", CONFIG_FILE);
	    st = new StringTokenizer(servers, " ,");
	    int serverObjectCount = st.countTokens();
	    log.log(Log.INFO, "" + serverObjectCount + " Server" + (serverObjectCount == 1 ? ": " : "s: ") + servers, null);
	} catch (IOException ex) {
	    log.log(Log.EMERGENCY, "Unable to load properties from " + CONFIG_FILE, ex);
	    throw new RuntimeException(ex.getMessage());
	} catch (NullPointerException ex) {
	    log.log(Log.EMERGENCY, "Unable to load properties from " + CONFIG_FILE, ex);
	    throw ex;
	}

	for (int i = 0; st.hasMoreTokens(); ++i) {
	    String servername = st.nextToken();
	    log.log(Log.INFO, "Reading properties for server " + servername, null);

	    Properties serverprops = null;

	    try {
		String serverpropsfile = Prefs.get(servername + ".properties", CONFIG_FILE);
		serverprops = Prefs.getProperties(serverpropsfile);
	    } catch (IOException ex) {
		log.log(Log.CRITICAL, "Unable to load properties for server " + servername, ex);
		continue;
	    }

	    // Find out what class this object is supposed to be of.
	    String classname = serverprops.getProperty("Class");

	    try {

		// Load the class
		Class objClass = Class.forName(classname);

		// Let's find the constructor that takes an "InetPoolManager" and a Properties.
		Constructor objConstructor = objClass.getConstructor(new Class[]{InetPoolManager.class, Properties.class});

		// Invoke Constructor(InetPoolManager, Properties) on class
		serverObjects.put(servername, objConstructor.newInstance(new Object[]{new InetPoolManager(serverprops), serverprops}));

	    } catch (ClassNotFoundException ex) {
		log.log(Log.CRITICAL, "Unable to find class " + classname, ex);
	    } catch (NoSuchMethodException ex) {
		log.log(Log.CRITICAL, "Class " + classname + " does not have a compatible constructor " + classname + "(InetPoolManager, Properties)", ex);
	    } catch (InstantiationException ex) {
		log.log(Log.CRITICAL, "Failed to invoke found constructor " + classname + "(InetPoolManager, Properties) on class " + classname, ex);
	    } catch (IllegalAccessException ex) {
		log.log(Log.CRITICAL, "Failed to invoke found constructor " + classname + "(InetPoolManager, Properties) on class " + classname, ex);
	    } catch (java.lang.reflect.InvocationTargetException ex) {
		log.log(Log.CRITICAL, "Failed to invoke found constructor " + classname + "(InetPoolManager, Properties) on class " + classname, ex.getTargetException());
	    } catch (java.sql.SQLException ex) {
		log.log(Log.CRITICAL, "Failed to create connectionpool and datasource for " + servername, ex);
	    }
	}

	log.log(Log.NOTICE, "ImCMS Daemon " + VERSION, null);
	log.log(Log.NOTICE, "imcmsd started: " + new java.util.Date(), null);
	log.log(Log.NOTICE, "imcmsd running...", null);
    }


    // return server count
    /**
     *  Gets the serverCount attribute of the ApplicationServer object
     *
     *@return    The serverCount value
     */
    public int getServerCount() {
	return serverObjects.size();
    }


    /**
     *  Gets the serverObject attribute of the ApplicationServer object
     *
     *@param  serverObjectName  Description of Parameter
     *@return                   The serverObject value
     */
    public Object getServerObject(String serverObjectName) {
	return serverObjects.get(serverObjectName);
    }

}
