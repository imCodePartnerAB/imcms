package imcode.server;

import java.util.Vector;
import java.util.Hashtable;

class SqlHelpers {

    static String[] sqlProcedure( InetPoolManager conPool, String procedure, String[] params, boolean trim ) {
        procedure = trimAndCheckNoWhitespace(procedure) ;

        DBConnect dbc = new DBConnect( conPool );
        dbc.setTrim(trim) ;
        Vector data = dbc.executeProcedure(procedure,params) ;

        return createStringArrayFromSqlResults( data );
    }

    public static Hashtable sqlProcedureHash( InetPoolManager conPool, String procedure, String[] params ) {
        procedure = trimAndCheckNoWhitespace(procedure) ;

        DBConnect dbc = new DBConnect( conPool );
        Vector data = dbc.executeProcedure(procedure,params);
        String[] meta = dbc.getMetaData();

        return createHashtableOfStringArrayFromSqlResults( data, meta );
    }


    static String[][] sqlProcedureMulti( InetPoolManager conPool, String procedure, String[] params ) {
        procedure = trimAndCheckNoWhitespace(procedure) ;

        DBConnect dbc = new DBConnect( conPool );
        Vector data = dbc.executeProcedure( procedure, params );

        int columns = dbc.getColumnCount();

        return create2DStringArrayFromSqlResults( data, columns );

    }

    static String sqlProcedureStr( InetPoolManager conPool, String procedure, String[] params ) {
        DBConnect dbc = new DBConnect( conPool );
        Vector data = dbc.executeProcedure(procedure, params) ;

        return createStringFromSqlResults( data );
    }

    static int sqlUpdateProcedure( InetPoolManager conPool, String procedure, String[] params ) {
        DBConnect dbc = new DBConnect( conPool );
        int res = dbc.executeUpdateProcedure(procedure, params);
        return res;
    }

    public static int sqlUpdateQuery(InetPoolManager conPool, String sqlStr, String[] params) {
        DBConnect dbc = new DBConnect(conPool) ;
        dbc.setSQLString(sqlStr,params);
        int res = dbc.executeUpdateQuery();
        return res ;
    }

    static String[] sqlQuery(InetPoolManager conPool, String sqlQuery, String[] parameters) {
        DBConnect dbc = new DBConnect( conPool );
        dbc.setSQLString(sqlQuery, parameters);
        Vector data = (Vector)dbc.executeQuery();
        return createStringArrayFromSqlResults(data) ;
    }

    static String sqlQueryStr(InetPoolManager conPool, String sqlStr, String[] params) {
        DBConnect dbc = new DBConnect(conPool);
        dbc.setSQLString(sqlStr, params);
        Vector data = (Vector) dbc.executeQuery();
        return createStringFromSqlResults(data);
    }

    static Hashtable sqlQueryHash(InetPoolManager conPool, String sqlQuery, String[] params) {
        DBConnect dbc = new DBConnect(conPool);
        dbc.setSQLString(sqlQuery, params);

        Vector data = (Vector) dbc.executeQuery();
        String[] meta = (String[]) dbc.getMetaData();
        return createHashtableOfStringArrayFromSqlResults(data, meta) ;
    }

    static String[][] sqlQueryMulti(InetPoolManager conPool, String sqlQuery, String[] params) {
        Vector data = new Vector();

        DBConnect dbc = new DBConnect(conPool);
        dbc.setSQLString(sqlQuery, params);

        data = (Vector) dbc.executeQuery();
        int columns = dbc.getColumnCount();

        return create2DStringArrayFromSqlResults(data,columns) ;
    }

    private static String trimAndCheckNoWhitespace(String procedure) {
        procedure = procedure.trim();
        if (procedure.matches("\\s")) {
            throw new IllegalArgumentException("Procedurename contains whitespace. Procedure-parameters are not allowed in this method.");
        }
        return procedure;
    }

    private static String createStringFromSqlResults(Vector data) {
        if (data != null && !data.isEmpty()) {
            return
                    null != data.elementAt(0)
                    ? data.elementAt(0).toString()
                    : null;
        } else {
            return null;
        }
    }

    private static String[][] create2DStringArrayFromSqlResults(Vector data, int columns) {
        if (columns == 0)
            return new String[0][0];

        int rows = data.size() / columns;

        String result[][] = new String[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result[i][j] =
                        null != data.elementAt(i * columns + j)
                        ? data.elementAt(i * columns + j).toString()
                        : null;
            }

        }

        return result;
    }

    private static String[] createStringArrayFromSqlResults(Vector data) {
        if (data != null) {
            String result[] = new String[data.size()];
            for (int i = 0; i < data.size(); i++) {
                result[i] =
                        null != data.elementAt(i)
                        ? data.elementAt(i).toString()
                        : null;
            }
            return result;
        } else {
            return null;
        }
    }

    private static Hashtable createHashtableOfStringArrayFromSqlResults(Vector data, String[] meta) {
        Hashtable result = new Hashtable(meta.length, 0.5f);

        if (data.size() > 0) {

            for (int i = 0; i < meta.length; i++) {
                String temp_str[] = new String[data.size() / meta.length];
                int counter = 0;


                for (int j = i; j < data.size(); j += meta.length) {
                    temp_str[counter++] =
                            null != data.elementAt(j)
                            ? data.elementAt(j).toString()
                            : null;
                }
                result.put(meta[i], temp_str);
            }
            return result;
        } else {
            return new Hashtable(1, 0.5f);
        }
    }

}
