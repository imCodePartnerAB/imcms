package imcode.server.document.textdocument;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentReference;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.imcode.imcms.api.DocumentVersionSelector;

//TODO: refactor equals, hashcode
@Embeddable
public class MenuItemDomainObject implements Cloneable, Serializable {

	@Transient
    private DocumentReference documentReference;
    
	@Column(name="manual_sort_order")
	private Integer sortKey;
	
	@Column(name="tree_sort_index")
	private String treeSortIndex;	
    
    @Transient
    private TreeSortKeyDomainObject treeSortKey;
    
    public MenuItemDomainObject() {}

    public MenuItemDomainObject( DocumentReference documentReference,
                                 Integer sortKey, TreeSortKeyDomainObject treeSortKey ) {
        this.documentReference = documentReference;
        this.sortKey = sortKey;
        this.treeSortKey = treeSortKey;
    }

    public MenuItemDomainObject(DocumentReference documentReference) {
        this(documentReference, null, new TreeSortKeyDomainObject(""));
    }

    /*
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof MenuItemDomainObject ) ) {
            return false;
        }

        final MenuItemDomainObject otherMenuItem = (MenuItemDomainObject)o;

        return sortKey.equals(otherMenuItem.sortKey);
               //&& treeSortKey.equals(otherMenuItem.treeSortKey)
               //&& documentReference.equals(otherMenuItem.documentReference);
    }
    */

    //public int hashCode() {
    //    return documentReference.hashCode();
   // }

    public Integer getSortKey() {
        return sortKey;
    }

    public TreeSortKeyDomainObject getTreeSortKey() {
        return treeSortKey;
    }

    public DocumentReference getDocumentReference() {
        return documentReference;
    }
    
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public DocumentDomainObject getDocument() {
        return documentReference.getDocument();
    }    

    public int getDocumentId() {
        return documentReference.getDocumentId();
    }

    public void setSortKey(Integer sortKey) {
        this.sortKey = sortKey;
    }

    public void setTreeSortKey(TreeSortKeyDomainObject treeSortKey) {
        this.treeSortKey = treeSortKey;
    }

	public String getTreeSortIndex() {
		return treeSortIndex;
	}

	public void setTreeSortIndex(String treeSortIndex) {
		this.treeSortIndex = treeSortIndex;
	}

	public void setDocumentReference(DocumentReference documentReference) {
		this.documentReference = documentReference;
	}
}