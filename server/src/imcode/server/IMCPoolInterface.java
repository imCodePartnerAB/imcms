package imcode.server ;

public interface IMCPoolInterface {

    // Send a procedure to the database and return a string array
    String[] sqlProcedure(String procedure, String[] params);

    // Send a procedure to the database and return a multistring array
	String[][] sqlProcedureMulti(String procedure, String[] params);

    String sqlProcedureStr( String procedure, String[] params );

    int sqlUpdateProcedure( String procedure, String[] params );

    String[][] sqlQueryMulti( String sqlQuery, String[] params );

}
