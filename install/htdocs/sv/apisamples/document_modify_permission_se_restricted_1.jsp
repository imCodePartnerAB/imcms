<%@ page import="com.imcode.imcms.*"%>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentService = imcmsSystem.getDocumentService();
    int documentId = 1001;
    Document document = documentService.getDocument( documentId );
    DocumentPermissionSet restrictedOne = document.getPermissionSetRestrictedOne();
    if( restrictedOne != null ){%>

In document <%=documentId%> the permission set "<%=restrictedOne.getName()%>" has the following rights:
<ul>
  <li>Edit document information: <%=restrictedOne.getEditDocumentInformation()%></li>
  <li>Edit headline: <%=restrictedOne.getEditHeadline()%></li>
  <li>Edit includes: <%=restrictedOne.getEditIncludes()%></li>
  <li>Edit permissions: <%=restrictedOne.getEditPermissions()%></li>
  <li>Edit pictures: <%=restrictedOne.getEditPictures()%></li>
  <li>Edit texts: <%=restrictedOne.getEditTexts()%></li>
</ul>

<%
//    if( null != restrictedOne.getEditableMenuNames() ){
        String[] menuNames = restrictedOne.getEditableMenuNames();
            for( int i = 0; i < menuNames.length ; i++ ) {%>
                <%=menuNames[i].toString()%><br><%
            }
        }
%>






