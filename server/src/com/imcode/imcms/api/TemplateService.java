package com.imcode.imcms.api;

import imcode.server.document.TemplateDomainObject;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;
import imcode.server.document.TemplateMapper;
import imcode.server.document.TemplateGroupDomainObject;

import java.util.ArrayList;
import java.util.Arrays;

public class TemplateService {
    private SecurityChecker securityChecker;
    private TemplateMapper templateMapper;

    TemplateService( IMCServiceInterface service, SecurityChecker securityChecker ) {
        this.securityChecker = securityChecker;
        this.templateMapper = new TemplateMapper( service );
    }

    /**
     * Get a list of all template groups the calling user has right to see for this document
     *
     * @param textDocument The textDocument for witch we would like to see the possible groups.
     * @return Only the templategroups that the current logged in user has the permissions to see
     * @throws NoPermissionException If the current user isn't superadmin
     */
    public TemplateGroup[] getTemplatesGroups( TextDocument textDocument ) throws NoPermissionException {
        securityChecker.hasEditPermission( textDocument );
        UserDomainObject user = securityChecker.getCurrentLoggedInUser();
        TemplateGroupDomainObject[] internalTemplates = templateMapper.getAllTemplateGroupsAvailableForUserOnDocument( user, textDocument.getId() );
        return createTemplateGroupArray( internalTemplates );
    }

    /**
     * Get all the template groups found in the system.
     * @return
     */
    public TemplateGroup[] getAllTemplateGroups() {
        TemplateGroupDomainObject[] templateGroupDomainObject = templateMapper.getAllTemplateGroups();
        return createTemplateGroupArray( templateGroupDomainObject );
    }

    private TemplateGroup[] createTemplateGroupArray( TemplateGroupDomainObject[] internalTemplates ) {
        TemplateGroup[] result = new TemplateGroup[internalTemplates.length];
        for (int i = 0; i < internalTemplates.length; i++) {
            result[i] = new TemplateGroup( internalTemplates[i] );
        }
        return result;
    }

    /**
     * Get an array of all the Templates in a TemplateGroup.
     *
     * @param templateGroup The wanted TemplateGroup
     * @return An array of all the Templates in the given TemplateGroup
     * @throws NoPermissionException If the current user doesn't have permission to list the templates in the templategroup.
     */
    public Template[] getTemplates( TemplateGroup templateGroup ) throws NoPermissionException {
        TemplateDomainObject[] templates = templateMapper.getTemplatesInGroup( templateGroup.getInternal() );
        Template[] result = new Template[templates.length];
        for (int i = 0; i < templates.length; i++) {
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
        securityChecker.hasEditPermission( textDocument );
        TemplateGroup[] groups = getTemplatesGroups( textDocument );
        ArrayList temp = new ArrayList();
        for (int i = 0; i < groups.length; i++) {
            Template[] templates = getTemplates( groups[i] );
            temp.addAll( Arrays.asList( templates ) );
        }
        Template[] result = (Template[]) temp.toArray( new Template[temp.size()] );
        return result;
    }

    public Template getTemplate( String templateName ) {
        TemplateDomainObject template = templateMapper.getTemplateByName( templateName );
        return (null != template) ? new Template( template ) : null;
    }

    public Template getTemplateById( int templateId ) {
        TemplateDomainObject template = templateMapper.getTemplateById( templateId );
        return (null != template) ? new Template( template ) : null;
    }

    public TemplateGroup getTemplateGroupById( int templateGroupId ) {
        TemplateGroupDomainObject template = templateMapper.getTemplateGroupById( templateGroupId );
        return (null != template) ? new TemplateGroup( template ) : null;
    }
}
