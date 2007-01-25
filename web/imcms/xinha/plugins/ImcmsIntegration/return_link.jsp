<%@ page import="com.imcode.imcms.servlet.admin.InsertLink, org.apache.commons.lang.StringEscapeUtils"%><%
    String href = InsertLink.getLink(request);
%><html>
<body>
<script type="text/javascript">
    var param = new Object();
    param["href"] = '<%=StringEscapeUtils.escapeJavaScript(request.getContextPath()+"/"+href)%>';
    window.opener.Dialog._return(param) ;
    window.close();
</script>    
</body>
</html>