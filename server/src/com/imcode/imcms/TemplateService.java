package com.imcode.imcms;

import imcode.server.TemplateDomainObject;
import imcode.server.IMCService;
import imcode.server.user.UserDomainObject;
import imcode.server.document.TemplateMapper;

public class TemplateService {
    private IMCService service;
    private SecurityChecker securityChecker;
    private TemplateMapper templateMapper;

    public TemplateService( IMCService service, SecurityChecker securityChecker ) {
        this.service = service ;
        this.securityChecker = securityChecker;
        this.templateMapper = new TemplateMapper( service );
    }

    /**
     *
     * @return Only the templategroups that the current logged in user has the permissions to see
     */
    TemplateGroup[] getAllTemplateGropus() {
        // todo securityChecker.????
        return null;
    }


    /**
     *
     * @param document The document for witch we would like to se the possible groups.
     * @return Only the templategroups that the current logged in user has the permissions to see
     */
    public Template[] getAllTemplatesGroups( Document document ) {
        // todo securityChecker.????
        UserDomainObject user = securityChecker.getCurrentLoggedInUser();
        TemplateDomainObject[] internalTemplates = templateMapper.getAllTemplateGroups( user, document.getInternal() );
        Template[] result = new Template[internalTemplates.length];
        for( int i = 0; i < internalTemplates.length; i++ ) {
            result[i] = new Template( internalTemplates[i] );
        }
        return result;
    }
}
