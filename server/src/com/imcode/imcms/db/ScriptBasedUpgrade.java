package com.imcode.imcms.db;

import org.apache.commons.lang.NotImplementedException;
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

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.StringTokenizer;

import imcode.server.Imcms;

/**
 * Extends database upgrade functionality with script based option.
 */
public class ScriptBasedUpgrade extends DatabaseTypeSpecificUpgrade {

    private final static Logger LOG = Logger.getLogger(ScriptBasedUpgrade.class);

    private DatabaseVersion currentVersion;

    public ScriptBasedUpgrade(Database ddl, DatabaseVersion currentVersion) {
        super(ddl);
        this.currentVersion = currentVersion;
    }

    @Override
    public void upgradeMssql(com.imcode.db.Database database) throws DatabaseException {
        upgrade(database, "mssql");
    }

    @Override
    public void upgradeMysql(com.imcode.db.Database database) throws DatabaseException {
        upgrade(database, "mysql");
    }

    @Override
    public void upgradeOther(com.imcode.db.Database database) throws DatabaseException {
        String msg = String.format("Database schema upgrade is not implemented for platform %s.", database);
        LOG.error(msg);
        throw new NotImplementedException(msg);
    }

    /**
     *
     * @param database
     * @param vendorName used to select upgrade scripts from schema upgrade configuration file. 
     * @throws DatabaseException
     */
    private void upgrade(final com.imcode.db.Database database, final String vendorName) throws DatabaseException {
        LOG.info("Running script-based schema upgrade");

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document doc = builder.parse(new File(Imcms.getPath(), "WEB-INF/sql/diff/schema-upgrade.xml"));

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            xpath.setXPathFunctionResolver(new XPathFunctionResolver() {
                final QName dbVersionCompareQName = new QName("imcms", "db-version-compare");
                final XPathFunction dbVersionCompare = new XPathFunction() {
                    @Override
                    public Integer evaluate(List args) throws XPathFunctionException {
                        String[] l = ((String)args.get(0)).split("\\.");
                        String[] r = ((String)args.get(1)).split("\\.");

                        return new DatabaseVersion(Integer.parseInt(l[0]), Integer.parseInt(l[1])).compareTo(
                               new DatabaseVersion(Integer.parseInt(r[0]), Integer.parseInt(r[1])));
                    }
                };

                @Override
                public XPathFunction resolveFunction(QName qname, int arity) {
                    return qname.equals(dbVersionCompareQName) && arity == 2 ? dbVersionCompare : null;
                }
            });

            final NodeList scriptList = (NodeList)xpath.evaluate(
                    String.format("/schema-upgrade/diff[imcms:db-version-compare(string(@version), '%s') > 0]/vendor[@name = '%s']/script",
                    currentVersion, vendorName), doc, XPathConstants.NODESET);

            final int updatesCount = scriptList.getLength();

            if (updatesCount == 0) {
                LOG.info(String.format
                        ("No schema upgrade required for vendor %s. Current version is %s.", vendorName, currentVersion));
                return;
            }

            database.execute(new TransactionDatabaseCommand() {
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
                            setDatabaseVersion(database, dv);

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
            String errMsg = "Failed to run script based upgrade.";
            LOG.fatal(errMsg, ex);
            throw new DatabaseException(errMsg, ex);
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
