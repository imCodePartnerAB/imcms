<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<html>
<head>


    <title><? templates/sv/docinfo.html/1 ?></title>

    <link rel="stylesheet" type="text/css" href="${contextPath}/dist/imcms_admin.css">


</head>
<body bgcolor="#FFFFFF">

<ui:imcms_gui_outer_start/>
<c:set var="heading">
    <fmt:message key="global/imcms_administration"/>
</c:set>
<ui:imcms_gui_head heading="${heading}"/>

<table border="0" cellspacing="0" cellpadding="0">
    <form action="BackDoc">
        <tr>
            <td><input type="submit" class="imcmsFormBtn" value="<? templates/sv/docinfo.html/2001 ?>"></td>
        </tr>
    </form>
</table>
<ui:imcms_gui_mid/>

${doc_type_description}
<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>


<div align="center">${adminMode}</div>

</body>
</html>
