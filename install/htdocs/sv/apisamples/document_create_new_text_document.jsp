<%@ page import="com.imcode.imcms.*"%>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    DocumentService documentMapper = imcmsSystem.getDocumentService() ;

    int parentId = 1001;
    int parentMenuNumber = 1;
    Document document = documentMapper.createNewTextDocument( parentId, parentMenuNumber ) ;
    document.setHeadline( "Nyligen skapat dokument" );
%>
Skapade dokument med id "<%= document.getId() %>"  med länk från dokument med id "<%= parentId %>".<br>
Se resultatet <a href="../servlet/GetDoc?meta_id=1001">här.</a><br>
