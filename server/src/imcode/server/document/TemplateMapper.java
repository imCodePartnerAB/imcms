package imcode.server.document;

import imcode.server.IMCService;
import imcode.server.TemplateDomainObject;
import imcode.server.IMCServiceInterface;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.UserDomainObject;

public class TemplateMapper {
    protected IMCService service;

    public TemplateMapper( IMCService service ) {
        this.service = service;
    }

    private static final String SPROC_GET_TEXT_DOC_DATA = "GetTextDocData";
    public static final String SPROC_GET_TEMPLATE_GROUPS_FOR_USER = "GetTemplateGroupsForUser";

    // todo make sure all sproc and sql mehtods are private
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
    private static TemplateDomainObject[] sprocGetTemplateGroupsForUser( IMCService service, int user_id, int meta_id ){
        String[] params = new String[]{ String.valueOf(meta_id), String.valueOf(user_id)};
        String[] sprocResult = service.sqlProcedure( SPROC_GET_TEMPLATE_GROUPS_FOR_USER, params );
        int noOfColumnsInResult = 2;
        TemplateDomainObject[] result = new TemplateDomainObject[sprocResult.length/noOfColumnsInResult];
        for( int i = 0, k = 0 ; i < sprocResult.length; i=i + noOfColumnsInResult, k++ ) {
            String name = sprocResult[i+1];
            int id = Integer.parseInt(sprocResult[i]);
            result[k] = new TemplateDomainObject( id, name );
        }
        return result;
    }

    static String[] sprocGetTextDocData( IMCServiceInterface service, int metaId ) {
        String[] textdoc_data = service.sqlProcedure( SPROC_GET_TEXT_DOC_DATA, new String[]{String.valueOf( metaId )} );
        return textdoc_data;
    }

    public TemplateDomainObject[] getAllTemplateGroups( UserDomainObject user, DocumentDomainObject document ) {
        return sprocGetTemplateGroupsForUser( service, user.getUserId(), document.getMetaId() );
    }

}
