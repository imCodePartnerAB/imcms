<%@ page import="com.imcode.imcms.api.UrlDocument,
                 imcode.server.document.UrlDocumentDomainObject,
                 imcode.server.ApplicationServer,
                 imcode.server.document.DocumentMapper,
                 imcode.server.document.textdocument.TextDocumentDomainObject,
                 org.apache.commons.lang.StringEscapeUtils,
                 com.imcode.imcms.servlet.admin.AdminDoc,
                 imcode.server.IMCConstants,
                 imcode.server.user.UserDomainObject,
                 imcode.util.Utility"%>
<%@page contentType="text/html"%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%><%
    UserDomainObject user = Utility.getLoggedOnUser( request ) ;
    if (!user.isSuperAdmin()) {
        return ;
    }
    int id = Integer.parseInt(request.getParameter( "id" ) ) ;
    DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
    UrlDocumentDomainObject urlDocument = (UrlDocumentDomainObject)documentMapper.getDocument( id ) ;
    DocumentMapper.TextDocumentMenuIndexPair[] documentMenuPairs = documentMapper.getDocumentsMenuPairsContainingDocument( urlDocument ) ;
%><vel:velocity><html>
    <head>
        <title><? web/imcms/lang/jsp/linkcheck/linkcheckrefs.jsp/heading ?></title>
        <link rel="stylesheet" href="$contextPath/imcms/css/imcms_admin.css" type="text/css">
    </head>
    <body>
        #gui_outer_start()
        #gui_head( '<? web/imcms/lang/jsp/linkcheck/linkcheckrefs.jsp/heading ?>' )
        <form method="GET" action="<%= request.getContextPath() %>/servlet/LinkCheck">
            <input type="submit" class="imcmsFormBtn" name="Submit" value="<? global/back ?>">&nbsp;
        </form>
        #gui_mid()
        #gui_heading( '<? web/imcms/lang/jsp/linkcheck/linkcheckrefs.jsp/explanation ?>' )
        <%
            for ( int i = 0; i < documentMenuPairs.length; i++ ) {
                DocumentMapper.TextDocumentMenuIndexPair textDocumentMenuIndexPair = documentMenuPairs[i];
                TextDocumentDomainObject textDocument = textDocumentMenuIndexPair.getDocument();
                int menuIndex = textDocumentMenuIndexPair.getMenuIndex();
        %>
                <a href="<%= request.getContextPath() %>/servlet/GetDoc?meta_id=<%= textDocument.getId() %>">
                    <%= textDocument.getId() %> "<%= StringEscapeUtils.escapeHtml(textDocument.getHeadline()) %>"
                </a>
                -
                <a href="<%= request.getContextPath() %>/servlet/AdminDoc?meta_id=<%= textDocument.getId() %>&<%= AdminDoc.PARAMETER__DISPATCH_FLAGS %>=<%= IMCConstants.DISPATCH_FLAG__EDIT_MENU %>&editmenu=<%= menuIndex %>">
                    <? web/imcms/lang/jsp/linkcheck/linkcheckrefs.jsp/heading_menu ?> <%= menuIndex %>
                </a>
        <div>#gui_hr( 'blue' )</div>
        <% } %>
        #gui_bottom()
        #gui_outer_end()
    </body>
</html>
</vel:velocity>
