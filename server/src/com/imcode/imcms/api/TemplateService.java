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

    TemplateService( IMCService service, SecurityChecker securityChecker ) {
        this.securityChecker = securityChecker;
        this.templateMapper = new TemplateMapper( service );
    }

    /**
     * Get a list of all
     *
     * @param textDocument The textDocument for witch we would like to se the possible groups.
     * @return Only the templategroups that the current logged in user has the permissions to see
     * @throws NoPermissionException If the current user isn't superadmin
     */
    public TemplateGroup[] getTemplatesGroups( TextDocument textDocument ) throws NoPermissionException {
        securityChecker.isSuperAdmin();
        UserDomainObject user = securityChecker.getCurrentLoggedInUser();
        TemplateGroupDomainObject[] internalTemplates = templateMapper.getAllTemplateGroups( user, textDocument.getId() );
        TemplateGroup[] result = new TemplateGroup[internalTemplates.length];
        for( int i = 0; i < internalTemplates.length; i++ ) {
            result[i] = new TemplateGroup( internalTemplates[i] );
        }
        return result;
    }

    /**
     * Get an array of all the Templates in a TemplateGroup
     *
     * @param templateGroup The wanted TemplateGroup
     * @return An array of all the Templates in the given TemplateGroup
     * @throws NoPermissionException If the current user doesn't have permission to list the templates in the templategroup.
     */
    public Template[] getTemplates( TemplateGroup templateGroup ) throws NoPermissionException {
        securityChecker.hasTemplateGroupPermission(templateGroup);
        TemplateDomainObject[] templates = templateMapper.getTemplates( templateGroup.getId() );
        Template[] result = new Template[templates.length];
        for( int i = 0; i < templates.length; i++ ) {
            TemplateDomainObject domainObject = templates[i];
            result[i] = new Template( domainObject );
        }
        return result;
    }

    /**
     * Get an array of all templates that may be used for a TextDocument.
     *
     * @param textDocument The TextDocument
     * @return An array of all templates that may be used for the given TextDocument.
     */
    public Template[] getPossibleTemplates( TextDocument textDocument ) throws NoPermissionException {
        securityChecker.hasEditPermission(textDocument);
        TemplateGroup[] groups = getTemplatesGroups( textDocument );
        ArrayList temp = new ArrayList();
        for( int i = 0; i < groups.length; i++ ) {
            Template[] templates = getTemplates( groups[i]);
            temp.addAll( Arrays.asList( templates ));
        }
        Template[] result = (Template[])temp.toArray( new Template[ temp.size()] );
        return result;
    }

    public Template getTemplate( String templateName ) {
        TemplateDomainObject template = templateMapper.getTemplate(templateName) ;
        return (null != template) ? new Template(template) : null ;
    }

}
