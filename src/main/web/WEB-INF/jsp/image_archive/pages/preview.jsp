<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<spring:message var="title" code="archive.title.previewImage" arguments="${imageId}"/>

<c:set var="javascript">
    <script type="text/javascript">
        initImagePreview();
    </script>
</c:set>
<c:set var="css">
    <style type="text/css">
        body { height: 100%; };
    </style>
</c:set>
<%@ include file="/WEB-INF/jsp/image_archive/includes/header.jsp" %>

<table id="imageTbl" cellpadding="0" cellspacing="0" style="width:100%;height:100%;">
    <tr>
        <td align="center" valign="middle">
            <c:url var="imageUrl" value="/web/archive/preview_img">
                <c:param name="id" value="${imageId}"/>
                <c:param name="tmp" value="${temporary}"/>
            </c:url>
            <img id="image" src="${fn:escapeXml(imageUrl)}"/>
        </td>
    </tr>
</table>

</body>
</html>
