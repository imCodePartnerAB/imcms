/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-feb-28
 * Time: 20:25:56
 */
package imcode.server.document;

import com.imcode.imcms.servlet.admin.DocumentComposer;
import imcode.server.user.UserDomainObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TextDocumentDomainObject extends DocumentDomainObject {

    private int textDocumentMenuSortOrder;
    private TemplateDomainObject textDocumentTemplate;
    private int textDocumentTemplateGroupId;
    private int textDocumentDefaultTemplateIdForRestrictedPermissionSetOne;
    private int textDocumentDefaultTemplateIdForRestrictedPermissionSetTwo;

    public int getTextDocumentMenuSortOrder() {
        return textDocumentMenuSortOrder;
    }

    public void setTextDocumentMenuSortOrder( int v ) {
        this.textDocumentMenuSortOrder = v;
    }

    public TemplateDomainObject getTextDocumentTemplate() {
        return textDocumentTemplate;
    }

    public void setTextDocumentTemplate( TemplateDomainObject v ) {
        this.textDocumentTemplate = v;
    }

    public int getTextDocumentTemplateGroupId() {
        return textDocumentTemplateGroupId;
    }

    public void setTextDocumentTemplateGroupId( int v ) {
        this.textDocumentTemplateGroupId = v;
    }

    public void setTextDocumentDefaultTemplateIdForRestrictedPermissionSetOne(
            int textDocumentDefaultTemplateIdForRestrictedPermissionSetOne ) {
        this.textDocumentDefaultTemplateIdForRestrictedPermissionSetOne = textDocumentDefaultTemplateIdForRestrictedPermissionSetOne;
    }

    public int getTextDocumentDefaultTemplateIdForRestrictedPermissionSetOne() {
        return textDocumentDefaultTemplateIdForRestrictedPermissionSetOne;
    }

    public void setTextDocumentDefaultTemplateIdForRestrictedPermissionSetTwo(
            int textDocumentDefaultTemplateIdForRestrictedPermissionSetTwo ) {
        this.textDocumentDefaultTemplateIdForRestrictedPermissionSetTwo = textDocumentDefaultTemplateIdForRestrictedPermissionSetTwo;
    }

    public int getTextDocumentDefaultTemplateIdForRestrictedPermissionSetTwo() {
        return textDocumentDefaultTemplateIdForRestrictedPermissionSetTwo;
    }

    public int getDocumentTypeId() {
        return DOCTYPE_TEXT;
    }

    public void processNewDocumentInformation( DocumentComposer documentInformation,
                                               DocumentComposer.NewDocumentParentInformation newDocumentParentInformation,
                                               UserDomainObject user, HttpServletRequest request,
                                               HttpServletResponse response ) throws IOException {
            documentInformation.saveNewDocumentAndAddToMenuAndRemoveSessionAttributesAndRedirectToParent( this, newDocumentParentInformation, user, request, response );
    }

    public void saveDocument( DocumentMapper documentMapper ) {
            documentMapper.saveTextDocument(this) ;
    }

    public void saveNewDocument( DocumentMapper documentMapper ) {
        documentMapper.saveNewTextDocument( this );
    }

    public void initDocumentFromDb( DocumentMapper documentMapper ) {
        documentMapper.initTextDocumentFromDb( this ) ;
    }

}