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
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class EditFileDocumentPageFlow extends EditDocumentPageFlow {

    private static final String MIME_TYPE__APPLICATION_OCTET_STREAM = "application/octet-stream";
    private static final String MIME_TYPE__UNKNOWN_DEFAULT = MIME_TYPE__APPLICATION_OCTET_STREAM;
    public static final String REQUEST_PARAMETER__FILE_DOC__FILE = "file";
    public static final String REQUEST_PARAMETER__FILE_DOC__MIME_TYPE = "mimetype";
    private static final String URL_I15D_PAGE__FILEDOC = "/jsp/docadmin/file_document.jsp";

    private MimeTypeRestriction mimeTypeRestriction = new ValidMimeTypeRestriction();
    private ServletContext servletContext;
    private static final LocalizedMessage ERROR_MESSAGE__UNABLE_TO_AUTODETECT_MIMETYPE = new LocalizedMessage( "server/src/com/imcode/imcms/flow/EditFileDocumentPageFlow/unable_to_autodetect_mimetype" );

    public EditFileDocumentPageFlow( FileDocumentDomainObject document, ServletContext servletContext,
                                     DispatchCommand returnCommand,
                                     SaveDocumentCommand saveDocumentCommand,
                                     MimeTypeRestriction mimeTypeRestriction ) {
        super( document, returnCommand, saveDocumentCommand );
        this.servletContext = servletContext;
        if ( null != mimeTypeRestriction ) {
            this.mimeTypeRestriction = mimeTypeRestriction;
        }
    }

    protected void dispatchOkFromEditPage( HttpServletRequest r, HttpServletResponse response ) throws IOException, ServletException {
        MultipartHttpServletRequest request = (MultipartHttpServletRequest)r;

        FileDocumentDomainObject fileDocument = (FileDocumentDomainObject)document;
        FileItem fileItem = request.getParameterFileItem( REQUEST_PARAMETER__FILE_DOC__FILE );
        String filename = fileDocument.getFilename() ;
        if ( 0 != fileItem.getSize() ) {
            filename = fileItem.getName();
            if ( StringUtils.isNotBlank( filename ) ) {
                fileDocument.setFilename( filename );
            }
            fileDocument.setInputStreamSource( new FileItemInputStreamSource( fileItem ) );
        }

        String mimeType = getMimeTypeFromRequestAndFilename( request, filename );
        LocalizedMessage mimeTypeErrorMessage = null;
        if ( StringUtils.isBlank( mimeType ) ) {
            mimeTypeErrorMessage = ERROR_MESSAGE__UNABLE_TO_AUTODETECT_MIMETYPE;
            setFileDocumentMimeTypeIfAllowed( fileDocument, MIME_TYPE__UNKNOWN_DEFAULT );
        } else if ( !mimeTypeRestriction.allows( mimeType ) ) {
            mimeTypeErrorMessage = mimeTypeRestriction.getErrorMessage();
            setFileDocumentMimeTypeIfAllowed( fileDocument, getMimeTypeForFilename( filename ) );
        }

        if ( null != mimeTypeErrorMessage ) {
            FileDocumentEditPage fileDocumentEditPage = createFileDocumentEditPage();
            fileDocumentEditPage.setErrorMessage( mimeTypeErrorMessage );
            fileDocumentEditPage.forward( request, response );
        } else {
            fileDocument.setMimeType( mimeType );
        }
    }

    private void setFileDocumentMimeTypeIfAllowed( FileDocumentDomainObject fileDocument, String mimeTypeForFilename ) {
        if (mimeTypeRestriction.allows( mimeTypeForFilename )) {
            fileDocument.setMimeType( mimeTypeForFilename );
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

    private String getMimeTypeFromRequestAndFilename( MultipartHttpServletRequest request, String filename ) {
        final DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
        Set predefinedMimeTypes = new HashSet( Arrays.asList( documentMapper.getAllMimeTypes() ) );
        String[] mimeTypeParameters = request.getParameterValues( REQUEST_PARAMETER__FILE_DOC__MIME_TYPE );
        String mimeType = null;
        for ( int i = 0; i < mimeTypeParameters.length; i++ ) {
            mimeType = mimeTypeParameters[i].trim().toLowerCase();
            if ( predefinedMimeTypes.contains( mimeType ) ) {
                break;
            }
            if ( StringUtils.isBlank( mimeType ) ) {
                mimeType = getMimeTypeForFilename( filename );
            } else if ( -1 == mimeType.indexOf( '/' ) ) {
                mimeType = getMimeTypeIfTreatedAsFilenameExtension( mimeType );
            }
        }
        return mimeType;
    }

    private String getMimeTypeForFilename( String filename ) {
        if ( null == filename ) {
            return null ;
        }
        return servletContext.getMimeType( filename.toLowerCase() );
    }

    private String getMimeTypeIfTreatedAsFilenameExtension( String mimeType ) {
        String mimeTypeTreatedAsFilenameExtension = mimeType;
        if ( '.' != mimeTypeTreatedAsFilenameExtension.charAt( 0 ) ) {
            mimeTypeTreatedAsFilenameExtension = '.' + mimeTypeTreatedAsFilenameExtension;
        }
        mimeTypeTreatedAsFilenameExtension = servletContext.getMimeType( '_'
                                                                         + mimeTypeTreatedAsFilenameExtension );
        if ( null != mimeTypeTreatedAsFilenameExtension ) {
            mimeType = mimeTypeTreatedAsFilenameExtension;
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

    public interface MimeTypeRestriction {
        boolean allows( String mimeType ) ;
        LocalizedMessage getErrorMessage() ;
    }

    public static class ValidMimeTypeRestriction implements MimeTypeRestriction {

        protected LocalizedMessage errorMessage;
        private static final LocalizedMessage ERROR__INVALID_MIMETYPE = new LocalizedMessage( "server/src/com/imcode/imcms/flow/EditFileDocumentPageFlow/invalid_mimetype" );

        public boolean allows( String mimeType ) {
            if ( StringUtils.isBlank( mimeType ) || !isValidMimeType( mimeType ) ) {
                errorMessage = ERROR__INVALID_MIMETYPE;
                return false;
            }
            return true;
        }

        private boolean isValidMimeType( String mimeType ) {
            return Pattern.compile( "^(x-[a-z-]|application|audio|image|message|model|multipart|text|video)/", Pattern.CASE_INSENSITIVE ).matcher( mimeType ).find();
        }

        public LocalizedMessage getErrorMessage() {
            return errorMessage;
        }
    }

    public static class ArrayMimeTypeRestriction extends ValidMimeTypeRestriction {

        private String[] allowedMimeTypes;

        public ArrayMimeTypeRestriction( String[] allowedMimeTypes, LocalizedMessage errorMessage ) {
            this.allowedMimeTypes = allowedMimeTypes;
            this.errorMessage = errorMessage;
        }

        public boolean allows( String mimeType ) {
            return super.allows( mimeType ) && ArrayUtils.contains( allowedMimeTypes, mimeType );
        }

    }

}
