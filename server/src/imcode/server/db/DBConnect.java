package imcode.server.db;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.*;

public class DBConnect {
    private final static String CVS_REV = "$Revision$";
    private final static String CVS_DATE = "$Date$";

    ConnectionPool conPool; // Inet poolmanager

    protected Connection con = null;                 // The JDBC Connection
    protected PreparedStatement preparedStatement = null;		    // The JDBC Statement
    protected CallableStatement callableStatement = null;	    // The JDBC CallableStatement
    protected ResultSet resultSet = null;		    // The JDBC ResultSet
    protected ResultSetMetaData rsmd = null;	    // The JDBC ResultSetMetaData
    protected String[] meta_data;       // Meta info
    protected String catalog = "";		    // Current database
    protected String default_catalog = "";	    // Default database
    protected boolean trimStr = true;
    protected int columnCount;                       // Column count

    private final static Logger log = Logger.getLogger( "imcode.server.db.DBConnect" );

    // constructor
    public DBConnect( ConnectionPool conPool ) {
        this.conPool = conPool;
    }

    // constructor
    public DBConnect( ConnectionPool conPool, String sqlString ) {
        this(conPool) ;
        setSQLString(sqlString);
    }

    private void getConnection() {
        try {
            if (null == con || con.isClosed()) {
                con = conPool.getConnection();
            }
        } catch( SQLException ex ) {
            log.error( "getConnection()", ex );
        }
    }

    /**
     * <p>Set sqlquery.
     */
    public void setSQLString( String sqlString ) {
        setSQLString( sqlString, null );
    }

    public void setSQLString( String sqlString, String[] params )  {
        getConnection();
        try {
            preparedStatement = con.prepareStatement(sqlString) ;
            setParameters(preparedStatement, params);
        } catch ( SQLException e ) {
            log.error( "setSQLString("+Arrays.asList(emptyArrayIfNull( params ))+")", e );
            throw new DatabaseException( "Failed to set sql string and parameters.", e );
        }
    }

    class DatabaseException extends RuntimeException {

        DatabaseException( String message, Throwable cause ) {
            super( message, cause );
        }

    }

    /**
     * <p>Set procedure.
     */
    public void setProcedure( String procedure, String param ) {
        getConnection();
        if( procedure == null ) {
            throw new NullPointerException( "DBConnect.setProcedure() procedure == null" );
        }
        String strProcedure ;
        if( param == null ) {
            strProcedure = "{call " + procedure + "}";
        } else {
            strProcedure = "{call " + procedure + " (?)}";
        }
        try {
            prepareCallAndSetParameters( strProcedure, Arrays.asList(new String[] { param }) );
        } catch( SQLException e ) {
            log.error( "", e );
        }
    }

    /**
     * <p>Set procedure.
     */
    public void setProcedure( String procedure, String params[] ) {
        getConnection();
        if( procedure == null ) {
            throw new NullPointerException( "DBConnect.setProcedure() procedure == null" );
        }

        StringBuffer procStr = new StringBuffer();
        procStr.append( "{call " );
        procStr.append( procedure.trim() );
        procStr.append( "(");
        for( int i = 0; null != params && i < params.length ; i++ ){
            procStr.append( "?" );
            if( i < params.length - 1 ) {
                procStr.append(",");
            }
        }
        procStr.append(")}");

        try {
            prepareCallAndSetParameters( procStr.toString(), Arrays.asList(emptyArrayIfNull(params))) ;
        } catch( SQLException ex ) {
            log.error( procStr.toString(), ex );
        }
    }

