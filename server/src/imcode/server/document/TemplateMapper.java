package imcode.server.document;

import imcode.server.IMCService;
import imcode.server.db.*;
import imcode.server.user.UserDomainObject;

import java.util.Vector;
import java.util.Iterator;

public class TemplateMapper {

    private IMCService service; // todo: remove this.
    private DatabaseService databaseService;

    public TemplateMapper( IMCService service ) {
        this.service = service;
        databaseService = service.getDatabaseService();
    }

    public TemplateGroupDomainObject[] getAllTemplateGroups( UserDomainObject user, int metaId ) {
        DatabaseService.Table_templategroups[] templateGroups = databaseService.sproc_GetTemplateGroupsForUser( metaId, user.getUserId() );
        TemplateGroupDomainObject[] result = new TemplateGroupDomainObject[templateGroups.length];
        for (int i = 0; i < templateGroups.length; i++) {
            DatabaseService.Table_templategroups templateGroup = templateGroups[i];
            int group_id = templateGroup.group_id;
            String group_name = templateGroup.group_name;
            result[i] = new TemplateGroupDomainObject( group_id, group_name );
        }
        return result;
    }

    public TemplateDomainObject[] getTemplates( int groupId ) {
        DatabaseService.Table_templates[] templates = databaseService.sproc_GetTemplatesInGroup( groupId );
        TemplateDomainObject[] result = new TemplateDomainObject[templates.length];
        if( templates.length > 0 ) {
            for (int i = 0; i < templates.length; i++) {
                DatabaseService.Table_templates template = templates[i];
                result[i] = getTemplate( service, template.template_id );
            }
        }
        return result;
    }

    public static TemplateDomainObject getTemplate( IMCService service, int template_id ) {
        Vector queryResult = DatabaseAccessor.sqlSelectGetTemplate( service, template_id );
        int templateId = Integer.parseInt((String)queryResult.elementAt(0));
        String templateName = (String)queryResult.elementAt(1);
        String simpleName = (String)queryResult.elementAt(2);
        TemplateDomainObject result = new TemplateDomainObject( templateId, templateName, simpleName );
        return result;
    }

}
