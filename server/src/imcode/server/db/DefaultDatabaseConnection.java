package imcode.server.db;

import org.apache.commons.lang.UnhandledException;

import java.sql.*;

public class DefaultDatabaseConnection implements DatabaseConnection {

    private Connection connection;

    public DefaultDatabaseConnection( Connection connection ) {
        this.connection = connection;
    }

    public void executeUpdate( String sql, String[] parameters ) {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement( sql );
            setPreparedStatementParameters( preparedStatement, parameters );
            preparedStatement.executeUpdate();
        } catch ( SQLException se ) {
            throw new UnhandledException( se );
        }
    }

    public Number executeUpdateAndGetGeneratedKey( String sql, String[] parameters ) {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
            setPreparedStatementParameters( preparedStatement, parameters );
            preparedStatement.executeUpdate();
            ResultSet generatedKeysResultSet = preparedStatement.getGeneratedKeys();
            Number result = null ;
            if ( generatedKeysResultSet.next() ) {
                result = (Number)generatedKeysResultSet.getObject( 1 );
            }
            return result ;
        } catch ( SQLException se ) {
            throw new UnhandledException( se );
        }
    }

    private void setPreparedStatementParameters( PreparedStatement preparedStatement, String[] parameters ) throws SQLException {
        for ( int i = 0; i < parameters.length; i++ ) {
            String parameter = parameters[i];
            preparedStatement.setString( i + 1, parameter );
        }
    }
}
