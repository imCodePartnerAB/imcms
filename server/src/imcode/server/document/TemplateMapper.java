package imcode.server.document;

import imcode.server.IMCService;
import imcode.server.db.DBConnect;
import imcode.server.user.UserDomainObject;

import java.util.Vector;
import java.util.Iterator;

public class TemplateMapper {

    private IMCService service;

    public TemplateMapper( IMCService service ) {
        this.service = service;
    }

    public TemplateGroupDomainObject[] getAllTemplateGroups( UserDomainObject user, int metaId ) {
        DBConnect dbc = new DBConnect( service.getConnectionPool() );
        dbc.getConnection();
        Vector sprocResult = DatabaseAccessor.sprocGetTemplateGroupsForUser( dbc, user, metaId );
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
        Vector templates = DatabaseAccessor.sprocGetTemplatesInGroup( dbc, groupId );
        dbc.closeConnection();
        Iterator iterator = templates.iterator();
        int noOfColumns = 2;
        TemplateDomainObject[] result = new TemplateDomainObject[templates.size()/noOfColumns];
        if( templates.size() > 0 ){
            for( int k=0; iterator.hasNext(); k++ ) {
                String templateIdStr = (String)iterator.next();
                String templateNameStr = (String)iterator.next();
                result[k] = getTemplate( service, Integer.parseInt(templateIdStr) );
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