    /**
     * Set procedure. This method employs an NFA to do a little bit of magic to fix faulty unescaped parameters.
     * It probably isn't fast, and it certainly isn't optimal. Needs to be fixed, which requires a rewriting of everything that uses this.
     */
    public void setProcedure( String procedure ) {
        // The problem is... this method didn't accept the character "}", because it ends escape processing of the java procedure string.
        // So... this method was changed to parse the parameter string, and enter them properly, using setString()
        // The string comes in as (for example) "ProcedureName 'String', 47911,'{ThisIsAStringInsideBraces}',17, 'ThisIsAString,WithACommas,And''SingleQuotes'''"
        // This needs to become "ProcedureName (?,?,?,?,?)", and the appropriate calls to setString().

        getConnection();
        StringTokenizer st = new StringTokenizer( procedure, ",' ", true );
        String procedurename = st.nextToken();
        LinkedList params = new LinkedList();
        StringBuffer param = new StringBuffer();
        boolean instring = false;
        boolean inparam = true;
        StringBuffer result = new StringBuffer( procedurename );
        result.append( " (" );
        if( st.hasMoreTokens() ) { // Are there any parameters?
            result.append( '?' );    // If there are parameters, we always start with a "?"
            ArrayList vec = new ArrayList( st.countTokens() ); // Wohoo, look at me! I'm using an ArrayList! Note how i presize it.
            while( st.hasMoreTokens() ) {
                vec.add( st.nextToken() );  // Put all the tokens into the ArrayList.
            }
            ListIterator lit = vec.listIterator(); // We need an iterator to go both forward and backward.
            while( lit.hasNext() ) {               // Now iterate over the ArrayList
                String tok = (String)lit.next();
                switch( tok.charAt( 0 ) ) {             // Test the token. If it matches one of these, it is one-char only.
                    case ',':                              // We struck a "," !
                        if( !instring ) {                 // If we're not inside a string...
                            params.add( param.toString() ); // then we have a full parameter, so let's add it.
                            param.setLength( 0 );           // Begin anew...
                            result.append( ",?" );          // ... with the next parameter.
                            inparam = true;
                        } else {                           // We're inside a string...
                            param.append( tok );            // ... so let's just add the "," to the string.
                        }
                        break;

                    case '\'':                                                    // We struck a "'" !
                        if( instring && lit.hasNext() ) {                         // If we are in a string, and we have more chars, then...
                            if( (tok = (String)lit.next()).charAt( 0 ) == '\'' ) { // ... if the next char also is a "'", then...
                                param.append( '\'' );	                      // ...add it to the string, and continue.
                            } else {                                              // The next char is not a "'"!
                                instring = false;                                // Hopefully the string ends here...
                                lit.previous();                                  // ...so backup to the previous token again, and continue.
                            }
                        } else {
                            if( instring ) {                                     // The string ends here, since we have no more tokens.
                                inparam = false;                                 // So we're not in a param anymore.
                            }
                            instring = !instring;                                // If we weren't in a string, we are now, and vice versa.
                        }
                        break;

                    case ' ':                                                     // Got (white)space
                        if( instring ) {                                         // Ignore unless in string.
                            param.append( ' ' );
                        }
                        break;

                    default:
                        if( inparam || instring ) {                              // If we're in a parameter or a string
                            param.append( tok );                                   // Just keep appending whatever we got.
                        }
                        break;
                }
            }
            params.add( param.toString().trim() );
        }
        result.append( ')' ); // And finally, top it off with a ')'.

        // Build the ugly java sql-escape-string. The very reason we need this method at all.
        String strProcedure = "{call " + result.toString() + "}";
        // Prepare the call.
        try {
            prepareCallAndSetParameters(strProcedure, params) ;
        } catch( SQLException ex ) {
            StringBuffer paramstr = new StringBuffer();
            Iterator it = params.iterator();
            while( it.hasNext() ) {
                paramstr.append( (String)it.next() );
                if( it.hasNext() ) {
                    paramstr.append(", ");
                }
            }
            log.error( "setProcedure("+paramstr+")", ex );
        }
    }

    /**
     * <p>Execute a database query.
     */
    public List executeQuery() {

        List results = new ArrayList();

        // Execute SQL-string
        try {
            resultSet = preparedStatement.executeQuery();
            rsmd = resultSet.getMetaData();
            columnCount = rsmd.getColumnCount();
            meta_data = new String[columnCount];
            for( int i = 0; i < columnCount; ) {
                meta_data[i] = rsmd.getColumnLabel( ++i );
            }

            while( resultSet.next() ) {
                for( int i = 1; i <= columnCount; i++ ) {
                    String s = resultSet.getString( i );
                    if ( s != null && trimStr ) {
                        s = s.trim();
                    }
                    results.add( s );
                }
            }
            resultSet.close() ;
            preparedStatement.close() ;

        } catch( SQLException e ) {
            log.error( "executeQuery() failed: "+preparedStatement, e );
        } finally {
            returnConnection() ;
        }

        return results;
    }

