<%@ page import="com.imcode.imcms.api.*,
                 java.util.*" errorPage="error.jsp" %>
<html>
<body>
<h1>Show document permissions</h1>
There are three basic kinds of permissions<br>
<ul>
  <li>Full</li>
  <li>Read</li>
  <li>None</li>
</ul>
<p>
In between "Full" and "Read" there can also be defined two that can be modified, they are called
<ul>
  <li>Restricted One (1)</li>
  <li>Restricted Two (2)</li>
</ul>
and can be set differently for different pages (and sub pages).
</p>
<%  int documentId = 1001;
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentService = imcmsSystem.getDocumentService();
    Document document = documentService.getTextDocument(documentId);
%>
<p>
Every document has a mapping of permissions to roles.<br>
This is a map of the format (RoleName,DocumentPermissionSet)
</p>
<p>
This is the mapping for document <%= documentId %>:<br>
<%
    Map permissionsMap = document.getAllRolesMappedToPermissions();
    Set roles = permissionsMap.keySet();
    Iterator roleIterator = roles.iterator();
    %><ul><%
    while( roleIterator.hasNext() ) {
        String roleName = (String)roleIterator.next();
        DocumentPermissionSet documentPermission = (DocumentPermissionSet)permissionsMap.get( roleName );%>
        <li>The role "<%=roleName%>" has permission "<%= documentPermission.toString() %>"</li><%
    }
    %></ul><%
%>
</p>
<p>
<% if (imcmsSystem.getCurrentUser().canEdit( document )) { %>
    You have the following permissions for document <%= documentId %>: "<%= document.getDocumentPermissionSetForUser() %>"
<% } else { %>
    You have no permissions for document <%= documentId %>.
<% } %>
</p>
<p>
Notice: Only the roles that has some permissions is shown above. If a role has permission "None" then
that role is not part of the result map.
</p>
</body>
</html>
