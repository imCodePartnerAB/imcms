<%@ page import="org.apache.commons.lang.StringEscapeUtils,
                 imcode.server.document.DocumentMapper,
                 imcode.util.Utility,
                 imcode.server.Imcms,
                 imcode.util.Html"%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%>
<% String IMG_SRC = Html.getLinkedStatusIconTemplate(document, Utility.getLoggedOnUser( request ), request ); %>

<vel:velocity>
<% if ( expand ) { %>
<!-- Expanded item -->
<tr valign="top" <%= i%2 == 1 ? "bgcolor=\"#ffffff\"" : "" %> >
    <td><img src="<%= IMG_PATH %>/1x1.gif" width="1" height="3"><br>
        <a href="$contextPath/servlet/GetDoc?meta_id=<%= document.getId() %>"><%= document.getId() %></a></td>
    <td align="center"><%= IMG_SRC %></td>
    <td><img src="<%= IMG_PATH %>/1x1.gif" width="1" height="3"><br>
        <a href="$contextPath/servlet/GetDoc?meta_id=<%= document.getId()%>"><%= StringEscapeUtils.escapeHtml(document.getHeadline()) %></a><br>
        <%= StringEscapeUtils.escapeHtml(document.getMenuText() ) %></td>
    <td align="right"><img src="<%= IMG_PATH %>/1x1.gif" width="1" height="3">
        <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td rowspan="3"><img src="<%= IMG_PATH %>/1x1.gif" width="1" height="1"></td>
            <td nowrap>Ändr:&nbsp;</td>
            <td nowrap><%= null != document.getModifiedDatetime() ? formatDatetime( document.getModifiedDatetime() ) : "--"%></td>
            <td rowspan="3"><img src="<%= IMG_PATH %>/1x1.gif" width="1" height="1"></td>
            <td>&nbsp;</td>
            <td align="right">&nbsp;</td>
        </tr>
        <tr>
            <td nowrap>Skap:&nbsp;</td>
            <td nowrap><%= null != document.getCreatedDatetime() ? formatDatetime(document.getCreatedDatetime()) : "--" %></td>
            <td nowrap>Arkiv:&nbsp;</td>
            <td nowrap><%= null != document.getArchivedDatetime() ? formatDatetime( document.getArchivedDatetime() ) : "--"%></td>
        </tr>
        <tr>
            <td nowrap>Publ:&nbsp;</td>
            <td nowrap><%= null != document.getPublicationStartDatetime() ? formatDatetime( document.getPublicationStartDatetime() ) : "--"%></td>
            <td nowrap>Avpubl:&nbsp;</td>
            <td nowrap><%= null != document.getPublicationEndDatetime() ? formatDatetime( document.getPublicationEndDatetime() ) : "--"%></td>
        </tr>
        <tr>
            <td><img src="<%= IMG_PATH %>/1x1.gif" width="8" height="1"></td>
            <td><img src="<%= IMG_PATH %>/1x1.gif" width="1" height="1"></td>
            <td><img src="<%= IMG_PATH %>/1x1.gif" width="90" height="1"></td>
            <td><img src="<%= IMG_PATH %>/1x1.gif" width="8" height="1"></td>
            <td><img src="<%= IMG_PATH %>/1x1.gif" width="1" height="1"></td>
            <td><img src="<%= IMG_PATH %>/1x1.gif" width="90" height="1"></td>
        </tr>
    <%} else { %>
    <!-- Not Expanded item -->
    <tr valign="top" <%= i%2 == 1 ? "bgcolor=\"#ffffff\"" : "" %> >
        <td><img src="<%= IMG_PATH %>/1x1.gif" width="1" height="3"><br>
            <a href="$contextPath/servlet/GetDoc?meta_id=<%= document.getId() %>"><%= document.getId() %></a></td>
        <td align="center"><%= IMG_SRC %></td>
        <td><img src="<%= IMG_PATH %>/1x1.gif" width="1" height="3"><br>
            <a href="$contextPath/servlet/GetDoc?meta_id=<%= document.getId()%>"><%= StringEscapeUtils.escapeHtml(document.getHeadline()) %></a></td>
        <td align="right"><img src="<%= IMG_PATH %>/1x1.gif" width="1" height="3">
        <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td><img src="<%= IMG_PATH %>/1x1.gif" width="1" height="1"></td>
            <td nowrap>Ändr:&nbsp;</td>
            <td nowrap><%= null != document.getModifiedDatetime() ? formatDatetime( document.getModifiedDatetime() ) : "--"%></td>
            <td><img src="<%= IMG_PATH %>/1x1.gif" width="1" height="1"></td>
            <td>&nbsp;</td>
            <td align="right">&nbsp;</td>
        </tr>
        <tr>
            <td><img src="<%= IMG_PATH %>/1x1.gif" width="8" height="1"></td>
            <td><img src="<%= IMG_PATH %>/1x1.gif" width="1" height="1"></td>
            <td><img src="<%= IMG_PATH %>/1x1.gif" width="90" height="1"></td>
            <td><img src="<%= IMG_PATH %>/1x1.gif" width="8" height="1"></td>
            <td><img src="<%= IMG_PATH %>/1x1.gif" width="40" height="1"></td>
            <td><img src="<%= IMG_PATH %>/1x1.gif" width="90" height="1"></td>
        </tr>

    <% }%>


        </table></td>
   </tr>
</vel:velocity>


