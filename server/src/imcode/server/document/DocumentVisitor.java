package imcode.server.document;

import imcode.server.document.textdocument.TextDocumentDomainObject;

public class DocumentVisitor {

    public void visitTextDocument( TextDocumentDomainObject textDocument ) {
    }

    public void visitFormerExternalDocument( FormerExternalDocumentDomainObject formerExternalDocument ) {
    }

    public void visitBrowserDocument( BrowserDocumentDomainObject browserDocument ) {
    }

    public void visitFileDocument( FileDocumentDomainObject fileDocument ) {
    }

    public void visitHtmlDocument( HtmlDocumentDomainObject htmlDocument ) {
    }

    public void visitUrlDocument( UrlDocumentDomainObject urlDocument ) {
    }
}