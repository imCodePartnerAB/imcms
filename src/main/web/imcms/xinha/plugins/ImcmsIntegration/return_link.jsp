<%@ page import="com.imcode.imcms.servlet.admin.EditLink, org.apache.commons.lang.StringEscapeUtils"%><%
    EditLink.Link link = EditLink.getLink(request);
%><html>
<body>
<script type="text/javascript">
    var param = null;
    <% if (null != link) { %>
    param = new Object();
    param["href"] = '<%=StringEscapeUtils.escapeJavaScript(link.getHref())%>';
    param["title"] = '<%=StringEscapeUtils.escapeJavaScript(link.getTitle())%>';
    param["target"] = '<%=StringEscapeUtils.escapeJavaScript(link.getTarget())%>';
    <% } %>
    window.opener.Dialog._return(param) ;
    window.close();
</script>    
</body>
</html>
