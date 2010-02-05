package com.imcode.imcms.db;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.Platform;
import org.apache.log4j.Logger;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import com.imcode.db.DatabaseException;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.commands.TransactionDatabaseCommand;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.db.commands.InsertIntoTableDatabaseCommand;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.StringTokenizer;

import imcode.server.Imcms;

/**
 * Extends database upgrade functionality with script based option.
 */
public class ScriptBasedUpgrade extends ImcmsDatabaseUpgrade {

    private final static Logger LOG = Logger.getLogger(ScriptBasedUpgrade.class);

    private String databaseVendor;
    private DatabaseVersion currentVersion;

    public ScriptBasedUpgrade(String databaseVendor,
                              DatabaseVersion currentVersion,
                              Database ddl) {
        super(ddl);
        this.databaseVendor = databaseVendor;
        this.currentVersion = currentVersion;
    }

    public void upgrade(final com.imcode.db.Database db) throws DatabaseException {
        LOG.info("Running script-based schema upgrade");
        
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document doc = builder.parse(new File(Imcms.getPath(), "WEB-INF/sql/diff/schema-upgrade.xml"));

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            final NodeList scriptList = (NodeList)xpath.evaluate(
                    String.format("/schema-upgrade/diff[@version > %s]/vendor[@name = '%s']/script",
                    currentVersion, databaseVendor), doc, XPathConstants.NODESET);

            final int updatesCount = scriptList.getLength();

            if (updatesCount == 0) {
                LOG.info(String.format
                        ("No schema upgrade required for vendor %s. Current version is %s.", databaseVendor, currentVersion));
                return;
            }

            db.execute(new TransactionDatabaseCommand() {
                public Object executeInTransaction(DatabaseConnection connection) throws DatabaseException {
                    File scriptFile = null;
                    try {
                        Element scriptElement = null;
                        for (int i = 0; i < updatesCount; i++) {
                            scriptElement = (Element)scriptList.item(i);
                            String script = scriptElement.getAttribute("location");
                            scriptFile = new File(Imcms.getPath(), "WEB-INF/sql/diff/" + script);
                            FileInputStream fs = new FileInputStream(scriptFile);
                            InputStreamReader reader = new InputStreamReader(fs);
                            String sql = IOUtils.toString(reader);
                            reader.close();
                            Platform platform = DatabaseUtils.getPlatform(connection);
                            platform.evaluateBatch(sql, false);
                        }

                        if (scriptElement != null) {
                            Element diffElement = (Element)scriptElement.getParentNode().getParentNode();
                            String version = diffElement.getAttribute("version");
                            StringTokenizer st = new StringTokenizer(version, ".");
                            DatabaseVersion dv = new DatabaseVersion(
                                    Integer.parseInt(st.nextToken()),
                                    Integer.parseInt(st.nextToken()));
                            setDatabaseVersion(db, dv);

                            currentVersion = dv;
                        }
                    }
                    catch (Exception ex) {
                        LOG.fatal("Failed to run script based upgrade.", ex);
                        throw new DatabaseException("Failed to run the script " + scriptFile.getName(), ex);
                    }

                    return null;
                }
            });

            LOG.info("Database upgraded to version " + currentVersion);

        } catch (Exception ex) {
            LOG.fatal("Failed to run script based upgrade.", ex);
        }
    }

    private void setDatabaseVersion(com.imcode.db.Database database, DatabaseVersion newVersion) {
        Integer rowsUpdated = (Integer) database.execute(new SqlUpdateCommand("UPDATE database_version SET major = ?, minor = ?",
                                                                              new Object[] {
                                                                                      newVersion.getMajorVersion(),
                                                                                      newVersion.getMinorVersion() }));
        if (0 == rowsUpdated) {
            database.execute(new InsertIntoTableDatabaseCommand("database_version", new Object[][] {
                    { "major", newVersion.getMajorVersion() },
                    { "minor", newVersion.getMinorVersion() },
            })) ;
        }
    }
}
