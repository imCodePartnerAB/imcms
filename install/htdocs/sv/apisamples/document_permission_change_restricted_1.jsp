<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>
<html>
<head>
<title>Delete a role named "Test role"</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<body>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentService = imcmsSystem.getDocumentService();
    int documentId = 1001;
    TextDocument document = documentService.getTextDocument( documentId );
    DocumentPermissionSet restrictedTwo = document.getPermissionSetRestrictedTwo();

    // lets invert all the permissions
    restrictedTwo.setEditDocumentInformationPermission( !restrictedTwo.getEditDocumentInformationPermission() );
    restrictedTwo.setEditHeadlinePermission( !restrictedTwo.getEditHeadlinePermission() );
    restrictedTwo.setEditIncludesPermission( !restrictedTwo.getEditIncludesPermission() );
    restrictedTwo.setEditPicturesPermission( !restrictedTwo.getEditPicturesPermission() );
    restrictedTwo.setEditRolePermissionsPermission( !restrictedTwo.getEditRolePermissionsPermission() );
    restrictedTwo.setEditTextsPermission( !restrictedTwo.getEditTextsPermission() );

    documentService.saveChanges( document );
%>
Done.
</body>
</html>