package imcode.server.document;

import com.imcode.db.Database;
import com.imcode.db.DatabaseException;
import com.imcode.db.commands.InsertIntoTableDatabaseCommand;
import com.imcode.db.commands.SqlQueryCommand;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.db.handlers.CollectionHandler;
import com.imcode.db.handlers.RowTransformer;
import com.imcode.imcms.db.StringArrayArrayResultSetHandler;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.functors.NotPredicate;
import org.apache.commons.collections4.functors.NullPredicate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@SuppressWarnings("unused")
public class TemplateMapper {

    private static final String SPROC_GET_TEMPLATES_IN_GROUP = "GetTemplatesInGroup";
    private static final String SPROC_GET_TEMPLATE_GROUPS_FOR_USER = "GetTemplategroupsForUser";
    private static final String SPROC_GET_TEMPLATE_GROUPS = "GetTemplateGroups";

    private Database database;
    private ImcmsServices services;

    public TemplateMapper(ImcmsServices service) {
        database = service.getDatabase();
        services = service;
    }

    private static File getTemplateDirectory() {
        return new File(Imcms.getPath(), "WEB-INF/templates/text");
    }

    public void addTemplateToGroup(TemplateDomainObject template, TemplateGroupDomainObject templateGroup) {
        database.execute(new SqlUpdateCommand("INSERT INTO imcms_template_group_crossref (group_id,template_name) VALUES(?,?)",
                new Object[]{templateGroup.getId(), template.getName()}));
    }

    public List<TemplateDomainObject> getAllTemplatesExceptOne(TemplateDomainObject template) {
        List<TemplateDomainObject> allTemplates = getAllTemplates();
        allTemplates.remove(template);
        return allTemplates;
    }

    public String createHtmlOptionListOfTemplateGroups(TemplateGroupDomainObject selectedTemplateGroup) {
        final TemplateGroupDomainObject[] templateGroups = getAllTemplateGroups();
        return createHtmlOptionListOfTemplateGroups(Arrays.asList(templateGroups), selectedTemplateGroup);
    }

    public String createHtmlOptionListOfTemplateGroups(Collection<TemplateGroupDomainObject> templateGroups,
                                                       TemplateGroupDomainObject selectedTemplateGroup) {
        StringBuilder temps = new StringBuilder();
        for (TemplateGroupDomainObject templateGroup : templateGroups) {
            boolean selected = null != selectedTemplateGroup && selectedTemplateGroup.equals(templateGroup);
            temps.append("<option value=\"")
                    .append(templateGroup.getId())
                    .append("\"")
                    .append(selected ? " selected" : "")
                    .append(">")
                    .append(templateGroup.getName())
                    .append("</option>");
        }
        return temps.toString();
    }

    public String createHtmlOptionListOfTemplates(Iterable<TemplateDomainObject> templates,
                                                  TemplateDomainObject selectedTemplate) {
        StringBuilder temps = new StringBuilder();
        for (TemplateDomainObject template : templates) {

            boolean selected = selectedTemplate != null && selectedTemplate.equals(template);
            final String templateName = StringEscapeUtils.escapeHtml4(template.getNameAdmin());
            temps.append("<option value=\"")
                    .append(templateName)
                    .append("\"")
                    .append(selected ? " selected" : "")
                    .append(">")
                    .append(templateName)
                    .append("</option>");
        }
        return temps.toString();
    }

    /**
     * Delete template from db/disk
     *
     * @param template Template object to delete
     */
    public void deleteTemplate(TemplateDomainObject template) {

        database.execute(new SqlUpdateCommand("delete from imcms_template_group_crossref where template_name = ?", new String[]{template.getName()}));
        database.execute(new SqlUpdateCommand("delete from template where template_name = ?", new String[]{template.getName()}));

        // test if template exists and delete it
        File f = getTemplateFile(template);
        if (f.exists()) {
            f.delete();
        }
    }

    private File getTemplateFile(TemplateDomainObject template) {
        return new File(getTemplateDirectory(), template.getFileName());
    }

    public void deleteTemplateGroup(int grp_id) {
        final Object[] parameters1 = new String[]{"" + grp_id};
        database.execute(new SqlUpdateCommand("delete from imcms_template_group_crossref where group_id = ?", parameters1));
        final Object[] parameters = new String[]{"" + grp_id};
        database.execute(new SqlUpdateCommand("delete from imcms_template_group where group_id = ?", parameters));
    }

    public TemplateGroupDomainObject[] getAllTemplateGroups() {
        final Object[] parameters = new String[]{};
        String[][] sprocResult = services.getProcedureExecutor().executeProcedure(
                SPROC_GET_TEMPLATE_GROUPS, parameters, new StringArrayArrayResultSetHandler()
        );
        return createTemplateGroupsFromSqlResult(sprocResult);
    }

