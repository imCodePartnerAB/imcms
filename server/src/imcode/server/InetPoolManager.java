package imcode.server ;

import com.inet.pool.PoolManager ;
import com.inet.pool.PDataSource ;
import java.util.Properties ;
import java.io.* ;
import java.sql.* ;
import javax.sql.* ;

import imcode.util.log.* ;

public class InetPoolManager {


    // Inet poolmanager
    PoolManager manager ;

    // DataSource
    ConnectionPoolDataSource ds;

    // Log
    Log log = Log.getLog("server") ;

    // Properties for the DataSource. Why, oh why, does not DataSource have something like that?
    Properties props ;

    public InetPoolManager(Properties props) throws SQLException {

	this.props = props ;
	
	// Create the connection pool.
	manager = new PoolManager();
	try {
	    manager.setMaxConnectionCount(Integer.parseInt(props.getProperty("MaxConnectionCount"))) ;
	} catch (NumberFormatException ex) {
	    
	}
	log.log(Log.INFO, "MaxConnectionCount: "+manager.getMaxConnectionCount()) ;

	// Create the DataSource.
	PDataSource pds = new PDataSource();

	// Set the datasource properties.
	try {
	    pds.setServerName( props.getProperty("ServerName") );
	} catch (NullPointerException ex) {
	    log.log(Log.ERROR, "Failed to find ServerName!",ex) ;	    
	    throw ex ;
	}
	log.log(Log.INFO, "ServerName: "+pds.getServerName()) ;

	try {
	    pds.setPort( props.getProperty("Port") ) ;
	} catch (NullPointerException ex) {
	    log.log(Log.WARNING, "Failed to find Port!",ex) ;	    
	}
	log.log(Log.INFO, "Port: "+pds.getPort()) ;

	try {
	    pds.setDatabaseName( props.getProperty("DatabaseName") );
	} catch (NullPointerException ex) {
	    log.log(Log.ERROR, "Failed to find DatabaseName!",ex) ;	    
	    throw ex ;
	}
	log.log(Log.INFO, "DatabaseName: "+pds.getDatabaseName()) ;

	try {
	    pds.setUser( props.getProperty("User") );
	} catch (NullPointerException ex) {
	    log.log(Log.ERROR, "Failed to find User!",ex) ;	    
	    throw ex ;
	}
	log.log(Log.INFO, "User: "+pds.getUser()) ;

	try {
	    pds.setPassword( props.getProperty("Password") );
	} catch (NullPointerException ex) {
	    log.log(Log.ERROR, "Failed to find Password!",ex) ;	    
	    throw ex ;
	}

	try {
	    pds.setLoginTimeout(Integer.parseInt( props.getProperty("LoginTimeout") ) );
	} catch (NumberFormatException ex) {
	    
	}

	log.log(Log.INFO, "LoginTimeout: "+pds.getLoginTimeout()) ;

	ds = pds;

	Connection connection = null ;
	
	try {
	    // request the Connection
	    connection = getConnection() ;
	
	    //to get the driver version
	    DatabaseMetaData conMD = connection.getMetaData();

	    log.log(Log.INFO, "Driver Name: " + conMD.getDriverName(),null );
	    log.log(Log.INFO, "Driver Version:" + conMD.getDriverVersion(),null );

	} catch (SQLException ex) {
	    log.log(Log.CRITICAL, "Failed to make first contact with the DataSource.",ex );
	    throw ex ;
	} finally {
	    connection.close() ;
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

	    log.log(Log.WARNING, err, ex.getMessage()) ;
	    throw ex ;
	}
    }

    /**
       return a pool manager
    */
    public PoolManager getManager() {
	return manager ;
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
