<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%!
    int documentId = 1001 ;
    int menuIndex = 1 ;
    private String makeLink(int documentId) {
        return "<a href=\"../servlet/GetDoc?meta_id="+ documentId +"\">document "+ documentId +"</a>" ;
    }

    private String makeOption(int value, String label, int currentSelection) {
        return "<option value="+value+ (currentSelection == value ? " selected" : "")+">"+label+"</option>" ;
    }
%>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TextDocument document = documentService.getTextDocument(documentId) ;

    String sortOrderStr = request.getParameter("sortorder") ;

    if (null != sortOrderStr) {
        int sortOrder = Integer.parseInt(sortOrderStr) ;
        document.setMenuSortOrder(sortOrder);
        documentService.saveChanges(document);
        %><p>The sort order of <%= makeLink(documentId)%> has been changed. See <a href="document_get_menu.jsp">document_get_menu.jsp</a></p><%
    }
    int currentSortOrder =  document.getMenuSortOrder();
%>
        <form method="POST">
            Sort <%= makeLink(documentId) %> by <select name="sortorder">
                <%= makeOption(TextDocument.Menu.SORT_BY_HEADLINE, "Headline", currentSortOrder) %>
                <%= makeOption(TextDocument.Menu.SORT_BY_MODIFIED_DATETIME_DESCENDING, "Modified date/time", currentSortOrder) %>
                <%= makeOption(TextDocument.Menu.SORT_BY_MANUAL_ORDER_DESCENDING, "Manual order", currentSortOrder) %>
            </select>
            <input type="submit" value="Set sort order of document <%= documentId %>">
        </form>
