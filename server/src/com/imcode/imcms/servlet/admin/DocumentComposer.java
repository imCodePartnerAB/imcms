package com.imcode.imcms.servlet.admin;

/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-feb-23
 * Time: 16:19:25
 */

import imcode.server.*;
import imcode.server.document.*;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import imcode.util.InputStreamSource;
import imcode.util.MultipartHttpServletRequest;
import imcode.util.Utility;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.ObjectUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DocumentComposer extends HttpServlet {

    final static String URL_I15D_PAGE__PREFIX = "/imcms/";

    private final static String URL_I15D_PAGE__DOCINFO = "/jsp/docadmin/document_information.jsp";
    private final static String URL_I15D_PAGE__URLDOC = "/jsp/docadmin/url_document.jsp";
    private final static String URL_I15D_PAGE__HTMLDOC = "/jsp/docadmin/html_document.jsp";
    private static final String URL_I15D_PAGE__FILEDOC = "/jsp/docadmin/file_document.jsp";
    private static final String URL__BROWSER_DOCUMENT_COMPOSER = "BrowserDocumentComposer";

    private static final String MIME_TYPE__APPLICATION_OCTET_STREAM = "application/octet-stream";
    private static final String MIME_TYPE__UNKNOWN_DEFAULT = MIME_TYPE__APPLICATION_OCTET_STREAM;

    public static final String REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME = "document.sessionAttributeName";
    public static final String REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME = "newDocumentParentInformation.sessionAttributeName";

    public static final String REQUEST_ATTR_OR_PARAM__ACTION = "action";

    public static final String PARAMETER__URL_DOC__URL = "url";
    public static final String PARAMETER__HTML_DOC__HTML = "html";
    public static final String PARAMETER__FILE_DOC__FILE = "file";
    public static final String PARAMETER__FILE_DOC__MIME_TYPE = "mimetype";

    public static final String PARAMETER__GO_TO_IMAGE_BROWSE = "browseForMenuImage";
    public static final String PARAMETER__RETURNING_FROM_IMAGE_BROWSE = "returningFromImageBrowse";
    public static final String PARAMETER__IMAGE_BROWSE_ORIGINAL_ACTION = "imageBrowse.originalAction";

    public static final String PARAMETER_BUTTON__OK = "ok";

    public static final String PARAMETER__HEADLINE = "headline";
    public static final String PARAMETER__MENUTEXT = "menutext";
    public static final String PARAMETER__COPY_HEADLINE_AND_TEXT_TO_TEXTFIELDS = "copy_headline_and_text_to_textfields";
    public static final String PARAMETER__IMAGE = "image";
    public static final String PARAMETER__PUBLICATION_START_DATE = "activated_date";
    public static final String PARAMETER__PUBLICATION_START_TIME = "activated_time";
    public static final String PARAMETER__ARCHIVED_DATE = "archived_date";
    public static final String PARAMETER__ARCHIVED_TIME = "archived_time";
    public static final String PARAMETER__SECTIONS = "change_section";
    public static final String PARAMETER__PUBLICATION_END_DATE = "publication_end_date";
    public static final String PARAMETER__PUBLICATION_END_TIME = "publication_end_time";
    public static final String PARAMETER__LANGUAGE = "lang_prefix";
    public static final String PARAMETER__CATEGORIES = "categories";
    public static final String PARAMETER__VISIBLE_IN_MENU_FOR_UNAUTHORIZED_USERS = "show_meta";
    public static final String PARAMETER__LINKABLE_BY_OTHER_USERS = "shared";
    public static final String PARAMETER__KEYWORDS = "classification";
    public static final String PARAMETER__SEARCH_DISABLED = "disable_search";
    public static final String PARAMETER__TARGET = "target";
    public static final String PARAMETER__CREATED_DATE = "date_created";
    public static final String PARAMETER__CREATED_TIME = "created_time";
    public static final String PARAMETER__MODIFIED_DATE = "date_modified";
    public static final String PARAMETER__MODIFIED_TIME = "modified_time";
    public static final String PARAMETER__PUBLISHER_ID = "publisher_id";
    public static final String PARAMETER__STATUS = "status";

    public static final String ACTION__CREATE_NEW_DOCUMENT = "createNewDocument";
    public static final String ACTION__PROCESS_NEW_DOCUMENT_INFORMATION = "processNewDocumentInformation";
    public static final String ACTION__CREATE_NEW_URL_DOCUMENT = "createNewUrlDocument";
    public static final String ACTION__CREATE_NEW_HTML_DOCUMENT = "createNewHtmlDocument";
    public static final String ACTION__CREATE_NEW_FILE_DOCUMENT = "createNewFileDocument";
    public static final String ACTION__CREATE_NEW_BROWSER_DOCUMENT = "createNewBrowserDocument";

    public static final String ACTION__EDIT_DOCUMENT_INFORMATION = "editDocumentInformation";
    public static final String ACTION__EDIT_BROWSER_DOCUMENT = "editBrowserDocument";
    public static final String ACTION__EDIT_FILE_DOCUMENT = "editFileDocument";
    public static final String ACTION__EDIT_HTML_DOCUMENT = "editHtmlDocument";
    public static final String ACTION__EDIT_URL_DOCUMENT = "editUrlDocument";
    public static final String ACTION__PROCESS_EDITED_DOCUMENT_INFORMATION = "processEditedDocumentInformation";
    public static final String ACTION__PROCESS_EDITED_BROWSER_DOCUMENT = "processEditedBrowserDocument";
    public static final String ACTION__PROCESS_EDITED_HTML_DOCUMENT = "processEditedHtmlDocument";
    public static final String ACTION__PROCESS_EDITED_FILE_DOCUMENT = "processEditedFileDocument";
    public static final String ACTION__PROCESS_EDITED_URL_DOCUMENT = "processEditedUrlDocument";

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doPost( request, response );
    }

    public void doPost( HttpServletRequest r, HttpServletResponse response ) throws ServletException, IOException {
        r.setCharacterEncoding( WebAppGlobalConstants.DEFAULT_ENCODING_CP1252 );
        MultipartHttpServletRequest request = new MultipartHttpServletRequest( r );

        String action = request.getParameter( REQUEST_ATTR_OR_PARAM__ACTION );
        request.setAttribute( REQUEST_ATTR_OR_PARAM__ACTION, action );

        NewDocumentParentInformation newDocumentParentInformation = (NewDocumentParentInformation)getObjectFromSessionWithKeyInRequest( request, REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME );

        DocumentDomainObject document = (DocumentDomainObject)getObjectFromSessionWithKeyInRequest( request, REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME );

        if ( null != request.getParameter( PARAMETER__GO_TO_IMAGE_BROWSE ) ) {
            String parentInfoAttributeName = getSessionAttributeNameFromRequest( request, REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME );
            String sessionAttributeName = getSessionAttributeNameFromRequest( request, REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME );
            String originalAction = request.getParameter( PARAMETER__IMAGE_BROWSE_ORIGINAL_ACTION );
            String returningUrl = "DocumentComposer?" + PARAMETER__RETURNING_FROM_IMAGE_BROWSE + "=" + "true" + "&"
                                  + REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME
                                  + "="
                                  + parentInfoAttributeName
                                  + "&"
                                  + REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME
                                  + "="
                                  + sessionAttributeName
                                  + "&"
                                  + PARAMETER__IMAGE_BROWSE_ORIGINAL_ACTION
                                  + "="
                                  + originalAction;
            request.getRequestDispatcher( "ImageBrowse?" + ImageBrowse.PARAMETER__CALLER + "="
                                          + java.net.URLEncoder.encode( returningUrl ) ).forward( request, response );
            return;
        }

        UserDomainObject user = Utility.getLoggedOnUser( request );
        DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();

        try {
            // TODO: Replace this rats-nest of dispatching with a bunch of Application Controllers a la Fowler,
            // preferably stored in the session instead of the objects we put there now.
            // http://www.martinfowler.com/eaaCatalog/applicationController.html

            if ( null != request.getParameter( PARAMETER__RETURNING_FROM_IMAGE_BROWSE ) ) {
                action = request.getParameter( PARAMETER__IMAGE_BROWSE_ORIGINAL_ACTION );
                request.setAttribute( REQUEST_ATTR_OR_PARAM__ACTION, action );
                String imageUrl = AdminDoc.getImageUri( request );
                if (null != request.getParameter( ImageBrowse.PARAMETER_BUTTON__OK)) {
                    document.setMenuImage( imageUrl );
                }
            }

            if ( ACTION__CREATE_NEW_DOCUMENT.equalsIgnoreCase( action ) ) {
                DocumentDomainObject parentDocument = documentMapper.getDocument( newDocumentParentInformation.parentId );
                DocumentDomainObject newDocument = createDocumentOfTypeFromParent( newDocumentParentInformation.documentTypeId, parentDocument );
                newDocument.setHeadline( "" );
                newDocument.setMenuText( "" );
                newDocument.setMenuImage( "" );
                newDocument.setStatus( DocumentDomainObject.STATUS_NEW );
                newDocument.setPublicationStartDatetime( new Date() );
                newDocument.setArchivedDatetime( null );
                newDocument.setPublicationEndDatetime( null );
                newDocument.setCreator( user );
                addObjectToSessionAndSetSessionAttributeNameInRequest( "newDocument", newDocument, request, REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME );
                forwardToDocinfoPage( request, response, user );
            } else if ( ACTION__PROCESS_NEW_DOCUMENT_INFORMATION.equalsIgnoreCase( action ) ) {
                if ( null != request.getParameter( PARAMETER_BUTTON__OK ) ) {
                    processNewDocumentInformation( newDocumentParentInformation, request, response, user );
                } else {
                    redirectToDocumentIdInMenumode( response, newDocumentParentInformation.parentId );
                }
            } else if ( ACTION__CREATE_NEW_URL_DOCUMENT.equalsIgnoreCase( action ) ) {
                if ( null != request.getParameter( PARAMETER_BUTTON__OK ) ) {

                    UrlDocumentDomainObject newUrlDocument = (UrlDocumentDomainObject)document;
                    newUrlDocument.setUrlDocumentUrl( request.getParameter( PARAMETER__URL_DOC__URL ) );
                    newUrlDocument.setTarget( getTargetFromRequest( request ) );
                    saveNewDocumentAndAddToMenuAndRemoveSessionAttribute( newUrlDocument, newDocumentParentInformation, user, request );
                }
                redirectToDocumentIdInMenumode( response, newDocumentParentInformation.parentId );
            } else if ( ACTION__CREATE_NEW_HTML_DOCUMENT.equalsIgnoreCase( action ) ) {
                if ( null != request.getParameter( PARAMETER_BUTTON__OK ) ) {
                    HtmlDocumentDomainObject newHtmlDocument = (HtmlDocumentDomainObject)document;
                    newHtmlDocument.setHtmlDocumentHtml( request.getParameter( PARAMETER__HTML_DOC__HTML ) );
                    saveNewDocumentAndAddToMenuAndRemoveSessionAttribute( newHtmlDocument, newDocumentParentInformation, user, request );
                }
                redirectToDocumentIdInMenumode( response, newDocumentParentInformation.parentId );
            } else if ( ACTION__CREATE_NEW_FILE_DOCUMENT.equalsIgnoreCase( action ) ) {
                if ( null != request.getParameter( PARAMETER_BUTTON__OK ) ) {
                    FileDocumentDomainObject newFileDocument = (FileDocumentDomainObject)document;
                    final FileItem fileItem = request.getParameterFileItem( PARAMETER__FILE_DOC__FILE );
                    newFileDocument.setFilename( fileItem.getName() );
                    newFileDocument.setInputStreamSource( new FileItemInputStreamSource( fileItem ) );
                    newFileDocument.setMimeType( getMimeTypeFromRequest( request ) );
                    saveNewDocumentAndAddToMenuAndRemoveSessionAttribute( newFileDocument, newDocumentParentInformation, user, request );
                }
                redirectToDocumentIdInMenumode( response, newDocumentParentInformation.parentId );
            } else if ( ACTION__CREATE_NEW_BROWSER_DOCUMENT.equalsIgnoreCase( action ) ) {
                if ( null != request.getParameter( PARAMETER_BUTTON__OK ) ) {
                    BrowserDocumentDomainObject newBrowserDocument = (BrowserDocumentDomainObject)document;
                    saveNewDocumentAndAddToMenuAndRemoveSessionAttribute( newBrowserDocument, newDocumentParentInformation, user, request );
                }
                redirectToDocumentIdInMenumode( response, newDocumentParentInformation.parentId );
            } else if ( ACTION__EDIT_BROWSER_DOCUMENT.equalsIgnoreCase( action ) ) {
                forwardToBrowserDocumentComposer( request, response );
            } else if ( ACTION__PROCESS_EDITED_BROWSER_DOCUMENT.equalsIgnoreCase( action ) ) {
                documentMapper.saveDocument( document );
                redirectToDocument( response, document );
            } else if ( ACTION__EDIT_FILE_DOCUMENT.equalsIgnoreCase( action ) ) {
                forwardToFileDocumentPage( request, response, user );
            } else if ( ACTION__PROCESS_EDITED_FILE_DOCUMENT.equalsIgnoreCase( action ) ) {
                if ( null != request.getParameter( PARAMETER_BUTTON__OK ) ) {
                    FileDocumentDomainObject fileDocument = (FileDocumentDomainObject)document;
                    final FileItem fileItem = request.getParameterFileItem( PARAMETER__FILE_DOC__FILE );
                    String fileName = fileItem.getName();
                    if ( !"".equals( fileName ) ) {
                        fileDocument.setFilename( fileName );
                        if ( 0 != fileItem.getSize() ) {
                            fileDocument.setInputStreamSource( new FileItemInputStreamSource( fileItem ) );
                        }
                    }
                    fileDocument.setMimeType( getMimeTypeFromRequest( request ) );
                    documentMapper.saveDocument( fileDocument );
                }
                redirectToDocument( response, document );
            } else if ( ACTION__EDIT_HTML_DOCUMENT.equalsIgnoreCase( action ) ) {
                forwardToHtmlDocumentPage( request, response, user );
            } else if ( ACTION__PROCESS_EDITED_HTML_DOCUMENT.equalsIgnoreCase( action ) ) {
                if ( null != request.getParameter( PARAMETER_BUTTON__OK ) ) {
                    HtmlDocumentDomainObject htmlDocument = (HtmlDocumentDomainObject)document;
                    htmlDocument.setHtmlDocumentHtml( request.getParameter( PARAMETER__HTML_DOC__HTML ) );
                    documentMapper.saveDocument( htmlDocument );
                }
                redirectToDocument( response, document );
            } else if ( ACTION__EDIT_URL_DOCUMENT.equalsIgnoreCase( action ) ) {
                forwardToUrlDocumentPage( request, response, user );
            } else if ( ACTION__PROCESS_EDITED_URL_DOCUMENT.equalsIgnoreCase( action ) ) {
                if ( null != request.getParameter( PARAMETER_BUTTON__OK ) ) {
                    UrlDocumentDomainObject urlDocument = (UrlDocumentDomainObject)document;
                    urlDocument.setTarget( getTargetFromRequest( request ) );
                    String url = request.getParameter( PARAMETER__URL_DOC__URL );
                    urlDocument.setUrlDocumentUrl( url );
                    documentMapper.saveDocument( urlDocument );
                }
                redirectToDocument( response, document );
            } else if ( ACTION__EDIT_DOCUMENT_INFORMATION.equalsIgnoreCase( action ) ) {
                forwardToDocinfoPage( request, response, user );
            } else if ( ACTION__PROCESS_EDITED_DOCUMENT_INFORMATION.equalsIgnoreCase( action ) ) {
                if ( null != request.getParameter( PARAMETER_BUTTON__OK ) ) {
                    setDocumentAttributesFromRequestParameters( document, request );
                    documentMapper.saveDocument( document );
                }
                redirectToDocument( response, document );
            }
        } catch ( MaxCategoryDomainObjectsOfTypeExceededException e ) {
            throw new ServletException( e );
        }
    }

    private void redirectToDocument( HttpServletResponse response, DocumentDomainObject document ) throws IOException {
        response.sendRedirect( "AdminDoc?meta_id=" + document.getId() );
    }

    private String getMimeTypeFromRequest( MultipartHttpServletRequest request ) {
        FileItem fileItem = request.getParameterFileItem( PARAMETER__FILE_DOC__FILE );
        String filename = fileItem.getName();
        final DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
        Set predefinedMimeTypes = new HashSet( Arrays.asList( documentMapper.getAllMimeTypes() ) );
        String[] mimeTypes = request.getParameterValues( PARAMETER__FILE_DOC__MIME_TYPE );
        String mimeType = null;
        for ( int i = 0; i < mimeTypes.length; i++ ) {
            mimeType = mimeTypes[i].trim();
            if ( predefinedMimeTypes.contains( mimeType ) ) {
                break;
            }
            if ( "".equals( mimeType ) ) {
                if ( null != filename ) {
                    mimeType = getServletContext().getMimeType( filename );
                }
            } else if ( -1 == mimeType.indexOf( '/' ) ) {
                if ( '.' != mimeType.charAt( 0 ) ) {
                    mimeType = '.' + mimeType;
                }
                mimeType = getServletContext().getMimeType( '_' + mimeType );
            }
            if ( null == mimeType || "".equals( mimeType ) ) {
                mimeType = MIME_TYPE__UNKNOWN_DEFAULT;
            }
        }
        return mimeType;
    }

    private void redirectToDocumentIdInMenumode( HttpServletResponse response, int parentId ) throws IOException {
        response.sendRedirect( "AdminDoc?meta_id=" + parentId + "&flags="
                               + IMCConstants.PERM_DT_TEXT_EDIT_MENUS );
    }

    public static void saveNewDocumentAndAddToMenuAndRemoveSessionAttribute( DocumentDomainObject newDocument,
                                                                             NewDocumentParentInformation newDocumentParentInformation,
                                                                             UserDomainObject user,
                                                                             HttpServletRequest request ) throws IOException {
        try {
            final IMCServiceInterface service = ApplicationServer.getIMCServiceInterface();
            final DocumentMapper documentMapper = service.getDocumentMapper();
            documentMapper.saveNewDocumentAndAddToMenu( newDocument, user, newDocumentParentInformation );

            removeObjectFromSessionWithKeyInRequest( request, REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME );
            removeObjectFromSessionWithKeyInRequest( request, REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME );

        } catch ( MaxCategoryDomainObjectsOfTypeExceededException e ) {
            throw new RuntimeException( e );
        } catch ( DocumentMapper.DocumentAlreadyInMenuException e ) {
            throw new RuntimeException( e );
        }
    }

    private void processNewDocumentInformation( NewDocumentParentInformation newDocumentParentInformation,
                                                HttpServletRequest request, HttpServletResponse response,
                                                UserDomainObject user ) throws ServletException, IOException {
        DocumentDomainObject newDocument = (DocumentDomainObject)getObjectFromSessionWithKeyInRequest( request, REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME );

        setDocumentAttributesFromRequestParameters( newDocument, request );

        request.setAttribute( REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME, getSessionAttributeNameFromRequest( request, REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME ) );
        request.setAttribute( REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME, getSessionAttributeNameFromRequest( request, REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME ) );

        newDocument.processNewDocumentInformation( this, newDocumentParentInformation, user, request, response );
    }

    private DocumentDomainObject createDocumentOfTypeFromParent( int documentTypeId, final DocumentDomainObject parent ) throws ServletException {
        DocumentDomainObject newDocument;
        try {
            if ( DocumentDomainObject.DOCTYPE_TEXT == documentTypeId ) {
                newDocument = (DocumentDomainObject)parent.clone();
                ( (TextDocumentDomainObject)newDocument ).removeAllTexts();
            } else {
                newDocument = DocumentDomainObject.fromDocumentTypeId( documentTypeId );
                newDocument.setDocumentProperties( (DocumentDomainObject.DocumentProperties)parent.getDocumentProperties().clone() );
            }
        } catch ( CloneNotSupportedException e ) {
            throw new ServletException( e );
        }
        return newDocument;
    }

    public void processNewBrowserDocumentInformation( HttpServletRequest request, HttpServletResponse response,
                                                      UserDomainObject user ) throws IOException, ServletException {
        forwardToBrowserDocumentComposer( request, response );
    }

    private void forwardToBrowserDocumentComposer( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        request.getRequestDispatcher( URL__BROWSER_DOCUMENT_COMPOSER ).forward( request, response );
    }

    public void processNewFileDocumentInformation( HttpServletRequest request, HttpServletResponse response,
                                                   UserDomainObject user ) throws IOException, ServletException {
        forwardToFileDocumentPage( request, response, user );
    }

    private void forwardToFileDocumentPage( HttpServletRequest request, HttpServletResponse response,
                                            UserDomainObject user ) throws ServletException, IOException {
        request.getRequestDispatcher( URL_I15D_PAGE__PREFIX + user.getLanguageIso639_2() + URL_I15D_PAGE__FILEDOC ).forward( request, response );
    }

    public void processNewHtmlDocumentInformation( HttpServletRequest request, HttpServletResponse response,
                                                   UserDomainObject user ) throws IOException, ServletException {
        forwardToHtmlDocumentPage( request, response, user );

    }

    private void forwardToHtmlDocumentPage( HttpServletRequest request, HttpServletResponse response,
                                            UserDomainObject user ) throws ServletException, IOException {
        request.getRequestDispatcher( URL_I15D_PAGE__PREFIX + user.getLanguageIso639_2() + URL_I15D_PAGE__HTMLDOC ).forward( request, response );
    }

    public void processNewFormerExternalDocument( NewDocumentParentInformation newDocumentParentInformation,
                                                  UserDomainObject user, HttpServletRequest request,
                                                  HttpServletResponse response,
                                                  FormerExternalDocument externalDocument ) throws IOException {
        // todo: spara inte här och ta bort active flaggan
        saveNewDocumentAndAddToMenuAndRemoveSessionAttribute( externalDocument, newDocumentParentInformation, user, request );
        final IMCServiceInterface service = ApplicationServer.getIMCServiceInterface();
        DocumentMapper.sqlUpdateDocumentActivated( service, externalDocument.getId(), false );
        redirectToExternalDocType( service, externalDocument.getId(), user, newDocumentParentInformation.parentId, response );
    }

    public static void redirectToExternalDocType( IMCServiceInterface imcref, int metaId,
                                                  UserDomainObject user, int parentMetaId, HttpServletResponse res ) throws IOException {
        // check if external doc
        ExternalDocType ex_doc;
        ex_doc = imcref.isExternalDoc( metaId, user );
        String paramStr = "?meta_id=" + metaId + "&";
        paramStr += "parent_meta_id=" + parentMetaId + "&";
        paramStr += "cookie_id=" + "1A" + "&action=new";
        res.sendRedirect( ex_doc.getCallServlet() + paramStr );
    }

    public void processNewUrlDocumentInformation( HttpServletRequest request, HttpServletResponse response,
                                                  UserDomainObject user ) throws ServletException, IOException {
        forwardToUrlDocumentPage( request, response, user );
    }

    private void forwardToUrlDocumentPage( HttpServletRequest request, HttpServletResponse response,
                                           UserDomainObject user ) throws ServletException, IOException {
        request.getRequestDispatcher( URL_I15D_PAGE__PREFIX + user.getLanguageIso639_2() + URL_I15D_PAGE__URLDOC ).forward( request, response );
    }

    public static void addObjectToSessionAndSetSessionAttributeNameInRequest( final String sessionObjectName,
                                                                              final Object objectToAddToSession,
                                                                              HttpServletRequest request,
                                                                              final String sessionAttributeNameRequestAttributeName ) {
        final String sessionAttributeName = DocumentComposer.class.getName() + "." + sessionObjectName + "."
                                            + System.currentTimeMillis();
        request.getSession().setAttribute( sessionAttributeName, objectToAddToSession );
        request.setAttribute( sessionAttributeNameRequestAttributeName, sessionAttributeName );
    }

    public static Object getObjectFromSessionWithKeyInRequest( HttpServletRequest request,
                                                               String requestAttributeOrParameterName ) {
        String sessionAttributeName = getSessionAttributeNameFromRequest( request, requestAttributeOrParameterName );
        return request.getSession().getAttribute( sessionAttributeName );
    }

    public static void removeObjectFromSessionWithKeyInRequest( HttpServletRequest request,
                                                                String requestAttributeOrParameterName ) {
        String sessionAttributeName = getSessionAttributeNameFromRequest( request, requestAttributeOrParameterName );
        request.getSession().removeAttribute( sessionAttributeName );
    }

    public static String getSessionAttributeNameFromRequest( HttpServletRequest request,
                                                             String requestAttributeOrParameterName ) {
        String sessionAttributeName = (String)request.getAttribute( requestAttributeOrParameterName );
        if ( null == sessionAttributeName ) {
            sessionAttributeName = request.getParameter( requestAttributeOrParameterName );
        }
        return sessionAttributeName;
    }

    private void setDocumentAttributesFromRequestParameters( DocumentDomainObject document, HttpServletRequest request ) {

        final IMCServiceInterface service = ApplicationServer.getIMCServiceInterface();
        final DocumentMapper documentMapper = service.getDocumentMapper();

        String headline = request.getParameter( PARAMETER__HEADLINE );
        document.setHeadline( headline );

        String menuText = request.getParameter( PARAMETER__MENUTEXT );
        document.setMenuText( menuText );

        String imageUrl = request.getParameter( PARAMETER__IMAGE );
        document.setMenuImage( imageUrl );

        int status = Integer.parseInt( request.getParameter( PARAMETER__STATUS ) );
        document.setStatus( status );

        SimpleDateFormat dateFormat = new SimpleDateFormat( DateConstants.DATE_FORMAT_STRING );
        SimpleDateFormat timeFormat = new SimpleDateFormat( DateConstants.TIME_FORMAT_NO_SECONDS_STRING );

        Date publicationStartDatetime = parseDatetimeParameters( request, PARAMETER__PUBLICATION_START_DATE, PARAMETER__PUBLICATION_START_TIME, dateFormat,
                                                                 timeFormat );
        Date archivedDatetime = parseDatetimeParameters( request, PARAMETER__ARCHIVED_DATE, PARAMETER__ARCHIVED_TIME, dateFormat,
                                                         timeFormat );
        Date publicationEndDatetime = parseDatetimeParameters( request, PARAMETER__PUBLICATION_END_DATE, PARAMETER__PUBLICATION_END_TIME, dateFormat,
                                                               timeFormat );

        document.setPublicationStartDatetime( publicationStartDatetime );
        document.setArchivedDatetime( archivedDatetime );
        document.setPublicationEndDatetime( publicationEndDatetime );

        document.removeAllSections();
        String[] sectionIds = request.getParameterValues( PARAMETER__SECTIONS );
        for ( int i = 0; null != sectionIds && i < sectionIds.length; i++ ) {
            int sectionId = Integer.parseInt( sectionIds[i] );
            SectionDomainObject section = documentMapper.getSectionById( sectionId );
            document.addSection( section );
        }

        String languageIso639_2 = request.getParameter( PARAMETER__LANGUAGE );
        document.setLanguageIso639_2( languageIso639_2 );

        document.removeAllCategories();
        String[] categoryIds = request.getParameterValues( PARAMETER__CATEGORIES );
        for ( int i = 0; null != categoryIds && i < categoryIds.length; i++ ) {
            try {
                int categoryId = Integer.parseInt( categoryIds[i] );
                CategoryDomainObject category = documentMapper.getCategoryById( categoryId );
                document.addCategory( category );
            } catch ( NumberFormatException ignored ) {
                // OK, empty category id
            }
        }

        boolean visibleInMenuForUnauthorizedUsers = "1".equals( request.getParameter( PARAMETER__VISIBLE_IN_MENU_FOR_UNAUTHORIZED_USERS ) );
        document.setVisibleInMenusForUnauthorizedUsers( visibleInMenuForUnauthorizedUsers );

        boolean linkableByOtherUsers = "1".equals( request.getParameter( PARAMETER__LINKABLE_BY_OTHER_USERS ) );
        document.setLinkableByOtherUsers( linkableByOtherUsers );

        String keywordsString = request.getParameter( PARAMETER__KEYWORDS );
        String[] keywords = keywordsString.split( "[^\\p{L}\\d]+" );
        document.setKeywords( keywords );

        boolean searchDisabled = "1".equals( request.getParameter( PARAMETER__SEARCH_DISABLED ) );
        document.setSearchDisabled( searchDisabled );

        String target = getTargetFromRequest( request );
        document.setTarget( target );

        Date createdDatetime = (Date)ObjectUtils.defaultIfNull( parseDatetimeParameters( request, PARAMETER__CREATED_DATE, PARAMETER__CREATED_TIME, dateFormat, timeFormat ), new Date() );

        Date modifiedDatetime = (Date)ObjectUtils.defaultIfNull( parseDatetimeParameters( request, PARAMETER__MODIFIED_DATE, PARAMETER__MODIFIED_TIME, dateFormat, timeFormat ), createdDatetime );

        document.setCreatedDatetime( createdDatetime );
        document.setModifiedDatetime( modifiedDatetime );

        UserDomainObject publisher = null;
        try {
            int publisherId = Integer.parseInt( request.getParameter( PARAMETER__PUBLISHER_ID ) );
            ImcmsAuthenticatorAndUserMapper userAndRoleMapper = service.getImcmsAuthenticatorAndUserAndRoleMapper();
            publisher = userAndRoleMapper.getUser( publisherId );
        } catch ( NumberFormatException ignored ) {
            // OK, no publisher
        }
        document.setPublisher( publisher );
    }

    private String getTargetFromRequest( HttpServletRequest request ) {
        String[] possibleTargets = request.getParameterValues( PARAMETER__TARGET );
        String target = null;
        for ( int i = 0; i < possibleTargets.length; i++ ) {
            target = possibleTargets[i];
            boolean targetIsPredefinedTarget
                    = "_self".equalsIgnoreCase( target )
                      || "_blank".equalsIgnoreCase( target )
                      || "_top".equalsIgnoreCase( target );
            if ( targetIsPredefinedTarget ) {
                break;
            }
        }
        return target;
    }

    private void forwardToDocinfoPage( HttpServletRequest request,
                                       HttpServletResponse response, UserDomainObject user ) throws ServletException, IOException {
        request.getRequestDispatcher( URL_I15D_PAGE__PREFIX + user.getLanguageIso639_2() + URL_I15D_PAGE__DOCINFO ).forward( request, response );
    }

    static Date parseDatetimeParameters( HttpServletRequest req, final String dateParameterName,
                                         final String timeParameterName, DateFormat dateformat,
                                         DateFormat timeformat ) {
        String dateStr = req.getParameter( dateParameterName );
        String timeStr = req.getParameter( timeParameterName );

        Date date;
        try {
            date = dateformat.parse( dateStr );
        } catch ( ParseException pe ) {
            return null;
        } catch ( NullPointerException npe ) {
            return null;
        }

        Date time;
        try {
            timeformat.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
            time = timeformat.parse( timeStr );
        } catch ( ParseException pe ) {
            return date;
        } catch ( NullPointerException npe ) {
            return date;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime( date );
        calendar.add( Calendar.MILLISECOND, (int)time.getTime() );
        return calendar.getTime();
    }

    public void processNewTextDocumentInformation( TextDocumentDomainObject newTextDocument,
                                                   NewDocumentParentInformation newDocumentParentInformation,
                                                   HttpServletRequest request, HttpServletResponse response,
                                                   UserDomainObject user ) throws IOException {

        if ( null != request.getParameter( PARAMETER__COPY_HEADLINE_AND_TEXT_TO_TEXTFIELDS ) ) {
            newTextDocument.setText( 1, new TextDocumentDomainObject.Text( newTextDocument.getHeadline(), TextDocumentDomainObject.Text.TEXT_TYPE_PLAIN ) );
            newTextDocument.setText( 2, new TextDocumentDomainObject.Text( newTextDocument.getMenuText(), TextDocumentDomainObject.Text.TEXT_TYPE_PLAIN ) );
        }
        saveNewDocumentAndAddToMenuAndRemoveSessionAttribute( newTextDocument, newDocumentParentInformation, user, request );
        redirectToDocumentIdInMenumode( response, newDocumentParentInformation.parentId );
    }

    public static class NewDocumentParentInformation implements Serializable {

        public int parentId;
        public int parentMenuNumber;
        public int documentTypeId;
        public static final String MENU_NUMBER_PARAMETER_NAME = "doc_menu_no";
        public static final String DOCUMENT_TYPE_PARAMETER_NAME = "edit_menu";
        public static final String PARENT_ID_PARAMETER_NAME = "parent_meta_id";

        public NewDocumentParentInformation( HttpServletRequest request ) throws NumberFormatException {
            parentMenuNumber = Integer.parseInt( request.getParameter( MENU_NUMBER_PARAMETER_NAME ) );
            parentId =
            Integer.parseInt( request.getParameter( PARENT_ID_PARAMETER_NAME ) );
            documentTypeId = Integer.parseInt( request.getParameter( DOCUMENT_TYPE_PARAMETER_NAME ) );
        }

    }

    private static class FileItemInputStreamSource implements InputStreamSource {

        private final FileItem fileItem;

        public FileItemInputStreamSource( FileItem fileItem ) {
            this.fileItem = fileItem;
        }

        public InputStream getInputStream() throws IOException {
            return fileItem.getInputStream();
        }
    }

}