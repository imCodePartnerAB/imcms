package imcode.server.document;

import com.imcode.imcms.api.*;
import imcode.server.user.*;

/**
 * Created by IntelliJ IDEA.
 * User: Hasse
 * Date: 2004-jan-20
 * Time: 18:09:49
 * To change this template use Options | File Templates.
 */
public class MenuDomainObject {
    private DocumentDomainObject ownerDocument;
    private int menuIndex;
    private DocumentMapper documentMapper;

    public int getMenuIndex() {
        return menuIndex;
    }

    public MenuDomainObject( DocumentDomainObject owner, int menuIndex, DocumentMapper documentMapper ) {
        this.ownerDocument = owner;
        this.menuIndex = menuIndex;
        this.documentMapper = documentMapper;
    }

    public MenuItemDomainObject[] getMenuItems() throws NoPermissionException {
        MenuItemDomainObject[] menuItemsDomainObjects = documentMapper.getDocIdsFromMenu( ownerDocument.getMetaId(), menuIndex );
        return menuItemsDomainObjects;
    }

     public void addDocument( DocumentDomainObject documentToAdd, UserDomainObject user ) throws NoPermissionException, DocumentAlreadyInMenuException {
         try {
             documentMapper.addDocumentToMenu( user, ownerDocument.getMetaId(), menuIndex, documentToAdd.getMetaId() );
         } catch (DocumentMapper.DocumentAlreadyInMenuException e) {
             throw new DocumentAlreadyInMenuException( "Menu " + menuIndex + " of ownerDocument " + ownerDocument.getMetaId() + " already contains ownerDocument " + documentToAdd.getMetaId() );
         }
     }

     public void removeDocument( DocumentDomainObject documentToRemove, UserDomainObject user ) throws NoPermissionException {
         documentMapper.removeDocumentFromMenu( user, ownerDocument.getMetaId(), menuIndex, documentToRemove.getMetaId() );
     }

    public DocumentDomainObject getOwnerDocument() {
        return  ownerDocument;
    }
}
