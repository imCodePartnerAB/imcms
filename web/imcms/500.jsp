<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="vel" uri="imcmsvelocity"%>
<vel:velocity>
<html>
<head>
    <title><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/1"/></title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/imcms/css/imcms_admin.css.jsp">

</head>
<body bgcolor="#FFFFFF">
#gui_outer_start()
#gui_head( "<fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/2"/>" )
<table border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td>
            <table border="0" cellpadding="0" cellspacing="0">
                <form action="<%= request.getContextPath() %>/servlet/StartDoc">
                    <tr>
                        <td><input type="Submit" value="<fmt:message key="templates/Startpage"/>" class="imcmsFormBtn"></td>
                    </tr>
                </form>
            </table>
        </td>
        <td>&nbsp;</td>
        <td>
            <table border="0" cellpadding="0" cellspacing="0">
                <form action="<%= request.getContextPath() %>/servlet/BackDoc">
                    <tr>
                        <td><input type="Submit" value="<fmt:message key="templates/Back"/>" class="imcmsFormBtn"></td>
                    </tr>
                </form>
            </table>
        </td>
    </tr>
</table>
#gui_mid()
<table border="0" cellspacing="0" cellpadding="2">
    <tr>
        <td align="left" class="imcmsAdmText">
            <p><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/3"/></p>
            <p><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/4"/></p>
        </td>
    </tr>
</table>
<h2><fmt:message key="install/htdocs/sv/jsp/internalerrorpage.jsp/6"/><%= request.getAttribute("error-id") %></h2>
#gui_bottom()
#gui_outer_end()
</body>
</html>
</vel:velocity>
