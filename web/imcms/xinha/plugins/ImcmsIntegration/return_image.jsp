<%@ page import="org.apache.commons.lang.StringEscapeUtils, imcode.server.document.textdocument.ImageDomainObject, com.imcode.imcms.servlet.admin.EditImage, org.apache.commons.lang.StringUtils, imcode.util.ImcmsImageUtils, imcode.util.Utility"%><%
    ImageDomainObject image = EditImage.getImage(request);
%>
<html>
<body>
<script type="text/javascript">
    var param = null;
    <% if (null != image && StringUtils.isNotBlank(image.getUrlPathRelativeToContextPath())) { %>
        <%
            String url = request.getContextPath() + image.getUrlPathRelativeToContextPath();
            int format = (image.getFormat() != null ? image.getFormat().getOrdinal() : 0);
            int cropX1 = -1;
            int cropY1 = -1;
            int cropX2 = -1;
            int cropY2 = -1;
            
            ImageDomainObject.CropRegion region = image.getCropRegion();
            if (region.isValid()) {
            	cropX1 = region.getCropX1();
                cropY1 = region.getCropY1();
                cropX2 = region.getCropX2();
                cropY2 = region.getCropY2();
            }
            
            String rel = String.format("%s;%d;%d;%d;%d;%d;%d;%d", Utility.encodeUrl(url), format, 
            	    image.getWidth(), image.getHeight(), cropX1, cropY1, cropX2, cropY2);
        %>
    
        param = new Object();
        param.src = '<%= StringEscapeUtils.escapeJavaScript(ImcmsImageUtils.getImageUrl(null, null, image, request)) %>';
        param.rel = '<%= StringEscapeUtils.escapeJavaScript(rel) %>';
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
