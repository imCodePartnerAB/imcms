<%@ page import="imcode.server.document.DocumentDomainObject,
                 imcode.util.Utility"%>
<%@page contentType="text/html; charset=UTF-8" %>
<jsp:useBean id="expandableDatesBean" class="com.imcode.imcms.servlet.beans.AdminManagerExpandableDatesBean" scope="request"/>
<%
    String imagesPath  = request.getContextPath()+"/imcms/"+Utility.getLoggedOnUser( request ).getLanguageIso639_2()+"/images/admin/" ;
    DocumentDomainObject document = expandableDatesBean.getDocument();
%>
      <% if ( expandableDatesBean.isExpanded() ) { %>
            <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td rowspan="3"><img src="<%= imagesPath %>/1x1.gif" width="1" height="1"></td>
                    <td nowrap><? web/imcms/lang/jsp/admin/admin_manager_expandable_dates_column.jsp/modified_abbreviation ?>&nbsp;</td>
                    <td nowrap><%= null != document.getModifiedDatetime() ? Utility.formatHtmlDatetime( document.getModifiedDatetime() ) : "--"%></td>
                    <td rowspan="3"><img src="<%= imagesPath %>/1x1.gif" width="1" height="1"></td>
                </tr>
                <tr>
                    <td nowrap><? web/imcms/lang/jsp/admin/admin_manager_expandable_dates_column.jsp/created_abbreviation ?>&nbsp;</td>
                    <td nowrap><%= null != document.getCreatedDatetime() ? Utility.formatHtmlDatetime(document.getCreatedDatetime()) : "--" %></td>
                    <td nowrap><? web/imcms/lang/jsp/admin/admin_manager_expandable_dates_column.jsp/archived_abbreviation ?>&nbsp;</td>
                    <td nowrap><%= null != document.getArchivedDatetime() ? Utility.formatHtmlDatetime( document.getArchivedDatetime() ) : "--"%></td>
                </tr>
                <tr>
                    <td nowrap><? web/imcms/lang/jsp/admin/admin_manager_expandable_dates_column.jsp/publication_start_abbreviation ?>&nbsp;&nbsp;</td>
                    <td nowrap><%= null != document.getPublicationStartDatetime() ? Utility.formatHtmlDatetime( document.getPublicationStartDatetime() ) : "--"%></td>
                    <td nowrap><? web/imcms/lang/jsp/admin/admin_manager_expandable_dates_column.jsp/publication_end_abbreviation ?>&nbsp;&nbsp;</td>
                    <td nowrap><%= null != document.getPublicationEndDatetime() ? Utility.formatHtmlDatetime( document.getPublicationEndDatetime() ) : "--"%></td>
                </tr>
                <tr>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="8" height="1"></td>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="1" height="1"></td>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="90" height="1"></td>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="8" height="1"></td>
                </tr>
            </table>
        <%} else { %>
            <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="1" height="1"></td>
                    <td nowrap><? web/imcms/lang/jsp/admin/admin_manager_expandable_dates_column.jsp/modified_abbreviation ?>&nbsp;</td>
                    <td nowrap><%= null != document.getModifiedDatetime() ? Utility.formatHtmlDatetime( document.getModifiedDatetime() ) : "--"%></td>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="1" height="1"></td>
                </tr>
                <tr>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="8" height="1"></td>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="1" height="1"></td>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="90" height="1"></td>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="8" height="1"></td>
                </tr>
            </table>
        <% }%>
