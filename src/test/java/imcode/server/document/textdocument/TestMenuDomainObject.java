//package imcode.server.document.textdocument;
//
//import imcode.server.document.DocumentDomainObject;
//import imcode.server.document.DocumentReference;
//import imcode.server.document.DocumentTypeDomainObject;
//import imcode.server.document.DocumentVisitor;
//import junit.framework.TestCase;
//
//import java.util.List;
//import java.util.Random;
//
//public class TestMenuDomainObject extends TestCase {
//
//    MenuDomainObject menuDomainObject;
//    final Random random = new Random();
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//
//        menuDomainObject = new MenuDomainObject();
//        menuDomainObject.addMenuItem(getNewItemWithTreeSortKey("1"));
//        menuDomainObject.addMenuItem(getNewItemWithTreeSortKey("1.1"));
//        menuDomainObject.addMenuItem(getNewItemWithTreeSortKey("1.2"));
//        menuDomainObject.addMenuItem(getNewItemWithTreeSortKey("1.2.1"));
//        menuDomainObject.addMenuItem(getNewItemWithTreeSortKey("1.2.2"));
//        menuDomainObject.addMenuItem(getNewItemWithTreeSortKey("1.2.3"));
//        menuDomainObject.addMenuItem(getNewItemWithTreeSortKey("1.2.3.1"));
//        menuDomainObject.addMenuItem(getNewItemWithTreeSortKey("1.2.4"));
//        menuDomainObject.addMenuItem(getNewItemWithTreeSortKey("1.3"));
//        menuDomainObject.addMenuItem(getNewItemWithTreeSortKey("2"));
//        menuDomainObject.addMenuItem(getNewItemWithTreeSortKey("2.1"));
//        menuDomainObject.addMenuItem(getNewItemWithTreeSortKey("2.1.1"));
//        menuDomainObject.addMenuItem(getNewItemWithTreeSortKey("3"));
//    }
//
////    public void testMenuObject() throws Exception {
////        List a = menuDomainObject.getMenuItemsAsTree();
////        a.toString();
////    }
//
//    private MenuItemDomainObject getNewItemWithTreeSortKey(String sortKey) {
//        MenuItemDomainObject item = new MenuItemDomainObject(createRandomDocument());
//        item.setTreeSortIndex(sortKey);
//        return item;
//    }
//
//    private DocumentReference createRandomDocument() {
//        return new DocumentReference(random.nextInt()) {
//            @Override
//            public DocumentDomainObject getDocument() {
//                return new DocumentDomainObject() {
//                    @Override
//                    public DocumentTypeDomainObject getDocumentTypeId() {
//                        return null;
//                    }
//
//                    @Override
//                    public void accept(DocumentVisitor documentVisitor) {
//
//                    }
//                };
//            }
//        };
//    }
//
//}