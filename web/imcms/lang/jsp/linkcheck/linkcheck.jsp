<%@ page import="com.imcode.imcms.servlet.superadmin.LinkCheck,
                 java.util.Iterator,
                 com.imcode.imcms.servlet.admin.AdminDoc,
                 imcode.server.ImcmsConstants,
                 imcode.server.document.DocumentMapper,
                 imcode.server.Imcms,
                 imcode.server.user.UserDomainObject,
                 imcode.util.Utility,
                 imcode.server.document.UrlDocumentDomainObject,
                 imcode.server.document.textdocument.TextDocumentDomainObject,
                 imcode.server.document.DocumentDomainObject,
                 com.imcode.imcms.servlet.superadmin.DocumentReferences,
                 imcode.util.Html"%>
<%@page contentType="text/html"%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%>
<vel:velocity>
    #gui_start_of_page( "<? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading ?>" "AdminManager" "" "18" "" )
    <table border="0" cellspacing="2" cellpadding="2">
        <tr align="left">
            <td><b><? web/imcms/lang/jsp/heading_type ?></b></td>
            <td><b><? web/imcms/lang/jsp/heading_adminlink ?></b></td>
            <td><b><? web/imcms/lang/jsp/heading_references ?></b></td>
            <td><b><? web/imcms/lang/jsp/heading_status ?></b></td>
            <td><b><? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading_url ?></b></td>
            <td align="center" style="width: 5em;"><b><? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading_host_found ?></b></td>
            <td align="center" style="width: 5em;"><b><? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading_host_reachable ?></b></td>
            <td align="center" style="width: 5em;"><b><? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading_ok ?></b></td>
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
                                <a href="<%= request.getContextPath() %>/servlet/AdminDoc?meta_id=<%= document.getId() %>&<%= AdminDoc.PARAMETER__DISPATCH_FLAGS%>=<%= ImcmsConstants.DISPATCH_FLAG__EDIT_URL_DOCUMENT %>">
                                    <%= document.getId() %> - <%= document.getHeadline() %>
                                </a>
                            </td>
                            <td nowrap>
                                <% if (documentMenuPairsContainingUrlDocument.length > 0) { %><a href="<%= request.getContextPath() %>/servlet/DocumentReferences?<%= DocumentReferences.REQUEST_PARAMETER__REFERENCED_DOCUMENT_ID %>=<%= document.getId() %>&<%= DocumentReferences.REQUEST_PARAMETER__RETURNURL %>=LinkCheck"><% } %>
                                    <%= documentMenuPairsContainingUrlDocument.length %> <? web/imcms/lang/jsp/parent_count_unit ?>
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
                    <td><%= Html.getLinkedStatusIconTemplate( document, user ) %></td>
                    <td><a href="<%= link.getUrl() %>"><%= link.getUrl() %></a></td>
										<vel:velocity>
                    <td align="center"><img
										src="$contextPath/imcms/$language/images/admin/btn_checked_<%= (link.isHostFound()) ? "1" : "0" %>.gif"></td>
                    <td align="center"><img
										src="$contextPath/imcms/$language/images/admin/btn_checked_<%= (link.isHostReachable()) ? "1" : "0" %>.gif"></td>
                    <td align="center"><img
										src="$contextPath/imcms/$language/images/admin/btn_checked_<%= (link.isOk()) ? "1" : "0" %>.gif"></td>
										</vel:velocity>
                </tr><%
                out.flush();
            }
        %>
<vel:velocity>
    </table>

#gui_end_of_page()
</vel:velocity>
