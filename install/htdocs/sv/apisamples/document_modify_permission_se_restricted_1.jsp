<%@ page import="com.imcode.imcms.*"%>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentService = imcmsSystem.getDocumentService();
    int documentId = 1001;
    Document document = documentService.getDocument( documentId );
    DocumentPermissionSet restrictedOne = document.getPermissionSetRestrictedOne();
    if( restrictedOne != null ){%>

In document <%=documentId%> the permission set "<%=restrictedOne.getType()%>" has the following rights:
<ul>
  <li>Edit document information: <%=restrictedOne.getEditDocumentInformationPermission()%></li>
  <li>Edit headline: <%=restrictedOne.getEditHeadlinePermission()%></li>
  <li>Edit includes: <%=restrictedOne.getEditIncludesPermission()%></li>
  <li>Edit permissions: <%=restrictedOne.getEditRolePermissionsPermission()%></li>
  <li>Edit pictures: <%=restrictedOne.getEditPicturesPermission()%></li>
  <li>Edit texts: <%=restrictedOne.getEditTextsPermission()%></li>
</ul>
<br>
 <%
        if( null != restrictedOne.getEditableMenuDocumentTypeNames() ){ %>
            "<%=restrictedOne.getType()%>" allows to edit menus, the following menus are allowed to change:
            <ul><%
            String[] menuNames = restrictedOne.getEditableMenuDocumentTypeNames();
            for( int i = 0; i < menuNames.length ; i++ ) {%>
                <li><%=menuNames[i].toString()%></li><%
            }%>
            </ul> <%
        }%>
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
    }
%>






