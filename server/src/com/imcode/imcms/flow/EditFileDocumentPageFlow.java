package com.imcode.imcms.flow;

import imcode.server.ApplicationServer;
import imcode.server.document.DocumentMapper;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.InputStreamSource;
import imcode.util.LocalizedMessage;
import imcode.util.MultipartHttpServletRequest;
import imcode.util.Utility;
import org.apache.commons.collections.Factory;
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
import java.util.*;
import java.util.regex.Pattern;

public class EditFileDocumentPageFlow extends EditDocumentPageFlow {

    private static final String MIME_TYPE__APPLICATION_OCTET_STREAM = "application/octet-stream";
    private static final String MIME_TYPE__UNKNOWN_DEFAULT = MIME_TYPE__APPLICATION_OCTET_STREAM;
    public static final String REQUEST_PARAMETER__FILE_DOC__FILE = "file";
    public static final String REQUEST_PARAMETER__FILE_DOC__MIME_TYPE = "mimetype";
    public static final String REQUEST_PARAMETER__EDIT_VARIANT_BUTTON_PREFIX = "select_variant";
    public static final String REQUEST_PARAMETER__SELECT_DEFAULT_VARIANT_BUTTON_PREFIX = "select_default_variant";
    public static final String REQUEST_PARAMETER__DELETE_VARIANT_BUTTON_PREFIX = "delete_variant";
    public static final String REQUEST_PARAMETER__FILE_DOC__VARIANT_NAME = "variant_name";
    public static final String REQUEST_PARAMETER__FILE_DOC__OLD_VARIANT_NAME = "old_variant_name";
    public static final String REQUEST_PARAMETER__FILE_DOC__NEW_VARIANT_NAME = "new_variant_name";
    public static final String REQUEST_PARAMETER__NEW_VARIANT_BUTTON = "new_variant_button";

    private static final String URL_I15D_PAGE__FILEDOC = "/jsp/docadmin/file_document.jsp";

    private MimeTypeRestriction mimeTypeRestriction = new ValidMimeTypeRestriction();
    private ServletContext servletContext;
    private static final LocalizedMessage ERROR_MESSAGE__UNABLE_TO_AUTODETECT_MIMETYPE = new LocalizedMessage( "server/src/com/imcode/imcms/flow/EditFileDocumentPageFlow/unable_to_autodetect_mimetype" );
    private static final LocalizedMessage ERROR_MESSAGE__NO_FILE_UPLOADED = new LocalizedMessage( "server/src/com/imcode/imcms/flow/EditFileDocumentPageFlow/no_file_uploaded" );

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

