package com.imcode.imcms.domain.service.api;

import com.imcode.db.DatabaseException;
import com.imcode.imcms.api.DatabaseService;
import com.imcode.imcms.api.VersionData;
import com.imcode.imcms.domain.service.VersionDataService;
import com.imcode.imcms.servlet.Version;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static ucar.httpservices.HTTPAuthStore.log;

/**
 * General service for get data versions
 */
@Service
public class DefaultVersionDataService implements VersionDataService {

    private final DatabaseService databaseService;

    @Value("/WEB-INF/version.txt")
    private Path versionFile;

    @Value("schema.xml")
    private Path schema;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    public DefaultVersionDataService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public VersionData getVersionData() {
        VersionData versionData = new VersionData();
        versionData.setImcmsVersion(getImcmsVersion());
        versionData.setJavaVersion(getJavaVersion());
        versionData.setDbVersion(getDbVersion());
        versionData.setServerInfo(getServerInfo());
        versionData.setDbNameVersion(getProductNameVersionData());

        return versionData;
    }

    private String getImcmsVersion() {
        try {
            return Files.readAllLines(versionFile).toString();
        } catch (IOException e) {
            final String errorMessage = "Error reading imcms version.";
            log.error(errorMessage);
            return null;
        }
    }

    private String getJavaVersion() {
        return System.getProperty("java.vm.vendor") +
                " " + System.getProperty("java.vm.name") +
                " " + System.getProperty("java.vm.version") +
                " Java version: " + System.getProperty("java.specification.version");
    }

    private String getDbVersion() { //todo: rewrite get required db version!
        return "Required DB schema version: " + StringUtils.defaultString(Version.getRequiredDbVersion(), "Not Found");
    }


    private String getServerInfo() {
        return servletContext.getServerInfo();
    }

    private String getProductNameVersionData() {
        try {
            DatabaseMetaData metaData = databaseService.getConnection().getMetaData();
            return metaData.getDatabaseProductName() + " " + metaData.getDatabaseProductVersion();
        } catch (SQLException e) {
            final String errorMessage = "Failed get database version and product name !";
            log.error(errorMessage);
            throw new DatabaseException(errorMessage, e);
        }
    }
}
