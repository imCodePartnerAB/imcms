/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-feb-28
 * Time: 20:38:07
 */
package imcode.server.document;

import com.imcode.imcms.servlet.admin.DocumentComposer;
import com.imcode.imcms.servlet.admin.SaveNewMeta;
import imcode.server.user.UserDomainObject;
import imcode.server.IMCServiceInterface;
import imcode.server.ApplicationServer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ConferenceDocumentDomainObject extends FormerExternalDocument {

    public int getDocumentTypeId() {
        return DOCTYPE_CONFERENCE;
    }
}