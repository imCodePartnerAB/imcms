<%@ page import="com.imcode.imcms.servlet.superadmin.LinkCheck,
                 java.util.Iterator,
                 com.imcode.imcms.servlet.admin.AdminDoc,
                 imcode.server.IMCConstants"%>
<%@page contentType="text/html"%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%>
<vel:velocity>
<html>
    <head>
        <link rel="stylesheet" href="$contextPath/imcms/css/imcms_admin.css" type="text/css">
    </head>
    <body>
        #gui_outer_start()
        #gui_head( '<? web/imcms/lang/jsp/linkcheck.jsp/heading ?>' )
        <form method="GET" action="AdminManager" >
            <input type="submit" class="imcmsFormBtn" name="Submit" value="<? global/back ?>">&nbsp;
            <input type="button" value="<? global/help ?>" class="imcmsFormBtn" onClick="openHelpW(18)">
        </form>
        #gui_mid()
        <table border="0" cellspacing="2" cellpadding="2">
            <tr>
                <th><? web/imcms/lang/jsp/linkcheck.jsp/heading_type ?></th>
                <th><? web/imcms/lang/jsp/linkcheck.jsp/heading_adminlink ?></th>
                <th><? web/imcms/lang/jsp/linkcheck.jsp/heading_url ?></th>
                <th width="5%"><? web/imcms/lang/jsp/linkcheck.jsp/heading_valid_url ?></th>
                <th width="5%"><? web/imcms/lang/jsp/linkcheck.jsp/heading_host_found ?></th>
                <th width="5%"><? web/imcms/lang/jsp/linkcheck.jsp/heading_host_reachable ?></th>
                <th width="5%"><? web/imcms/lang/jsp/linkcheck.jsp/heading_ok ?></th>
            </tr>
</vel:velocity>
            <% out.flush();
                Iterator linksIterator = (Iterator)request.getAttribute( LinkCheck.REQUEST_ATTRIBUTE__LINKS_ITERATOR ) ;
                while ( linksIterator.hasNext() ) {
                    LinkCheck.Link link = (LinkCheck.Link)linksIterator.next();
                    %><tr>
                        <td>
                            <% if (link instanceof LinkCheck.UrlDocumentLink) {
                                LinkCheck.UrlDocumentLink urlDocumentLink = (LinkCheck.UrlDocumentLink)link ;
                                %><? web/imcms/lang/jsp/linkcheck.jsp/url_document ?></td>
                                <td><a href="<%= request.getContextPath() %>/servlet/AdminDoc?meta_id=<%= urlDocumentLink.getUrlDocument().getId() %>&<%= AdminDoc.PARAMETER__DISPATCH_FLAGS%>=<%= IMCConstants.DISPATCH_FLAG__EDIT_URL_DOCUMENT %>">
                                    <%= urlDocumentLink.getUrlDocument().getId() %> - <%= urlDocumentLink.getUrlDocument().getHeadline() %>
                                </a>
                            <% } else if (link instanceof LinkCheck.TextLink) {
                                LinkCheck.TextLink textLink = (LinkCheck.TextLink)link ;
                                %><? web/imcms/lang/jsp/linkcheck.jsp/text ?></td>
                                <td><a href="<%= request.getContextPath() %>/servlet/ChangeText?meta_id=<%= textLink.getTextDocument().getId() %>&txt=<%=textLink.getTextIndex()%>">
                                    <%= textLink.getTextDocument().getId() %> - <%= textLink.getTextIndex() %> - <%= textLink.getTextDocument().getHeadline() %>
                                </a>
                            <% } else if (link instanceof LinkCheck.ImageLink) {
                                LinkCheck.ImageLink imageLink = (LinkCheck.ImageLink)link ;
                                %><? web/imcms/lang/jsp/linkcheck.jsp/image ?></td>
                                <td><a href="<%= request.getContextPath() %>/servlet/ChangeImage?meta_id=<%= imageLink.getTextDocument().getId() %>&img=<%=imageLink.getImageIndex()%>">
                                    <%= imageLink.getTextDocument().getId() %> - <%= imageLink.getImageIndex() %> - <%= imageLink.getTextDocument().getHeadline() %>
                                </a>
                            <% } %>
                        </td>
                        <td><a href="<%= link.getUrl() %>"><%= link.getUrl() %></a></td>
                        <td bgcolor="<% if (link.isValidUrl()) { %>green<% } else { %>red<% } %>">&nbsp;</td>
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
