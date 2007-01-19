<%@ page import="org.apache.commons.lang.StringEscapeUtils, imcode.server.document.textdocument.ImageDomainObject, com.imcode.imcms.servlet.admin.InsertImage"%><%
    ImageDomainObject image = InsertImage.getImage(request);
%><html>
<body>
<script type="text/javascript">
    var param = new Object();
    param["f_url"] = '<%=StringEscapeUtils.escapeJavaScript(request.getContextPath()+image.getUrlPathRelativeToContextPath())%>';
    param["f_alt"] = '<%= StringEscapeUtils.escapeJavaScript(image.getAlternateText())%>';
    param["f_align"] = '<%= StringEscapeUtils.escapeJavaScript(image.getAlign())%>';
    param["f_border"] = '<%= image.getBorder() %>';
    param["f_horiz"] = '<%= image.getHorizontalSpace() %>';
    param["f_vert"] = '<%= image.getVerticalSpace() %>';
    window.opener.Dialog._return(param) ;
    window.close();
</script>    
</body>
</html>