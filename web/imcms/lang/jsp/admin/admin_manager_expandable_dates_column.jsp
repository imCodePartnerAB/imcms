<%@ page import="imcode.util.Utility,
                 imcode.server.document.DocumentDomainObject,
                 java.util.Date,
                 java.text.DateFormat,
                 java.text.SimpleDateFormat,
                 imcode.util.DateConstants"%>
<%@page contentType="text/html"%>
<jsp:useBean id="listItemBean" class="com.imcode.imcms.servlet.superadmin.AdminManagerSubReportListItemBean" scope="request"/>
<%
    String imagesPath  = request.getContextPath()+"/imcms/"+Utility.getLoggedOnUser( request ).getLanguageIso639_2()+"/images/admin/" ;
    DocumentDomainObject document = listItemBean.getDocument();
%>
      <% if ( listItemBean.isExpanded() ) { %>
            <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td rowspan="3"><img src="<%= imagesPath %>/1x1.gif" width="1" height="1"></td>
                    <td nowrap>Ändr:&nbsp;</td>
                    <td nowrap><%= null != document.getModifiedDatetime() ? Utility.formatHtmlDatetime( document.getModifiedDatetime() ) : "--"%></td>
                    <td rowspan="3"><img src="<%= imagesPath %>/1x1.gif" width="1" height="1"></td>
                    <td>&nbsp;</td>
                    <td align="right">&nbsp;</td>
                </tr>
                <tr>
                    <td nowrap>Skap:&nbsp;</td>
                    <td nowrap><%= null != document.getCreatedDatetime() ? Utility.formatHtmlDatetime(document.getCreatedDatetime()) : "--" %></td>
                    <td nowrap>Arkiv:&nbsp;</td>
                    <td nowrap><%= null != document.getArchivedDatetime() ? Utility.formatHtmlDatetime( document.getArchivedDatetime() ) : "--"%></td>
                </tr>
                <tr>
                    <td nowrap>Publ:&nbsp;</td>
                    <td nowrap><%= null != document.getPublicationStartDatetime() ? Utility.formatHtmlDatetime( document.getPublicationStartDatetime() ) : "--"%></td>
                    <td nowrap>Avpubl:&nbsp;</td>
                    <td nowrap><%= null != document.getPublicationEndDatetime() ? Utility.formatHtmlDatetime( document.getPublicationEndDatetime() ) : "--"%></td>
                </tr>
                <tr>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="8" height="1"></td>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="1" height="1"></td>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="90" height="1"></td>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="8" height="1"></td>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="1" height="1"></td>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="90" height="1"></td>
                </tr>
            </table>
        <%} else { %>
            <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="1" height="1"></td>
                    <td nowrap>Ändr:&nbsp;</td>
                    <td nowrap><%= null != document.getModifiedDatetime() ? Utility.formatHtmlDatetime( document.getModifiedDatetime() ) : "--"%></td>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="1" height="1"></td>
                    <td>&nbsp;</td>
                    <td align="right">&nbsp;</td>
                </tr>
                <tr>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="8" height="1"></td>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="1" height="1"></td>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="90" height="1"></td>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="8" height="1"></td>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="40" height="1"></td>
                    <td><img src="<%= imagesPath %>/1x1.gif" width="90" height="1"></td>
                </tr>
            </table>
        <% }%>
