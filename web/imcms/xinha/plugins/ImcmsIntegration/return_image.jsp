<%@ page import="org.apache.commons.lang.StringEscapeUtils, imcode.server.document.textdocument.ImageDomainObject, com.imcode.imcms.servlet.admin.EditImage, org.apache.commons.lang.StringUtils, imcode.util.ImcmsImageUtils, imcode.util.Utility"%><%
    ImageDomainObject image = EditImage.getImage(request);
%>
<html>
<body>
<script type="text/javascript">
    var param = null;
    <% if (null != image && StringUtils.isNotBlank(image.getUrlPathRelativeToContextPath())) { %>
        param = new Object();
        param.src = '<%= StringEscapeUtils.escapeJavaScript(ImcmsImageUtils.getImageUrl(null, image, request.getContextPath(), true)) %>';
        param["alt"] = '<%= StringEscapeUtils.escapeJavaScript(image.getAlternateText())%>';
        <% if (StringUtils.isNotBlank(image.getAlign())) { %>
            param["align"] = '<%= StringEscapeUtils.escapeJavaScript(image.getAlign())%>';
        <% } %>
        param["border"] = '<%= image.getBorder() %>';
        param["horiz"] = '<%= image.getHorizontalSpace() %>';
        param["vert"] = '<%= image.getVerticalSpace() %>';
        <% if (0 != image.getWidth()) { %>
            param["width"] = '<%= image.getWidth() %>';
        <% } %>
        <% if (0 != image.getHeight()) { %>
            param["height"] = '<%= image.getHeight() %>';
        <% } %>
        param["name"] = '<%= StringEscapeUtils.escapeJavaScript(image.getName()) %>';
    <% } %>
    window.opener.Dialog._return(param) ;
    window.close();
</script>    
</body>
</html>
