<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="imcms" uri="imcms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Simple jsp page</title>
    <style type="text/css">
        .imageArchive {
            float: right;
        }
    </style>
</head>
<body>
<div><imcms:admin/></div>
<div class="imageArchiveHead">
    <imcms:image no="1" label="<br/>Logo<br/>" style="logo"/>
    <imcms:text no="1" pre="<span>" post="</span>"/>
</div>

<div style="float:left;width:20%;">
    <imcms:menu no='1' label='<br/><br/>Meny (punktlista)'>
        <ul>
            <imcms:menuloop>
                <imcms:menuitem>
                    <li style="padding-bottom:5px; color:green;"><imcms:menuitemlink><c:out
                            value="${menuitem.document.headline}"/></imcms:menuitemlink></li>
                </imcms:menuitem>
                <imcms:menuitem>
                    <imcms:menuitemhide>
                        <li style="padding-bottom:5px; color:red;"><imcms:menuitemlink><c:out
                                value="${menuitem.document.headline}"/></imcms:menuitemlink></li>
                    </imcms:menuitemhide>
                </imcms:menuitem>
            </imcms:menuloop>
        </ul>
    </imcms:menu>
</div>
<imcms:imageArchive styleClass="imageArchive"><link href="${contextPath}/css/tag_image_archive.css.jsp" rel="stylesheet" type="text/css" /></imcms:imageArchive>
</body>
</html>