package com.imcode.imcms.flow;

import imcode.server.ApplicationServer;
import imcode.server.document.DocumentMapper;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.InputStreamSource;
import imcode.util.MultipartHttpServletRequest;
import imcode.util.Utility;
import org.apache.commons.fileupload.FileItem;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.imcode.imcms.servlet.WebComponent;

public class EditFileDocumentPageFlow extends EditDocumentPageFlow {

    private ServletContext servletContext;
    private static final String MIME_TYPE__APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String MIME_TYPE__UNKNOWN_DEFAULT = MIME_TYPE__APPLICATION_OCTET_STREAM;
    public static final String REQUEST_PARAMETER__FILE_DOC__FILE = "file";
    public static final String REQUEST_PARAMETER__FILE_DOC__MIME_TYPE = "mimetype";
    private static final String URL_I15D_PAGE__FILEDOC = "/jsp/docadmin/file_document.jsp";

    public EditFileDocumentPageFlow( FileDocumentDomainObject document, ServletContext servletContext,
                                     WebComponent.DispatchCommand returnCommand,
                                     SaveDocumentCommand saveDocumentCommand ) {
        super( document, returnCommand, saveDocumentCommand );
        this.servletContext = servletContext;
    }

    protected void dispatchOkFromEditPage( HttpServletRequest r, HttpServletResponse response ) throws IOException {
        MultipartHttpServletRequest request = (MultipartHttpServletRequest)r;
        FileDocumentDomainObject fileDocument = (FileDocumentDomainObject)document;
        final FileItem fileItem = request.getParameterFileItem( REQUEST_PARAMETER__FILE_DOC__FILE );
        String fileName = fileItem.getName();
        if ( !"".equals( fileName ) ) {
            fileDocument.setFilename( fileName );
            if ( 0 != fileItem.getSize() ) {
                fileDocument.setInputStreamSource( new FileItemInputStreamSource( fileItem ) );
            }
        }
        fileDocument.setMimeType( getMimeTypeFromRequest( request ) );
    }

    protected void dispatchToFirstPage( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        request.getRequestDispatcher( URL_I15D_PAGE__PREFIX + user.getLanguageIso639_2() + URL_I15D_PAGE__FILEDOC ).forward( request, response );
    }

    protected void dispatchFromEditPage( HttpServletRequest request, HttpServletResponse response, String page ) throws IOException, ServletException {
    }

    private String getMimeTypeFromRequest( MultipartHttpServletRequest request ) {
        FileItem fileItem = request.getParameterFileItem( REQUEST_PARAMETER__FILE_DOC__FILE );
        String filename = fileItem.getName();
        final DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
        Set predefinedMimeTypes = new HashSet( Arrays.asList( documentMapper.getAllMimeTypes() ) );
        String[] mimeTypeParameters = request.getParameterValues( REQUEST_PARAMETER__FILE_DOC__MIME_TYPE );
        String mimeType = null;
        for ( int i = 0; i < mimeTypeParameters.length; i++ ) {
            mimeType = mimeTypeParameters[i].trim().toLowerCase();
            if ( predefinedMimeTypes.contains( mimeType ) ) {
                break;
            }
            if ( "".equals( mimeType ) ) {
                if ( null != filename ) {
                    filename = filename.toLowerCase();
                    mimeType = servletContext.getMimeType( filename );
                }
            } else if ( -1 == mimeType.indexOf( '/' ) ) {
                if ( '.' != mimeType.charAt( 0 ) ) {
                    mimeType = '.' + mimeType;
                }
                mimeType = servletContext.getMimeType( '_' + mimeType );
            }
            if ( null == mimeType || "".equals( mimeType ) ) {
                mimeType = MIME_TYPE__UNKNOWN_DEFAULT;
            }
        }
        return mimeType;
    }

    public static class FileItemInputStreamSource implements InputStreamSource {

        private final FileItem fileItem;

        public FileItemInputStreamSource( FileItem fileItem ) {
            this.fileItem = fileItem;
        }

        public InputStream getInputStream() throws IOException {
            return fileItem.getInputStream();
        }
    }
}
