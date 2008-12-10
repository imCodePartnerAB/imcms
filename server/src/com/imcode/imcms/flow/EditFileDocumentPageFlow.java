package com.imcode.imcms.flow;

import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import com.imcode.util.MultipartHttpServletRequest;
import imcode.server.Imcms;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.CounterStringFactory;
import imcode.util.Utility;
import imcode.util.io.InputStreamSource;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class EditFileDocumentPageFlow extends EditDocumentPageFlow {

    public static final String REQUEST_PARAMETER__FILE_DOC__FILE = "file";
    public static final String REQUEST_PARAMETER__FILE_DOC__MIME_TYPE = "mimetype";
    public static final String REQUEST_PARAMETER__SELECT_FILE_BUTTON_PREFIX = "select_file_";
    public static final String REQUEST_PARAMETER__DEFAULT_FILE = "default_file";
    public static final String REQUEST_PARAMETER__DELETE_FILE_BUTTON_PREFIX = "delete_file_";
    public static final String REQUEST_PARAMETER__FILE_DOC__SELECTED_FILE_ID = "selected_file_id";
    public static final String REQUEST_PARAMETER__SAVE_FILE_BUTTON = "save_file_button";
    public static final String REQUEST_PARAMETER__FILE_DOC__NEW_FILE_ID_PREFIX = "new_file_id_";

    private static final String URL_I15D_PAGE__FILEDOC = "/jsp/docadmin/file_document.jsp";

    private static final LocalizedMessage ERROR_MESSAGE__UNABLE_TO_AUTODETECT_MIMETYPE = new LocalizedMessage( "server/src/com/imcode/imcms/flow/EditFileDocumentPageFlow/unable_to_autodetect_mimetype" );
    private static final LocalizedMessage ERROR_MESSAGE__NO_FILE_UPLOADED = new LocalizedMessage( "server/src/com/imcode/imcms/flow/EditFileDocumentPageFlow/no_file_uploaded" );

    private MimeTypeRestriction mimeTypeRestriction = new ValidMimeTypeRestriction();
    private ServletContext servletContext;
    private FileDocumentDomainObject.FileDocumentFile unfinishedNewFile;

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

        updateFromRequestAndDispatchIfError( request, response );

    }

    private void updateFromRequestAndDispatchIfError( MultipartHttpServletRequest request,
                                                                        HttpServletResponse response ) throws IOException, ServletException {
        document.setTarget( EditDocumentInformationPageFlow.getTargetFromRequest( request, EditDocumentInformationPageFlow.REQUEST_PARAMETER__TARGET));

        final FileDocumentDomainObject fileDocument = (FileDocumentDomainObject)document;
        String defaultFileId = request.getParameter( REQUEST_PARAMETER__DEFAULT_FILE );
        if ( null != defaultFileId ) {
            fileDocument.setDefaultFileId( defaultFileId );
        }

        String selectedFileId = changeFileIds( request, fileDocument );

        FileDocumentDomainObject.FileDocumentFile file = fileDocument.getFile( selectedFileId );
        boolean isNewFile = null == file;
        if ( isNewFile ) {
            file = getUnfinishedNewFile();
        } else {
            unfinishedNewFile = null ;
        }

        boolean isChecked = !isNewFile || null != request.getParameter( REQUEST_PARAMETER__SAVE_FILE_BUTTON );
        if ( isChecked ) {
            FileItem fileItem = request.getParameterFileItem( REQUEST_PARAMETER__FILE_DOC__FILE );
            boolean fileUploaded = 0 != fileItem.getSize();
            if ( fileUploaded ) {
                String fileItemName = fileItem.getName();
                if ( StringUtils.isNotBlank( fileItemName ) ) {
                    file.setFilename( fileItemName );
                }
                file.setInputStreamSource( new FileItemInputStreamSource( fileItem ) );
            }

            String mimeType = getMimeTypeFromRequestAndFilename( request, file.getFilename() );
            LocalizedMessage errorMessage = null;
            if ( null == file.getInputStreamSource() ) {
                errorMessage = ERROR_MESSAGE__NO_FILE_UPLOADED;
            } else if ( StringUtils.isBlank( mimeType ) ) {
                errorMessage = ERROR_MESSAGE__UNABLE_TO_AUTODETECT_MIMETYPE;
                setFileDocumentMimeTypeIfAllowed( file, FileDocumentDomainObject.MIME_TYPE__UNKNOWN_DEFAULT );
            } else if ( !mimeTypeRestriction.allows( mimeType ) ) {
                errorMessage = mimeTypeRestriction.getErrorMessage();
                String mimeTypeForFilename = getMimeTypeForFilename( file.getFilename() );
                setFileDocumentMimeTypeIfAllowed( file, mimeTypeForFilename );
            } else {
                file.setMimeType( mimeType );
                if ( isNewFile ) {
                    String newFileId = (String)Utility.findMatch(new CounterStringFactory(fileDocument.getFiles().size()+1), new UniqueFileIdPredicate( fileDocument ));
                    selectedFileId = newFileId ;
                    unfinishedNewFile = null ;
                }
                fileDocument.addFile( selectedFileId, file );
            }

            if ( null != errorMessage ) {
                FileDocumentEditPage fileDocumentEditPage = createFileDocumentEditPage( selectedFileId );
                fileDocumentEditPage.setErrorMessage( errorMessage );
                fileDocumentEditPage.forward( request, response );
            }
        }
    }

    private String changeFileIds( MultipartHttpServletRequest request,
                                       final FileDocumentDomainObject fileDocument ) {
        String selectedFileId = request.getParameter( REQUEST_PARAMETER__FILE_DOC__SELECTED_FILE_ID );

        Map fileDocumentFiles = fileDocument.getFiles();
        for ( Iterator iterator = IteratorUtils.unmodifiableIterator( fileDocumentFiles.keySet().iterator() ); iterator.hasNext(); ) {
            String fileId = (String)iterator.next();
            String newFileId = request.getParameter( REQUEST_PARAMETER__FILE_DOC__NEW_FILE_ID_PREFIX
                                                              + fileId );
            if (  StringUtils.isNotBlank( newFileId )
                 && !selectedFileId.equals( newFileId )
                 && null == fileDocument.getFile( newFileId ) ) {
                fileDocument.changeFileId( fileId, newFileId );
                if ( fileId.equals( selectedFileId ) ) {
                    selectedFileId = newFileId;
                }
            }
        }
        return selectedFileId;
    }

    private void setFileDocumentMimeTypeIfAllowed( FileDocumentDomainObject.FileDocumentFile file,
                                                   String mimeTypeForFilename ) {
        if ( mimeTypeRestriction.allows( mimeTypeForFilename ) ) {
            file.setMimeType( mimeTypeForFilename );
        }
    }

    private FileDocumentEditPage createFileDocumentEditPage( String fileId ) {
        FileDocumentDomainObject.FileDocumentFile file = ( (FileDocumentDomainObject)document ).getFile( fileId );
        if ( null == file ) {
            file = getUnfinishedNewFile();
        }
        return new FileDocumentEditPage( mimeTypeRestriction, fileId, file );
    }

    private FileDocumentDomainObject.FileDocumentFile getUnfinishedNewFile() {
        if ( null == unfinishedNewFile ) {
            unfinishedNewFile = new FileDocumentDomainObject.FileDocumentFile();
        }
        return unfinishedNewFile;
    }

    protected void dispatchToFirstPage( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        createFileDocumentEditPage( null ).forward( request, response );
    }

    protected void dispatchFromEditPage( HttpServletRequest r, HttpServletResponse response, String page ) throws IOException, ServletException {
        MultipartHttpServletRequest request = (MultipartHttpServletRequest)r;
        FileDocumentDomainObject fileDocument = (FileDocumentDomainObject)document;

        updateFromRequestAndDispatchIfError( request, response );
        String selectedFileId = null ;

        if ( !response.isCommitted() ) {
            Map files = fileDocument.getFiles();
            for ( Iterator iterator = files.entrySet().iterator() ; iterator.hasNext(); ) {
                Map.Entry entry = (Map.Entry)iterator.next();
                String fileId = (String)entry.getKey();
                FileDocumentDomainObject.FileDocumentFile fileDocumentFile = (FileDocumentDomainObject.FileDocumentFile)entry.getValue();

                if ( null
                     != request.getParameter( REQUEST_PARAMETER__SELECT_FILE_BUTTON_PREFIX + fileId ) ) {
                    selectedFileId = fileId;
                }
                if ( null
                     != request.getParameter( REQUEST_PARAMETER__DELETE_FILE_BUTTON_PREFIX + fileId )
                     || null == fileDocumentFile.getInputStreamSource() ) {
                    unfinishedNewFile = fileDocument.removeFile( fileId );
                }
            }
            createFileDocumentEditPage( selectedFileId ).forward( request, response );
        }
    }

    private String getMimeTypeFromRequestAndFilename( MultipartHttpServletRequest request, String filename ) {
        final DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
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
            return null;
        }
        return servletContext.getMimeType( filename.toLowerCase() );
    }

    private String getMimeTypeIfTreatedAsFilenameExtension( String mimeType ) {
        String filenameExtension = mimeType;
        if ( '.' != filenameExtension.charAt( 0 ) ) {
            filenameExtension = '.' + filenameExtension;
        }
        String mimeTypeFromFilenameExtension = servletContext.getMimeType( '_'
                                                                         + filenameExtension );
        if ( null == mimeTypeFromFilenameExtension ) {
            mimeTypeFromFilenameExtension = mimeType ;
        }
        return mimeTypeFromFilenameExtension;
    }

    public static class FileItemInputStreamSource implements InputStreamSource {

        private final FileItem fileItem;

        public FileItemInputStreamSource( FileItem fileItem ) {
            this.fileItem = fileItem;
        }

        public InputStream getInputStream() throws IOException {
            return fileItem.getInputStream();
        }

        public long getSize() throws IOException {
            return fileItem.getSize() ;
        }
    }

    public static class FileDocumentEditPage {

        private final static String REQUEST_ATTRIBUTE__FILE_DOCUMENT_EDIT_PAGE = "fileDocumentEditPage";

        private LocalizedMessage errorMessage;
        private MimeTypeRestriction pageMimeTypeRestriction;
        private String selectedFileId;
        private FileDocumentDomainObject.FileDocumentFile selectedFile;

        public FileDocumentEditPage( MimeTypeRestriction allowedMimeTypes, String fileId,
                                     FileDocumentDomainObject.FileDocumentFile file ) {
            this.pageMimeTypeRestriction = allowedMimeTypes;
            this.selectedFileId = fileId;
            this.selectedFile = file;
        }

        public MimeTypeRestriction getPageMimeTypeRestriction() {
            return pageMimeTypeRestriction;
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

        public FileDocumentDomainObject.FileDocumentFile getSelectedFile() {
            return selectedFile;
        }

        public String getSelectedFileId() {
            return selectedFileId;
        }
    }

    public interface MimeTypeRestriction extends Serializable {

        boolean allows( String mimeType );

        LocalizedMessage getErrorMessage();
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
            return Pattern.compile( "^(x-[a-z-]+|application|audio|image|message|model|multipart|text|video)/", Pattern.CASE_INSENSITIVE ).matcher( mimeType ).find();
        }

        public LocalizedMessage getErrorMessage() {
            return errorMessage;
        }
    }

    public static class ArrayMimeTypeRestriction extends ValidMimeTypeRestriction {

        private String[] allowedMimeTypes;

        public ArrayMimeTypeRestriction( String[] allowedMimeTypes, LocalizedMessage errorMessage ) {
            this.allowedMimeTypes = (String[])ArrayUtils.clone(allowedMimeTypes);
            this.errorMessage = errorMessage;
        }

        public boolean allows( String mimeType ) {
            return super.allows( mimeType ) && ArrayUtils.contains( allowedMimeTypes, mimeType );
        }

    }

    private static class UniqueFileIdPredicate implements Predicate {

        private final FileDocumentDomainObject fileDocument;

        UniqueFileIdPredicate( FileDocumentDomainObject fileDocument ) {
            this.fileDocument = fileDocument;
        }

        public boolean evaluate( Object object ) {
            return null == fileDocument.getFile( (String)object );
        }
    }

}
