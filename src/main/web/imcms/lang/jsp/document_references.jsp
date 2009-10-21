<%@ page import="imcode.server.document.textdocument.TextDocumentDomainObject,
                 org.apache.commons.lang.StringEscapeUtils,
                 com.imcode.imcms.servlet.admin.AdminDoc,
                 imcode.server.ImcmsConstants,
                 imcode.server.user.UserDomainObject,
                 imcode.util.Utility,
                 com.imcode.imcms.servlet.superadmin.DocumentReferences,
                 imcode.util.Html"%><%@ page import="com.imcode.imcms.mapping.DocumentMapper"%>
<%@page contentType="text/html; charset=UTF-8"%><%@taglib prefix="vel" uri="imcmsvelocity"%><%
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
				<table border="0" cellspacing="0" cellpadding="0">
				<form method="GET" action="DocumentReferences">
				<input type="hidden" name="<%= DocumentReferences.REQUEST_PARAMETER__RETURNURL %>" value="<%= request.getParameter(DocumentReferences.REQUEST_PARAMETER__RETURNURL) %>">
				<tr>
					<td><input type="submit" class="imcmsFormBtn" name="<%= DocumentReferences.REQUEST_PARAMETER__BUTTON_RETURN %>" value="<? global/back ?>"></td>
				</tr>
				</form>
				</table>
        #gui_mid()
        <table border="0" cellspacing="0" cellpadding="2" width="400">
            <tr>
                <td colspan="2">#gui_heading( '<? web/imcms/lang/jsp/document_references.jsp/explanation ?>' )</td>
            </tr>
            <tr>
                <td width="15%" align="center"><b><? web/imcms/lang/jsp/heading_status ?>&nbsp;</b></td>
                <td width="85%"><b><? web/imcms/lang/jsp/heading_adminlink ?></b></td>
            </tr>
        <%
            for ( int i = 0; i < documentMenuPairs.length; i++ ) {
                DocumentMapper.TextDocumentMenuIndexPair textDocumentMenuIndexPair = documentMenuPairs[i];
                TextDocumentDomainObject textDocument = textDocumentMenuIndexPair.getDocument();
                int menuIndex = textDocumentMenuIndexPair.getMenuIndex();
        %>
            <tr>
                <td colspan="2"><img
								src="$contextPath/imcms/$language/images/admin/1x1_cccccc.gif" width="100%" height="1" vspace="4"></td>
            </tr>
            <tr>
                <td align="center"><%= Html.getLinkedStatusIconTemplate( textDocument, user, request ) %></td>
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
