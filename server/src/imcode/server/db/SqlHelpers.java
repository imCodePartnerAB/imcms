package imcode.server.db;

import java.util.Hashtable;
import java.util.List;

public class SqlHelpers {

    public static String[] sqlProcedure( ConnectionPool conPool, String procedure, String[] params, boolean trim ) {
        procedure = trimAndCheckNoWhitespace(procedure) ;

        DBConnect dbc = new DBConnect( conPool );
        dbc.setTrim(trim) ;
        List data = dbc.executeProcedure(procedure,params) ;

        return createStringArrayFromSqlResults( data );
    }

    public static Hashtable sqlProcedureHash( ConnectionPool conPool, String procedure, String[] params ) {
        procedure = trimAndCheckNoWhitespace(procedure) ;

        DBConnect dbc = new DBConnect( conPool );
        List data = dbc.executeProcedure(procedure,params);
        String[] meta = dbc.getMetaData();

        return createHashtableOfStringArrayFromSqlResults( data, meta );
    }


    public static String[][] sqlProcedureMulti( ConnectionPool conPool, String procedure, String[] params ) {
        procedure = trimAndCheckNoWhitespace(procedure) ;

        DBConnect dbc = new DBConnect( conPool );
        List data = dbc.executeProcedure( procedure, params );

        int columns = dbc.getColumnCount();

        return create2DStringArrayFromSqlResults( data, columns );

    }

    public static String sqlProcedureStr( ConnectionPool conPool, String procedure, String[] params ) {
        DBConnect dbc = new DBConnect( conPool );
        List data = dbc.executeProcedure(procedure, params) ;

        return createStringFromSqlResults( data );
    }

    public static int sqlUpdateProcedure( ConnectionPool conPool, String procedure, String[] params ) {
        DBConnect dbc = new DBConnect( conPool );
        int res = dbc.executeUpdateProcedure(procedure, params);
        return res;
    }

    public static int sqlUpdateQuery(ConnectionPool conPool, String sqlStr, String[] params) {
        DBConnect dbc = new DBConnect(conPool) ;
        dbc.setSQLString(sqlStr,params);
        int res = dbc.executeUpdateQuery();
        return res ;
    }

    public static String[] sqlQuery(ConnectionPool conPool, String sqlQuery, String[] parameters) {
        DBConnect dbc = new DBConnect( conPool );
        dbc.setSQLString(sqlQuery, parameters);
        List data = (List)dbc.executeQuery();
        return createStringArrayFromSqlResults(data) ;
    }

    public static String sqlQueryStr(ConnectionPool conPool, String sqlStr, String[] params) {
        DBConnect dbc = new DBConnect(conPool);
        dbc.setSQLString(sqlStr, params);
        List data = (List) dbc.executeQuery();
        return createStringFromSqlResults(data);
    }

    public static Hashtable sqlQueryHash(ConnectionPool conPool, String sqlQuery, String[] params) {
        DBConnect dbc = new DBConnect(conPool);
        dbc.setSQLString(sqlQuery, params);

        List data = (List) dbc.executeQuery();
        String[] meta = (String[]) dbc.getMetaData();
        return createHashtableOfStringArrayFromSqlResults(data, meta) ;
    }

    public static String[][] sqlQueryMulti(ConnectionPool conPool, String sqlQuery, String[] params) {
        DBConnect dbc = new DBConnect(conPool);
        dbc.setSQLString(sqlQuery, params);

        List data = (List) dbc.executeQuery();
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

    private static String createStringFromSqlResults(List data) {
        if (data != null && !data.isEmpty()) {
            return
                    null != data.get(0)
                    ? data.get(0).toString()
                    : null;
        } else {
            return null;
        }
    }

    private static String[][] create2DStringArrayFromSqlResults(List data, int columns) {
        if (columns == 0)
            return new String[0][0];

        int rows = data.size() / columns;

        String result[][] = new String[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result[i][j] =
                        null != data.get(i * columns + j)
                        ? data.get(i * columns + j).toString()
                        : null;
            }

        }

        return result;
    }

    private static String[] createStringArrayFromSqlResults(List data) {
        if (data != null) {
            String result[] = new String[data.size()];
            for (int i = 0; i < data.size(); i++) {
                result[i] =
                        null != data.get(i)
                        ? data.get(i).toString()
                        : null;
            }
            return result;
        } else {
            return null;
        }
    }

    private static Hashtable createHashtableOfStringArrayFromSqlResults(List data, String[] meta) {
        Hashtable result = new Hashtable(meta.length, 0.5f);

        if (data.size() > 0) {

            for (int i = 0; i < meta.length; i++) {
                String temp_str[] = new String[data.size() / meta.length];
                int counter = 0;


                for (int j = i; j < data.size(); j += meta.length) {
                    temp_str[counter++] =
                            null != data.get(j)
                            ? data.get(j).toString()
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
