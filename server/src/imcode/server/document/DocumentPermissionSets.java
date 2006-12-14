package imcode.server.document;

import imcode.util.LazilyLoadedObject;
import imcode.util.ShouldNotBeThrownException;

import java.io.Serializable;

public class DocumentPermissionSets implements Serializable, Cloneable, LazilyLoadedObject.Copyable {

    private DocumentPermissionSetDomainObject[] permissionSets = new DocumentPermissionSetDomainObject[] {
            new TextDocumentPermissionSetDomainObject( DocumentPermissionSetTypeDomainObject.RESTRICTED_1 ),
            new TextDocumentPermissionSetDomainObject( DocumentPermissionSetTypeDomainObject.RESTRICTED_2 )
    };

    public void setRestricted1(DocumentPermissionSetDomainObject restricted1) {
        permissionSets[0] = restricted1;
    }

    public void setRestricted2(DocumentPermissionSetDomainObject restricted2) {
        permissionSets[1] = restricted2;
    }

    public DocumentPermissionSetDomainObject getRestricted1() {
        return permissionSets[0];
    }

    public DocumentPermissionSetDomainObject getRestricted2() {
        return permissionSets[1];
    }

    public DocumentPermissionSetDomainObject getRestricted(int n) {
        return permissionSets[n-1] ;
    }

    public Object clone() throws CloneNotSupportedException {
        DocumentPermissionSets clone = (DocumentPermissionSets) super.clone();
        clone.permissionSets = new DocumentPermissionSetDomainObject[] {
                (DocumentPermissionSetDomainObject) permissionSets[0].clone(),
                (DocumentPermissionSetDomainObject) permissionSets[1].clone()
        };
        return clone ;
    }

    public LazilyLoadedObject.Copyable copy() {
        try {
            return (LazilyLoadedObject.Copyable) clone() ;
        } catch ( CloneNotSupportedException e ) {
            throw new ShouldNotBeThrownException(e);
        }
    }
}
