/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-feb-28
 * Time: 20:47:40
 */
package imcode.server.document;

import imcode.server.user.UserDomainObject;

public class HtmlDocumentDomainObject extends DocumentDomainObject {

    private String htmlDocumentHtml = "";

    public void setHtmlDocumentHtml( String htmlDocumentHtml ) {
        this.htmlDocumentHtml = htmlDocumentHtml;
    }

    public String getHtmlDocumentHtml() {
        return htmlDocumentHtml;
    }

    protected void loadAllLazilyLoadedDocumentTypeSpecificAttributes() {
        // nothing lazily loaded
    }

    public int getDocumentTypeId() {
        return DOCTYPE_HTML;
    }

    public void saveDocument( DocumentMapper documentMapper, UserDomainObject user ) {
        documentMapper.saveHtmlDocument( this );
    }

    public void saveNewDocument( DocumentMapper documentMapper, UserDomainObject user ) {
        documentMapper.saveNewHtmlDocument( this );
    }

    public void initDocument( DocumentMapper documentMapper ) {
        documentMapper.initHtmlDocument( this );
    }

}