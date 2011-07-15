package imcode.server.document;

import java.io.Serializable;

/**
 * Customizable permissions sets (restricted-1 and restricted-2) for doc and doc's profile.
 *
 * Note: Unfortunately the name of this class is a bit misleading.
 *
 * @see DocumentPermissionSetTypeDomainObject
 * @see DocumentDomainObject#getPermissionSets()
 * @see DocumentDomainObject#getPermissionSetsForNewDocuments()
 */
public class DocumentPermissionSets implements Serializable, Cloneable {

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

    @Override
    public DocumentPermissionSets clone() {
    	try {
	        DocumentPermissionSets clone = (DocumentPermissionSets) super.clone();
	        clone.permissionSets = new DocumentPermissionSetDomainObject[] {
	                (DocumentPermissionSetDomainObject) permissionSets[0].clone(),
	                (DocumentPermissionSetDomainObject) permissionSets[1].clone()
	        };
	        
	        return clone ;
    	} catch (CloneNotSupportedException e) {
    		throw new AssertionError(e);
    	}    	
    }
}
