<%@ page import="com.imcode.imcms.api.*,
                 java.util.*" errorPage="error.jsp" %>

<h1><? sv/apisamples/document_permissions.jsp/1 ?></h1>
<? sv/apisamples/document_permissions.jsp/2 ?>
<ul>
  <li><? sv/apisamples/document_permissions.jsp/3 ?></li>
  <li><? sv/apisamples/document_permissions.jsp/4 ?></li>
  <li><? sv/apisamples/document_permissions.jsp/5 ?></li>
</ul>

<? sv/apisamples/document_permissions.jsp/6 ?>
<ul>
  <li><? sv/apisamples/document_permissions.jsp/7 ?></li>
  <li><? sv/apisamples/document_permissions.jsp/8 ?></li>
</ul>
<? sv/apisamples/document_permissions.jsp/9 ?><ul><%
    while( roleIterator.hasNext() ) {
        String roleName = (String)roleIterator.next();
        DocumentPermissionSet documentPermission = (DocumentPermissionSet)permissionsMap.get( roleName );%>
        <li><? sv/apisamples/document_permissions.jsp/10 ?></li><%
    }
    %></ul><? sv/apisamples/document_permissions.jsp/11 ?>

