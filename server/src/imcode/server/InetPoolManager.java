package imcode.server ;

import com.inet.pool.PoolManager ;
import com.inet.pool.PDataSource ;
import java.util.Properties ;
import java.io.* ;
import java.sql.* ;
import javax.sql.* ;

import org.apache.log4j.Category;

public class InetPoolManager {
	private final static String CVS_REV="$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;


    // Inet poolmanager
    private PoolManager manager ;

    // DataSource
    private ConnectionPoolDataSource ds;

    // Log
    private static Category log = Category.getInstance("server");

    // Properties for the DataSource. Why, oh why, does not DataSource have something like that?
    private Properties props ;

    public InetPoolManager(Properties props) throws SQLException {

	this.props = props ;
	
	// Create the connection pool.
	manager = new PoolManager();
	try {
	    manager.setMaxConnectionCount(Integer.parseInt(props.getProperty("MaxConnectionCount"))) ;
	} catch (NumberFormatException ex) {
	    log.info("setMaxConnectionCount",ex) ;
	}
	log.info("MaxConnectionCount: "+manager.getMaxConnectionCount()) ;

	// Create the DataSource.
	PDataSource pds = new PDataSource();

	// Set the datasource properties.
	try {
	    pds.setServerName( props.getProperty("ServerName") );
	} catch (NullPointerException ex) {
	    log.error("Failed to find ServerName!",ex) ;	    
	    throw ex ;
	}
	log.info("ServerName: "+pds.getServerName()) ;

	try {
	    pds.setPort( props.getProperty("Port") ) ;
	} catch (NullPointerException ex) {
	    log.warn( "Failed to find Port!",ex) ;	    
	}
	log.info("Port: "+pds.getPort()) ;

	try {
	    pds.setDatabaseName( props.getProperty("DatabaseName") );
	} catch (NullPointerException ex) {
	    log.error( "Failed to find DatabaseName!",ex) ;	    
	    throw ex ;
	}
	log.info("DatabaseName: "+pds.getDatabaseName()) ;

	try {
	    pds.setUser( props.getProperty("User") );
	} catch (NullPointerException ex) {
	    log.error( "Failed to find User!",ex) ;	    
	    throw ex ;
	}
	log.info("User: "+pds.getUser()) ;

	try {
	    pds.setPassword( props.getProperty("Password") );
	} catch (NullPointerException ex) {
	    log.error("Failed to find Password!",ex) ;	    
	    throw ex ;
	}

	try {
	    pds.setLoginTimeout(Integer.parseInt( props.getProperty("LoginTimeout") ) );
	} catch (NumberFormatException ex) {
		log.debug("Exception occured" ,ex );	   
	}

	log.info("LoginTimeout: "+pds.getLoginTimeout()) ;

	ds = pds;

	Connection connection = null ;
	
	try {
	    // request the Connection
	    connection = getConnection() ;
	
	    //to get the driver version
	    DatabaseMetaData conMD = connection.getMetaData();

	    log.info("Driver Name: " + conMD.getDriverName() );
	    log.info("Driver Version:" + conMD.getDriverVersion() );

	    connection.close() ;
	} catch (SQLException ex) {
	    log.fatal("Failed to make first contact with the DataSource.",ex );
	    throw ex ;
	}
    }

    Connection getConnection() throws SQLException {
	try {
	    return manager.getConnection( ds );
	} catch (SQLException ex) {
	    String err = "Failed to get connection from pool: "
		+ props.getProperty("User") + "@"
		+ props.getProperty("ServerName") + ":"
		+ props.getProperty("Port") + "/"
		+ props.getProperty("DatabaseName") + " Connections Used: "
		+ getUsedConnectionCount() + "/"
		+ getMaxConnectionCount() ;

	    log.warn( err, ex) ;
	    throw ex ;
	}
    }

    /**
       return used connections
    */
    public int getUsedConnectionCount() {
	return manager.getUsedConnectionCount() ;
    }

    /**
       return max connections
    */
    public int getMaxConnectionCount() {
	return manager.getMaxConnectionCount() ;
    }
}
