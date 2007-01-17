package com.imcode.imcms.db;

import com.imcode.db.Database;
import com.imcode.db.DatabaseException;
import com.imcode.db.commands.SqlQueryDatabaseCommand;
import com.imcode.imcms.db.refactoring.DatabasePlatform;
import com.imcode.imcms.db.refactoring.model.Type;
import com.imcode.imcms.db.refactoring.model.SimpleColumn;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.File;

import imcode.util.io.FileUtility;

public class TemplateNamesUpgrade implements DatabaseUpgrade {

    private final File templatesDirectory;

    public TemplateNamesUpgrade(File templatesDirectory) {
        this.templatesDirectory = templatesDirectory;
    }

    public void upgrade(Database database) throws DatabaseException {
        if (!templatesDirectory.isDirectory()) {
            throw new DatabaseException("Templates directory does not exist: "+templatesDirectory, null);
        }
        database.execute(new SqlQueryDatabaseCommand("SELECT template_id, simple_name FROM templates", null, new ResultSetHandler() {
            public Object handle(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    int templateId = rs.getInt("template_id");
                    String templateName = rs.getString("simple_name");
                    File templateFile = new File(templatesDirectory, templateId+".html");
                    if (templateFile.exists()) {
                        templateFile.renameTo(new File(templatesDirectory, FileUtility.escapeFilename(templateName).replaceAll("_005f", "_" )+".html")) ;
                    }
                }
                return null;
            }
        })) ;
        DatabasePlatform dp = DatabasePlatform.getInstance(database);
        SimpleColumn templateNameColumn = new SimpleColumn("template_name", Type.VARCHAR, 255, SimpleColumn.Required.NOT_NULL);
        dp.alterColumn("templates_cref", "template_id", templateNameColumn) ;
        updateTemplateName(dp, "templates_cref", "template_name");

        dp.alterColumn("text_docs", "template_id", templateNameColumn) ;
        updateTemplateName(dp, "text_docs", "template_name");

        templateNameColumn.setRequired(SimpleColumn.Required.NULL);

        alterTemplateNameColumn(dp, "text_docs", "default_template_1", templateNameColumn);
        alterTemplateNameColumn(dp, "text_docs", "default_template_2", templateNameColumn);
        alterTemplateNameColumn(dp, "text_docs", "default_template", templateNameColumn);

        dp.dropTable("templates");
    }

    private void alterTemplateNameColumn(DatabasePlatform dp, String tableName, String columnName,
                                         SimpleColumn templateNameColumn) {
        templateNameColumn.setName(columnName) ;
        dp.alterColumn(tableName, columnName, templateNameColumn) ;
        updateTemplateName(dp, tableName, columnName);
    }

    private void updateTemplateName(DatabasePlatform dp, String tableName, String columnName) {
        dp.update("UPDATE "+tableName+" SET " + columnName + " = (SELECT simple_name FROM templates WHERE template_id = " + tableName + "."+columnName +")");
    }

}
