<%@ page import="com.imcode.imcms.api.*,
java.util.*" errorPage="error.jsp" %>
<%!
    int documentId = 1001;
    int permissionSet = DocumentPermissionSet.NONE;
    String roleName = RoleConstants.USERS;
    //String roleName = "Non-existent role" ;
%>

<html>
<body>
<%
    ContentManagementSystem imcms = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcms.getDocumentService() ;

    TextDocument document = documentService.getTextDocument(documentId) ;

    try {
        document.setPermissionSetForRole(roleName, permissionSet) ;
        documentService.saveChanges( document );
        %>Done, see <a href="document_permissions.jsp">document_permissions.jsp</a>.<%
    } catch (NoSuchRoleException nsre) {
        %>No such role '<%= roleName %>'.<%
    }

%>
</body>
</html>