<%@ page import="imcode.server.Imcms,
                 imcode.server.document.DocumentMapper,
                 imcode.server.document.textdocument.TextDocumentDomainObject,
                 org.apache.commons.lang.StringEscapeUtils,
                 com.imcode.imcms.servlet.admin.AdminDoc,
                 imcode.server.ImcmsConstants,
                 imcode.server.user.UserDomainObject,
                 imcode.util.Utility,
                 com.imcode.imcms.servlet.superadmin.LinkCheck,
                 com.imcode.imcms.servlet.superadmin.DocumentReferences,
                 imcode.util.Html"%>
<%@page contentType="text/html"%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%><%
    UserDomainObject user = Utility.getLoggedOnUser( request ) ;
    if (!user.isSuperAdmin()) {
        return ;
    }
    DocumentMapper.TextDocumentMenuIndexPair[] documentMenuPairs = (DocumentMapper.TextDocumentMenuIndexPair[])request.getAttribute( DocumentReferences.REQUEST_ATTRIBUTE__DOCUMENT_MENU_PAIRS ) ;
%><vel:velocity><html>
    <head>
        <title><? web/imcms/lang/jsp/document_references.jsp/heading ?></title>
        <link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
    </head>
    <body>
        #gui_outer_start()
        #gui_head( '<? web/imcms/lang/jsp/document_references.jsp/heading ?>' )
        <form method="GET" action="DocumentReferences">
            <input type="hidden" name="<%= DocumentReferences.REQUEST_PARAMETER__RETURNURL %>" value="<%= request.getParameter(DocumentReferences.REQUEST_PARAMETER__RETURNURL) %>">
            <input type="submit" class="imcmsFormBtn" name="<%= DocumentReferences.REQUEST_PARAMETER__BUTTON_RETURN %>" value="<? global/back ?>">
        </form>
        #gui_mid()
        #gui_heading( '<? web/imcms/lang/jsp/document_references.jsp/explanation ?>' )
        <table border="0">
            <tr>
                <th><? web/imcms/lang/jsp/heading_status ?></th>
                <th><? web/imcms/lang/jsp/heading_adminlink ?></th>
            </tr>
        <%
            for ( int i = 0; i < documentMenuPairs.length; i++ ) {
                DocumentMapper.TextDocumentMenuIndexPair textDocumentMenuIndexPair = documentMenuPairs[i];
                TextDocumentDomainObject textDocument = textDocumentMenuIndexPair.getDocument();
                int menuIndex = textDocumentMenuIndexPair.getMenuIndex();
        %>
            <tr>
                <td><%= Html.getLinkedStatusIconTemplate( textDocument, user, request ) %></td>
                <td>
                    <a href="<%= request.getContextPath() %>/servlet/AdminDoc?meta_id=<%= textDocument.getId() %>&<%= AdminDoc.PARAMETER__DISPATCH_FLAGS %>=<%= ImcmsConstants.DISPATCH_FLAG__EDIT_MENU %>&editmenu=<%= menuIndex %>">
                        <%= textDocument.getId() %>: "<%= StringEscapeUtils.escapeHtml(textDocument.getHeadline()) %>"
                        -
                        <? web/imcms/lang/jsp/document_references.jsp/heading_menu ?> <%= menuIndex %>
                    </a>
                </td>
            </tr>
        <% } %>
        </table>
        #gui_bottom()
        #gui_outer_end()
    </body>
</html>
</vel:velocity>
