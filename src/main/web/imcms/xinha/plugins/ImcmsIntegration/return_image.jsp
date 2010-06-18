<%@ page import="org.apache.commons.lang.StringEscapeUtils, 
        imcode.server.document.textdocument.ImageDomainObject,
        com.imcode.imcms.servlet.admin.EditImage,
        org.apache.commons.lang.StringUtils,
        imcode.util.ImcmsImageUtils,
        com.imcode.util.ImageSize"%><%
    ImageDomainObject image = EditImage.getImage(request);
%><html>
<body>
<script type="text/javascript">
    var param = null;
    <% if (null != image && StringUtils.isNotBlank(image.getUrlPathRelativeToContextPath())) { %>
        <% ImageSize displaySize = image.getDisplayImageSize(); %>
        param = new Object();
        param.src = '<%= StringEscapeUtils.escapeJavaScript(ImcmsImageUtils.getImageUrl(image, request.getContextPath())) %>';
        param["alt"] = '<%= StringEscapeUtils.escapeJavaScript(image.getAlternateText())%>';
        <% if (StringUtils.isNotBlank(image.getAlign())) { %>
            param["align"] = '<%= StringEscapeUtils.escapeJavaScript(image.getAlign())%>';
        <% } %>
        param["border"] = '<%= image.getBorder() %>';
        param["horiz"] = '<%= image.getHorizontalSpace() %>';
        param["vert"] = '<%= image.getVerticalSpace() %>';
        param["width"] = '<%= displaySize.getWidth() %>';
        param["height"] = '<%= displaySize.getHeight() %>';
    <% } %>
    window.opener.Dialog._return(param) ;
    window.close();
</script>    
</body>
</html>