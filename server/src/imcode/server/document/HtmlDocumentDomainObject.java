/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-feb-28
 * Time: 20:47:40
 */
package imcode.server.document;

import com.imcode.imcms.servlet.admin.DocumentComposer;
import imcode.server.user.UserDomainObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HtmlDocumentDomainObject extends DocumentDomainObject {

    private String htmlDocumentHtml;

    public void setHtmlDocumentHtml( String htmlDocumentHtml ) {
        this.htmlDocumentHtml = htmlDocumentHtml;
    }

    public String getHtmlDocumentHtml() {
        return htmlDocumentHtml;
    }

    public int getDocumentTypeId() {
        return DOCTYPE_HTML;
    }

    public void processNewDocumentInformation( DocumentComposer documentInformation,
                                               DocumentComposer.NewDocumentParentInformation newDocumentParentInformation,
                                               UserDomainObject user, HttpServletRequest request,
                                               HttpServletResponse response ) throws IOException, ServletException {
        documentInformation.forwardToCreateNewHtmlDocumentPage( request, response, user );

    }

    public void saveNewDocument( DocumentMapper documentMapper ) {
        documentMapper.saveNewHtmlDocument( this );
    }

    public void initDocumentFromDb( DocumentMapper documentMapper ) {
        documentMapper.initHtmlDocumentFromDb(this) ;
    }

}