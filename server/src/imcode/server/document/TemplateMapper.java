package imcode.server.document;

import imcode.server.IMCService;
import imcode.server.db.DBConnect;
import imcode.server.user.UserDomainObject;

import java.util.Vector;
import java.util.Iterator;

public class TemplateMapper {
    private static final String SPROC_GET_TEMPLATES_IN_GROUP = "GetTemplatesInGroup";
    private static final String SPROC_GET_TEMPLATE_GROUPS_FOR_USER = "GetTemplategroupsForUser";

    private IMCService service;

    public TemplateMapper( IMCService service ) {
        this.service = service;
    }


    // todo make sure all sproc and sql mehtods are private, start with making them not public.
    /** @return the template for a text-internalDocument, or null if the internalDocument isn't a text-internalDocument. **/
/*
    public TemplateDomainObject getTemplate( int meta_id ) {
        String[] textdoc_data = TemplateMapper.sprocGetTextDocData( service, meta_id );
        if( textdoc_data.length < 2 ) {
            return null;
        }
        return new TemplateDomainObject( Integer.parseInt( textdoc_data[0] ), textdoc_data[1] );
    }
 */


    public static Vector sprocGetTemplateGroupsForUser( DBConnect dbc, UserDomainObject user, int meta_id ) {
        String sqlStr = SPROC_GET_TEMPLATE_GROUPS_FOR_USER;
        String[] sqlAry2 = {String.valueOf( meta_id ), String.valueOf( user.getUserId() )};
        dbc.setProcedure( sqlStr, sqlAry2 );
        Vector templategroups = dbc.executeProcedure();
        dbc.clearResultSet();
        return templategroups;
    }

    public static Vector sprocGetTemplatesInGroup( DBConnect dbc, int selected_group ) {
        String sqlStr = SPROC_GET_TEMPLATES_IN_GROUP;
        dbc.setProcedure( sqlStr, String.valueOf( selected_group ) );
        Vector templates = dbc.executeProcedure();
        dbc.clearResultSet();
        return templates;
    }

    /*
    private static TemplateGroupDomainObject[] sprocGetTemplateGroupsForUser( IMCService service, int user_id, int meta_id ){
        String[] params = new String[]{ String.valueOf(meta_id), String.valueOf(user_id)};
        String[] sprocResult = service.sqlProcedure( SPROC_GET_TEMPLATE_GROUPS_FOR_USER, params );
        int noOfColumnsInResult = 2;
        TemplateGroupDomainObject[] result = new TemplateGroupDomainObject[sprocResult.length/noOfColumnsInResult];
        for( int i = 0, k = 0 ; i < sprocResult.length; i=i + noOfColumnsInResult, k++ ) {
            String name = sprocResult[i+1];
            int id = Integer.parseInt(sprocResult[i]);
            result[k] = new TemplateGroupDomainObject( id, name );
        }
        return result;
    }
    */

    public TemplateGroupDomainObject[] getAllTemplateGroups( UserDomainObject user, int metaId ) {
        DBConnect dbc = new DBConnect( service.getConnectionPool() );
        dbc.getConnection();
        Vector sprocResult = sprocGetTemplateGroupsForUser( dbc, user, metaId );
        dbc.closeConnection();
        Iterator iter = sprocResult.iterator();
        int noOfColumnsInResult = 2;
        TemplateGroupDomainObject[] result = new TemplateGroupDomainObject[sprocResult.size()/noOfColumnsInResult];
        for( int i = 0, k = 0 ; iter.hasNext() ; i=i + noOfColumnsInResult, k++ ) {
            int id = Integer.parseInt((String)iter.next());
            String name = (String)iter.next();
            result[k] = new TemplateGroupDomainObject( id, name );
        }
        return result;
    }

    public TemplateDomainObject[] getTemplates( int groupId ) {
        DBConnect dbc = new DBConnect( service.getConnectionPool() );
        dbc.getConnection();
        Vector templates = sprocGetTemplatesInGroup( dbc, groupId );
        dbc.closeConnection();
        Iterator iterator = templates.iterator();
        int noOfColumns = 2;
        TemplateDomainObject[] result = new TemplateDomainObject[templates.size()/noOfColumns];
        for( int k=0; iterator.hasNext(); k++ ) {
            String templateId = (String)iterator.next();
            String templateName = (String)iterator.next();
            result[k] = new TemplateDomainObject( Integer.parseInt(templateId), templateName );
        }
        return result;
    }

    public static Vector sqlSelectGrouuName( DBConnect dbc, String group_id ) {
        String sqlStr = "select group_name from templategroups where group_id = " + group_id;
        dbc.setSQLString( sqlStr );
        Vector groupnamevec = dbc.executeQuery();
        dbc.clearResultSet();
        return groupnamevec;
    }

    public static void sqlUpdateUnassignTemplateFromGroup( IMCService service, int[] group_id, int template_id ) {
        DBConnect dbc = new DBConnect( service.getConnectionPool() );
        dbc.getConnection();

        // delete current refs
        for( int i = 0; i < group_id.length; i++ ) {
            String sqlStr = "delete from templates_cref\n";
            sqlStr += "where template_id = " + template_id;
            sqlStr += "and group_id = " + group_id[i];
            dbc.setSQLString( sqlStr );
            dbc.createStatement();
            dbc.executeUpdateQuery();
        }

        dbc.closeConnection();
    }
}
