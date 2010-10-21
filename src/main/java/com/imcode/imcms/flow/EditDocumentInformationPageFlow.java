package com.imcode.imcms.flow;

import com.imcode.imcms.api.DocumentLabels;
import com.imcode.imcms.mapping.DocumentSaveException;
import com.imcode.imcms.util.Factory;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.*;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import imcode.util.HttpSessionUtils;
import imcode.util.ShouldHaveCheckedPermissionsEarlierException;
import imcode.util.Utility;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;

import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.Meta;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.servlet.admin.ImageBrowser;
import com.imcode.imcms.servlet.admin.ListDocumentAliasPage;
import com.imcode.imcms.servlet.admin.UserFinder;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import com.imcode.imcms.dao.MetaDao;
import com.imcode.util.KeywordsParser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;

/**
 * Historically DocumentPageFlow is build around a single document editing.
 * @see com.imcode.imcms.flow.DocumentPageFlow.SaveDocumentCommand
 *
 * i18n support requirement - labels editing in different languages required to introduce a hack.
 */
public class EditDocumentInformationPageFlow extends EditDocumentPageFlow {

    private final static String URL_I15D_PAGE__DOCINFO = "/jsp/docadmin/document_information.jsp";
    public static final String REQUEST_PARAMETER__INTERNAL_ID = "internal_id";
    public static final String REQUEST_PARAMETER__HEADLINE = "headline";
    public static final String REQUEST_PARAMETER__MENUTEXT = "menutext";
    public static final String REQUEST_PARAMETER__COPY_HEADLINE_AND_TEXT_TO_TEXTFIELDS = "copy_headline_and_text_to_textfields";
    public static final String REQUEST_PARAMETER__IMAGE = "image";
    public static final String REQUEST_PARAMETER__DOCUMENT_ALIAS = "document_alias";
    public static final String REQUEST_PARAMETER__PUBLICATION_START_DATE = "activated_date";
    public static final String REQUEST_PARAMETER__PUBLICATION_START_TIME = "activated_time";
    public static final String REQUEST_PARAMETER__ARCHIVED_DATE = "archived_date";
    public static final String REQUEST_PARAMETER__ARCHIVED_TIME = "archived_time";
    public static final String REQUEST_PARAMETER__PUBLICATION_END_DATE = "publication_end_date";
    public static final String REQUEST_PARAMETER__PUBLICATION_END_TIME = "publication_end_time";
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
    
    public static final String REQUEST_PARAMETER__MISSING_I18N_SHOW_RULE = "missingI18nShowRule";
    public static final String REQUEST_PARAMETER__ENABLED_I18N = "activeLanguage";
    public static final String REQUEST_PARAMETER__I18N_CODE = "i18nCode";

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

    /** Document languages enabled/disabled states. */
    private Map<I18nLanguage, Boolean> languagesStates = new HashMap<I18nLanguage, Boolean>();

    private Map<I18nLanguage, DocumentLabels> labelsMap = new HashMap<I18nLanguage, DocumentLabels>(); 


    public EditDocumentInformationPageFlow(DocumentDomainObject document, DispatchCommand returnCommand,
                                                SaveDocumentCommand saveDocumentCommand ) {
        
        super(document, returnCommand, saveDocumentCommand);

        // i18n support
        Set<I18nLanguage> languages = new HashSet(Imcms.getI18nSupport().getLanguages());

        for (I18nLanguage language: languages) {
            languagesStates.put(language, false);
        }

        // ???
        // languagesStates.put(Imcms.getI18nSupport().getDefaultLanguage(), true);

        Integer docId = document.getMeta().getId();
        
        MetaDao metaDao = (MetaDao)Imcms.getSpringBean("metaDao");
        
        if (docId == null) {
            for (I18nLanguage language: languages) {
                labelsMap.put(language, Factory.createLabels(docId, language));
            }

            labelsMap.put(document.getLanguage(), document.getLabels());
        } else {
            for (I18nLanguage language: document.getMeta().getLanguages()) {
                languagesStates.put(language, true);
            }

            for (I18nLanguage language: languages) {
                DocumentLabels labels = metaDao.getLabels(docId, language);
                if (labels == null) {
                    labels = Factory.createLabels(docId, language);    
                }

                labelsMap.put(language, labels);
            }
        }
    }

    
    @Override
    protected synchronized void saveDocument( HttpServletRequest request ) {
        try {
            saveDocumentCommand.saveI18nDocument(getDocument(), labelsMap, Utility.getLoggedOnUser(request));
        } catch ( NoPermissionToEditDocumentException e ) {
            throw new ShouldHaveCheckedPermissionsEarlierException(e);
        } catch ( NoPermissionToAddDocumentToMenuException e ) {
            throw new ConcurrentDocumentModificationException(e);
        } catch (DocumentSaveException e) {
            throw new UnhandledException(e);
        }
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
        String i18nCode = request.getParameter(REQUEST_PARAMETER__I18N_CODE);

        if (StringUtils.isBlank(i18nCode)) {
            throw new IllegalStateException("i18nCode request parameter is blank.");
        }

        final I18nLanguage language = Imcms.getI18nSupport().getByCode(i18nCode);

        if (language == null) {
            throw new IllegalArgumentException(String.format("Language with code %s does not exists.", i18nCode));
        }

    	    	
        final String flowSessionAttributeName = HttpSessionUtils.getSessionAttributeNameFromRequest( request, REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW );
        imageBrowser.setCancelCommand( new DispatchCommand() {
            public void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
                request.setAttribute( REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW, flowSessionAttributeName );
                dispatchToFirstPage( request, response );
            }
        } );


