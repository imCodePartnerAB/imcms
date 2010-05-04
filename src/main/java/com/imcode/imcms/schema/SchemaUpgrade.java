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
import javax.xml.validation.Validator;
import java.io.*;
import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;

import com.ibatis.common.jdbc.ScriptRunner;

/**
 * Intended to run incremental imcms database schema upgrades.
 *
 * If run against empty schema then the first diff must create databse_version table.
 *
 * TODO: ?Add vendor support?
 */
public final class SchemaUpgrade {

    /** Selects schema version number string in format 'major.minor'. */
	private static final String MYSQL__SELECT_SCHEMA_VERSION = "SELECT concat(" +
			"cast(major as char), '.', cast(minor as char)) " +
			"FROM database_version";

    /** Selects database tables names. */
	private static final String MYSQL__SHOW_TABLES = "SHOW TABLES";

    /** Logger. */
    private final Logger logger = Logger.getLogger(getClass());


    /** Configuration xml. */
    private final String xml;


    /** Upgrade scripts directory. */
    private final File scriptsDir;


    /** Databse vendor */
    private final Vendor vendor;
    

    /**
     * Creates new Upgrade instance.
     *
     * @param xml configuration xml.
     * @param scriptsDir directory which contains upgrade scripts.
     * 
     */
    private SchemaUpgrade(String xml, File scriptsDir) {
        this.xml = xml;
        this.scriptsDir = scriptsDir;
        this.vendor = Vendor.mysql;
    }


    /**
     * Creates and returns a new configured SchemaUpgrade instance.
     *
     * @throws SchemaUpgradeConfException in case of connfiguration error.
     */
    public static SchemaUpgrade createInstance(File confXmlFile, File confXsdFile, File scriptsDir) {
        if (!confXmlFile.isFile()) {
            throw new SchemaUpgradeConfException("Schema upgrade XML file '" + confXmlFile.getAbsolutePath() + "' does not exist.");
        }


        if (!confXsdFile.isFile()) {
            throw new SchemaUpgradeConfException("Schema upgrade XSD file '" + confXsdFile.getAbsolutePath() + "' does not exist.");
        }


        if (!scriptsDir.isDirectory()) {
            throw new SchemaUpgradeConfException("Schema diff scripts dir '" + scriptsDir.getAbsolutePath() + "' does not exist.");
        }


        String xml = SchemaUpgrade.validateAndReadUpgradeConf(confXmlFile, confXsdFile);
        
        return new SchemaUpgrade(xml, scriptsDir);
    }
    

    /**
     * Validates confXmlFile using xsdFile and returns confXsdFile content.
     *
     * @param confXmlFile xml file.
     * @param confXsdFile xsd schema file.
     *
     * @return xmlFile content.
     * @throws SchemaUpgradeConfException in case of an error.
     */
    public static String validateAndReadUpgradeConf(File confXmlFile, File confXsdFile) {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        try {
            javax.xml.validation.Schema schema = schemaFactory.newSchema(confXsdFile);
            Validator validator = schema.newValidator();

            validator.validate(new StreamSource(confXmlFile));
        } catch (Exception e) {
            throw new SchemaUpgradeConfException("Schema upgrade conf file validation failed.", e);
        }

        try {
            return FileUtils.readFileToString(confXmlFile);
        } catch (IOException e) {
            throw new SchemaUpgradeConfException("Failed to read schema upgrade conf file.", e);
        }
    }    


    /**
     * Upgrades schema by .
     *
     * @param connection database connection.
     */
    @SuppressWarnings("unchecked")
    public void upgrade(Connection connection) throws SQLException {
        logger.info("Beginning schema upgrade.");

        Collection<String> tablesNames = getTablesNames(connection);

        final double schemaVersion = tablesNames.size() == 0
                ? 0.0
                : getSchemaVersion(connection);

        logger.info(String.format("Current schema version: %s.", schemaVersion));

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


        ScriptRunner scriptRunner = new ScriptRunner(connection, false, true); // autocommit, stopOnErrors       
        PreparedStatement stmt = connection.prepareStatement("UPDATE database_version SET major = ?, minor = ?");

        // todo: add log writers.
        //scriptRunner.setLogWriter();
        //scriptRunner.setErrorLogWriter();

        try {
            for (Diff diff: diffs) {
                runDiffScripts(scriptRunner, diff);

                // check if schema version contains proper values.
                getSchemaVersion(connection);

                stmt.setInt(1, diff.version.major);
                stmt.setInt(2, diff.version.minor);

                stmt.executeUpdate();
            }

            logger.info(String.format("Finished schema upgrade. Current schema version: %s.",
                    getSchemaVersion(connection)));
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
     * Returns database schema version.
     *
     * @param connection database connection.
     * @return database schema version.
     * @throws SchemaUpgradeException if table database_version does not exist or contains invalid records count.
     */
    public static double getSchemaVersion(Connection connection)
    throws SQLException {
        Collection<String> tablesNames = getTablesNames(connection);

        boolean tableExists = CollectionUtils.exists(tablesNames, new Predicate() {
            public boolean evaluate(Object o) {
                return ((String)o).equalsIgnoreCase("database_version");
            }
        });

        if (!tableExists) {
            throw new SchemaUpgradeException("Schema is corrupted - table database_version does not exist.");
        }

        List<Double> schemaVersions = getSchemaVersions(connection);
        int count = schemaVersions.size();

        if (count == 0) {
            throw new SchemaUpgradeException(
                    "Schema is corrupted - table database_version is empty. It must contain exactly 1 record.");
        } else if (count > 1) {
            throw new SchemaUpgradeException(
                    String.format("Schema is corrupted - it must contain exactly 1 record - %s found.", count));
        }

        return schemaVersions.get(0);
    }
    

    /**
     * Returns schema versions list.
     * Valid versions list contains one item. Non valid contains zero or more than one items.
     *
     * @param connection database connection.
     * @return current schema versions list.
     */
    public static List<Double> getSchemaVersions(Connection connection)
    throws SQLException {
        Statement stmt = connection.createStatement();
        List<Double> versionList = new LinkedList<Double>();

        try {
            ResultSet rs = stmt.executeQuery(MYSQL__SELECT_SCHEMA_VERSION);

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