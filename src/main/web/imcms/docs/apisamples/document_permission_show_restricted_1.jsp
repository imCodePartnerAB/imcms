<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcmsSystem.getDocumentService();
    int documentId = 1001;
    Document document = documentService.getTextDocument( documentId );
    DocumentPermissionSet restrictedOne = document.getPermissionSetRestrictedOne();
%>

In document <%=documentId%> the permission set "<%=restrictedOne.getType()%>" has the following rights:
<ul>
  <li>Edit document information: <%=restrictedOne.getEditDocumentInformationPermission()%></li>
  <li>Edit includes: <%=restrictedOne.getEditIncludesPermission()%></li>
  <li>Edit menus: <%=restrictedOne.getEditMenusPermission()%></li>
  <li>Edit permissions: <%=restrictedOne.getEditRolePermissionsPermission()%></li>
  <li>Edit pictures: <%=restrictedOne.getEditPicturesPermission()%></li>
  <li>Edit texts: <%=restrictedOne.getEditTextsPermission()%></li>
</ul>
<br>
        <%
        if( null != restrictedOne.getEditableTemplateGroupNames() ){ %>
            "<%=restrictedOne.getType()%>" allows editing template groups, the following groups are allowed to change:
            <ul> <%
            String[] templateNames = restrictedOne.getEditableTemplateGroupNames();
            for( int i = 0; i < templateNames.length ; i++ ) {%>
                <li><%=templateNames[i].toString()%></li><%
            }%>
            </ul> <%
        }
%>
