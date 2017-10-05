package com.imcode.imcms.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public final class DB {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final JdbcTemplate jdbcTemplate;

    public DB(DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    public List<String> tables() {
        return jdbcTemplate.query("SHOW TABLES", (resultSet, rowNum) -> resultSet.getString(1));
    }

    public boolean isNew() {
        return tables().isEmpty();
    }

    public Version getVersion() {
        String versionStr = jdbcTemplate.queryForObject("SELECT concat(major, '.', minor) FROM database_version", String.class);

        return Version.parse(versionStr);
    }

    private synchronized void updateVersion(Version newVersion) {
        logger.info("Updating database version from {} to {}.", getVersion(), newVersion);
        jdbcTemplate.update("UPDATE database_version SET major=?, minor=?", newVersion.getMajor(), newVersion.getMinor());
    }

    private String scriptFullPath(Schema schema, String script) {
        return String.join("/", schema.getScriptsDir(), script);
    }

    private Version update(Schema schema) {
        Version dbVersion = getVersion();
        Version requiredVersion = schema.getVersion();

        switch (dbVersion.compareTo(requiredVersion)) {
            case 0:
                logger.info("Database is up-to-date.");
                return dbVersion;

            case 1:
                String errorMsg = String.format(
                        "Unexpected database version. Database version %s is greater that required version %s",
                        requiredVersion, requiredVersion);

                logger.error(errorMsg);
                throw new IllegalStateException(errorMsg);

            default:
                logger.info("Database have to be updated. Required version: {}, database version: {}", requiredVersion, dbVersion);
                List<Diff> diffs = schema.diffsChainFrom(dbVersion);

                if (diffs.isEmpty()) {
                    final String errorMessage = String.format("No diff is available for version %s", dbVersion);
                    logger.error(errorMessage);
                    throw new IllegalStateException(errorMessage);
                }

                logger.info("The following diff will be applied: {}.", diffs);

                diffs.forEach(diff -> {
                    runScripts(diff.getScripts().stream().map(script -> scriptFullPath(schema, script)));
                    updateVersion(diff.getTo());
                });

                Version updatedDbVersion = getVersion();
                logger.info("Database has been updated. Database version is {}.", updatedDbVersion);

                return updatedDbVersion;
        }

    }

    public synchronized Version prepare(Schema schema) {
        logger.info("Preparing database.");

        if (isNew()) {
            logger.info("Database is empty and need to be initialized.");
            logger.info("The following init will be applied: {}", schema.getInit());

            runScripts(schema.getInit().getScripts().stream().map(script -> scriptFullPath(schema, script)));
            updateVersion(schema.getInit().getVersion());

            logger.info("Database has been initialized.");
        }

        return update(schema);
    }

    private synchronized void runScripts(Stream<String> scripts) {
        jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
            IBatisPatchedScriptRunner scriptRunner = new IBatisPatchedScriptRunner(connection);
            scriptRunner.setAutoCommit(false);
            scriptRunner.setStopOnError(true);

            scripts.forEach(script -> {
                logger.debug("Running script {}.", script);
                try (FileReader reader = new FileReader(script)) {
                    scriptRunner.runScript(reader);
                } catch (IOException e) {
                    throw new RuntimeSqlException(e);
                }
            });

            return null;
        });
    }

}