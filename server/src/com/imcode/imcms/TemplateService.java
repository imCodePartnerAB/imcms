package com.imcode.imcms;

import imcode.server.TemplateDomainObject;
import imcode.server.IMCService;
import imcode.server.user.UserDomainObject;
import imcode.server.document.TemplateMapper;
import imcode.server.document.TemplateGroupDomainObject;

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
    public TemplateGroup[] getTemplatesGroups( Document document ) {
        // todo securityChecker.????
        UserDomainObject user = securityChecker.getCurrentLoggedInUser();
        TemplateGroupDomainObject[] internalTemplates = templateMapper.getAllTemplateGroups( user, document.getId() );
        TemplateGroup[] result = new TemplateGroup[internalTemplates.length];
        for( int i = 0; i < internalTemplates.length; i++ ) {
            result[i] = new TemplateGroup( internalTemplates[i] );
        }
        return result;
    }

    public Template[] getTemplates( TemplateGroup templateGroup ) {
        // todo securityChecker.????
        TemplateDomainObject[] templates = templateMapper.getTemplates( templateGroup.getId() );
        Template[] result = new Template[templates.length];
        for( int i = 0; i < templates.length; i++ ) {
            TemplateDomainObject domainObject = templates[i];
            result[i] = new Template( domainObject );
        }
        return result;
    }
}
