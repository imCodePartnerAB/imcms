<%@ page
        import="com.imcode.imcms.servlet.superadmin.AdminProfiles, imcode.server.document.Profile, org.apache.commons.text.StringEscapeUtils"
        contentType="text/html; charset=UTF-8" %>
<%
    Profile profile = (Profile) request.getAttribute("profile");
%>
<%@taglib prefix="vel" uri="imcmsvelocity" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<vel:velocity>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
    <script src="$contextPath/js/imcms/imcms_admin.js.jsp" type="text/javascript"></script>
</head>
<body>
    #gui_outer_start()
    #gui_head( "<fmt:message key="profile/headline"/>" )
    
    <form action="<%= request.getContextPath() %>/imcms/admin/profile/edit" method="POST">
        <table border="0" cellspacing="0" cellpadding="2" width="400" align="center">
            <tr>
                <td>
                    <input type="submit" name="<%= AdminProfiles.Parameter.BACK %>" value="<fmt:message key="global/back"/>" title="<fmt:message key="global/back"/>" class="imcmsFormBtn">
                    <input type="button" value="<fmt:message key="global/help"/>" title="<fmt:message key="global/help"/>" class="imcmsFormBtn" onClick="openHelpW('CategoryAdmin')">
                </td>
            </tr>
        </table>
        #gui_mid()
        <input type="hidden" name="<%= AdminProfiles.Parameter.PROFILE_ID %>" value="<%= profile.getId() %>"/>
        <table border="0" cellspacing="0" cellpadding="2" width="100%" align="center">
            <tr>
                <td><fmt:message key="profile/name"/></td>
                <td><input type="text" name="<%= AdminProfiles.Parameter.PROFILE_NAME %>" value="<%= StringEscapeUtils.escapeHtml4(profile.getName() )%>"/></td>
            </tr>  
            <tr>
                <td><fmt:message key="profile/document_name"/></td>
                <td><input type="text" name="<%= AdminProfiles.Parameter.PROFILE_DOCUMENT_NAME %>" value="<%= StringEscapeUtils.escapeHtml4(profile.getDocumentName() )%>"/></td>
            </tr>
        </table>
            <input type="submit" value="<fmt:message key="profile/save"/>" class="imcmsFormBtn"/>
    </form>
    <ui:imcms_gui_bottom/>
    #gui_outer_end()
</body>
</html>
</vel:velocity>