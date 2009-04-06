package com.imcode.imcms.flow;

import com.imcode.imcms.api.Document;
import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.servlet.admin.ImageBrowser;
import com.imcode.imcms.servlet.admin.ListDocumentAliasPage;
import com.imcode.imcms.servlet.admin.UserFinder;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import com.imcode.util.KeywordsParser;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.*;
import imcode.util.image.ImageInfo;

import org.apache.commons.lang.ObjectUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EditDocumentInformationPageFlow extends EditDocumentPageFlow {

    private final static String URL_I15D_PAGE__DOCINFO = "/jsp/docadmin/document_information.jsp";
    public static final String REQUEST_PARAMETER__HEADLINE = "headline";
    public static final String REQUEST_PARAMETER__MENUTEXT = "menutext";
    public static final String REQUEST_PARAMETER__COPY_HEADLINE_AND_TEXT_TO_TEXTFIELDS = "copy_headline_and_text_to_textfields";
    public static final String REQUEST_PARAMETER__IMAGE = "image";
    public static final String REQUEST_PARAMETER__DOCUMENT_ALIAS = "document_alias";
    public static final String REQUEST_PARAMETER__PUBLICATION_START_DATE = "activated_date";
    public static final String REQUEST_PARAMETER__PUBLICATION_START_TIME = "activated_time";
    public static final String REQUEST_PARAMETER__ARCHIVED_DATE = "archived_date";
    public static final String REQUEST_PARAMETER__ARCHIVED_TIME = "archived_time";
    public static final String REQUEST_PARAMETER__SECTIONS = "change_section";
    public static final String REQUEST_PARAMETER__PUBLICATION_END_DATE = "publication_end_date";
    public static final String REQUEST_PARAMETER__PUBLICATION_END_TIME = "publication_end_time";
    public static final String REQUEST_PARAMETER__LANGUAGE = "lang_prefix";
    public static final String REQUEST_PARAMETER__CATEGORIES = "categories";
    public static final String REQUEST_PARAMETER__CATEGORY_IDS_TO_REMOVE = "categories_to_remove";
    public static final String REQUEST_PARAMETER__CATEGORY_IDS_TO_ADD = "categories_to_add";
    public static final String REQUEST_PARAMETER__ADD_CATEGORY = "add_category";
    public static final String REQUEST_PARAMETER__REMOVE_CATEGORY = "remove_category";
    public static final String REQUEST_PARAMETER__VISIBLE_IN_MENU_FOR_UNAUTHORIZED_USERS = "show_meta";
    public static final String REQUEST_PARAMETER__LINKABLE_BY_OTHER_USERS = "shared";
    public static final String REQUEST_PARAMETER__KEYWORDS = "classification";
    public static final String REQUEST_PARAMETER__SEARCH_DISABLED = "disable_search";
    public static final String REQUEST_PARAMETER__TARGET = "target";
    public static final String REQUEST_PARAMETER__CREATED_DATE = "date_created";
    public static final String REQUEST_PARAMETER__CREATED_TIME = "created_time";
    public static final String REQUEST_PARAMETER__MODIFIED_DATE = "date_modified";
    public static final String REQUEST_PARAMETER__MODIFIED_TIME = "modified_time";
    public static final String REQUEST_PARAMETER__STATUS = "status";
    public static final String REQUEST_PARAMETER__GO_TO_PUBLISHER_BROWSER = "browseForPublisher";
    public static final String REQUEST_PARAMETER__GO_TO_CREATOR_BROWSER = "browseForCreator";
    public static final String REQUEST_PARAMETER__GO_TO_IMAGE_BROWSER = "browseForMenuImage";
    public static final String REQUEST_PARAMETER__GO_TO_ALIAS_LIST = "listDocumentAlias";
    public static final String PAGE__DOCUMENT_INFORMATION = "document_information";

    public static final String REQUEST_PARAMETER__STATUS__NEW = "new";
    public static final String REQUEST_PARAMETER__STATUS__APPROVED = "approved";
    public static final String REQUEST_PARAMETER__STATUS__DISAPPROVED = "disapproved";

    private static final LocalizedMessage BUTTON_TEXT__SELECT_USER = new LocalizedMessage( "templates/sv/AdminChangeUser.htm/2007" );
    private static final LocalizedMessage HEADLINE__SELECT_CREATOR = new LocalizedMessage( "server/src/com/imcode/imcms/flow/EditDocumentInformationPageFlow/select_creator_headline" );
    private static final LocalizedMessage HEADLINE__SELECT_PUBLISHER = new LocalizedMessage( "server/src/com/imcode/imcms/flow/EditDocumentInformationPageFlow/select_publisher_headline" );
    public static final LocalizedMessage ALIAS_ERROR__ALREADY_EXIST   = new LocalizedMessage("server/src/com/imcode/imcms/flow/EditDocumentInformationPageFlow/alias_error__already_exist_message");
    public static final LocalizedMessage ALIAS_ERROR__USED_BY_SYSTEM   = new LocalizedMessage("server/src/com/imcode/imcms/flow/EditDocumentInformationPageFlow/alias_error__used_by_system_message");

    private Set<LocalizedMessage> errors = new HashSet();

    private boolean adminButtonsHidden;

    public EditDocumentInformationPageFlow( DocumentDomainObject document, DispatchCommand returnCommand,
                                            SaveDocumentCommand saveDocumentCommand ) {
        super( document, returnCommand, saveDocumentCommand );
    }

    protected void dispatchFromEditPage( HttpServletRequest request, HttpServletResponse response, String page ) throws IOException, ServletException {
        if ( PAGE__DOCUMENT_INFORMATION.equals( page ) ) {
            dispatchFromDocumentInformation( request, response );
        }
    }

    private void dispatchFromDocumentInformation( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        setDocumentAttributesFromRequestParameters( document, request, errors);
        if ( null != request.getParameter( REQUEST_PARAMETER__GO_TO_PUBLISHER_BROWSER ) ) {
            dispatchToPublisherUserBrowser( request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__GO_TO_CREATOR_BROWSER ) ) {
            dispatchToCreatorUserBrowser( request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__GO_TO_IMAGE_BROWSER ) ) {
            dispatchToImageBrowser( request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__GO_TO_ALIAS_LIST ) ) {
           dispatchToAliasList( request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__ADD_CATEGORY ) ) {
            if ( null != request.getParameter( REQUEST_PARAMETER__CATEGORY_IDS_TO_ADD ) ){
                String[] categoriesToAdd = request.getParameterValues(REQUEST_PARAMETER__CATEGORY_IDS_TO_ADD);
                for ( String categoryIdToAdd : categoriesToAdd ) {
                    document.addCategoryId(Integer.parseInt(categoryIdToAdd));
                }
            }
            dispatchToFirstPage( request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__REMOVE_CATEGORY ) ) {
            if ( null != request.getParameter( REQUEST_PARAMETER__CATEGORY_IDS_TO_REMOVE ) ){
                String[] categoriesToRemove = request.getParameterValues(REQUEST_PARAMETER__CATEGORY_IDS_TO_REMOVE);
                for ( String categoryIdToRemove : categoriesToRemove ) {
                    document.removeCategoryId(Integer.parseInt(categoryIdToRemove));
                }
            }
            dispatchToFirstPage( request, response );
        }
    }

    private void dispatchToImageBrowser( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        ImageBrowser imageBrowser = new ImageBrowser();
        final String flowSessionAttributeName = HttpSessionUtils.getSessionAttributeNameFromRequest( request, REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW );
        imageBrowser.setCancelCommand( new DispatchCommand() {
            public void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
                request.setAttribute( REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW, flowSessionAttributeName );
                dispatchToFirstPage( request, response );
            }
        } );
        imageBrowser.setSelectImageUrlCommand( new ImageBrowser.SelectImageUrlCommand() {
            public void selectImageUrl( String imageUrl, HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
                document.setMenuImage( imageUrl );
                request.setAttribute( REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW, flowSessionAttributeName );
                dispatchToFirstPage( request, response );
            }
        } );
        imageBrowser.forward( request, response );
    }

    private void dispatchToAliasList( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        final String flowSessionAttributeName = HttpSessionUtils.getSessionAttributeNameFromRequest( request, REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW );
        DispatchCommand cancelCommand = new DispatchCommand() {
            public void dispatch(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                request.setAttribute(REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW, flowSessionAttributeName);
                dispatchToFirstPage(request, response);
            }
        };
        new ListDocumentAliasPage(null, cancelCommand, request ).forward(request, response);
    }

    private void dispatchToPublisherUserBrowser( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        final String flowSessionAttributeName = HttpSessionUtils.getSessionAttributeNameFromRequest( request, REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW );
        dispatchToUserBrowser( request, response, true, HEADLINE__SELECT_PUBLISHER, new UserFinder.SelectUserCommand() {
            public void selectUser( UserDomainObject selectedUser, HttpServletRequest request,
                                    HttpServletResponse response ) throws ServletException, IOException {
                document.setPublisher( selectedUser );
                request.setAttribute( REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW, flowSessionAttributeName );
                dispatchToFirstPage( request, response );
            }
        } );
    }

    private void dispatchToCreatorUserBrowser( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        final String flowSessionAttributeName = HttpSessionUtils.getSessionAttributeNameFromRequest( request, REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW );
        dispatchToUserBrowser( request, response, false, HEADLINE__SELECT_CREATOR, new UserFinder.SelectUserCommand() {
            public void selectUser( UserDomainObject selectedUser, HttpServletRequest request,
                                    HttpServletResponse response ) throws ServletException, IOException {
                document.setCreator( selectedUser );
                request.setAttribute( REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW, flowSessionAttributeName );
                dispatchToFirstPage( request, response );
            }
        } );
    }

    private void dispatchToUserBrowser( HttpServletRequest request, HttpServletResponse response,
                                        boolean nullSelectable, LocalizedMessage headline,
                                        UserFinder.SelectUserCommand selectUserCommand ) throws IOException, ServletException {
        UserFinder userFinder = UserFinder.getInstance( request );
        userFinder.setHeadline( headline );
        userFinder.setSelectButtonText( BUTTON_TEXT__SELECT_USER );
        userFinder.setUsersAddable( false );
        userFinder.setNullSelectable( nullSelectable );
        userFinder.setSelectUserCommand( selectUserCommand );
        final String flowSessionAttributeName = HttpSessionUtils.getSessionAttributeNameFromRequest( request, REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW );
        userFinder.setCancelCommand( new DispatchCommand() {
            public void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
                request.setAttribute( REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW, flowSessionAttributeName );
                dispatchToFirstPage( request, response );
            }
        } );
        userFinder.forward( request, response );
    }

    protected void dispatchToFirstPage( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        dispatchToDocumentInformationPage( request, response );
    }

    private void dispatchToDocumentInformationPage( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        DocumentInformationPage documentInformationPage = new DocumentInformationPage(getDocument(), adminButtonsHidden, errors);
        documentInformationPage.forward( request, response );
    }

    protected void dispatchOkFromEditPage( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        dispatchOkFromDocumentInformation( request, response );
    }

    private void dispatchOkFromDocumentInformation(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        setDocumentAttributesFromRequestParameters( document, request, errors);
        if (!errors.isEmpty()) {
            dispatchToDocumentInformationPage(request, response);
        }
    }

    private static void setDocumentAttributesFromRequestParameters(DocumentDomainObject document, HttpServletRequest request, Set errors) {

        final ImcmsServices service = Imcms.getServices();
        final CategoryMapper categoryMapper = service.getCategoryMapper();
        final DocumentMapper documentMapper = service.getDocumentMapper();

        String headline = request.getParameter( REQUEST_PARAMETER__HEADLINE );
        document.setHeadline( headline );

        String menuText = request.getParameter( REQUEST_PARAMETER__MENUTEXT );
        document.setMenuText( menuText );

        String imageUrl = request.getParameter( REQUEST_PARAMETER__IMAGE );
        document.setMenuImage( imageUrl );

        String status = request.getParameter( REQUEST_PARAMETER__STATUS );
        Document.PublicationStatus publicationStatus = publicationStatusFromString(status);
        document.setPublicationStatus( publicationStatus );

        SimpleDateFormat dateFormat = new SimpleDateFormat( DateConstants.DATE_FORMAT_STRING );
        SimpleDateFormat timeFormat = new SimpleDateFormat( DateConstants.TIME_NO_SECONDS_FORMAT_STRING );

        Date publicationStartDatetime = parseDatetimeParameters( request, REQUEST_PARAMETER__PUBLICATION_START_DATE, REQUEST_PARAMETER__PUBLICATION_START_TIME, dateFormat,
                                                                 timeFormat );
        Date archivedDatetime = parseDatetimeParameters( request, REQUEST_PARAMETER__ARCHIVED_DATE, REQUEST_PARAMETER__ARCHIVED_TIME, dateFormat,
                                                         timeFormat );
        Date publicationEndDatetime = parseDatetimeParameters( request, REQUEST_PARAMETER__PUBLICATION_END_DATE, REQUEST_PARAMETER__PUBLICATION_END_TIME, dateFormat,
                                                               timeFormat );

        document.setPublicationStartDatetime( publicationStartDatetime );
        document.setArchivedDatetime( archivedDatetime );
        document.setPublicationEndDatetime( publicationEndDatetime );

        document.removeAllSections();
        String[] sectionIds = request.getParameterValues( REQUEST_PARAMETER__SECTIONS );
        for ( int i = 0; null != sectionIds && i < sectionIds.length; i++ ) {
            int sectionId = Integer.parseInt( sectionIds[i] );
            document.addSectionId( sectionId );
        }

        String languageIso639_2 = request.getParameter( REQUEST_PARAMETER__LANGUAGE );
        document.setLanguageIso639_2( languageIso639_2 );

        //*** Remove all categories except multi without picture
        CategoryTypeDomainObject[] categoryTypes = categoryMapper.getAllCategoryTypes() ;
        for ( CategoryTypeDomainObject categoryType : categoryTypes ) {
            boolean categoryTypeIsSingleChoice = 1 == categoryType.getMaxChoices();
            boolean shouldRemoveCategoriesOfType = categoryTypeIsSingleChoice || categoryType.hasImages();
            if ( shouldRemoveCategoriesOfType ) {
                Set<Integer> categoryIds = document.getCategoryIds();
                Set<CategoryDomainObject> categoriesOfType = categoryMapper.getCategoriesOfType(categoryType, categoryIds);
                for ( CategoryDomainObject category : categoriesOfType ) {
                    document.removeCategoryId(category.getId());
                }
            }
        }

        String[] categoryIds = request.getParameterValues( REQUEST_PARAMETER__CATEGORIES );
        for ( int i = 0; null != categoryIds && i < categoryIds.length; i++ ) {
            try {
                int categoryId = Integer.parseInt( categoryIds[i] );
                document.addCategoryId( categoryId );
            } catch ( NumberFormatException ignored ) {
                // OK, empty category id
            }
        }

        boolean visibleInMenuForUnauthorizedUsers = "1".equals( request.getParameter( REQUEST_PARAMETER__VISIBLE_IN_MENU_FOR_UNAUTHORIZED_USERS ) );
        document.setLinkedForUnauthorizedUsers( visibleInMenuForUnauthorizedUsers );

        boolean linkableByOtherUsers = "1".equals( request.getParameter( REQUEST_PARAMETER__LINKABLE_BY_OTHER_USERS ) );
        document.setLinkableByOtherUsers( linkableByOtherUsers );

        String keywordsString = request.getParameter( REQUEST_PARAMETER__KEYWORDS );
        KeywordsParser keywordsParser = new KeywordsParser();
        String[] keywords =  keywordsParser.parseKeywords( keywordsString );
        document.setKeywords( new ArraySet(keywords) );
        if ( null != request.getParameter(REQUEST_PARAMETER__DOCUMENT_ALIAS) ) {
            errors.remove(ALIAS_ERROR__ALREADY_EXIST);
            errors.remove(ALIAS_ERROR__USED_BY_SYSTEM);
            String oldAlias = document.getAlias();
            String newAlias = request.getParameter(REQUEST_PARAMETER__DOCUMENT_ALIAS).trim().replaceAll("[%?]", "");
            if(oldAlias==null || !newAlias.equals(oldAlias.toLowerCase()) && newAlias.length()>0){
                Set<String> allAlias = documentMapper.getAllDocumentAlias();
                File path = new File( Imcms.getPath(), newAlias );
                if (allAlias.contains(newAlias.toLowerCase())) {
                    errors.add(ALIAS_ERROR__ALREADY_EXIST) ;
                    newAlias = oldAlias;
                }else if (newAlias.length()>0 && path.exists()) {
                    errors.add(ALIAS_ERROR__USED_BY_SYSTEM) ;
                    newAlias = oldAlias;
                }
            }
            if(newAlias!=null && newAlias.length()>0){
                document.setAlias(newAlias);
            }else{
                document.setAlias(null);
            }
        }

        boolean searchDisabled = "1".equals( request.getParameter( REQUEST_PARAMETER__SEARCH_DISABLED ) );
        document.setSearchDisabled( searchDisabled );

        String target = getTargetFromRequest( request, REQUEST_PARAMETER__TARGET);
        document.setTarget( target );

        Date createdDatetime = (Date)ObjectUtils.defaultIfNull( parseDatetimeParameters( request, REQUEST_PARAMETER__CREATED_DATE, REQUEST_PARAMETER__CREATED_TIME, dateFormat, timeFormat ), new Date() );

        Date modifiedDatetime = (Date)ObjectUtils.defaultIfNull( parseDatetimeParameters( request, REQUEST_PARAMETER__MODIFIED_DATE, REQUEST_PARAMETER__MODIFIED_TIME, dateFormat, timeFormat ), createdDatetime );

        document.setCreatedDatetime( createdDatetime );
        document.setModifiedDatetime( modifiedDatetime );

    }

    private static Document.PublicationStatus publicationStatusFromString(String status) {
        Document.PublicationStatus publicationStatus = null ;
        if (status.equals(REQUEST_PARAMETER__STATUS__NEW)) {
            publicationStatus = Document.PublicationStatus.NEW;
        } else if (status.equals(REQUEST_PARAMETER__STATUS__APPROVED)) {
            publicationStatus = Document.PublicationStatus.APPROVED;
        } else if (status.equals(REQUEST_PARAMETER__STATUS__DISAPPROVED)) {
            publicationStatus = Document.PublicationStatus.DISAPPROVED;
        }
        return publicationStatus;
    }

    public static String getTargetFromRequest(HttpServletRequest request, String parameterName) {
        String[] possibleTargets = request.getParameterValues( parameterName );
        String target = null;
        if (null != possibleTargets) {
            for ( String possibleTarget : possibleTargets ) {
                target = possibleTarget;
                boolean targetIsPredefinedTarget
                        = "_self".equalsIgnoreCase(target)
                          || "_blank".equalsIgnoreCase(target)
                          || "_parent".equalsIgnoreCase(target)
                          || "_top".equalsIgnoreCase(target);
                if ( targetIsPredefinedTarget ) {
                    break;
                }
            }
        }
        return target;
    }

    private static Date parseDatetimeParameters( HttpServletRequest req, final String dateParameterName,
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

    public void setAdminButtonsHidden( boolean adminButtonsHidden ) {
        this.adminButtonsHidden = adminButtonsHidden;
    }

    public static class DocumentInformationPage {

        private static final String REQUEST_ATTRIBUTE__DOCUMENT_INFORMATION_PAGE = "documentInformationPage";
        private DocumentDomainObject document;
        private boolean adminButtonsHidden;
        private Set errors;

        public DocumentInformationPage( DocumentDomainObject document, boolean adminButtonsHidden, Set errors ) {
            this.document = document;
            this.adminButtonsHidden = adminButtonsHidden;
            this.errors = errors;
        }

        public void forward( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
            request.setAttribute( REQUEST_ATTRIBUTE__DOCUMENT_INFORMATION_PAGE, this );
            UserDomainObject user = Utility.getLoggedOnUser( request );
            request.getRequestDispatcher( URL_I15D_PAGE__PREFIX + user.getLanguageIso639_2() + URL_I15D_PAGE__DOCINFO ).forward( request, response );
        }

        public static DocumentInformationPage fromRequest( HttpServletRequest request ) {
            return (DocumentInformationPage)request.getAttribute( REQUEST_ATTRIBUTE__DOCUMENT_INFORMATION_PAGE ) ;
        }

        public DocumentDomainObject getDocument() {
            return document;
        }

        public boolean isAdminButtonsHidden() {
            return adminButtonsHidden;
        }

        public Set getErrors() {
            return errors;
        }
    }
}
