<%@ page import="com.imcode.imcms.api.UrlDocument,
                 imcode.server.document.UrlDocumentDomainObject,
                 imcode.server.ApplicationServer,
                 imcode.server.document.DocumentMapper,
                 imcode.server.document.textdocument.TextDocumentDomainObject,
                 org.apache.commons.lang.StringEscapeUtils,
                 com.imcode.imcms.servlet.admin.AdminDoc,
                 imcode.server.IMCConstants,
                 imcode.server.user.UserDomainObject,
                 imcode.util.Utility,
                 com.imcode.imcms.servlet.superadmin.LinkCheck"%>
<%@page contentType="text/html"%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%><%
    UserDomainObject user = Utility.getLoggedOnUser( request ) ;
    if (!user.isSuperAdmin()) {
        return ;
    }
    DocumentMapper.TextDocumentMenuIndexPair[] documentMenuPairs = (DocumentMapper.TextDocumentMenuIndexPair[])request.getAttribute( LinkCheck.REQUEST_ATTRIBUTE__DOCUMENT_MENU_PAIRS ) ;
%><vel:velocity><html>
    <head>
        <title><? web/imcms/lang/jsp/linkcheck/linkcheckrefs.jsp/heading ?></title>
        <link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
    </head>
    <body>
        #gui_outer_start()
        #gui_head( '<? web/imcms/lang/jsp/linkcheck/linkcheckrefs.jsp/heading ?>' )
        <form method="GET" action="<%= request.getContextPath() %>/servlet/LinkCheck">
            <input type="submit" class="imcmsFormBtn" value="<? global/back ?>">&nbsp;
        </form>
        #gui_mid()
        #gui_heading( '<? web/imcms/lang/jsp/linkcheck/linkcheckrefs.jsp/explanation ?>' )
        <table border="0">
            <tr>
                <th><? web/imcms/lang/jsp/linkcheck/heading_status ?></th>
                <th><? web/imcms/lang/jsp/linkcheck/heading_adminlink ?></th>
            </tr>
        <%
            for ( int i = 0; i < documentMenuPairs.length; i++ ) {
                DocumentMapper.TextDocumentMenuIndexPair textDocumentMenuIndexPair = documentMenuPairs[i];
                TextDocumentDomainObject textDocument = textDocumentMenuIndexPair.getDocument();
                int menuIndex = textDocumentMenuIndexPair.getMenuIndex();
        %>
            <tr>
                <td><%= Utility.getLinkedStatusIconTemplate( textDocument, user ) %></td>
                <td>
                    <a href="<%= request.getContextPath() %>/servlet/AdminDoc?meta_id=<%= textDocument.getId() %>&<%= AdminDoc.PARAMETER__DISPATCH_FLAGS %>=<%= IMCConstants.DISPATCH_FLAG__EDIT_MENU %>&editmenu=<%= menuIndex %>">
                        <%= textDocument.getId() %>: "<%= StringEscapeUtils.escapeHtml(textDocument.getHeadline()) %>"
                        -
                        <? web/imcms/lang/jsp/linkcheck/linkcheckrefs.jsp/heading_menu ?> <%= menuIndex %>
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
