package imcode.server.document;

import com.imcode.imcms.servlet.admin.DocumentComposer;
import imcode.server.user.UserDomainObject;
import imcode.server.IMCServiceInterface;
import imcode.server.ApplicationServer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Hasse
 * Date: 2004-mar-02
 * Time: 17:41:36
 * To change this template use File | Settings | File Templates.
 */
public abstract class FormerExternalDocument extends DocumentDomainObject {
    public void processNewDocumentInformation( DocumentComposer documentInformation,
                                               DocumentComposer.NewDocumentParentInformation newDocumentParentInformation,
                                               UserDomainObject user, HttpServletRequest request,
                                               HttpServletResponse response ) throws IOException, ServletException {
        documentInformation.processNewFormerExternalDocument(newDocumentParentInformation, user, request, response, this );
    }

    public void saveDocument( DocumentMapper documentMapper, UserDomainObject user ) {
    }

    public void saveNewDocument( DocumentMapper documentMapper, UserDomainObject user ) {
    }

    public void initDocument( DocumentMapper documentMapper ) {
    }
}
