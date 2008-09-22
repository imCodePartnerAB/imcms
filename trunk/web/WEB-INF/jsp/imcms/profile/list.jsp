<%@ page import="com.imcode.imcms.servlet.superadmin.AdminProfiles, imcode.server.document.Profile, org.apache.commons.lang.StringEscapeUtils, java.util.List" contentType="text/html; charset=UTF-8"%><%
    List<Profile> profiles = (List<Profile>) request.getAttribute("profiles");
    %><%@taglib prefix="vel" uri="imcmsvelocity"%><%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><vel:velocity>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
    <script src="$contextPath/imcms/$language/scripts/imcms_admin.js.jsp" type="text/javascript"></script>
</head>
<body>
    #gui_outer_start()
    #gui_head( "<fmt:message key="profile/headline"/>" )
    
    <form action="<%= request.getContextPath() %>/imcms/admin/profile/list" method="POST">
        <table border="0" cellspacing="0" cellpadding="2" width="400" align="center">
            <tr>
                <td>
                    <input type="submit" name="<%= AdminProfiles.Parameter.BACK %>" value="<fmt:message key="global/back"/>" title="<fmt:message key="global/back"/>" class="imcmsFormBtn">
                    <input type="button" value="<fmt:message key="global/help"/>" title="<fmt:message key="global/help"/>" class="imcmsFormBtn" onClick="openHelpW('CategoryAdmin')">
                </td>
            </tr>
        </table>
        #gui_mid()
        #gui_heading( '<fmt:message key="profile/headline"/>' )
        <table border="0" cellspacing="0" cellpadding="2" width="100%" align="center">
            <%
            for ( Profile profile : profiles) {
                %><tr>
                    <td><%= StringEscapeUtils.escapeHtml(profile.getName()) %></td>
                    <td><%= StringEscapeUtils.escapeHtml(profile.getDocumentName()) %></td>
                    <td align="right">
                        <input type="submit" class="imcmsFormBtnSmall" name="<%= AdminProfiles.Parameter.EDIT_PREFIX+profile.getId().toString() %>" value="<fmt:message key="profile/edit"/>"/>
                        <input type="submit" class="imcmsFormBtnSmall" name="<%= AdminProfiles.Parameter.DELETE_PREFIX+profile.getId( ).toString() %>" value="<fmt:message key="profile/delete"/>"/>
                    </td>
                </tr><%
            }
            %>
        </table>
        #gui_hr( 'blue' )
        <input type="submit" name="<%= AdminProfiles.Parameter.NEW_PROFILE %>" value="<fmt:message key="profile/create"/>" class="imcmsFormBtn"/>
    </form>
    #gui_bottom()
    #gui_outer_end()
</body>
</html>
</vel:velocity>