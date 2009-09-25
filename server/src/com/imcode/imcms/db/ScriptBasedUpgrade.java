package com.imcode.imcms.db;

import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.Platform;
import org.apache.log4j.Logger;
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
import java.io.File;
import java.io.FileInputStream;
import java.util.StringTokenizer;

import imcode.server.Imcms;

/**
 * Extends database upgrade functionality with script based option. New mechanism uses 'db.schema.version'
 * property. This property defines a set of scripts that must be applied to database to upgrade it to
 * actual version.
 */
public class ScriptBasedUpgrade extends ImcmsDatabaseUpgrade {

    private final static Logger LOG = Logger.getLogger(ScriptBasedUpgrade.class);

    private String databaseVendor;
    private DatabaseVersion currentVersion;
    private DatabaseVersion requiredVersion;

    public ScriptBasedUpgrade(String databaseVendor,
                              DatabaseVersion currentVersion,
                              DatabaseVersion requiredVersion,
                              Database ddl) {
        super(ddl);
        this.databaseVendor = databaseVendor;
        this.currentVersion = currentVersion;
        this.requiredVersion = requiredVersion;
    }

    public void upgrade(com.imcode.db.Database database) throws DatabaseException {
        final com.imcode.db.Database db = database;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document doc = builder.parse(new File(Imcms.getPath(), "WEB-INF/sql/diff/schema-upgrade.xml"));

            NodeList diffList = doc.getElementsByTagName("diff");

            for (int i = 0; i < diffList.getLength(); i++)
            {
                Element diffElement = (Element)diffList.item(i);
                String version = diffElement.getAttribute("version");

                StringTokenizer st = new StringTokenizer(version, ".");
                final DatabaseVersion dv = new DatabaseVersion(
                        Integer.parseInt(st.nextToken()),
                        Integer.parseInt(st.nextToken()));

                if (dv.compareTo(currentVersion) > 0 && dv.compareTo(requiredVersion) <= 0) {
                    final String[] scripts = getScripts(diffElement, databaseVendor);

                    LOG.info("Upgrading database to version " + version);

                    database.execute(new TransactionDatabaseCommand() {
                        public Object executeInTransaction(DatabaseConnection connection) throws DatabaseException {
                            File scriptFile = null;
                            try {
                                for (String script : scripts) {
                                    scriptFile = new File(Imcms.getPath(), "WEB-INF/sql/diff/" + script);
                                    FileInputStream fs = new FileInputStream(scriptFile);

                                    byte[] b = new byte[fs.available()];
                                    fs.read(b);
                                    fs.close();

                                    Platform platform = DatabaseUtils.getPlatform(connection);
                                    platform.evaluateBatch(new String(b), true);
                                }

                                setDatabaseVersion(db, dv);
                                currentVersion = dv;
                            }
                            catch (Exception ex) {
                                LOG.fatal("Failed to run script based upgrade.", ex);
                                throw new DatabaseException("Failed to run the script " + scriptFile.getName(), ex);
                            }
                            
                            return null;
                        }
                    });
                }
            }

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

    private String[] getScripts(Element diffElement, String vendor)  {
        NodeList vendorList = diffElement.getElementsByTagName("vendor");
        Element vendorElement = null;
        for (int i = 0; i < vendorList.getLength(); i++) {
            Element v = (Element)vendorList.item(i);
            if (v.getAttribute("name").equalsIgnoreCase(vendor)) {
                vendorElement = v;
                break;
            }
        }

        if (vendorElement != null) {
            NodeList scriptList = vendorElement.getElementsByTagName("script");
            String[] result = new String[scriptList.getLength()];

            for (int i = 0; i < scriptList.getLength(); i++) {
                Element scriptElement = (Element)scriptList.item(i);
                result[i] = scriptElement.getAttribute("location");
            }

            return result;
        }
        else {
            return new String[0];
        }
    }
}
