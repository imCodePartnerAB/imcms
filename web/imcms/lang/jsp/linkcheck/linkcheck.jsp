<%@ page import="com.imcode.imcms.servlet.superadmin.LinkCheck,
                 java.util.Iterator,
                 com.imcode.imcms.servlet.admin.AdminDoc,
                 imcode.server.IMCConstants,
                 imcode.server.document.DocumentMapper,
                 imcode.server.ApplicationServer,
                 imcode.server.user.UserDomainObject,
                 imcode.util.Utility,
                 imcode.server.document.UrlDocumentDomainObject,
                 imcode.server.document.textdocument.TextDocumentDomainObject,
                 imcode.server.document.DocumentDomainObject"%>
<%@page contentType="text/html"%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%>
<vel:velocity>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
        <script src="$contextPath/imcms/$language/scripts/imcms_admin.js" type="text/javascript"></script>
    </head>
    <body>
        #gui_outer_start()
        #gui_head( '<? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading ?>' )
        <form method="GET" action="AdminManager" >
            <input type="submit" class="imcmsFormBtn" name="Submit" value="<? global/back ?>">&nbsp;
            <input type="button" value="<? global/help ?>" class="imcmsFormBtn" onClick="openHelpW(18)">
        </form>
        #gui_mid()
        <table border="0" cellspacing="2" cellpadding="2">
            <tr>
                <th align="left"><? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading_type ?></th>
                <th align="left"><? web/imcms/lang/jsp/linkcheck/heading_adminlink ?></th>
                <th align="left"><? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading_references ?></th>
                <th align="left"><? web/imcms/lang/jsp/linkcheck/heading_status ?></th>
                <th align="left"><? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading_url ?></th>
                <th align="left" style="width: 5em;"><? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading_host_found ?></th>
                <th align="left" style="width: 5em;"><? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading_host_reachable ?></th>
                <th align="left" style="width: 5em;"><? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading_ok ?></th>
            </tr>
</vel:velocity>
            <% out.flush();
                UserDomainObject user = Utility.getLoggedOnUser( request ) ;
                Iterator linksIterator = (Iterator)request.getAttribute( LinkCheck.REQUEST_ATTRIBUTE__LINKS_ITERATOR ) ;
                while ( linksIterator.hasNext() ) {
                    LinkCheck.Link link = (LinkCheck.Link)linksIterator.next();
                    DocumentDomainObject document = link.getDocument() ;
                    %><tr>
                            <% if (link instanceof LinkCheck.UrlDocumentLink) {
                                LinkCheck.UrlDocumentLink urlDocumentLink = (LinkCheck.UrlDocumentLink)link ;
                                DocumentMapper.TextDocumentMenuIndexPair[] documentMenuPairsContainingUrlDocument = urlDocumentLink.getDocumentMenuPairsContainingUrlDocument();
                            %>  <td><? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/url_document ?></td>
                                <td>
                                    <a href="<%= request.getContextPath() %>/servlet/AdminDoc?meta_id=<%= document.getId() %>&<%= AdminDoc.PARAMETER__DISPATCH_FLAGS%>=<%= IMCConstants.DISPATCH_FLAG__EDIT_URL_DOCUMENT %>">
                                        <%= document.getId() %> - <%= document.getHeadline() %>
                                    </a>
                                </td>
                                <td>
                                    <% if (documentMenuPairsContainingUrlDocument.length > 0) { %><a href="<%= request.getContextPath() %>/servlet/LinkCheck?<%= LinkCheck.REQUEST_PARAMETER__REFERENCED_DOCUMENT_ID %>=<%= document.getId() %>"><% } %>
                                        <%= documentMenuPairsContainingUrlDocument.length %> <? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/parent_count_unit ?>
                                    <% if (documentMenuPairsContainingUrlDocument.length > 0) { %></a><% } %>
                                </td>
                            <% } else {
                                LinkCheck.TextDocumentElementLink textDocumentElementLink = (LinkCheck.TextDocumentElementLink)link ;
                            %>  <td>
                                    <% if (link instanceof LinkCheck.TextLink) { %>
                                        <? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/text ?>
                                    <% } else { %>
                                        <? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/image ?>
                                    <% } %>
                                </td>
                                <td>
                                    <% if (link instanceof LinkCheck.TextLink) { %>
                                        <a href="ChangeText?meta_id=<%= document.getId() %>&txt=<%=textDocumentElementLink.getIndex()%>">
                                    <% } else { %>
                                        <a href="ChangeImage?meta_id=<%= document.getId() %>&img=<%=textDocumentElementLink.getIndex()%>">
                                    <% } %>
                                        <%= document.getId() %> - <%= textDocumentElementLink.getIndex() %> - <%= document.getHeadline() %>
                                    </a>
                                </td>
                                <td>&nbsp;</td>
                            <% } %>
                        <td><%= Utility.getLinkedStatusIconTemplate( document, user ) %></td>
                        <td><a href="<%= link.getUrl() %>"><%= link.getUrl() %></a></td>
                        <td bgcolor="<% if (link.isHostFound()) { %>green<% } else { %>red<% } %>">&nbsp;</td>
                        <td bgcolor="<% if (link.isHostReachable()) { %>green<% } else { %>red<% } %>">&nbsp;</td>
                        <td bgcolor="<% if (link.isOk()) { %>green<% } else { %>red<% } %>">&nbsp;</td>
                    </tr><%
                    out.flush();
                }
            %>
<vel:velocity>
        </table>
        #gui_bottom()
        #gui_outer_end()
    </body>
</html>
</vel:velocity>
