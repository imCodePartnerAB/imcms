package imcode.server;

public enum DatabaseVendor {
    MYSQL("com.mysql.jdbc.Driver"), 
    MSSQL("net.sourceforge.jtds.jdbc.Driver");

    private final String driverClass;

    private DatabaseVendor(String driverClass) {
        this.driverClass = driverClass;
    }

    public static DatabaseVendor findByDriverClass(String klass) {
        for (DatabaseVendor vendor : DatabaseVendor.values()) {
            if (vendor.driverClass.equals(klass)) {
                return vendor;
            }
        }

        return null;
    }
}