    public TemplateGroupDomainObject[] getAllTemplateGroupsAvailableForUserOnDocument(UserDomainObject user,
                                                                                      int metaId) {
        String[][] sprocResult = sprocGetTemplateGroupsForUser(user, metaId);
        return createTemplateGroupsFromSqlResult(sprocResult);
    }

    public Set<Integer> getAllTemplateGroupIds() {
        return database.execute(new SqlQueryCommand<>(
                "SELECT group_id FROM imcms_template_group",
                null,
                new CollectionHandler<>(new HashSet<>(), new IntRowTransformer())
        ));
    }

    public List<TemplateDomainObject> getAllTemplates() {
        File[] templateFiles = getTemplateDirectory().listFiles(pathname -> {
            String fileName = pathname.getName().toLowerCase();
            return pathname.isFile() && (fileName.endsWith(".jsp") || fileName.endsWith(".jspx") || fileName.endsWith(".html"));
        });

        if (templateFiles == null) return Collections.emptyList();

        UserDomainObject udo = Imcms.getUser();
        SortedSet<TemplateDomainObject> templates = new TreeSet<>();

        for (File templateFile : templateFiles) {
            String nameWithoutExtension = StringUtils.substringBeforeLast(templateFile.getName(), ".");
            Boolean isHidden = database.execute(new SqlQueryCommand<>(
                    "select is_hidden from template where template_name = ?",
                    new String[]{nameWithoutExtension},
                    Utility.SINGLE_BOOLEAN_HANDLER
            ));

            if (isHidden == null) {
                database.execute(new SqlUpdateCommand(
                        "INSERT INTO template (template_name,is_hidden) VALUES(?,?)",
                        new Object[]{nameWithoutExtension, false}
                ));
                isHidden = false;
            }
            TemplateDomainObject tdo = new TemplateDomainObject(nameWithoutExtension, templateFile.getName(), isHidden);
            if (udo.isSuperAdmin() || !tdo.isHidden()) {
                templates.add(tdo);
            }
        }
        return new ArrayList<>(templates);
    }

    public int getCountOfDocumentsUsingTemplate(TemplateDomainObject template) {
        String queryResult = database.execute(new SqlQueryCommand<>("SELECT COUNT(meta_id)" + " FROM text_docs"
                + " WHERE template_name = ?", new String[]{"" + template.getName()}, Utility.SINGLE_STRING_HANDLER));
        return Integer.parseInt(queryResult);
    }

    // todo: fix meta headline
    public DocumentDomainObject[] getDocumentsUsingTemplate(TemplateDomainObject template) {
        String[][] temp = database.execute(new SqlQueryCommand<>(
                "select td.meta_id, td.meta_id as meta_headline from text_docs td join meta m on td.meta_id = m.meta_id where template_name = ? order by td.meta_id",
                new String[]{template.getName()},
                Utility.STRING_ARRAY_ARRAY_HANDLER
        ));
        DocumentMapper documentMapper = services.getDocumentMapper();
        DocumentDomainObject[] documents = new DocumentDomainObject[temp.length];

        for (int i = 0; i < documents.length; i++) {
            int documentId = Integer.parseInt(temp[i][0]);
            documents[i] = documentMapper.getDefaultDocument(documentId);
            documentMapper.getDocument(documentId);
        }
        return documents;
    }

    public TemplateDomainObject getTemplateByName(String templateName) {
        String[] extensions = new String[]{"jsp", "jspx", "html"};

        for (String extension : extensions) {
            String templateFileName = templateName + "." + extension;
            File templateFile = new File(getTemplateDirectory(), templateFileName);

            if (templateFile.exists()) {
                String nameWithoutExtension = StringUtils.substringBeforeLast(templateFile.getName(), ".");
                Boolean isHidden = database.execute(new SqlQueryCommand<>(
                        "select is_hidden from template where template_name = ?",
                        new String[]{nameWithoutExtension},
                        Utility.SINGLE_BOOLEAN_HANDLER
                ));

                if (isHidden == null) {
                    database.execute(new SqlUpdateCommand(
                            "INSERT INTO template (template_name,is_hidden) VALUES(?,?)",
                            new Object[]{nameWithoutExtension, false}
                    ));
                    isHidden = false;
                }
                return new TemplateDomainObject(templateName, templateFileName, isHidden);
            }
        }
        return null;
    }

    public TemplateGroupDomainObject getTemplateGroupById(int templateGroupId) {
        String sqlStr = "SELECT group_id,group_name FROM imcms_template_group WHERE group_id = ?";
        final Object[] parameters = new String[]{"" + templateGroupId};
        String[] queryResult = database.execute(new SqlQueryCommand<>(sqlStr, parameters, Utility.STRING_ARRAY_HANDLER));

        return createTemplateGroupFromSqlResultRow(queryResult);
    }

