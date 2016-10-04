<%@ page contentType="text/html; charset=UTF8" %>
<%@taglib prefix="vel" uri="imcmsvelocity"%>
<vel:velocity>
<html>
<head>
    <title><? install/htdocs/sv/jsp/internalerrorpage.jsp/1 ?></title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/imcms/css/imcms_admin.css.jsp">

</head>
<body bgcolor="#FFFFFF">
#gui_outer_start()
#gui_head( "<? install/htdocs/sv/jsp/internalerrorpage.jsp/2 ?>" )
<table border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td>
            <table border="0" cellpadding="0" cellspacing="0">
                <form action="<%= request.getContextPath() %>/servlet/StartDoc">
                    <tr>
                        <td><input type="Submit" value="<? templates/Startpage ?>" class="imcmsFormBtn"></td>
                    </tr>
                </form>
            </table>
        </td>
        <td>&nbsp;</td>
        <td>
            <table border="0" cellpadding="0" cellspacing="0">
                <form action="<%= request.getContextPath() %>/servlet/BackDoc">
                    <tr>
                        <td><input type="Submit" value="<? templates/Back ?>" class="imcmsFormBtn"></td>
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
            <p><? install/htdocs/sv/jsp/internalerrorpage.jsp/3 ?> </p>
            <p><? install/htdocs/sv/jsp/internalerrorpage.jsp/4 ?> </p>
            <p><? install/htdocs/sv/jsp/internalerrorpage.jsp/5 ?> </p>
        </td>
    </tr>
</table>
<h2><? install/htdocs/sv/jsp/internalerrorpage.jsp/6 ?></h2>

<p><? install/htdocs/sv/jsp/internalerrorpage.jsp/8 ?></p>
<p><%= request.getAttribute("message") %></p>

<p><? install/htdocs/sv/jsp/internalerrorpage.jsp/7 ?></p>
<p><%= request.getAttribute("cause") %></p>
<% if (request.getAttribute("placement") != null) {%>
    <p><? install/htdocs/sv/jsp/internalerrorpage.jsp/9 ?></p>
    <div style="padding-left: 20px;">
        <%
            String placement = (String) request.getAttribute("placement");
            String[] splitPlacement = placement.split(";");
            for (String place : splitPlacement) {
                if (!place.isEmpty()) {
                    out.print("<p>");
                    out.print(place);
                    out.print("</p>");
                    out.println();
                }
            }
        %>
    </div>
<% } %>
#gui_bottom()
#gui_outer_end()
</body>
</html>
</vel:velocity>
