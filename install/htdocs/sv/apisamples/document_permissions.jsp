<%@ page import="com.imcode.imcms.api.*,
                 java.util.*" errorPage="error.jsp" %>

<h1>Show document permissions</h1>
There are three basic kinds of permissions<br>
<ul>
  <li>Full</li>
  <li>Read</li>
  <li>None</li>
</ul>

In between "Full" and "Read" there can also be defined two that can be modified, they are called
<ul>
  <li>Restricted One (1)</li>
  <li>Restricted Two (2)</li>
</ul>
and can be set differently for different pages (and sub pages).<br>
Every document has a mapping of permissions to roles.<br>
This is a map of the format (RoleName,DocumentPermissionSet)<br><br>
<% int documentId = 1001; %>
This is the mapping for document <%= documentId %>:<br>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentService = imcmsSystem.getDocumentService();
    Document doc = documentService.getTextDocument(documentId);
    Map permissionsMap = doc.getAllRolesMappedToPermissions();
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
<br>
Notice: Only the roles that has some permissions is shown above. If a role has permission "None" then
that role is not part of the result map.<br>

