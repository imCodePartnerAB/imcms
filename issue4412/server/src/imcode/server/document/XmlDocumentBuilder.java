package imcode.server.document;

import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import org.apache.commons.lang.UnhandledException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Iterator;
import java.util.Map;

public class XmlDocumentBuilder {

    private Document xmlDocument;
    private Element documentsElement;

    public XmlDocumentBuilder() {
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

    public void addDocument( DocumentDomainObject document ) {
        XmlBuildingDocumentVisitor documentVisitor = new XmlBuildingDocumentVisitor( xmlDocument );
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
        private Element documentElement;

        XmlBuildingDocumentVisitor( Document xmlDocument ) {
            this.xmlDocument = xmlDocument ;
        }

        public Element getDocumentElement() {
            return documentElement;
        }

        public void visitTextDocument( TextDocumentDomainObject textDocument ) {
            Element documentElement = xmlDocument.createElement( "textdocument" ) ;
            createTextElements( textDocument, documentElement );
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
    }
}
