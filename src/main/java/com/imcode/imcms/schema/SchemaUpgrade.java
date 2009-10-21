package com.imcode.imcms.schema;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.log4j.Logger;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import java.io.*;
import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;

import com.ibatis.common.jdbc.ScriptRunner;

/**
 * Intended to perform incremental database schema upgrades.
 *
 * If upgrading empty schema then the first diff must create databse_version table.
 */
public final class SchemaUpgrade {

    /** SQL select statement which returns schema version number string in format 'major.minor' */
	public static final String SQL__SELECT_SCHEMA_VERSION = "SELECT concat(" +
			"cast(major as char), '.', cast(minor as char)) " +
			"FROM database_version";

    /** MySql show tables command. */
	public static final String MYSQL__SHOW_TABLES = "SHOW TABLES";

    /** Logger. */
    private final Logger logger = Logger.getLogger(getClass());

    /** Upgrade scripts directory. */
    private final File scriptsDir;

    /** Configuration xml. */
    private final String xml;


    /**
     * Creates new Upgrade instance.
     *
     * @param confXMLFile schema upgrade configuration file.
     * @param confXSDFile schema upgrade validation file.
     * @param scriptsDir directory which contains upgrade scripts.
     * 
     */
    public SchemaUpgrade(File confXMLFile, File confXSDFile, File scriptsDir) {
        if (!confXMLFile.isFile()) {
            throw new SchemaUpgradeException("confXMLFile '" + confXMLFile.getAbsolutePath() + "' not found.");
        }


        if (!confXSDFile.isFile()) {
            throw new SchemaUpgradeException("confXSDFile '" + confXSDFile.getAbsolutePath() + "' not found.");
        }


        if (!scriptsDir.isDirectory()) {
            throw new SchemaUpgradeException("scriptDir '" + scriptsDir.getAbsolutePath() + "' not found.");
        }

        this.xml = validateAndGetContent(confXMLFile, confXSDFile);
        this.scriptsDir = scriptsDir;
    }


    /**
     * Upgrades schema by .
     *
     * @param connection database connection.
     */
    @SuppressWarnings("unchecked")
    public void upgrade(Connection connection) throws SQLException {
        logger.info("Beginning schema upgrade.");
        // TODO: ?find_vendor.
        Vendor vendor = Vendor.mysql;
        final double schemaVersion = getValidatedSchemaVersion(connection);

        logger.info(String.format("Current schema version: %s, database vendor: %s.", schemaVersion, vendor));

        Collection<Diff> diffs = DiffBuilder.buildDiffs(xml, vendor);
        diffs = CollectionUtils.select(diffs, new Predicate() {
            public boolean evaluate(Object o) {
                return ((Diff)o).version.number > schemaVersion;
            }
        });

        if (diffs.size() == 0) {
            logger.info("Schema is up-to-date. Upgrade is not required.");
            return;
        }


        // autocommit, stopOnErrors
        ScriptRunner scriptRunner = new ScriptRunner(connection, false, true);        
        PreparedStatement stmt = connection.prepareStatement("UPDATE database_version SET major = ?, minor = ?");

        // todo: add log writers.
        //scriptRunner.setLogWriter();
        //scriptRunner.setErrorLogWriter();

        try {
            for (Diff diff: diffs) {
                runDiffScripts(scriptRunner, diff);

                // check if schema version contains proper values.
                getValidatedSchemaVersion(connection);

                stmt.setInt(1, diff.version.major);
                stmt.setInt(2, diff.version.minor);

                stmt.executeUpdate();
            }

            logger.info(String.format("Schema upgrade is finished. Current schema version: %s.",
                    getValidatedSchemaVersion(connection)));
        } catch (IOException e) {
            throw new SchemaUpgradeException(e);
        } finally {
            stmt.close();
        }
    }


    /**
     * Iteratively runs diff scripts.
     *
     * @param scriptRunner
     * @param diff
     * @throws IOException
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    private void runDiffScripts(ScriptRunner scriptRunner, Diff diff)
    throws IOException, SQLException {
        logger.info("Processing diff " + diff + ".");
        
        Collection<File> files = CollectionUtils.collect(diff.scriptsLocations, new Transformer() {
            public File transform(Object o) {
                return new File(scriptsDir, (String)o);
            }
        });

        for (File file: files) {
            Reader reader = null;

            try {
                logger.info("\\--running script: " + file.getAbsolutePath() + ".");
                reader = new FileReader(file);

                scriptRunner.runScript(reader);
            } finally {
                IOUtils.closeQuietly(reader);
            }
        }

        logger.info("\\--finished diff processing.");
    }


    /**
     * Validates xmlFile using xsdFile and returns xmlFile content.
     *
     * @param xmlFile xml file.
     * @param xsdFile xsd schema file.
     *
     * @return xmlFile content.
     */
    public static String validateAndGetContent(File xmlFile, File xsdFile) {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        try {
            Schema schema = schemaFactory.newSchema(xsdFile);
            Validator validator = schema.newValidator();

            validator.validate(new StreamSource(xmlFile));

            return FileUtils.readFileToString(xmlFile);
        } catch (Exception e) {
            throw new SchemaUpgradeException(e);
        }
    }


    /**
     * Returns collection of schema tables names.
     *
     * @param connection databse connection.
     * @return collection of schema tables names.
     */
    public static Collection<String> getTablesNames(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
        Collection<String> tables = new LinkedList<String>();

        try {
            ResultSet rs = stmt.executeQuery(MYSQL__SHOW_TABLES);

            while (rs.next()) {
                tables.add(rs.getString(1));
            }

            rs.close();
        } finally {
            stmt.close();
        }

        return tables;
    }


    /**
     * Validates and returns schema version.
     */
    public static double getValidatedSchemaVersion(Connection connection)
    throws SQLException {
        Collection<String> tablesNames = getTablesNames(connection);

        if (tablesNames.size() == 0) {
            return 0.0;
        }

        boolean tableExists = CollectionUtils.exists(tablesNames, new Predicate() {
            public boolean evaluate(Object o) {
                return ((String)o).equalsIgnoreCase("database_version");
            }
        });

        if (!tableExists) {
            throw new SchemaUpgradeException("Schema is corrupted - table database_version does not exists.");
        }

        List<Double> schemaVersionList = getSchemaVersionAsList(connection);
        int count = schemaVersionList.size();

        if (count == 0) {
            throw new SchemaUpgradeException("Schema is corrupted - table database_version is empty. It must contain exactley 1 record.");
        } else if (count > 1) {
            throw new SchemaUpgradeException("Schema is corrupted - it must contain exactley 1 record - " + count + " found.");
        }

        return schemaVersionList.get(0);
    }
    

    /**
     * Returns current schema version as a list for future investigations.
     *
     * @param connection database connection.
     * @return current schema version as a list.
     */
    public static List<Double> getSchemaVersionAsList(Connection connection)
    throws SQLException {
        Statement stmt = connection.createStatement();
        List<Double> versionList = new LinkedList<Double>();

        try {
            ResultSet rs = stmt.executeQuery(SQL__SELECT_SCHEMA_VERSION);

            while (rs.next()) {
                versionList.add(Double.parseDouble(rs.getString(1)));
            }

            rs.close();
        } finally {
            stmt.close();
        }

        return versionList;
    }
}