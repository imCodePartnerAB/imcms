package imcode.server.document;

import imcode.server.IMCService;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;

import java.util.Arrays;
import java.util.List;

public class TemplateMapper {

    private static final String SPROC_GET_TEMPLATES_IN_GROUP = "GetTemplatesInGroup";
    private static final String SPROC_GET_TEMPLATE_GROUPS_FOR_USER = "GetTemplategroupsForUser";

    private IMCServiceInterface service;

    public TemplateMapper( IMCServiceInterface service ) {
        this.service = service;
    }

    public static String[][] sprocGetTemplateGroupsForUser( IMCServiceInterface service, UserDomainObject user, int meta_id ) {
        return service.sqlProcedureMulti( SPROC_GET_TEMPLATE_GROUPS_FOR_USER, new String[]{String.valueOf( meta_id ), String.valueOf( user.getUserId() )} );
    }

    public static String[][] sprocGetTemplatesInGroup( IMCServiceInterface service, int selected_group ) {
        return service.sqlProcedureMulti( SPROC_GET_TEMPLATES_IN_GROUP, new String[]{"" + selected_group} );
    }

    public TemplateGroupDomainObject[] getAllTemplateGroups( UserDomainObject user, int metaId ) {
        String[][] sprocResult = sprocGetTemplateGroupsForUser( service, user, metaId );
        TemplateGroupDomainObject[] templateGroups = new TemplateGroupDomainObject[sprocResult.length];
        for ( int i = 0; i < sprocResult.length; i++ ) {
            int templateGroupId = Integer.parseInt( sprocResult[i][0] );
            String templateGroupName = sprocResult[i][1];
            templateGroups[i] = new TemplateGroupDomainObject( templateGroupId, templateGroupName );
        }
        return templateGroups;
    }

    public TemplateDomainObject[] getTemplates( int groupId ) {
        String[][] templateData = sprocGetTemplatesInGroup( service, groupId );
        TemplateDomainObject[] templates = new TemplateDomainObject[templateData.length];
        for ( int i = 0; i < templateData.length; i++ ) {
            int templateId = Integer.parseInt( templateData[i][0] );
            templates[i] = getTemplate( service, templateId );
        }
        return templates;
    }

    public static List sqlSelectGroupName( IMCServiceInterface service, String group_id ) {
        return Arrays.asList( service.sqlQuery( "select group_name from templategroups where group_id = ?", new String[]{group_id} ) );
    }

    public static void sqlUpdateUnassignTemplateFromGroup( IMCService service, int[] group_id, int template_id ) {
        // delete current refs
        for ( int i = 0; i < group_id.length; i++ ) {
            String sqlStr = "delete from templates_cref where template_id = ? and group_id = ?";
            service.sqlUpdateQuery( sqlStr, new String[]{"" + template_id, "" + group_id[i]} );
        }
    }

    public static TemplateDomainObject getTemplate( IMCServiceInterface service, int template_id ) {
        String sqlStr = "select template_id,template_name,simple_name from templates where template_id = ?";
        String[] queryResult = service.sqlQuery( sqlStr, new String[]{"" + template_id} );
        int templateId = Integer.parseInt( queryResult[0] );
        String templateName = queryResult[1];
        String simpleName = queryResult[2];
        TemplateDomainObject result = new TemplateDomainObject( templateId, templateName, simpleName );
        return result;
    }

    public TemplateDomainObject getTemplate( String templateSimpleName ) {
        String sqlStr = "select template_id,template_name,simple_name from templates where simple_name = ?";
        String[] queryResult = service.sqlQuery( sqlStr, new String[]{templateSimpleName} );

        if ( 0 == queryResult.length ) {
            return null;
        }

        int templateId = Integer.parseInt( queryResult[0] );
        String templateName = queryResult[1];
        String simpleName = queryResult[2];
        return new TemplateDomainObject( templateId, templateName, simpleName );
    }

}
