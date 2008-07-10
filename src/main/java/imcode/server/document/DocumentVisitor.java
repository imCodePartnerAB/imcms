package imcode.server.document;

import imcode.server.document.textdocument.TextDocumentDomainObject;

public class DocumentVisitor {

    public void visitTextDocument( TextDocumentDomainObject textDocument ) {
        visitOtherDocument( textDocument ) ;
    }

    public void visitBrowserDocument( BrowserDocumentDomainObject browserDocument ) {
        visitOtherDocument( browserDocument );
    }

    public void visitFileDocument( FileDocumentDomainObject fileDocument ) {
        visitOtherDocument( fileDocument );
    }

    public void visitHtmlDocument( HtmlDocumentDomainObject htmlDocument ) {
        visitOtherDocument( htmlDocument );
    }

    public void visitUrlDocument( UrlDocumentDomainObject urlDocument ) {
        visitOtherDocument( urlDocument );
    }

    protected void visitOtherDocument( DocumentDomainObject otherDocument ) {
    }

}