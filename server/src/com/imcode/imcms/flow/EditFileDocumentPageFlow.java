package com.imcode.imcms.flow;

import imcode.server.ApplicationServer;
import imcode.server.document.DocumentMapper;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.InputStreamSource;
import imcode.util.LocalizedMessage;
import imcode.util.MultipartHttpServletRequest;
import imcode.util.Utility;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.ArrayUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EditFileDocumentPageFlow extends EditDocumentPageFlow {

    private ServletContext servletContext;
    private static final String MIME_TYPE__APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String MIME_TYPE__UNKNOWN_DEFAULT = MIME_TYPE__APPLICATION_OCTET_STREAM;
    public static final String REQUEST_PARAMETER__FILE_DOC__FILE = "file";
    public static final String REQUEST_PARAMETER__FILE_DOC__MIME_TYPE = "mimetype";
    private static final String URL_I15D_PAGE__FILEDOC = "/jsp/docadmin/file_document.jsp";
    private MimeTypeRestriction mimeTypeRestriction = new NoMimeTypeRestriction();

    public EditFileDocumentPageFlow( FileDocumentDomainObject document, ServletContext servletContext,
                                     DispatchCommand returnCommand,
                                     SaveDocumentCommand saveDocumentCommand, MimeTypeRestriction mimeTypeRestriction ) {
        super( document, returnCommand, saveDocumentCommand );
        this.servletContext = servletContext;
        if ( null != mimeTypeRestriction ) {
            this.mimeTypeRestriction = mimeTypeRestriction;
        }
    }

    protected void dispatchOkFromEditPage( HttpServletRequest r, HttpServletResponse response ) throws IOException, ServletException {
        MultipartHttpServletRequest request = (MultipartHttpServletRequest)r;
        String mimeType = getMimeTypeFromRequest( request );
        boolean mimeTypeAllowed = null == mimeTypeRestriction || mimeTypeRestriction.allows( mimeType );
        if ( !mimeTypeAllowed ) {
            FileDocumentEditPage fileDocumentEditPage = createFileDocumentEditPage();
            fileDocumentEditPage.setErrorMessage( mimeTypeRestriction.getErrorMessage() );
            fileDocumentEditPage.forward( request, response );
        } else {
            FileDocumentDomainObject fileDocument = (FileDocumentDomainObject)document;
            fileDocument.setMimeType( mimeType );
            final FileItem fileItem = request.getParameterFileItem( REQUEST_PARAMETER__FILE_DOC__FILE );
            String fileName = fileItem.getName();
            if ( !"".equals( fileName ) ) {
                fileDocument.setFilename( fileName );
                if ( 0 != fileItem.getSize() ) {
                    fileDocument.setInputStreamSource( new FileItemInputStreamSource( fileItem ) );
                }
            }
        }
    }

    private FileDocumentEditPage createFileDocumentEditPage() {
        return new FileDocumentEditPage( mimeTypeRestriction );
    }

    protected void dispatchToFirstPage( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        createFileDocumentEditPage().forward( request, response );
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

    public static class MimeTypeRestriction {

        private String[] allowedMimeTypes;
        private LocalizedMessage localizedErrorMessage;

        public MimeTypeRestriction( String[] allowedMimeTypes, LocalizedMessage localizedErrorMessage ) {
            this.allowedMimeTypes = allowedMimeTypes;
            this.localizedErrorMessage = localizedErrorMessage;
        }

        public boolean allows( String mimeType ) {
            return ArrayUtils.contains( allowedMimeTypes, mimeType );
        }

        public LocalizedMessage getErrorMessage() {
            return localizedErrorMessage;
        }
    }

    public static class FileDocumentEditPage {

        private final static String REQUEST_ATTRIBUTE__FILE_DOCUMENT_EDIT_PAGE = "fileDocumentEditPage";

        private LocalizedMessage errorMessage;
        private MimeTypeRestriction mimeTypeRestriction;

        public FileDocumentEditPage( MimeTypeRestriction allowedMimeTypes ) {
            this.mimeTypeRestriction = allowedMimeTypes;
        }

        public MimeTypeRestriction getMimeTypeRestriction() {
            return mimeTypeRestriction;
        }

        public static FileDocumentEditPage fromRequest( HttpServletRequest request ) {
            return (FileDocumentEditPage)request.getAttribute( REQUEST_ATTRIBUTE__FILE_DOCUMENT_EDIT_PAGE );
        }

        public void forward( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
            request.setAttribute( REQUEST_ATTRIBUTE__FILE_DOCUMENT_EDIT_PAGE, this );
            UserDomainObject user = Utility.getLoggedOnUser( request );
            request.getRequestDispatcher( URL_I15D_PAGE__PREFIX + user.getLanguageIso639_2() + URL_I15D_PAGE__FILEDOC ).forward( request, response );
        }

        public void setErrorMessage( LocalizedMessage localizedErrorMessage ) {
            this.errorMessage = localizedErrorMessage;
        }

        public LocalizedMessage getErrorMessage() {
            return errorMessage;
        }

    }

    private static class NoMimeTypeRestriction extends MimeTypeRestriction {

        private NoMimeTypeRestriction() {
            super( null, null );
        }

        public boolean allows( String mimeType ) {
            return true;
        }
    }
}
