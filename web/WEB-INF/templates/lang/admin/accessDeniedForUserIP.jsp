<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="vel" uri="imcmsvelocity" %>
<vel:velocity>
    <html>
    <head>
        <title><fmt:message key="accessDeniedForUserIP.title"/></title>

        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/imcms/css/imcms_admin.css.jsp">
    </head>
    <body bgcolor="#FFFFFF">
    #gui_outer_start()
    #gui_head( "<fmt:message key="accessDeniedForUserIP.title"/>" )
    <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td>
                <table border="0" cellpadding="0" cellspacing="0">
                    <form action="<%= request.getContextPath() %>/servlet/StartDoc">
                        <tr>
                            <td><input type="Submit" value="<fmt:message key="templates/Startpage"/>" class="imcmsFormBtn">
                            </td>
                        </tr>
                    </form>
                </table>
            </td>
        </tr>
    </table>
    #gui_mid()

    <table border="0" cellspacing="0" cellpadding="2" width="310">
        <tr>
            <td align="center" class="imcmsAdmText"><b><fmt:message key="accessDeniedForUserIP.message"/></b><br>
            </td>
        </tr>
    </table>
    #gui_bottom()
    #gui_outer_end()

    </body>
    </html>
</vel:velocity>
