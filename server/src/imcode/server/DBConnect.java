package imcode.server;

import java.sql.*;
import java.sql.Date;
import java.io.*;
import java.util.*;

import org.apache.log4j.Category;

public class DBConnect {

    protected Connection con = null;                 // The JDBC Connection
    protected ResultSet rs = null;		    // The JDBC ResultSet
    protected ResultSetMetaData rsmd = null;	    // The JDBC ResultSetMetaData
    private PreparedStatement ps;
    private CallableStatement cs = null;	    // The JDBC CallableStatement
    protected String strSQLString = "";		    // SQL query-string
    protected String strProcedure = "";		    // Procedure
    protected String[] meta_data;       // Meta info
    protected boolean trimStr = true;
    protected int columnCount;                       // Column count

    private static Category log = Category.getInstance("server");

    // constructor
    public DBConnect(imcode.server.InetPoolManager conPool) {
        try {
            con = conPool.getConnection();
        } catch (SQLException e) {
            log.error("Failed to get connection from connectionpool.",e);
        }
    }

    // constructor
    public DBConnect(imcode.server.InetPoolManager conPool, String sqlString) {
        this(conPool);
        setSQLString(sqlString);
    }

    /**
     * <p>Execute a database query.
     */
    public Vector executeQuery() {

        Vector results = new Vector();

        // Execute SQL-string
        try {
            ps.execute();
            rs = ps.getResultSet();
            rsmd = rs.getMetaData();
            columnCount = rsmd.getColumnCount();
            meta_data = new String[columnCount];
            for (int i = 0; i < columnCount;) {
                meta_data[i] = rsmd.getColumnLabel(++i);
            }

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String s = rs.getString(i);
                    if (s == null) {
                        s = "";
                    } else if (trimStr) {
                        s = s.trim();
                    }
                    results.addElement(s);
                }
            }

