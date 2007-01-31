<%@ page import="org.apache.commons.lang.StringEscapeUtils, imcode.server.document.textdocument.ImageDomainObject, com.imcode.imcms.servlet.admin.EditImage"%><%
    ImageDomainObject image = EditImage.getImage(request);
%><html>
<body>
<script type="text/javascript">
    var param = new Object();
    param["src"] = '<%=StringEscapeUtils.escapeJavaScript(request.getContextPath()+image.getUrlPathRelativeToContextPath())%>';
    param["alt"] = '<%= StringEscapeUtils.escapeJavaScript(image.getAlternateText())%>';
    param["align"] = '<%= StringEscapeUtils.escapeJavaScript(image.getAlign())%>';
    param["border"] = '<%= image.getBorder() %>';
    param["horiz"] = '<%= image.getHorizontalSpace() %>';
    param["vert"] = '<%= image.getVerticalSpace() %>';
    param["width"] = '<%= image.getWidth() %>';
    param["height"] = '<%= image.getHeight() %>';
    param["name"] = '<%= StringEscapeUtils.escapeJavaScript(image.getName()) %>';
    window.opener.Dialog._return(param) ;
    window.close();
</script>    
</body>
</html>