    public TemplateGroupDomainObject getTemplateGroupByName(String name) {
        final Object[] parameters = new String[]{name};
        String[] sqlResultRow = database.execute(new SqlQueryCommand<>(
                "select group_id, group_name from imcms_template_group where group_name = ?",
                parameters,
                Utility.STRING_ARRAY_HANDLER
        ));
        return createTemplateGroupFromSqlResultRow(sqlResultRow);
    }

    //TODO check this for getting templates depending by its availability for userGroup
    public Collection<TemplateDomainObject> getTemplatesInGroup(TemplateGroupDomainObject templateGroup) {
        final String[] params = new String[]{String.valueOf(templateGroup.getId())};
        final List<TemplateDomainObject> groupTemplates = services.getProcedureExecutor().executeProcedure(
                SPROC_GET_TEMPLATES_IN_GROUP, params, newArrayListHandler()
        );
        return CollectionUtils.select(groupTemplates, NotPredicate.notPredicate(NullPredicate.nullPredicate()));
    }

    private CollectionHandler<TemplateDomainObject, List<TemplateDomainObject>> newArrayListHandler() {
        return new CollectionHandler<>(new ArrayList<>(), newRowTransformer());
    }

    private RowTransformer<TemplateDomainObject> newRowTransformer() {
        return new RowTransformer<TemplateDomainObject>() {
            public TemplateDomainObject createObjectFromResultSetRow(ResultSet resultSet) throws SQLException {
                return getTemplateByName(resultSet.getString(1));
            }

            public Class<TemplateDomainObject> getClassOfCreatedObjects() {
                return TemplateDomainObject.class;
            }
        };
    }

    public boolean renameTemplate(String templateName, String newNameForTemplate) {
        if (null != getTemplateByName(newNameForTemplate)) {
            return false;
        }
        for (TemplateDomainObject template = getTemplateByName(templateName);
             null != template;
             template = getTemplateByName(templateName)) {
            File templateFile = getTemplateFile(template);
            String extension = StringUtils.substringAfterLast(template.getFileName(), ".");
            String newFilename = newNameForTemplate + "." + extension;

            if (templateFile.renameTo(new File(getTemplateDirectory(), newFilename))) {
                replaceAllUsagesOfTemplate(template, new TemplateDomainObject(
                        newNameForTemplate, newFilename, template.isHidden()
                ));
            }
        }
        return true;
    }

    public List<TemplateDomainObject> getTemplatesNotInGroup(TemplateGroupDomainObject templateGroup) {
        Collection<TemplateDomainObject> templatesInGroup = getTemplatesInGroup(templateGroup);
        Set<TemplateDomainObject> allTemplates = new HashSet<>(getAllTemplates());
        allTemplates.removeAll(templatesInGroup);
        List<TemplateDomainObject> templatesNotInGroup = new ArrayList<>(allTemplates);
        Collections.sort(templatesNotInGroup);
        return templatesNotInGroup;
    }

    public void removeTemplateFromGroup(TemplateDomainObject template, TemplateGroupDomainObject templateGroup) {
        database.execute(new SqlUpdateCommand("DELETE FROM imcms_template_group_crossref WHERE group_id = ? AND template_name = ?",
                new String[]{"" + templateGroup.getId(), template.getName()}));
    }

    public boolean updateAvailability(String templateName, boolean isAvailable) {
        try {
            database.execute(new SqlUpdateCommand(
                    "UPDATE template SET is_hidden = ? WHERE template_name = ?",
                    new Object[]{isAvailable, templateName}
            ));
            return true;
        } catch (DatabaseException e) {
            return false;
        }
    }

    public String createHtmlOptionListOfTemplatesWithDocumentCount(UserDomainObject user) {
        StringBuilder htmlStr = new StringBuilder();
        List<TemplateDomainObject> templates = getAllTemplates();

        for (TemplateDomainObject template : templates) {
            List<String> tags = new ArrayList<>();
            tags.add("#template_name#");
            tags.add(StringEscapeUtils.escapeHtml4(template.getNameAdmin()));
            tags.add("#docs#");
            tags.add("" + getCountOfDocumentsUsingTemplate(template));
            tags.add("#template_id#");
            tags.add(StringEscapeUtils.escapeHtml4(template.getNameAdmin()));
            htmlStr.append(services.getAdminTemplate("template_list_row.html", user, tags));
        }
        return htmlStr.toString();
    }

