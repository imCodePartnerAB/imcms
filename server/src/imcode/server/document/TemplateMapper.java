package imcode.server.document;

import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.ImcmsServices;
import imcode.server.db.Database;
import imcode.server.user.UserDomainObject;
import org.apache.commons.io.CopyUtils;
import org.apache.commons.lang.ArrayUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TemplateMapper {

    private static final String SPROC_GET_TEMPLATES_IN_GROUP = "GetTemplatesInGroup";
    private static final String SPROC_GET_TEMPLATE_GROUPS_FOR_USER = "GetTemplategroupsForUser";
    private static final String SPROC_GET_TEMPLATE_GROUPS = "GetTemplateGroups";

    private Database database;
    private ImcmsServices services;

    public TemplateMapper( ImcmsServices service ) {
        this.database = service.getDatabase();
        this.services = service ;
    }

    public void addTemplateToGroup( TemplateDomainObject template, TemplateGroupDomainObject templateGroup ) {
        String sqlStr = "INSERT INTO templates_cref (group_id,template_id) VALUES(?,?)";
        database.executeUpdateQuery( sqlStr, new String[]{"" + templateGroup.getId(), "" + template.getId()} );
    }

    public TemplateDomainObject[] getArrayOfAllTemplatesExceptOne( TemplateDomainObject template ) {
        TemplateDomainObject[] allTemplates = getAllTemplates();
        List allTemplatesExceptOne = new ArrayList( allTemplates.length - 1 );
        for ( int i = 0; i < allTemplates.length; i++ ) {
            if ( !template.equals( allTemplates[i] ) ) {
                allTemplatesExceptOne.add( allTemplates[i] );
            }
        }
        return (TemplateDomainObject[])allTemplatesExceptOne.toArray(
                new TemplateDomainObject[allTemplatesExceptOne.size()] );
    }

    public String createHtmlOptionListOfTemplateGroups( TemplateGroupDomainObject selectedTemplateGroup ) {
        TemplateGroupDomainObject[] templateGroups = services.getTemplateMapper().getAllTemplateGroups();
        return createHtmlOptionListOfTemplateGroups( templateGroups, selectedTemplateGroup );
    }

    public String createHtmlOptionListOfTemplateGroups( TemplateGroupDomainObject[] templateGroups,
                                                        TemplateGroupDomainObject selectedTemplateGroup ) {
        String temps = "";
        for ( int i = 0; i < templateGroups.length; i++ ) {
            TemplateGroupDomainObject templateGroup = templateGroups[i];
            boolean selected = null != selectedTemplateGroup && selectedTemplateGroup.equals( templateGroup );
            temps += "<option value=\"" + templateGroup.getId() + "\"" + ( selected ? " selected" : "" ) + ">"
                     + templateGroup.getName() + "</option>";
        }
        return temps;
    }

    public String createHtmlOptionListOfTemplates( TemplateDomainObject[] templates,
                                                   TemplateDomainObject selectedTemplate ) {
        Set demoTemplateIds = new HashSet();
        demoTemplateIds.addAll( Arrays.asList( getDemoTemplateIds() ) );
        String temps = "";
        for ( int i = 0; i < templates.length; i++ ) {
            TemplateDomainObject template = templates[i];
            boolean selected = selectedTemplate != null && selectedTemplate.equals( template );
            boolean hasDemoTemplate = demoTemplateIds.contains( "" + template.getId() );
            temps += "<option value=\""
                     + template.getId()
                     + "\""
                     + ( selected ? " selected" : "" )
                     + ">"
                     + ( hasDemoTemplate ? "*" : "" )
                     + template.getName() + "</option>";
        }
        return temps;
    }

    public String createHtmlOptionListOfTemplatesWithDocumentCount( UserDomainObject user ) {
        String htmlStr;
        htmlStr = "";
        TemplateMapper templateMapper = services.getTemplateMapper();
        TemplateDomainObject[] templates = templateMapper.getAllTemplates();
        for ( int i = 0; i < templates.length; i++ ) {
            TemplateDomainObject template = templates[i];
            List tags = new ArrayList();
            tags.add( "#template_name#" );
            tags.add( template.getName() );
            tags.add( "#docs#" );
            tags.add( "" + templateMapper.getCountOfDocumentsUsingTemplate( template ) );
            tags.add( "#template_id#" );
            tags.add( "" + template.getId() );
            htmlStr += services.getAdminTemplate( "template_list_row.html", user, tags );
        }
        return htmlStr;
    }

    /**
     * delete template from db/disk
     */
    public void deleteTemplate( TemplateDomainObject template ) {

        String sqlStr = "delete from templates_cref where template_id = ?";
        database.executeUpdateQuery( sqlStr, new String[]{"" + template.getId()} );

        // delete from database
        sqlStr = "delete from templates where template_id = ?";
        database.executeUpdateQuery( sqlStr, new String[]{"" + template.getId()} );

        // test if template exists and delete it
        File f = new File( services.getConfig().getTemplatePath() + "/text/" + template.getId() + ".html" );
        if ( f.exists() ) {
            f.delete();
        }
    }

    public void deleteTemplateGroup( int grp_id ) {
        database.executeUpdateQuery( "delete from templates_cref where group_id = ?", new String[]{"" + grp_id} );
        database.executeUpdateQuery( "delete from templategroups where group_id = ?", new String[]{"" + grp_id} );
    }

    public TemplateGroupDomainObject[] getAllTemplateGroups() {
        String[][] sprocResult = database.execute2dArrayProcedure( SPROC_GET_TEMPLATE_GROUPS, new String[]{} );
        return createTemplateGroupsFromSqlResult( sprocResult );
    }

    public TemplateGroupDomainObject[] getAllTemplateGroupsAvailableForUserOnDocument( UserDomainObject user,
                                                                                       int metaId ) {
        String[][] sprocResult = sprocGetTemplateGroupsForUser( database, user, metaId );
        return createTemplateGroupsFromSqlResult( sprocResult );
    }

    public TemplateDomainObject[] getAllTemplates() {
        String sqlStr = "select template_id,template_name,simple_name from templates order by simple_name";
        String[][] queryResult = database.execute2dArrayQuery( sqlStr, new String[0] );

        return createTemplatesFromSqlResult( queryResult );
    }

    private int getCountOfDocumentsUsingTemplate( TemplateDomainObject template ) {
        String queryResult = database.executeStringQuery(
                "SELECT COUNT(meta_id)" + " FROM text_docs" + " WHERE template_id = ?", new String[]{
                    "" + template.getId()
                } );
        return Integer.parseInt( queryResult );
    }

    public DocumentDomainObject[] getDocumentsUsingTemplate( TemplateDomainObject template ) {
        String[][] temp = database.execute2dArrayQuery(
                "select td.meta_id, meta_headline from text_docs td join meta m on td.meta_id = m.meta_id where template_id = ? order by td.meta_id",
                new String[]{"" + template.getId()} );
        DocumentMapper documentMapper = services.getDefaultDocumentMapper();
        DocumentDomainObject[] documents = new DocumentDomainObject[temp.length];
        for ( int i = 0; i < documents.length; i++ ) {
            int documentId = Integer.parseInt( temp[i][0] );
            documents[i] = documentMapper.getDocument( documentId );
        }
        return documents;
    }

    public TemplateDomainObject getTemplateById( int template_id ) {
        String sqlStr = "select template_id,template_name,simple_name from templates where template_id = ?";
        String[] queryResult = database.executeArrayQuery( sqlStr, new String[]{"" + template_id} );

        return createTemplateFromSqlResultRow( queryResult );
    }

    public TemplateDomainObject getTemplateByName( String templateSimpleName ) {
        String sqlStr = "select template_id,template_name,simple_name from templates where simple_name = ?";
        String[] queryResult = database.executeArrayQuery( sqlStr, new String[]{templateSimpleName} );

        return createTemplateFromSqlResultRow( queryResult );
    }

    public TemplateGroupDomainObject getTemplateGroupById( int templateGroupId ) {
        String sqlStr = "SELECT group_id,group_name FROM templategroups WHERE group_id = ?";
        String[] queryResult = database.executeArrayQuery( sqlStr, new String[]{"" + templateGroupId} );

        return createTemplateGroupFromSqlResultRow( queryResult );
    }

    public TemplateGroupDomainObject getTemplateGroupByName( String name ) {
        String[] sqlResultRow = database.executeArrayQuery(
                "select group_id, group_name from templategroups where group_name = ?", new String[]{name} );
        return createTemplateGroupFromSqlResultRow( sqlResultRow );
    }

    public TemplateDomainObject[] getTemplatesInGroup( TemplateGroupDomainObject templateGroup ) {
        String[][] templateData = database.execute2dArrayProcedure( SPROC_GET_TEMPLATES_IN_GROUP, new String[]{"" + templateGroup.getId()} );
        TemplateDomainObject[] templates = new TemplateDomainObject[templateData.length];
        for ( int i = 0; i < templateData.length; i++ ) {
            int templateId = Integer.parseInt( templateData[i][0] );
            templates[i] = getTemplateById( templateId );
        }
        return templates;
    }

    public TemplateDomainObject[] getTemplatesNotInGroup( TemplateGroupDomainObject templateGroup ) {
        List templatesInGroup = Arrays.asList( getTemplatesInGroup( templateGroup ) );
        Set allTemplates = new HashSet( Arrays.asList( getAllTemplates() ) );
        allTemplates.removeAll( templatesInGroup );
        TemplateDomainObject[] templatesNotInGroup = (TemplateDomainObject[])allTemplates.toArray( new TemplateDomainObject[allTemplates.size()] );
        Arrays.sort(templatesNotInGroup) ;
        return templatesNotInGroup;
    }

    public void removeTemplateFromGroup( TemplateDomainObject template, TemplateGroupDomainObject templateGroup ) {
        String sqlStr = "DELETE FROM templates_cref WHERE group_id = ? AND template_id = ?";
        database.executeUpdateQuery( sqlStr, new String[]{"" + templateGroup.getId(), "" + template.getId()} );
    }

    public void renameTemplate( TemplateDomainObject template, String newNameForTemplate ) {
        String sqlStr = "UPDATE templates SET simple_name = ? WHERE template_id = ?";
        database.executeUpdateQuery( sqlStr, new String[]{newNameForTemplate, "" + template.getId()} );
    }

    public void renameTemplateGroup( TemplateGroupDomainObject templateGroup, String newName ) {
        String sqlStr = "update templategroups\n"
                        + "set group_name = ?\n"
                        + "where group_id = ?\n";
        database.executeUpdateQuery( sqlStr, new String[]{newName, "" + templateGroup.getId()} );
    }

    public void replaceAllUsagesOfTemplate( TemplateDomainObject template, TemplateDomainObject newTemplate ) {
        if ( null != template && null != newTemplate ) {
            String sqlStr = "update text_docs set template_id = ? where template_id = ?";
            database.executeUpdateQuery( sqlStr, new String[]{"" + newTemplate.getId(), "" + template.getId()} );
        }
    }

    private static String[][] sprocGetTemplateGroupsForUser( Database service, UserDomainObject user,
                                                             int meta_id ) {
        return service.execute2dArrayProcedure( SPROC_GET_TEMPLATE_GROUPS_FOR_USER,
                                                new String[]{String.valueOf( meta_id ), String.valueOf( user.getId() )} );
    }

    private TemplateDomainObject createTemplateFromSqlResultRow( String[] sqlResultRow ) {
        if ( 0 == sqlResultRow.length ) {
            return null;
        }
        int templateId = Integer.parseInt( sqlResultRow[0] );
        String templateName = sqlResultRow[1];
        String simpleName = sqlResultRow[2];
        return new TemplateDomainObject( templateId, simpleName, templateName);
    }

    private TemplateGroupDomainObject createTemplateGroupFromSqlResultRow( String[] sqlResultRow ) {
        if ( 0 == sqlResultRow.length ) {
            return null;
        }

        int templateGroupId = Integer.parseInt( sqlResultRow[0] );
        String templateGroupName = sqlResultRow[1];
        return new TemplateGroupDomainObject( templateGroupId,
                                              templateGroupName );
    }

    private TemplateGroupDomainObject[] createTemplateGroupsFromSqlResult( String[][] sprocResult ) {
        TemplateGroupDomainObject[] templateGroups = new TemplateGroupDomainObject[sprocResult.length];
        for ( int i = 0; i < sprocResult.length; i++ ) {
            templateGroups[i] = createTemplateGroupFromSqlResultRow( sprocResult[i] );
        }
        return templateGroups;
    }

    private TemplateDomainObject[] createTemplatesFromSqlResult( String[][] queryResult ) {
        TemplateDomainObject[] templates = new TemplateDomainObject[queryResult.length];
        for ( int i = 0; i < queryResult.length; i++ ) {
            templates[i] = createTemplateFromSqlResultRow( queryResult[i] );
        }
        return templates;
    }

    public void createTemplateGroup( String name ) {
        database.executeUpdateQuery( "declare @new_id int\n"
                                     + "select @new_id = max(group_id)+1 from templategroups\n"
                                     + "insert into templategroups values(@new_id,?)", new String[]{name} );
    }

    public boolean templateGroupContainsTemplate( TemplateGroupDomainObject templateGroup,
                                                  TemplateDomainObject template ) {
        return ArrayUtils.contains( getTemplatesInGroup( templateGroup ), template ) ;
    }

    public void saveDemoTemplate( int template_id, InputStream data, String suffix ) throws IOException {

        deleteDemoTemplate( template_id );

        FileOutputStream fw = new FileOutputStream( services.getConfig().getTemplatePath() + "/text/demo/" + template_id + "."
                                                    + suffix );
        CopyUtils.copy(data, fw) ;
        fw.flush();
        fw.close();
    }

    public void deleteDemoTemplate( int template_id ) throws IOException {

        File demoTemplateDirectory = new File( new File( services.getConfig().getTemplatePath(), "text" ), "demo" );
        File[] demoTemplates = demoTemplateDirectory.listFiles();
        for ( int i = 0; i < demoTemplates.length; i++ ) {
            File demoTemplate = demoTemplates[i];
            String demoTemplateFileName = demoTemplate.getName();
            if ( demoTemplateFileName.startsWith( template_id + "." ) ) {
                if ( !demoTemplate.delete() ) {
                    throw new IOException( "fail to deleate" );
                }
            }
        }
    }

    public int saveTemplate( String name, String file_name, InputStream templateData, boolean overwrite, String lang_prefix ) {
        String sqlStr;
        // check if template exists
        sqlStr = "select template_id from templates where simple_name = ?";
        String templateId = database.executeStringQuery( sqlStr, new String[] {name} );
        if ( null == templateId ) {

            // get new template_id
            sqlStr = "select max(template_id) + 1 from templates\n";
            templateId = database.executeStringQuery( sqlStr, new String[0] );

            sqlStr = "insert into templates values (?,?,?,?,0,0,0)";
            database.executeUpdateQuery( sqlStr, new String[] {templateId, file_name, name,
                    lang_prefix} );
        } else { //update
            if ( !overwrite ) {
                return -1;
            }

            sqlStr = "update templates set template_name = ? where template_id = ?";
            database.executeUpdateQuery( sqlStr, new String[] {file_name, templateId} );
        }

        File f = new File( services.getConfig().getTemplatePath(), "text/" + templateId + ".html" );

        try {
            FileOutputStream fw = new FileOutputStream( f );
            CopyUtils.copy(templateData, fw);
            fw.flush();
            fw.close();

        } catch ( IOException e ) {
            return -2;
        }

        //  0 = OK
        // -1 = file exist
        // -2 = write error
        return 0;
    }

    public Object[] getDemoTemplate( int template_id ) throws IOException {
        StringBuffer str = new StringBuffer();
        BufferedReader fr = null;
        String suffix = null;
        String[] suffixList =
                {"jpg", "jpeg", "gif", "png", "html", "htm"};

        for ( int i = 0; i < suffixList.length; i++ ) { // Looking for a template with one of six suffixes
            File fileObj = new File( services.getConfig().getTemplatePath(), "/text/demo/" + template_id + "." + suffixList[i] );
            long date = 0;
            long fileDate = fileObj.lastModified();
            if ( fileObj.exists() && fileDate > date ) {
                // if a template was not properly removed, the template
                // with the most recens modified-date is returned
                try {
                    fr = new BufferedReader( new InputStreamReader( new FileInputStream( fileObj ), "8859_1" ) );
                    suffix = suffixList[i];
                } catch ( IOException e ) {
                    return null; //Could not read
                }
            } // end IF
        } // end FOR

        char[] buffer = new char[4096];
        try {
            int read;
            while ( ( read = fr.read( buffer, 0, 4096 ) ) != -1 ) {
                str.append( buffer, 0, read );
            }
            fr.close();
        } catch ( IOException e ) {
            return null;
        } catch ( NullPointerException e ) {
            return null;
        }

        return new Object[]{suffix, str.toString().getBytes( "8859_1" )}; //return the buffer
    }

    public String[] getDemoTemplateIds() {
        File demoDir = new File( services.getConfig().getTemplatePath() + "/text/demo/" );

        File[] file_list = demoDir.listFiles( new NonEmptyFileFilter() );

        String[] name_list = new String[file_list.length];

        if ( file_list != null ) {
            for ( int i = 0; i < name_list.length; i++ ) {
                String filename = file_list[i].getName();
                int dot = filename.indexOf( "." );
                name_list[i] = dot > -1 ? filename.substring( 0, dot ) : filename;
            }
        } else {
            return new String[0];

        }

        return name_list;

    }

    public String getTemplateData( int template_id ) throws IOException {
        return services.getFileCache().getCachedFileString( new File( services.getConfig().getTemplatePath(), "/text/" + template_id + ".html" ) );
    }

    private static class NonEmptyFileFilter implements FileFilter {

        public boolean accept( File file ) {
            return file.length() > 0;
        }
    }
}
