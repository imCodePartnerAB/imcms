<%@ page import="com.imcode.imcms.api.*,
                 java.util.SortedMap,
                 java.util.Map,
                 java.util.Iterator" errorPage="error.jsp" %>
<html>
<body>
<%!
    int documentId = 1001 ;
    private String makeLink(int documentId,HttpServletRequest request) {
        return "<a href=\""+request.getContextPath()+"/servlet/GetDoc?meta_id="+ documentId +"\">document "+ documentId +"</a>" ;
    }
%>
<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TextDocument document = documentService.getTextDocument(documentId) ;

    try {
        int sortOrder = Integer.parseInt(request.getParameter("sortorder")) ;

        Map menus = document.getMenus() ;
        for ( Iterator iterator = menus.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry)iterator.next();
            TextDocument.Menu menu = (TextDocument.Menu)entry.getValue();
            menu.setSortOrder( sortOrder );
        }

        documentService.saveChanges(document);
        %><p>The sort orders of all menus on <%= makeLink(documentId,request )%> have been changed. See <a href="document_get_menu.jsp">document_get_menu.jsp</a></p><%
    } catch (NumberFormatException ignored) {
    }
%>
        <form method="POST">
            Sort <%= makeLink(documentId,request ) %> by <select name="sortorder">
                <option value=""></option>
                <option value="<%= TextDocument.Menu.SORT_BY_HEADLINE %>">Headline</option>
                <option value="<%= TextDocument.Menu.SORT_BY_MODIFIED_DATETIME_DESCENDING %>">Modified date/time</option>
                <option value="<%= TextDocument.Menu.SORT_BY_MANUAL_ORDER_DESCENDING %>">Manual order</option>
            </select>
            <input type="submit" value="Set sort order of document <%= documentId %>">
        </form>
</body>
</html>
