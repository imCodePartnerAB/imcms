package imcode.server ;

import java.rmi.* ;
import java.rmi.registry.* ;
import java.rmi.server.UnicastRemoteObject ;
import java.net.* ;
import java.lang.reflect.Constructor ;
import java.io.IOException ;
import java.util.StringTokenizer ;
import java.util.Properties ;

import imcode.util.log.* ;
import imcode.util.* ;

public class ApplicationServer {

    final static String VERSION = "1.3.2 (2000-04-09 15:30)" ;
    final static String CONFIG_FILE = "ImcServer.cfg" ;

    final static int LOGINTERVAL = 10000 ;
    final static int LOGSIZE = 16384 ;

    UnicastRemoteObject[] remoteObjects ;

    Log log ;

    Registry reg ;

    public ApplicationServer() throws IOException {

	System.setSecurityManager(new RMISecurityManager()) ;

	log = Log.getLog("server") ;

	// Set up logfiles
	WriterLogger imcms = null ;
	try {
	    String mainlog = Prefs.get("Log.Main", CONFIG_FILE) ;
	    imcms = new WriterLogger(new java.io.BufferedWriter(new java.io.FileWriter(mainlog,true)),Log.INFO,Log.WARNING,LOGINTERVAL,LOGSIZE) ;
	} catch (NullPointerException ex) {
	    log.log(Log.NOTICE, "Failed to find and setup Log.Main (the main log) in "+CONFIG_FILE) ;
	}

	WriterLogger error = null ;
	try {
	    String errorlog = Prefs.get("Log.Error", CONFIG_FILE) ;
	    error = new WriterLogger(new java.io.BufferedWriter(new java.io.FileWriter(errorlog,true)),Log.WARNING,Log.WARNING,LOGINTERVAL,LOGSIZE) ;
	} catch (NullPointerException ex) {
	    log.log(Log.NOTICE, "Failed to find and setup Log.Error (the error log) in "+CONFIG_FILE) ;
	}

	log.addLogListener(imcms) ;
	log.addLogListener(error) ;

	// get list of servers
	StringTokenizer st = null ;
	try {
	    String servers = Prefs.get("Servers", CONFIG_FILE) ;
	    st = new StringTokenizer(servers, " ,") ;
	    remoteObjects = new UnicastRemoteObject[st.countTokens()] ;
	    log.log(Log.INFO,""+remoteObjects.length+" Server"+(remoteObjects.length==1?": ":"s: ")+servers, null) ;
	} catch (IOException ex) {
	    log.log(Log.EMERGENCY, "Unable to load properties from "+CONFIG_FILE, ex) ;
	    throw ex ;
	} catch (NullPointerException ex) {
	    log.log(Log.EMERGENCY, "Unable to load properties from "+CONFIG_FILE, ex) ;
	    throw ex ;
	}

	int port ;
	try {
	    port = Integer.parseInt(Prefs.get("RMIPort",CONFIG_FILE)) ;
	} catch (NumberFormatException ex) {
	    port = 1099 ;
	} catch (IOException ex) {
	    port = 1099 ;
	} catch (NullPointerException ex) {
	    port = 1099 ;
	}

	log.log(Log.INFO, "Starting rmiregistry on port "+port) ;

	try {
	    reg = LocateRegistry.createRegistry(port) ;
	} catch (RemoteException ex) {
	    log.log(Log.EMERGENCY, "Failed to start RMI-registry.", ex) ;
	    throw ex ;
	}

	for ( int i=0 ; st.hasMoreTokens() ; ++i ) {
	    String servername = st.nextToken() ;
	    log.log(Log.INFO, "Reading properties for server "+servername, null) ;

	    Properties serverprops = null ;

	    try {
		String serverpropsfile = Prefs.get(servername+".properties", CONFIG_FILE) ;
		serverprops = Prefs.getProperties(serverpropsfile) ;
	    } catch (IOException ex) {
		log.log(Log.CRITICAL, "Unable to load properties for server "+servername, ex) ;
		continue ;
	    }

	    // Find out what class this object is supposed to be of.
	    String classname = serverprops.getProperty("Class") ;

	    try {

		// Load the class
		Class objClass = Class.forName(classname) ;

		// Let's find the constructor that takes an "InetPoolManager" and a Properties.
		Constructor objConstructor = objClass.getConstructor(new Class[] {InetPoolManager.class, Properties.class}) ;

		// Invoke Constructor(InetPoolManager, Properties) on class
		remoteObjects[i] = (UnicastRemoteObject)objConstructor.newInstance(new Object[] {new InetPoolManager(serverprops), serverprops} ) ;

		// Bind object to rmi-registry
	    	reg.bind(servername,remoteObjects[i]) ;

	    } catch (ClassNotFoundException ex) {
		log.log(Log.CRITICAL, "Unable to find class "+classname, ex) ;
	    } catch (ClassCastException ex) {
		log.log(Log.CRITICAL, "Class "+classname+" is not a subclass of java.rmi.server.UnicastRemoteObject", ex) ;
	    } catch (NoSuchMethodException ex) {
		log.log(Log.CRITICAL, "Class "+classname+" does not have a compatible constructor "+classname+"(InetPoolManager, Properties)", ex) ;
	    } catch (RemoteException ex) {
		log.log(Log.CRITICAL, "Failed to bind "+servername+" to local RMI-registry", ex) ;
	    } catch (InstantiationException ex) {
		log.log(Log.CRITICAL, "Failed to invoke found constructor "+classname+"(InetPoolManager, Properties) on class "+classname, ex) ;
	    } catch (IllegalAccessException ex) {
		log.log(Log.CRITICAL, "Failed to invoke found constructor "+classname+"(InetPoolManager, Properties) on class "+classname, ex) ;
	    } catch (java.lang.reflect.InvocationTargetException ex) {
		log.log(Log.CRITICAL, "Failed to invoke found constructor "+classname+"(InetPoolManager, Properties) on class "+classname, ex.getTargetException()) ;
	    } catch (AlreadyBoundException ex) {
		log.log(Log.CRITICAL, "Failed to bind "+classname+" in RMI-registry. The name "+servername+" is already bound.", ex) ;
	    } catch (java.sql.SQLException ex) {
		log.log(Log.CRITICAL, "Failed to create connectionpool and datasource for "+servername, ex) ;
	    }
	}
	
	log.log(Log.NOTICE, "ImCMS Daemon "+VERSION,null) ;
	log.log(Log.NOTICE, "imcmsd started: " + new java.util.Date(), null) ;
	log.log(Log.NOTICE, "imcmsd running...",null) ;
    }
    
    // return server count
    public int getServerCount() {
	return remoteObjects.length ;
    }

}
