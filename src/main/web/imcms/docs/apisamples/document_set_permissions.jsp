<%@ page import="com.imcode.imcms.api.*,
java.util.*" errorPage="error.jsp" %>
<html>
<body>
<%
    final int documentId = 1001;

    ContentManagementSystem imcms = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcms.getDocumentService() ;
    UserService userService = imcms.getUserService();
    Role role = userService.getRole( Role.USERS_ID ) ;

    TextDocument document = documentService.getTextDocument(documentId) ;

    int oldPermissionSet = document.getPermissionSetIdForRole( role ) ;
    int permissionSet = DocumentPermissionSet.NONE;
    if (oldPermissionSet == DocumentPermissionSet.NONE ) {
        permissionSet = DocumentPermissionSet.READ ;
    }
    document.setPermissionSetIdForRole( role, permissionSet );
    documentService.saveChanges( document );
    %>Done, see <a href="document_permissions.jsp">document_permissions.jsp</a>.<%
%>
</body>
</html>