package com.imcode.imcms.api;

import imcode.server.document.TemplateDomainObject;
import imcode.server.IMCService;
import imcode.server.user.UserDomainObject;
import imcode.server.document.TemplateMapper;
import imcode.server.document.TemplateGroupDomainObject;

import java.util.ArrayList;
import java.util.Arrays;

import com.imcode.imcms.api.SecurityChecker;
import com.imcode.imcms.api.Template;
import com.imcode.imcms.api.TemplateGroup;

public class TemplateService {
    private SecurityChecker securityChecker;
    private TemplateMapper templateMapper;

    public TemplateService( IMCService service, SecurityChecker securityChecker ) {
        this.securityChecker = securityChecker;
        this.templateMapper = new TemplateMapper( service );
    }

    /**
     *
     * @param textDocument The textDocument for witch we would like to se the possible groups.
     * @return Only the templategroups that the current logged in user has the permissions to see
     */
    public TemplateGroup[] getTemplatesGroups( TextDocument textDocument ) {
        // todo securityChecker.????
        UserDomainObject user = securityChecker.getCurrentLoggedInUser();
        TemplateGroupDomainObject[] internalTemplates = templateMapper.getAllTemplateGroups( user, textDocument.getId() );
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

    /**
     * Convinient method that concat all the diffenrent templates from all the groups in one singel mehtod call.
     * @param textDocument
     * @return
     */
    public Template[] getPossibleTemplates( TextDocument textDocument ) {
        TemplateGroup[] groups = getTemplatesGroups( textDocument );
        ArrayList temp = new ArrayList();
        for( int i = 0; i < groups.length; i++ ) {
            Template[] templates = getTemplates( groups[i]);
            temp.addAll( Arrays.asList( templates ));
        }
        Template[] result = (Template[])temp.toArray( new Template[ temp.size()] );
        return result;
    }
}
