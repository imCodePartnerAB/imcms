<%@ page import="com.imcode.imcms.api.*,
                 java.util.Date" errorPage="error.jsp" %>
<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcmsSystem.getDocumentService() ;
    int documentId = 1001 ;
    TextDocument document = documentService.getTextDocument(documentId) ;
    com.imcode.imcms.persistence.entity.Language language = DocumentLanguages.getCurrentLanguage();

    document.setHeadline(language, "Test headline text");
    document.setMenuText(language, "Test menu text");
    document.setMenuImageURL(language, "Test menu image url");

    document.setPublicationStartDatetime( new Date() );
    document.setArchivedDatetime( new Date() );
    document.setPublicationStatus( Document.PublicationStatus.APPROVED );
    document.setVisibleInMenusForUnauthorizedUsers( false );

    //Section section = documentService.getSection( "sectionname" );
    Section section = documentService.getSection( 1 );
    if (null != section) {
        document.addSection(section) ;
    } else {
    %> (The section did not exist.)<br> <%
    }

    //Language english = Language.getLanguageByISO639_1( "en" );
    com.imcode.imcms.api.Language english = com.imcode.imcms.api.Language.getLanguageByISO639_2("eng");
    document.setLanguage( english );

    String categoryTypeName = "Type";
    CategoryType categoryType = documentService.getCategoryType(categoryTypeName);
    if( null != categoryType ) {
        String categoryName = "Image";
        Category legalSubjectCategory = documentService.getCategory(categoryType, categoryName) ;
        if (null != legalSubjectCategory) {
            document.addCategory(legalSubjectCategory) ;
        } else {
        %> (The category did not exist.)<br> <%
        }
    }

    UserService userService = imcmsSystem.getUserService();
    User admin = userService.getUser("admin");

    document.setPublisher( admin );
    document.setCreator( admin ) ;

    // don't forget to save your changes!
    document.setAlias("start");
    try{
        documentService.saveChanges( document );
    }catch(SaveException e){
        document.setAlias(null);
        documentService.saveChanges( document );
    }

%>
Done changing document <a href="<%= request.getContextPath() %>/servlet/GetDoc?meta_id=<%= documentId %>"><%= documentId %></a>.