        imageBrowser.setSelectImageUrlCommand( new ImageBrowser.SelectImageUrlCommand() {
            public void selectImageUrl( String imageUrl, HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
                labelsMap.get(language).setMenuImageURL(imageUrl);

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
        documentInformationPage.setLanguagesStates(languagesStates);
        documentInformationPage.setLabelsMap(labelsMap);
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

    private /*static*/ void setDocumentAttributesFromRequestParameters(DocumentDomainObject document, HttpServletRequest request, Set errors) {

        final ImcmsServices service = Imcms.getServices();
        final CategoryMapper categoryMapper = service.getCategoryMapper();
        final DocumentMapper documentMapper = service.getDocumentMapper();

        for (Map.Entry<I18nLanguage, DocumentLabels> l: labelsMap.entrySet()) {
        	String suffix = "_" + l.getKey().getCode();

            String headline = request.getParameter(REQUEST_PARAMETER__HEADLINE + suffix);
            String menuText = request.getParameter(REQUEST_PARAMETER__MENUTEXT + suffix);
            String imageURL = request.getParameter(REQUEST_PARAMETER__IMAGE + suffix);

            DocumentLabels labels = l.getValue();

            labels.setHeadline(headline);
            labels.setMenuText(menuText);
            labels.setMenuImageURL(imageURL);
        }

        document.setLabels(labelsMap.get(document.getLanguage()));

        Set<I18nLanguage> enabledLanguages = document.getMeta().getLanguages();

        enabledLanguages.clear();

        for (Map.Entry<I18nLanguage, Boolean> state: languagesStates.entrySet()) {
            I18nLanguage language = state.getKey();
        	String suffix = "_" + language.getCode();
            boolean enabled = request.getParameter(REQUEST_PARAMETER__ENABLED_I18N + suffix) != null;
            
            state.setValue(enabled);

            if (enabled) {
                enabledLanguages.add(language);
            }
        }


        String keywordsString = request.getParameter( REQUEST_PARAMETER__KEYWORDS);
        KeywordsParser keywordsParser = new KeywordsParser();

        String[] values =  keywordsParser.parseKeywords( keywordsString );

        Set<String> keywords = new HashSet<String>();
        for (String keyword: values) {
            keywords.add(keyword);
        }

        document.setKeywords(keywords);
                        
        String missingI18nShowRule = request.getParameter(REQUEST_PARAMETER__MISSING_I18N_SHOW_RULE); 
        
        document.getMeta().setDisabledLanguageShowSetting(Meta.DisabledLanguageShowSetting.valueOf(missingI18nShowRule));
        
        
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
        private Map<I18nLanguage, Boolean> languagesStates;
        private boolean adminButtonsHidden;
        private Set errors;
        private Map<I18nLanguage, DocumentLabels> labelsMap;

        public DocumentInformationPage( DocumentDomainObject document, boolean adminButtonsHidden, Set errors ) {
            this.document = document;
            this.adminButtonsHidden = adminButtonsHidden;
            this.errors = errors;
        }

        public Map<I18nLanguage, DocumentLabels> getLabelsMap() {
            return labelsMap;
        }

        public void setLabelsMap(Map<I18nLanguage, DocumentLabels> labelsMap) {
            this.labelsMap = labelsMap;
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

        public Map<I18nLanguage, Boolean> getLanguagesStates() {
            return languagesStates;
        }

        public void setLanguagesStates(Map<I18nLanguage, Boolean> languagesStates) {
            this.languagesStates = languagesStates;
        }
    }
}