    private String[][] sprocGetTemplateGroupsForUser(UserDomainObject user,
                                                     int meta_id) {
        final Object[] parameters = new String[]{String.valueOf(meta_id), String.valueOf(user.getId())};
        return services.getProcedureExecutor().executeProcedure(
                SPROC_GET_TEMPLATE_GROUPS_FOR_USER, parameters, new StringArrayArrayResultSetHandler()
        );
    }

    public void renameTemplateGroup(TemplateGroupDomainObject templateGroup, String newName) {
        String sqlStr = "update imcms_template_group\n"
                + "set group_name = ?\n"
                + "where group_id = ?\n";
        final Object[] parameters = new String[]{newName, "" + templateGroup.getId()};
        database.execute(new SqlUpdateCommand(sqlStr, parameters));
    }

    public void replaceAllUsagesOfTemplate(TemplateDomainObject template, TemplateDomainObject newTemplate) {
        if (null != template && null != newTemplate) {
            DocumentDomainObject[] documentsUsingTemplate = getDocumentsUsingTemplate(template);
            String sqlUpdateStr1 = "update text_docs set template_name = ? where template_name = ?";
            String sqlUpdateStr2 = "update template set template_name = ? where template_name = ?";
            final Object[] parameters = new String[]{"" + newTemplate.getName(), "" + template.getName()};
            database.execute(new SqlUpdateCommand(sqlUpdateStr1, parameters));
            database.execute(new SqlUpdateCommand(sqlUpdateStr2, parameters));
            for (DocumentDomainObject document : documentsUsingTemplate) {
                services.getDocumentMapper().invalidateDocument(document);
            }
        }
    }

    private class IntRowTransformer implements RowTransformer<Integer> {

        public Integer createObjectFromResultSetRow(ResultSet resultSet) throws SQLException {
            return resultSet.getInt(1);
        }

        public Class<Integer> getClassOfCreatedObjects() {
            return Integer.class;
        }
    }

    private TemplateGroupDomainObject createTemplateGroupFromSqlResultRow(String[] sqlResultRow) {
        if (0 == sqlResultRow.length) {
            return null;
        }

        int templateGroupId = Integer.parseInt(sqlResultRow[0]);
        String templateGroupName = sqlResultRow[1];
        return new TemplateGroupDomainObject(templateGroupId,
                templateGroupName);
    }

    private TemplateGroupDomainObject[] createTemplateGroupsFromSqlResult(String[][] sprocResult) {
        TemplateGroupDomainObject[] templateGroups = new TemplateGroupDomainObject[sprocResult.length];
        for (int i = 0; i < sprocResult.length; i++) {
            templateGroups[i] = createTemplateGroupFromSqlResultRow(sprocResult[i]);
        }
        return templateGroups;
    }

    public void createTemplateGroup(String name) {
        database.execute(new InsertIntoTableDatabaseCommand("imcms_template_group", new Object[][]{
                {"group_name", name},
        }));
    }

    public int saveTemplate(String name, String file_name, InputStream templateData, boolean overwrite, boolean isHidden) {

        File f = new File(getTemplateDirectory(), name + "." + StringUtils.substringAfterLast(file_name, "."));
        if (f.exists() && !overwrite) {
            return -1;
        }

        try {
            FileOutputStream fw = new FileOutputStream(f);
            IOUtils.copy(templateData, fw);
            fw.flush();
            fw.close();
            database.execute(new SqlUpdateCommand("INSERT INTO template (template_name,is_hidden) VALUES(?,?)", new Object[]{name, isHidden}));
        } catch (IOException e) {
            return -2;
        }

        //  0 = OK
        // -1 = file exist
        // -2 = write error
        return 0;
    }

    public String getTemplateData(String templateName) throws IOException {
        return services.getFileCache().getCachedFileString(getTemplateFile(getTemplateByName(templateName)));
    }

    @SuppressWarnings("unchecked")
    public List<TemplateGroupDomainObject> getTemplateGroups(Set<Integer> templateGroupIds) {
        List<TemplateGroupDomainObject> allowedTemplateGroups = new ArrayList<>(templateGroupIds.size());
        for (Integer allowedTemplateGroupId : templateGroupIds) {
            TemplateGroupDomainObject templateGroup = getTemplateGroupById(allowedTemplateGroupId);
            if (null != templateGroup) {
                allowedTemplateGroups.add(templateGroup);
            }
        }
        Collections.sort(allowedTemplateGroups);
        return allowedTemplateGroups;
    }

    public boolean templateGroupContains(TemplateGroupDomainObject templateGroup,
                                         TemplateDomainObject template) {
        Collection<TemplateDomainObject> templates = getTemplatesInGroup(templateGroup);
        for (TemplateDomainObject t : templates) {
            if (t.getName().equals(template.getName())) {
                return true;
            }
        }
        return false;
    }
}
