package imcode.server.db;

import org.apache.log4j.Logger;

class MimerDatabaseService extends DatabaseService {
    public MimerDatabaseService( String hostName, int port, String databaseName, String user, String password ) {
        super( Logger.getLogger( MimerDatabaseService.class ) );

        // log.debug( "Creating a 'Mimer' database service");
        String jdbcDriver = "com.mimer.jdbc.Driver";
        String jdbcUrl = "jdbc:mimer://";
        String serverUrl = jdbcUrl + hostName + ":" + port + "/" + databaseName;
        String serverName = "Mimer test server";

        super.initConnectionPoolAndSQLProcessor( serverName, jdbcDriver, serverUrl, user, password );

        // This is only needed to be done the first time
        // sqlProcessor.executeUpdate("CREATE DATABANK " + databaseName , null);
    }

    void backup( String fullPathToBackupFile ) {
            SQLProcessor.SQLTransaction transaction = sqlProcessor.startTransaction();
            try {
                transaction.executeUpdate( "START BACKUP", null );
                transaction.executeUpdate( "CREATE BACKUP IN '" + fullPathToBackupFile + "' FOR DATABANK test", null );
                transaction.executeUpdate( "COMMIT BACKUP", null );
            } catch( Exception ex ) {
                transaction.rollback();
            }
            transaction.commit();
    }

    /*
    Not working, but I'm tired, whaiting a few days before desiding on if this is needed.
    void restore( String fullPathToDatabaseFile, String fullPathToBackupFile )  throws IOException {
        SQLProcessor.SQLTransaction transaction = sqlProcessor.startTransaction();
        sqlProcessor.executeUpdate( "SET DATABASE OFFLINE", null );
        copyFile( fullPathToDatabaseFile, fullPathToBackupFile );
        sqlProcessor.executeUpdate( "ALTER DATABANK test RESTORE USING '" + fullPathToDatabaseFile + "'", null );
        transaction.commit();
        sqlProcessor.executeUpdate( "SET DATABASE ONLINE RESET LOG", null );
    }

    private void copyFile( String to, String from ) throws IOException {
        // Create channel on the source
        FileChannel srcChannel = new FileInputStream(from).getChannel();

        // Create channel on the destination
        FileChannel dstChannel = new FileOutputStream(to).getChannel();

        // Copy file contents from source to destination
        dstChannel.transferFrom(srcChannel, 0, srcChannel.size());

        // Close the channels
        srcChannel.close();
        dstChannel.close();
    }
    */
}
