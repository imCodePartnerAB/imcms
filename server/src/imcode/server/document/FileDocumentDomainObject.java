/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-feb-28
 * Time: 20:36:57
 */
package imcode.server.document;

import com.imcode.imcms.servlet.admin.DocumentComposer;
import imcode.server.user.UserDomainObject;
import imcode.util.InputStreamSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FileDocumentDomainObject extends DocumentDomainObject {

    private String filename;
    private String mimeType;
    private InputStreamSource inputStreamSource;

    public String getFilename() {
        return filename;
    }

    public void setFilename( String v ) {
        this.filename = v;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType( String mimeType ) {
        this.mimeType = mimeType;
    }

    public void setInputStreamSource( InputStreamSource inputStreamSource ) {
        this.inputStreamSource = inputStreamSource;
    }

    public InputStreamSource getInputStreamSource() {
        return inputStreamSource ;
    }

    public int getDocumentTypeId() {
        return DOCTYPE_FILE;
    }

    public void processNewDocumentInformation( DocumentComposer documentInformation,
                                               DocumentComposer.NewDocumentParentInformation newDocumentParentInformation,
                                               UserDomainObject user, HttpServletRequest request,
                                               HttpServletResponse response ) throws IOException, ServletException {
        documentInformation.processNewFileDocumentInformation( request, response, user );

    }

    public void saveDocument( DocumentMapper documentMapper, UserDomainObject user ) {
        documentMapper.saveFileDocument( this ) ;
    }

    public void saveNewDocument( DocumentMapper documentMapper, UserDomainObject user ) {
        documentMapper.saveNewFileDocument(this);
    }

    public void initDocument( DocumentMapper documentMapper ) {
        documentMapper.initFileDocument(this);
    }

}