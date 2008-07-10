package imcode.server.document;

import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;

import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.UnhandledException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class XmlDocumentBuilder {

    private Document xmlDocument;
    private Element documentsElement;
    private UserDomainObject currentUser;


    public XmlDocumentBuilder(UserDomainObject user) {
        currentUser = user;
        try {
            xmlDocument = createXmlDocument();
            Element imcmsElement = xmlDocument.createElement( "imcms" );
            documentsElement = xmlDocument.createElement( "documents" ) ;
            imcmsElement.appendChild( documentsElement ) ;
            xmlDocument.appendChild( imcmsElement );
        } catch ( ParserConfigurationException e ) {
            throw new UnhandledException( e );
        }

    }

    private Document createXmlDocument() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.newDocument();
    }

    public void addDocument(DocumentDomainObject document) {
        XmlBuildingDocumentVisitor documentVisitor = new XmlBuildingDocumentVisitor( xmlDocument, currentUser);
        document.accept( documentVisitor );
        Element documentElement = documentVisitor.getDocumentElement();
        documentElement.setAttribute( "id", "" + document.getId() );
        documentsElement.appendChild( documentElement ) ;
    }

    public Document getXmlDocument() {
        return xmlDocument;
    }

    private static class XmlBuildingDocumentVisitor extends DocumentVisitor {

        private Document xmlDocument;
        private UserDomainObject currentUser;
        private Element documentElement;


        XmlBuildingDocumentVisitor(Document xmlDocument, UserDomainObject currentUser) {
            this.xmlDocument = xmlDocument ;
            this.currentUser = currentUser;
        }

        public Element getDocumentElement() {
            return documentElement;
        }

        public void visitTextDocument( TextDocumentDomainObject textDocument ) {
            Element documentElement = xmlDocument.createElement( "textdocument" ) ;
            createTextElements( textDocument, documentElement );
            createImageElements( textDocument, documentElement );
            createMenuElements( textDocument, documentElement );

            this.documentElement = documentElement ;
        }

        private void createTextElements( TextDocumentDomainObject textDocument, Element textDocumentElement ) {
            Map texts = textDocument.getTexts();
            for ( Iterator iterator = texts.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry entry = (Map.Entry)iterator.next();
                Integer textIndex = (Integer)entry.getKey();
                TextDomainObject text = (TextDomainObject)entry.getValue();
                Element textElement = createTextElement( textIndex, text );
                textDocumentElement.appendChild( textElement );
            }
        }

        private Element createTextElement( Integer textIndex, TextDomainObject text ) {
            Element textElement = xmlDocument.createElement( "text" );
            textElement.setAttribute( "index", "" + textIndex );
            textElement.setAttribute( "type", TextDomainObject.TEXT_TYPE_PLAIN == text.getType() ? "text" : "html" );
            Text textNode = xmlDocument.createTextNode( text.getText() );
            textElement.appendChild( textNode );
            return textElement;
        }

        private void createImageElements( TextDocumentDomainObject textDocument, Element textDocumentElement ) {
            Map images = textDocument.getImages();
            for ( Iterator iterator = images.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry entry = (Map.Entry)iterator.next();
                Integer imageIndex = (Integer)entry.getKey();
                ImageDomainObject image = (ImageDomainObject)entry.getValue();
                Element imageElement = createImageElement( imageIndex, image);
                textDocumentElement.appendChild( imageElement );
            }
        }

        private Element createImageElement(Integer imageIndex, ImageDomainObject image) {
            Element imageElement = xmlDocument.createElement( "image" );
            imageElement.setAttribute( "index", "" + imageIndex );
            imageElement.setAttribute( "path", image.getUrlPath( "" )  );
            return imageElement;
        }

        private void createMenuElements(TextDocumentDomainObject textDocument, Element textDocumentElement) {
            Map menus = textDocument.getMenus() ;
            for ( Iterator iterator = menus.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry entry = (Map.Entry)iterator.next();
                Integer menuIndex = (Integer)entry.getKey();
                MenuDomainObject menu = (MenuDomainObject)entry.getValue();
                Element menuElement = createMenuElement( menuIndex, menu);
                textDocumentElement.appendChild( menuElement );
            }
        }

        private Element createMenuElement(Integer menuIndex, MenuDomainObject menu) {
            Element menuElement = xmlDocument.createElement( "menu" );
            menuElement.setAttribute( "index", "" + menuIndex );
            createMenuItemElements(menu, menuElement);
            return menuElement;
        }

        private void createMenuItemElements(MenuDomainObject menu, Element menuElement) {
            MenuItemDomainObject[] menuItems = menu.getMenuItems() ;
            for (int i = 0; i < menuItems.length; i++) {
                MenuItemDomainObject menuItem = menuItems[i];
                DocumentDomainObject document = menuItem.getDocument();
                if ( currentUser.canAccess(document) && document.isPublished() || currentUser.canEdit(document) ) {
                    Element menuItemElement = createMenuItemElement( menuItem.getDocument());
                    menuElement.appendChild( menuItemElement );
                }
            }
        }

        private Element createMenuItemElement(DocumentDomainObject document) {
            Element menuItemElement = xmlDocument.createElement( "menuItem" );
            menuItemElement.setAttribute( "documentid", "" + document.getId() );
            return menuItemElement;
        }
    }
}