            rs.close();
            ps.close();

        } catch (Exception ex) {
            log.error(ex);
        }

        return results;
    }

    /**
     * <p>Update databasequery.
     */
    public int executeUpdateQuery() {
        // Execute SQL-string
        try {
            int result = ps.executeUpdate();
            ps.close();
            return result;
        } catch (SQLException ex) {
            log.error(ex);
            return -1;
        }
    }

    /**
     * <p>Execute a database procedure.
     */
    public Vector executeProcedure() {

        Vector results = new Vector();
        try {
            if (cs == null) {
                throw new NullPointerException("DBConnect.executeProcedure() cs == null");
            }
            rs = cs.executeQuery();
            if (rs == null) {
                throw new NullPointerException("DBConnect.executeProcedure() rs == null");
            }
            rsmd = rs.getMetaData();
            columnCount = rsmd.getColumnCount();

            meta_data = new String[columnCount];
            for (int i = 0; i < columnCount;) {
                meta_data[i] = rsmd.getColumnLabel(++i);
            }
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String s = rs.getString(i);
                    if (null != s && trimStr) {
                        results.addElement(s.trim());
                    } else {
                        results.addElement(s);
                    }
                }
            }

            rs.close();
            cs.close();
        } catch (Exception ex) {
            log.error(ex);
        }
        return results;
    }


    /**
     * <p>Update database procedure.
     * 
     * @return updatecount or -1 if error
     */
    public int executeUpdateProcedure() {
        int res = 0;
        try {
            res = cs.executeUpdate();
            cs.close();
        } catch (Exception ex) {
            log.error(ex);
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


    /**
     * <p>Close a database connection.
     */
    public void closeConnection() {
        try {
            con.close();
        } catch (Exception ex) {
            log.error(ex);
        }
        con = null;
    }

    /**
     * <p>Get sqlquery.
     */
    public String getSQLString() {
        return strSQLString;
    }


    /**
     * <p>Set sqlquery.
     */
    public void setSQLString(String sqlString) {
        setSQLString(sqlString, new String[]{});
    }


    /**
     * <p>Set procedure.
     */
    public void setProcedure(String procedure, String param) {
        if (procedure == null) {
            throw new NullPointerException("DBConnect.setProcedure() procedure == null");
        }
        if (param == null) {
            strProcedure = "{call " + procedure + "}";
        } else {
            strProcedure = "{call " + procedure + " (?)}";
        }
        try {
            cs = con.prepareCall(strProcedure);
            cs.setString(1, param);
        } catch (Exception ex) {
            log.error(ex);
        }
    }

    /**
     * <p>Set procedure.
     */
    public void setProcedure(String procedure, String params[]) {
        if (procedure == null) {
            throw new NullPointerException("DBConnect.setProcedure() procedure == null");
        }
        if (params == null) {
            throw new NullPointerException("DBConnect.setProcedure() param == null");
        }
        strProcedure = "{call " + procedure + "}";
        prepareProcedureStatementAndSetParameters(params);
    }

    private void prepareProcedureStatementAndSetParameters(String[] params) {
        try {
            cs = con.prepareCall(strProcedure);
            for (int i = 0; i < params.length; ++i) {
                cs.setString(i + 1, params[i]);
            }
        } catch (SQLException ex) {
            log.error(ex);
        }
    }

    /**
     * Set procedure. This method employs an NFA to do a little bit of magic to fix faulty unescaped parameters.
     * It probably isn't fast, and it certainly isn't optimal. Needs to be fixed, which requires a rewriting of everything that uses this.
     */
    public void setProcedure(String procedure) {
        // The problem is... this method didn't accept the character "}", because it ends escape processing of the java procedure string.
        // So... this method was changed to parse the parameter string, and enter them properly, using setString()
        // The string comes in as (for example) "ProcedureName 'String', 47911,'{ThisIsAStringInsideBraces}',17, 'ThisIsAString,WithACommas,And''SingleQuotes'''"
        // This needs to become "ProcedureName (?,?,?,?,?)", and the appropriate calls to setString().

        StringTokenizer st = new StringTokenizer(procedure, ",' ", true);
        String procedurename = st.nextToken();
        LinkedList params = new LinkedList();
        StringBuffer param = new StringBuffer();
        boolean instring = false;
        boolean inparam = true;
        StringBuffer result = new StringBuffer(procedurename);
        result.append(" (");
        if (st.hasMoreTokens()) { // Are there any parameters?
            result.append('?');    // If there are parameters, we always start with a "?"
            ArrayList vec = new ArrayList(st.countTokens()); // Wohoo, look at me! I'm using an ArrayList! Note how i presize it.
            while (st.hasMoreTokens()) {
                vec.add(st.nextToken());  // Put all the tokens into the ArrayList.
            }
            ListIterator lit = vec.listIterator(); // We need an iterator to go both forward and backward.
            while (lit.hasNext()) {               // Now iterate over the ArrayList
                String tok = (String) lit.next();
                switch (tok.charAt(0)) {             // Test the token. If it matches one of these, it is one-char only.
                    case ',':                              // We struck a "," !
                        if (!instring) {                 // If we're not inside a string...
                            params.add(param.toString()); // then we have a full parameter, so let's add it.
                            param.setLength(0);           // Begin anew...
                            result.append(",?");          // ... with the next parameter.
                            inparam = true;
                        } else {                           // We're inside a string...
                            param.append(tok);            // ... so let's just add the "," to the string.
                        }
                        break;

                    case '\'':                                                    // We struck a "'" !
                        if (instring && lit.hasNext()) {                         // If we are in a string, and we have more chars, then...
                            if ((tok = (String) lit.next()).charAt(0) == '\'') { // ... if the next char also is a "'", then...
                                param.append('\'');	                      // ...add it to the string, and continue.
                            } else {                                              // The next char is not a "'"!
                                instring = false;                                // Hopefully the string ends here...
                                lit.previous();                                  // ...so backup to the previous token again, and continue.
                            }
                        } else {
                            if (instring) {                                     // The string ends here, since we have no more tokens.
                                inparam = false;                                 // So we're not in a param anymore.
                            }
                            instring = !instring;                                // If we weren't in a string, we are now, and vice versa.
                        }
                        break;

                    case ' ':                                                     // Got (white)space
                        if (instring) {                                         // Ignore unless in string.
                            param.append(' ');
                        }
                        break;

                    default:
                        if (inparam || instring) {                              // If we're in a parameter or a string
                            param.append(tok);                                   // Just keep appending whatever we got.
                        }
                        break;
                }
            }
            params.add(param.toString().trim());
        }
        result.append(')'); // And finally, top it off with a ')'.

        // Build the ugly java sql-escape-string. The very reason we need this method at all.
        strProcedure = "{call " + result.toString() + "}";
        // Prepare the call.
        try {
            cs = con.prepareCall(strProcedure);

            Iterator it = params.iterator();
            int i = 0;
            // Hand over the parameters.
            while (it.hasNext()) {
                String parm = (String) it.next();
                cs.setString(++i, parm);
            }
        } catch (Exception ex) {
            String paramstr = "";
            Iterator it = params.iterator();
            int i = 0;
            while (it.hasNext()) {
                paramstr += (String) it.next();
                if (it.hasNext()) {
                    paramstr += ", ";
                }
            }
            log.error(ex);
        }
    }


    /**
     * <p>Set trim. true = trim strings, false = do not trim strings.
     */
    public void setTrim(boolean status) {
        trimStr = status;
    }


    /**
     * <p>Execute a sql query and close connection.
     */
    public String sqlQueryStr(String sqlStr) {

        this.setSQLString(sqlStr);
        Vector result = (Vector) this.executeQuery();
        this.closeConnection();
        return result.elementAt(0).toString();
    }


    /**
     * <p>Execute a sql query and close connection.
     */
    public Vector sqlQuery(String sqlStr) {
        this.setSQLString(sqlStr);
        Vector result = (Vector) this.executeQuery();
        this.closeConnection();
        return result;
    }

    Vector executeProcedure(String procedure) {
        setProcedure(procedure);

        Vector data = executeProcedure();
        closeConnection();
        return data;
    }

    public Vector executeProcedure(String procedure, String[] params) {
        procedure = addQuestionMarksToProcedureCall(procedure, params);

        setProcedure(procedure, params);

        Vector data = executeProcedure();
        closeConnection();
        return data;
    }

    private static String addQuestionMarksToProcedureCall(String procedure, String[] params) {
        if (params.length > 0) {
            StringBuffer procedureBuffer = new StringBuffer(procedure);
            procedureBuffer.append(" ?");
            for (int i = 1; i < params.length; ++i) {
                procedureBuffer.append(",?");
            }
            procedure = procedureBuffer.toString();
        }
        return procedure;
    }

    public void setSQLString(String sqlStr, String[] params) {
        strSQLString = sqlStr;
        prepareQueryStatementAndSetParameters(params);
    }

    private void prepareQueryStatementAndSetParameters(String[] params) {
        try {
            ps = con.prepareStatement(strSQLString);
            for (int i = 0; i < params.length; ++i) {
                ps.setString(i + 1, params[i]);
            }
        } catch (SQLException ex) {
            log.error(ex);
        }
    }
} // END CLASS DBConnect
