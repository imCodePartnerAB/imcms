package imcode.server.document;

import imcode.server.document.textdocument.*;
import imcode.server.user.UserDomainObject;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.RoleGetter;
import imcode.server.user.RoleDomainObject;
import imcode.server.ImcmsServices;
import imcode.server.Imcms;
import imcode.util.Utility;
import imcode.util.DateConstants;
import org.apache.commons.lang.UnhandledException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.imcode.imcms.mapping.CategoryMapper;

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

    private void createMetaElements(DocumentDomainObject document, Element metaElement) {
        createSimpleElement("linkheading", document.getHeadline(), metaElement);
        createSimpleElement("linktext", document.getMenuText(), metaElement);
        createSimpleElement("linkimage", document.getMenuImage(), metaElement);
        createSimpleElement("alias", document.getAlias(), metaElement);
        ImcmsServices service = Imcms.getServices();
        ImcmsAuthenticatorAndUserAndRoleMapper userMapper = service.getImcmsAuthenticatorAndUserAndRoleMapper();
        UserDomainObject creator = userMapper.getUser(document.getCreatorId());
        createSimpleElement("creator", Utility.formatUser(creator), metaElement);
        if (document.getPublisherId() != null) {
            UserDomainObject publisher = userMapper.getUser(document.getPublisherId());
            createSimpleElement("publisher", Utility.formatUser(publisher), metaElement);
        }
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DateConstants.DATETIME_FORMAT_STRING);
        createSimpleElement("createdate",
            dateTimeFormat.format(document.getCreatedDatetime()), metaElement);
        if (document.getModifiedDatetime() != null) {
            createSimpleElement("changedate",
                dateTimeFormat.format(document.getModifiedDatetime()), metaElement);
        }
        if (document.getArchivedDatetime() != null) {
            createSimpleElement("archivedate",
                dateTimeFormat.format(document.getArchivedDatetime()), metaElement);
        }
        if (document.getPublicationEndDatetime() != null) {
            createSimpleElement("expiredate",
                dateTimeFormat.format(document.getPublicationEndDatetime()), metaElement);
        }
        if (document.getPublicationStartDatetime() != null) {
            createSimpleElement("publishingdate",
                dateTimeFormat.format(document.getPublicationStartDatetime()), metaElement);
        }
        Set<Integer> catIds = document.getCategoryIds();
        CategoryMapper categoryMapper = service.getCategoryMapper();
        for (Integer catId : catIds) {
            Element categoryElement = xmlDocument.createElement("category");
            CategoryDomainObject category = categoryMapper.getCategoryById(catId);
            categoryElement.setAttribute("type", category.getType().getName());
            categoryElement.setTextContent(category.getName());
            metaElement.appendChild(categoryElement);
        }

        Set keywords = document.getKeywords();
        if (!keywords.isEmpty()) {
            Iterator keywordsIterator = keywords.iterator();
            Element keywordsElement = xmlDocument.createElement("keywords");
            keywordsElement.setAttribute("blocked", document.isSearchDisabled() + "");
            while (keywordsIterator.hasNext()) {
                String kw = (String)keywordsIterator.next();
                createSimpleElement("keyword", kw, keywordsElement);
            }
            metaElement.appendChild(keywordsElement);
        }

        RoleIdToDocumentPermissionSetTypeMappings r2pMappings = document
            .getRoleIdsMappedToDocumentPermissionSetTypes();

        if (r2pMappings != null) {
            RoleGetter roleGetter = service.getRoleGetter();
            Element rolesElement = xmlDocument.createElement("roles");
            RoleIdToDocumentPermissionSetTypeMappings.Mapping[] mappings = r2pMappings.getMappings();
            for (RoleIdToDocumentPermissionSetTypeMappings.Mapping m : mappings) {
                RoleDomainObject role = roleGetter.getRole(m.getRoleId());
                Element roleElement = xmlDocument.createElement("role");
                roleElement.setAttribute("type", m.getDocumentPermissionSetType().name());
                roleElement.setTextContent(role.getName());
                rolesElement.appendChild(roleElement);
            }
            metaElement.appendChild(rolesElement);
        }

        if (document.getDocumentTypeId() == DocumentTypeDomainObject.TEXT_ID){
            TextDocumentDomainObject textDocument = (TextDocumentDomainObject)document;
            createSimpleElement("template", textDocument.getTemplateName(), metaElement);
        }

        Element shareElement = xmlDocument.createElement("share");
        createSimpleElement("showLink", document.isLinkedForUnauthorizedUsers() + "", shareElement);
        createSimpleElement("shareDocument", document.isLinkableByOtherUsers() + "", shareElement);
        metaElement.appendChild(shareElement);
        
        createSimpleElement("target", document.getTarget(), metaElement);
    }

    private void createSimpleElement(String elementName, String textContent, Element parentElement) {
        Element element = xmlDocument.createElement(elementName);
        element.setTextContent(textContent);
        parentElement.appendChild(element);
    }

    public void addDocument(DocumentDomainObject document) {
        XmlBuildingDocumentVisitor documentVisitor = new XmlBuildingDocumentVisitor( xmlDocument, currentUser);
        document.accept( documentVisitor );
        Element documentElement = documentVisitor.getDocumentElement();
        documentElement.setAttribute( "id", "" + document.getId() );
        Element metaElement = xmlDocument.createElement( "meta" );
        createMetaElements( document, metaElement );
        documentElement.appendChild( metaElement );
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
            Element documentElement = xmlDocument.createElement( "document" ) ;
            documentElement.setAttribute("type", "text");
            Element contentElement = xmlDocument.createElement( "content" );
            
            createTextElements( textDocument, contentElement );
            createImageElements( textDocument, contentElement );
            createMenuElements( textDocument, contentElement );

            documentElement.appendChild(contentElement);
            this.documentElement = documentElement ;
        }

        public void visitFileDocument( FileDocumentDomainObject fileDocument ) {
            Element documentElement = xmlDocument.createElement( "document" ) ;
            documentElement.setAttribute("type", "file");

            Element contentElement = xmlDocument.createElement( "content" );
            createFileElements(fileDocument, contentElement);
            documentElement.appendChild(contentElement);

            this.documentElement = documentElement ;
        }

        public void visitUrlDocument(UrlDocumentDomainObject urlDocument) {
            Element documentElement = xmlDocument.createElement( "document" ) ;
            documentElement.setAttribute("type", "link");

            Element contentElement = xmlDocument.createElement( "content" );
            createLinkElements(urlDocument, contentElement);
            documentElement.appendChild(contentElement);

            this.documentElement = documentElement ;
        }

        private void createTextElements( TextDocumentDomainObject textDocument, Element contentElement ) {
            Map texts = textDocument.getTexts();
            for ( Iterator iterator = texts.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry entry = (Map.Entry)iterator.next();
                Integer textIndex = (Integer)entry.getKey();
                TextDomainObject text = (TextDomainObject)entry.getValue();
                Element textElement = createTextElement( textIndex, text );
                contentElement.appendChild( textElement );
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

        private void createImageElements( TextDocumentDomainObject textDocument, Element contentElement ) {
            Map images = textDocument.getImages();
            for ( Iterator iterator = images.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry entry = (Map.Entry)iterator.next();
                Integer imageIndex = (Integer)entry.getKey();
                ImageDomainObject image = (ImageDomainObject)entry.getValue();
                Element imageElement = createImageElement( imageIndex, image);
                contentElement.appendChild( imageElement );
            }
        }

        private Element createImageElement(Integer imageIndex, ImageDomainObject image) {
            Element imageElement = xmlDocument.createElement( "image" );
            imageElement.setAttribute( "index", "" + imageIndex );
            imageElement.setAttribute( "path", image.getUrlPath( "" )  );
            imageElement.setAttribute( "alt-text", image.getAlternateText());
            
            return imageElement;
        }

        private void createMenuElements(TextDocumentDomainObject textDocument, Element contentElement) {
            Map menus = textDocument.getMenus() ;
            for ( Iterator iterator = menus.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry entry = (Map.Entry)iterator.next();
                Integer menuIndex = (Integer)entry.getKey();
                MenuDomainObject menu = (MenuDomainObject)entry.getValue();
                Element menuElement = createMenuElement( menuIndex, menu);
                contentElement.appendChild( menuElement );
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

        private void createFileElements( FileDocumentDomainObject file, Element contentElement ) {
            Map files = file.getFiles();

            for (Object key: files.keySet()) {
                FileDocumentDomainObject.FileDocumentFile docFile =
                        (FileDocumentDomainObject.FileDocumentFile)files.get(key);

                Element fileElement = xmlDocument.createElement( "file" );
                fileElement.setAttribute("id", docFile.getId() + "");
                fileElement.setAttribute("mime", docFile.getMimeType());
                long size;
                try {
                    size = docFile.getInputStreamSource().getSize();
                }
                catch(IOException ex) {
                    size = -1;
                }
                fileElement.setAttribute("size", size + "");
                fileElement.setAttribute("default",
                        (docFile.getId() == file.getDefaultFileId()) + "");
                fileElement.setTextContent(docFile.getFilename());

                contentElement.appendChild(fileElement);
            }
        }

        private void createLinkElements( UrlDocumentDomainObject urlDocument, Element contentElement ) {
            Element linkElement = xmlDocument.createElement("link");
            linkElement.setTextContent(urlDocument.getUrl());
            contentElement.appendChild(linkElement);
        }
    }
}
