/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-feb-28
 * Time: 20:36:57
 */
package imcode.server.document;

import com.imcode.imcms.servlet.admin.DocumentComposer;
import imcode.server.user.UserDomainObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class FileDocumentDomainObject extends DocumentDomainObject {

    private String fileDocumentFilename;
    private String fileDocumentMimeType;
    private InputStream fileDocumentInputStream;

    public String getFileDocumentFilename() {
        return fileDocumentFilename;
    }

    public void setFileDocumentFilename( String v ) {
        this.fileDocumentFilename = v;
    }

    public String getFileDocumentMimeType() {
        return fileDocumentMimeType;
    }

    public void setFileDocumentMimeType( String fileDocumentMimeType ) {
        this.fileDocumentMimeType = fileDocumentMimeType;
    }

    public void setFileDocumentInputStream( InputStream fileDocumentInputStream ) {
        this.fileDocumentInputStream = fileDocumentInputStream;
    }

    public InputStream getFileDocumentInputStream() {
        return fileDocumentInputStream;
    }

    public int getDocumentTypeId() {
        return DOCTYPE_FILE;
    }

    public void processNewDocumentInformation( DocumentComposer documentInformation,
                                               DocumentComposer.NewDocumentParentInformation newDocumentParentInformation,
                                               UserDomainObject user, HttpServletRequest request,
                                               HttpServletResponse response ) throws IOException, ServletException {
        documentInformation.forwardToCreateNewFileDocumentPage( request, response, user );

    }

    public void saveNewDocument( DocumentMapper documentMapper ) throws IOException {
        documentMapper.saveNewFileDocument(this);
    }

    public void initDocumentFromDb( DocumentMapper documentMapper ) {
        documentMapper.initFileDocumentFromDb(this);
    }

}