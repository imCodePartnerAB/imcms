package imcode.server;

import java.sql.*;
import java.sql.Date;
import java.io.*;
import java.util.*;

import imcode.server.*;


/**
 Database connection pool for the Imcode Net Server
 */
public class IMCPool implements IMCPoolInterface {

    //	ConnectionPool m_conPool ;            // our pool of connections
    imcode.server.InetPoolManager m_conPool; // inet pool of connections

    /**
     Construct a pool object
     */
    public IMCPool( imcode.server.InetPoolManager conPool, Properties props ) {
        super();
        m_conPool = conPool;
    }

    /**
     Send a sqlQuery to the database and return a string array
     */
    public String[] sqlQuery( String sqlQuery ) {

        Vector data = new Vector();

        DBConnect dbc = new DBConnect( m_conPool, sqlQuery );
        data = (Vector)dbc.executeQuery();

        if ( data != null ) {
            String result[] = new String[data.size()];
            for ( int i = 0; i < data.size(); i++ )
                result[i] = data.elementAt( i ).toString();

            dbc.closeConnection();
            dbc = null;
            data = null;
            return result;
        } else {
            dbc.closeConnection();
            dbc = null;
            data = null;
            return null;
        }
    }


    /**
     Send a sql update query to the database
     */
    public void sqlUpdateQuery( String sqlStr ) {
        DBConnect dbc = new DBConnect( m_conPool, sqlStr );
        dbc.executeUpdateQuery();
        dbc.closeConnection();
        dbc = null;
    }


    /**
     Send a procedure to the database and return a string array
     */
    public String[] sqlProcedure( String procedure ) {
        return SqlHelpers.sqlProcedure( m_conPool, procedure );
    }

    /**
     The preferred way of getting data from the db.
     String.trim()'s the results.
     @param procedure The name of the procedure
     @param params    The parameters of the procedure
     **/
    public String[] sqlProcedure( String procedure, String[] params ) {
        return SqlHelpers.sqlProcedure( m_conPool, procedure, params, true );
    }


    /**
     Send a procedure to the database and return a string
     */
    public String sqlProcedureStr( String procedure ) {
        Vector data = new Vector();

        DBConnect dbc = new DBConnect( m_conPool );
        dbc.setProcedure( procedure );
        data = (Vector)dbc.executeProcedure().clone();


        dbc.closeConnection();
        dbc = null;

        if ( data != null ) {
            if ( data.size() > 0 ) {
                return data.elementAt( 0 ).toString();
            } else {
                return null;
            }
        } else {
            throw new java.lang.NullPointerException( "Null in IMCPool.sqlProcedureStr(String)" );
        }
    }


    /**
     Send a update procedure to the database
     */
    public void sqlUpdateProcedure( String procedure ) {
        DBConnect dbc = new DBConnect( m_conPool );
        dbc.setProcedure( procedure );
        dbc.executeUpdateProcedure();
        dbc.closeConnection();
        dbc = null;
    }


    /**
     Send a sqlQuery to the database and return a string array
     Array[0]                 = number of field in the record
     Array[1]   - array[n]    = metadata
     Array[n+1] - array[size] = data
     */
    public String[] sqlQueryExt( String sqlQuery ) {

        Vector data = new Vector();
        String[] meta = new String[0];

        DBConnect dbc = new DBConnect( m_conPool, sqlQuery );
        data = (Vector)dbc.executeQuery();
        meta = dbc.getMetaData();

        if ( data.size() > 0 ) {
            String result[] = new String[data.size() + dbc.getColumnCount() + 1];

            // no of fields
            result[0] = dbc.getColumnCount() + "";

            // meta
            int i = 0;
            for ( i = 0; i < dbc.getColumnCount(); i++ )
                result[i + 1] = meta[i];

            // data
            for ( int j = 0; j < data.size(); j++ )
                result[j + i + 1] = data.elementAt( j ).toString();

            dbc.closeConnection();
            dbc = null;
            data = null;
            meta = null;
            return result;
        } else {
            dbc.closeConnection();
            dbc = null;
            data = null;
            meta = null;
            return null;
        }
    }

    /**
     Send a procedure to the database and return a string array
     Array[0]                 = number of field in the record
     Array[1]   - array[n]    = metadata
     Array[n+1] - array[size] = data
     */
    public String[] sqlProcedureExt( String procedure ) {

        Vector data = new Vector();
        String[] meta = new String[0];
        DBConnect dbc = new DBConnect( m_conPool );
        dbc.setProcedure( procedure );


        data = (Vector)dbc.executeProcedure();
        meta = dbc.getMetaData();

        if ( data != null && data.size() > 0 ) {


            String result[] = new String[data.size() + dbc.getColumnCount() + 1];

            // no of fields
            result[0] = dbc.getColumnCount() + "";

            // meta
            int i = 0;
            for ( i = 0; i < dbc.getColumnCount(); i++ )
                result[i + 1] = meta[i];

            // data
            for ( int j = 0; j < data.size(); j++ )
                result[j + i + 1] = data.elementAt( j ).toString();


            dbc.closeConnection();
            dbc = null;
            data = null;
            meta = null;
            return result;
        } else {
            dbc.closeConnection();
            dbc = null;
            data = null;
            meta = null;
            return null;
        }


    }


    /**
     Send a sqlQuery to the database and return a Hastable
     */
    public Hashtable sqlQueryHash( String sqlQuery ) {

        Vector data = new Vector();
        String[] meta = new String[0];

        DBConnect dbc = new DBConnect( m_conPool, sqlQuery );
        data = (Vector)dbc.executeQuery().clone();

        meta = dbc.getMetaData();
        int columns = dbc.getColumnCount();

        Hashtable result = new Hashtable( columns, 0.5f );


        dbc.closeConnection();


        if ( data.size() > 0 ) {

            for ( int i = 0; i < columns; i++ ) {
                String temp_str[] = new String[data.size() / columns];
                int counter = 0;

                for ( int j = i; j < data.size(); j += columns )
                    temp_str[counter++] = data.elementAt( j ).toString();
                ;

                result.put( meta[i], temp_str );
            }


            return result;
        } else {
            return new Hashtable( 1, 0.5f );
        }

    }


    /**
     Send a procedure to the database and return a multi string array
     */
    public String[][] sqlProcedureMulti( String procedure ) {
        return SqlHelpers.sqlProcedureMulti(m_conPool, procedure) ;
    }

    /**
     Send a procedure to the database and return a multi string array
     */
    public String[][] sqlProcedureMulti( String procedure, String[] params ) {
        return SqlHelpers.sqlProcedureMulti(m_conPool, procedure, params) ;
    }

    public String sqlProcedureStr( String procedure, String[] params ) {
        return SqlHelpers.sqlProcedureStr(m_conPool,procedure,params) ;
    }

    public int sqlUpdateProcedure( String procedure, String[] params) {
        return SqlHelpers.sqlUpdateProcedure(m_conPool, procedure, params) ;
    }


} // END CLASS IMCPool