        setSelectedFileVariantFromRequestAndDispatchIfError( request, response );

    }

    private void setSelectedFileVariantFromRequestAndDispatchIfError( MultipartHttpServletRequest request,
                                                                      HttpServletResponse response ) throws IOException, ServletException {
        final FileDocumentDomainObject fileDocument = (FileDocumentDomainObject)document;
        String oldFileVariantName = request.getParameter( REQUEST_PARAMETER__FILE_DOC__OLD_VARIANT_NAME );
        String newFileVariantName = request.getParameter( REQUEST_PARAMETER__FILE_DOC__NEW_VARIANT_NAME );
        if ( null == newFileVariantName ) {
            newFileVariantName = oldFileVariantName;
        }
        FileItem fileItem = request.getParameterFileItem( REQUEST_PARAMETER__FILE_DOC__FILE );
        FileDocumentDomainObject.FileDocumentFile fileVariant = fileDocument.getFileVariant( oldFileVariantName );
        if ( null == fileVariant ) {
            fileVariant = new FileDocumentDomainObject.FileDocumentFile();
        }
        if ( !oldFileVariantName.equals( newFileVariantName ) ) {
            newFileVariantName = findUniqueName( new UniqueFileVariantNamePredicate( fileDocument ), new CounterExtensionNameFactory( newFileVariantName ) );
            fileDocument.removeFileVariant( oldFileVariantName );
            fileDocument.addFileVariant( newFileVariantName, fileVariant );
        }
        boolean fileUploaded = 0 != fileItem.getSize();
        if ( fileUploaded ) {
            String fileItemName = fileItem.getName();
            if ( StringUtils.isNotBlank( fileItemName ) ) {
                fileVariant.setFilename( fileItemName );
            }
            fileVariant.setInputStreamSource( new FileItemInputStreamSource( fileItem ) );
        }
        String mimeType = getMimeTypeFromRequestAndFilename( request, fileVariant.getFilename() );

        LocalizedMessage errorMessage = null;
        if ( null == fileVariant.getInputStreamSource() ) {
            errorMessage = ERROR_MESSAGE__NO_FILE_UPLOADED;
        } else if ( StringUtils.isBlank( mimeType ) ) {
            errorMessage = ERROR_MESSAGE__UNABLE_TO_AUTODETECT_MIMETYPE;
            setFileDocumentMimeTypeIfAllowed( fileVariant, MIME_TYPE__UNKNOWN_DEFAULT );
        } else if ( !mimeTypeRestriction.allows( mimeType ) ) {
            errorMessage = mimeTypeRestriction.getErrorMessage();
            setFileDocumentMimeTypeIfAllowed( fileVariant, getMimeTypeForFilename( fileVariant.getFilename() ) );
        } else {
            fileVariant.setMimeType( mimeType );
            fileDocument.addFileVariant( newFileVariantName, fileVariant );
        }

        if ( null != errorMessage ) {
            FileDocumentEditPage fileDocumentEditPage = createFileDocumentEditPage( oldFileVariantName );
            fileDocumentEditPage.setErrorMessage( errorMessage );
            fileDocumentEditPage.forward( request, response );
        }
    }

    private String findUniqueName( Predicate uniqueNamePredicate, Factory nameFactory ) {
        String uniqueName ;
        do {
             uniqueName = (String)nameFactory.create();
        } while ( !uniqueNamePredicate.evaluate( uniqueName ) ) ;
        return uniqueName;
    }

    private void setFileDocumentMimeTypeIfAllowed( FileDocumentDomainObject.FileDocumentFile fileDocumentFile,
                                                   String mimeTypeForFilename ) {
        if ( mimeTypeRestriction.allows( mimeTypeForFilename ) ) {
            fileDocumentFile.setMimeType( mimeTypeForFilename );
        }
    }

    private FileDocumentEditPage createFileDocumentEditPage( String fileVariantName ) {
        FileDocumentDomainObject.FileDocumentFile fileVariant = ( (FileDocumentDomainObject)document ).getFileVariant( fileVariantName );
        if ( null == fileVariant ) {
            fileVariant = new FileDocumentDomainObject.FileDocumentFile();
        }
        return new FileDocumentEditPage( mimeTypeRestriction, fileVariantName, fileVariant );
    }

    protected void dispatchToFirstPage( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        createFileDocumentEditPage( ( (FileDocumentDomainObject)document ).getDefaultFileVariantName() ).forward( request, response );
    }

    protected void dispatchFromEditPage( HttpServletRequest r, HttpServletResponse response, String page ) throws IOException, ServletException {
        MultipartHttpServletRequest request = (MultipartHttpServletRequest)r;

        String selectedFileVariantName = request.getParameter( REQUEST_PARAMETER__FILE_DOC__OLD_VARIANT_NAME );
        FileDocumentDomainObject fileDocument = (FileDocumentDomainObject)document;
        if ( null != request.getParameter( REQUEST_PARAMETER__NEW_VARIANT_BUTTON ) ) {
            setSelectedFileVariantFromRequestAndDispatchIfError( request, response );
            if ( !response.isCommitted() ) {
                String uniqueFileVariantName = findUniqueName( new UniqueFileVariantNamePredicate( fileDocument ), new CounterNameFactory() );
                fileDocument.addFileVariant( uniqueFileVariantName, new FileDocumentDomainObject.FileDocumentFile() );
                selectedFileVariantName = uniqueFileVariantName;
            }
        } else {
            Map fileVariants = ( (FileDocumentDomainObject)this.document ).getFileVariants();
            for ( Iterator iterator = fileVariants.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry entry = (Map.Entry)iterator.next();
                String fileVariantName = (String)entry.getKey();
                FileDocumentDomainObject.FileDocumentFile fileVariant = (FileDocumentDomainObject.FileDocumentFile)entry.getValue();

                if ( null
                     != request.getParameter( REQUEST_PARAMETER__SELECT_DEFAULT_VARIANT_BUTTON_PREFIX + "_"
                                              + fileVariantName ) ) {
                    fileDocument.setDefaultFileVariantName( fileVariantName );
                    selectedFileVariantName = fileVariantName ;
                } else if ( null
                            != request.getParameter( REQUEST_PARAMETER__EDIT_VARIANT_BUTTON_PREFIX + "_"
                                                     + fileVariantName ) ) {
                    selectedFileVariantName = fileVariantName;
                } else if ( null
                            != request.getParameter( REQUEST_PARAMETER__DELETE_VARIANT_BUTTON_PREFIX + "_"
                                                     + fileVariantName ) ) {
                    iterator.remove();
                } else if ( null == fileVariant.getInputStreamSource() ) {
                    iterator.remove();
                }
            }
            Set fileVariantTreeSet = new TreeSet(String.CASE_INSENSITIVE_ORDER) ;
            fileVariantTreeSet.addAll( fileVariants.keySet()  ) ;
            String firstFileVariant = (String)fileVariantTreeSet.iterator().next();
            if ( !fileVariants.containsKey( selectedFileVariantName ) && !fileVariants.isEmpty() ) {
                selectedFileVariantName = firstFileVariant;
            }
            if ( !fileVariants.containsKey( fileDocument.getDefaultFileVariantName() ) && !fileVariants.isEmpty() ) {
                fileDocument.setDefaultFileVariantName( firstFileVariant );
            }
        }
        if ( !response.isCommitted() ) {
            createFileDocumentEditPage( selectedFileVariantName ).forward( request, response );
        }
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
            return null;
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
        private String fileVariantName;
        private FileDocumentDomainObject.FileDocumentFile fileVariant;

        public FileDocumentEditPage( MimeTypeRestriction allowedMimeTypes, String fileVariantName,
                                     FileDocumentDomainObject.FileDocumentFile fileVariant ) {
            this.mimeTypeRestriction = allowedMimeTypes;
            this.fileVariantName = fileVariantName;
            this.fileVariant = fileVariant;
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

        public FileDocumentDomainObject.FileDocumentFile getFileVariant() {
            return fileVariant;
        }

        public String getFileVariantName() {
            return fileVariantName;
        }
    }

    public interface MimeTypeRestriction {

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

    private static class UniqueFileVariantNamePredicate implements Predicate {

        private final FileDocumentDomainObject fileDocument;

        UniqueFileVariantNamePredicate( FileDocumentDomainObject fileDocument ) {
            this.fileDocument = fileDocument;
        }

        public boolean evaluate( Object object ) {
            return null == fileDocument.getFileVariant( (String)object );
        }
    }

    private static class CounterNameFactory implements Factory {

        int counter = 1 ;
        public Object create() {
            return ""+counter++ ;
        }
    }

    private static class CounterExtensionNameFactory extends CounterNameFactory {

        private final String name;
        private boolean first = true ;

        CounterExtensionNameFactory( String name ) {
            this.name = name;
        }

        public Object create() {
            if (first) {
                first = false ;
                return name ;
            }
            String result = (String)super.create();
            if (StringUtils.isNotBlank( name )) {
                result = name + "." + result ;
            }
            return result ;
        }
    }
}
