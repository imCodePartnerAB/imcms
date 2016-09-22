<%@ taglib prefix="imcms" uri="imcms" %>
<%@ page

        contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"

%>
<imcms:variables/>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title>${document.headline} - Powered by imCMS from imCode Partner AB</title>
    <meta charset="utf-8"/>
    <jsp:include page="/WEB-INF/jsp/imcms/imcms_admin_headtag.jsp">
        <jsp:param name="flags" value="56565"/>
    </jsp:include>
</head>
<body>
<imcms:admin/>
</body>
</html>