package com.imcode.imcms.api;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Class giving access to the database connection used by {@link ContentManagementSystem}
 */
public class DatabaseService {

    private DataSource dataSource;

    /**
     * Constructs DatabaseService with the given DataSource
     * @param dataSource DataSource to get connection from
     */
    public DatabaseService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Get a databaseconnection from the connectionpool.
     *
     * <strong>IMPORTANT</strong>: Do not forget to make sure that the connection is closed
     * (inside a "finally" block!), otherwise it won't be returned to the pool.
     *
     * @return a {@link Connection} from the connectionpool.
     * @throws SQLException if there was a problem getting the connection.
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection() ;
    }
    
    /**
     * Get the underlying DataSource.
     * 
     * @return a {@link DataSource}.
     */
    public DataSource getDataSource() {
        return dataSource ;
    }
}
