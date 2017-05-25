<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/1"/></title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/imcms/css/imcms_admin.css.jsp">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
</head>
<body bgcolor="#FFFFFF">
<!-- gui_outer_start -->
<table border="0" cellspacing="0" cellpadding="0" class="imcmsAdmTable" align="center">
    <tr>
        <td class="imcmsAdmTable">
            <!-- /gui_outer_start -->
            <!-- gui_head -->
            <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td class="imcmsAdmBgHead" colspan="6"><img
                            src="<%= request.getContextPath() %>/imcms/eng/images/admin/1x1.gif"
                            width="1" height="20" alt=""/></td>
                </tr>
                <tr class="imcmsAdmBgHead">
                    <td colspan="2"><img src="<%= request.getContextPath() %>/imcms/eng/images/admin/1x1.gif" width="1"
                                         height="1" alt=""/></td>
                    <td nowrap="nowrap"><span class="imcmsAdmHeadingTop"><fmt:message
                            key="install/htdocs/sv/jsp/internalerrorpage.jsp/2"/></span></td>
                    <td align="right"><a href="http://www.imcms.net/" target="_blank" title="www.imcms.net"><img
                            src="<%= request.getContextPath() %>/imcms/eng/images/admin/logo_imcms_admin.gif"
                            width="100" height="20" alt="www.imcms.net" border="0"/></a></td>
                    <td colspan="2"><img src="<%= request.getContextPath() %>/imcms/eng/images/admin/1x1.gif" width="1"
                                         height="1" alt=""/></td>
                </tr>
                <tr>
                    <td colspan="6" class="imcmsAdmBgHead"><img
                            src="<%= request.getContextPath() %>/imcms/eng/images/admin/1x1.gif"
                            width="1" height="20" alt=""/></td>
                </tr>
                <tr class="imcmsAdmBgHead">
                    <td colspan="2"><img src="<%= request.getContextPath() %>/imcms/eng/images/admin/1x1.gif" width="1"
                                         height="1" alt=""/></td>
                    <td colspan="2">
                        <!-- /gui_head -->
                        <table border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td>
                                    <table border="0" cellpadding="0" cellspacing="0">
                                        <form action="<%= request.getContextPath() %>/servlet/StartDoc">
                                            <tr>
                                                <td><input type="Submit" class="imcmsFormBtn"
                                                           value="<fmt:message key="templates/Startpage"/>"></td>
                                            </tr>
                                        </form>
                                    </table>
                                </td>
                                <td>&nbsp;</td>
                                <td>
                                    <table border="0" cellpadding="0" cellspacing="0">
                                        <form action="<%= request.getContextPath() %>/servlet/BackDoc">
                                            <tr>
                                                <td><input type="Submit" value="<fmt:message key="templates/Back"/>"
                                                           class="imcmsFormBtn"></td>
                                            </tr>
                                        </form>
                                    </table>
                                </td>
                            </tr>
                        </table>
                        <!-- gui_mid -->
                    </td>
                    <td colspan="2"><img src="<%= request.getContextPath() %>/imcms/eng/images/admin/1x1.gif" width="1"
                                         height="1" alt=""/></td>
                </tr>
                <tr>
                    <td class="imcmsAdmBgHead" colspan="6"><img
                            src="<%= request.getContextPath() %>/imcms/eng/images/admin/1x1.gif"
                            width="1" height="20" alt=""/></td>
                </tr>
                <tr>
                    <td height="10" class="imcmsAdmBorder"><img
                            src="<%= request.getContextPath() %>/imcms/eng/images/admin/1x1.gif"
                            width="1" height="1" alt=""/></td>
                    <td class="imcmsAdmBgCont" colspan="4"><img
                            src="<%= request.getContextPath() %>/imcms/eng/images/admin/1x1.gif"
                            width="1" height="1" alt=""/></td>
                    <td class="imcmsAdmBorder"><img src="<%= request.getContextPath() %>/imcms/eng/images/admin/1x1.gif"
                                                    width="1" height="1" alt=""/></td>
                </tr>
                <tr class="imcmsAdmBgCont">
                    <td class="imcmsAdmBorder"><img src="<%= request.getContextPath() %>/imcms/eng/images/admin/1x1.gif"
                                                    width="1" height="1" alt=""/></td>
                    <td><img src="<%= request.getContextPath() %>/imcms/eng/images/admin/1x1.gif" width="1" height="1"
                             alt=""/></td>
                    <td colspan="2">
                        <!-- /gui_mid -->
                        <table border="0" cellspacing="0" cellpadding="2">
                            <tr>
                                <td align="left" class="imcmsAdmText">
                                    <p><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/3"/></p>
                                    <p><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/4"/></p>
                                </td>
                            </tr>
                        </table>
                        <h2><fmt:message
                                key="install/htdocs/sv/jsp/internalerrorpage.jsp/6"/><%= request.getAttribute("error-id") %>
                        </h2>
                        <button class="imcmsFormBtn"
                                onclick="$('#detail-info').is(':visible') ? $('#detail-info').hide() : $('#detail-info').show();">
                            <fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/10"/>
                        </button>
                        <div id="detail-info" style="display: none;">
                            <br/>
                            <strong><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/11"/></strong>
                            <pre><%= request.getAttribute("error-url") %></pre>
                            <strong><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/12"/></strong>
                            <pre><%= request.getAttribute("message") %></pre>
                            <strong><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/13"/></strong>
                            <pre><%= request.getAttribute("cause") %></pre>
                            <strong><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/14"/></strong>
                            <pre><%= request.getAttribute("stack-trace") %></pre>
                        </div>
                        <!-- gui_bottom -->
                    </td>
                    <td><img src="<%= request.getContextPath() %>/imcms/eng/images/admin/1x1.gif" width="1" height="1"
                             alt=""/></td>
                    <td class="imcmsAdmBorder"><img src="<%= request.getContextPath() %>/imcms/eng/images/admin/1x1.gif"
                                                    width="1" height="1" alt=""/></td>
                </tr>
                <tr>
                    <td height="10" class="imcmsAdmBorder"><img
                            src="<%= request.getContextPath() %>/imcms/eng/images/admin/1x1.gif"
                            width="1" height="1" alt=""/></td>
                    <td colspan="4" class="imcmsAdmBgCont"><img
                            src="<%= request.getContextPath() %>/imcms/eng/images/admin/1x1.gif"
                            width="1" height="1" alt=""/></td>
                    <td class="imcmsAdmBorder"><img src="<%= request.getContextPath() %>/imcms/eng/images/admin/1x1.gif"
                                                    width="1" height="1" alt=""/></td>
                </tr>
                <tr class="imcmsAdmBgCont">
                    <td><img src="<%= request.getContextPath() %>/imcms/eng/images/admin/1x1.gif" width="1" height="1"
                             alt=""/></td>
                    <td><img src="<%= request.getContextPath() %>/imcms/eng/images/admin/1x1.gif" width="24" height="1"
                             alt=""/></td>
                    <td colspan="2"><img src="<%= request.getContextPath() %>/imcms/eng/images/admin/1x1.gif" width="1"
                                         height="1" alt=""/></td>
                    <td><img src="<%= request.getContextPath() %>/imcms/eng/images/admin/1x1.gif" width="24" height="1"
                             alt=""/></td>
                    <td><img src="<%= request.getContextPath() %>/imcms/eng/images/admin/1x1.gif" width="1" height="1"
                             alt=""/></td>
                </tr>
            </table>
            <!-- /gui_bottom -->
            <!-- gui_outer_end -->
        <td align="right" valign="top"
            style="background: transparent url(<%= request.getContextPath() %>/imcms/eng/images/admin/imcms_admin_shadow_right.gif) top left repeat-y;">
            <img src="<%= request.getContextPath() %>/imcms/eng/images/admin/imcms_admin_shadow_right_top.gif"
                 width="12" height="12" border="0" alt=""/></td>
    </tr>
    <tr>
        <td colspan="2">
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <tr>
                    <td style="background: transparent url(<%= request.getContextPath() %>/imcms/eng/images/admin/imcms_admin_shadow_bottom.gif) top left repeat-x;">
                        <img src="<%= request.getContextPath() %>/imcms/eng/images/admin/imcms_admin_shadow_bottom_left.gif"
                             width="12" height="12" border="0" alt=""/></td>
                    <td style="background: transparent url(<%= request.getContextPath() %>/imcms/eng/images/admin/imcms_admin_shadow_bottom.gif) top left repeat-x;"
                        align="right">
                        <img src="<%= request.getContextPath() %>/imcms/eng/images/admin/imcms_admin_shadow_bottom_right.gif"
                             width="12" height="12" border="0" alt=""/></td>
                </tr>
            </table>
        </td>
    </tr>
</table>
<!-- /gui_outer_end -->
</body>
</html>
