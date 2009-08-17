<%@ page import="com.imcode.imcms.api.*,
                 java.util.*" errorPage="error.jsp" %>
<html>
<body>
<h1>Document permissions</h1>
<p>There are three basic kinds of permissions:</p>
<ul>
  <li>Full</li>
  <li>Read</li>
  <li>None</li>
</ul>
<p>
In between "Full" and "Read" there can also be defined two that are modifiable:
<ul>
  <li>Restricted One (1)</li>
  <li>Restricted Two (2)</li>
</ul>
They can be set differently for different pages (and for new pages created from them).
</p>
<%  int documentId = 1001;
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcmsSystem.getDocumentService();
    Document document = documentService.getTextDocument(documentId);
%>
<p>
Every document has a mapping of permissions to roles.<br>
This is a map of the format (Role, DocumentPermissionSet)
</p>
<p>Permissions mappings for document <%= documentId %>:</p>
<p>
<%
    Map permissionsMap = document.getRolesMappedToPermissions();
    Set roles = permissionsMap.keySet();
    Iterator roleIterator = roles.iterator();
    %><ul><%
    while( roleIterator.hasNext() ) {
        Role role = (Role)roleIterator.next();
        DocumentPermissionSet documentPermission = (DocumentPermissionSet)permissionsMap.get( role );%>
        <li>The role "<%=role.getName()%>" has permission "<%= documentPermission.toString() %>"</li><%
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
Notice: Only the roles that have some permissions are shown above. If a role has permission "None" then
that role is not shown.
</p>
</body>
</html>
