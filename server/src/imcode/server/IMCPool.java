package imcode.server;

import java.util.Properties;

/**
 * Database connection pool for the Imcode Net Server
 */
public class IMCPool implements IMCPoolInterface {

    //	ConnectionPool m_conPool ;            // our pool of connections
    private imcode.server.InetPoolManager m_conPool; // inet pool of connections

    /**
     * Construct a pool object
     */
    public IMCPool(imcode.server.InetPoolManager conPool, Properties props) {
        super();
        m_conPool = conPool;
    }

    /**
     * The preferred way of getting data from the db.
     * String.trim()'s the results.
     * 
     * @param procedure The name of the procedure
     * @param params    The parameters of the procedure
     */
    public String[] sqlProcedure(String procedure, String[] params) {
        return SqlHelpers.sqlProcedure(m_conPool, procedure, params, true);
    }

    /**
     * Send a procedure to the database and return a multi string array
     */
    public String[][] sqlProcedureMulti(String procedure, String[] params) {
        return SqlHelpers.sqlProcedureMulti(m_conPool, procedure, params);
    }

    public String sqlProcedureStr(String procedure, String[] params) {
        return SqlHelpers.sqlProcedureStr(m_conPool, procedure, params);
    }

    public int sqlUpdateProcedure(String procedure, String[] params) {
        return SqlHelpers.sqlUpdateProcedure(m_conPool, procedure, params);
    }

    public String[][] sqlQueryMulti( String sqlQuery, String[] params ) {
        return SqlHelpers.sqlQueryMulti(m_conPool, sqlQuery, params ) ;
    }

    public String[] sqlQuery( String sqlQuery, String[] params ) {
        return SqlHelpers.sqlQuery(m_conPool, sqlQuery, params) ;
    }

} // END CLASS IMCPool
