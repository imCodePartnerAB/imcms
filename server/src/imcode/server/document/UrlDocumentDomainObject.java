/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-feb-28
 * Time: 20:38:07
 */
package imcode.server.document;

import com.imcode.imcms.servlet.admin.DocumentInformation;
import imcode.server.user.UserDomainObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UrlDocumentDomainObject extends DocumentDomainObject {

    private String urlDocumentUrl;

    public String getUrlDocumentUrl() {
        return urlDocumentUrl;
    }

    public void setUrlDocumentUrl( String urlDocumentUrl ) {
        this.urlDocumentUrl = urlDocumentUrl;
    }

    public int getDocumentTypeId() {
        return DOCTYPE_URL;
    }

    public void processNewDocumentInformation( DocumentInformation documentInformation,
                                               DocumentInformation.NewDocumentParentInformation newDocumentParentInformation,
                                               UserDomainObject user, HttpServletRequest request,
                                               HttpServletResponse response ) throws IOException, ServletException {
        documentInformation.forwardToCreateNewUrlDocumentPage( request, response, user );
    }

    public void saveNewDocument( DocumentMapper documentMapper ) {
        documentMapper.saveNewUrlDocument( this );
    }

    public void initDocumentFromDb( DocumentMapper documentMapper ) {
        documentMapper.initUrlDocumentFromDb( this );
    }

}