    /**
     * <p>Update databasequery.
     */
    public int executeUpdateQuery() {
        // Execute SQL-string
        int result = -1 ;
        try {
            result = preparedStatement.executeUpdate();
            preparedStatement.close() ;
        } catch( SQLException e ) {
            log.error( "executeUpdateQuery() failed: "+preparedStatement, e );
        } finally {
            returnConnection() ;
        }
        return result ;
    }

    /**
     * <p>Execute a database procedure.
     */
    public Vector executeProcedure() {

        Vector results = new Vector();
        try {
            if( callableStatement == null ) {
                throw new NullPointerException( "DBConnect.executeProcedure() callableStatement == null" );
            }
            resultSet = callableStatement.executeQuery();
            if( resultSet == null ) {
                throw new NullPointerException( "DBConnect.executeProcedure() resultSet == null" );
            }
            rsmd = resultSet.getMetaData();
            columnCount = rsmd.getColumnCount();

            meta_data = new String[columnCount];
            for( int i = 0; i < columnCount; ) {
                meta_data[i] = rsmd.getColumnLabel( ++i );
            }
            while( resultSet.next() ) {
                for( int i = 1; i <= columnCount; i++ ) {
                    String s = resultSet.getString( i );
                    if( null != s && trimStr ) {
                        results.addElement( s.trim() );
                    } else {
                        results.addElement( s );
                    }
                }
            }
            resultSet.close() ;
            callableStatement.close() ;
        } catch( SQLException e ) {
            log.error( "executeProcedure() failed: " + callableStatement, e );
        } finally {
            returnConnection() ;
        }
        return results;
    }


    /**
     * <p>Update database procedure.
     * @return updatecount or -1 if error
     */
    public int executeUpdateProcedure() {
        int res = 0;
        try {
            res = callableStatement.executeUpdate();
            callableStatement.close() ;
        } catch( SQLException e ) {
            log.error( "executeUpdateProcedure() - "+callableStatement, e );
        } finally {
            returnConnection() ;
        }
        return res;
    }


    /**
     * <p>Get metadata.
     */
    public String[] getMetaData() {
        return meta_data;
    }


    /**
     * <p>Get columncount.
     */
    public int getColumnCount() {
        return columnCount;
    }


    private void prepareCallAndSetParameters( String strProcedure, List params ) throws SQLException {
        callableStatement = con.prepareCall( strProcedure );
        setParameters(callableStatement, params ) ;
    }

    private static String[] emptyArrayIfNull( String[] params ) {
        if (null == params) {
            params = new String[]{};
        }
        return params;
    }

    private static void setParameters( PreparedStatement stmt, String[] params ) throws SQLException {
        params = emptyArrayIfNull( params );
        setParameters(stmt, Arrays.asList(params));
    }

    private static void setParameters( PreparedStatement stmt, List params ) throws SQLException {
        if (null == params ) {
            return ;
        }
        int i = 0;
        for( Iterator it = params.iterator() ; it.hasNext() ; ) {
            String param = (String)it.next();
            try {
                if ( null != param ) {
                    stmt.setString( ++i, param );
                }
            } catch(SQLException se) {
                log.error("Failed to set parameter "+i+" of statement "+stmt.toString()+" to "+param,se) ;
                throw se ;
            }
        }
    }

    /**
     * <p>Set trim. true = trim strings, false = do not trim strings.
     */
    public void setTrim( boolean status ) {
        trimStr = status;
    }

    private void returnConnection() {
        try {
            con.close() ;
            con = null ;
        } catch ( SQLException e ) {
            log.error("Failed to close connection.",e) ;
        }
    }

    protected void finalize() throws SQLException {
        returnConnection() ;
    }

} // END CLASS DBConnect
