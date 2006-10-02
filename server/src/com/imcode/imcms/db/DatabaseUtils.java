package com.imcode.imcms.db;

import com.imcode.db.DatabaseConnection;
import imcode.server.Imcms;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.io.DatabaseIO;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DatabaseUtils {

    private DatabaseUtils() {
    }

    public static Database getWantedDdl() throws IOException {
        DatabaseIO io = new DatabaseIO();
        io.setValidateXml(false);
        return io.read(new FileReader(new File(Imcms.getPath(), "WEB-INF/sql/ddl.xml")));
    }

    public static Platform getPlatform(DatabaseConnection databaseConnection) {
        return PlatformFactory.createNewPlatformInstance(new SingleConnectionDataSource(databaseConnection.getConnection()));
    }
}
