<%@ page import="com.imcode.imcms.web.admin.BuildInEditorSupport" %>
<%@ page import="imcode.server.Imcms" %>
<%@ page import="imcode.server.document.textdocument.TextDocumentDomainObject" %>
<html>
<body>
    <h3>Current query string: <%=request.getQueryString()%></h3>

    <h4>Text 1001:1 (in current (<%=Imcms.getUser().getDocGetterCallback().getLanguage()%>) language)</h4>
    <p>
        <a href="<%=BuildInEditorSupport.createTextEditorURL(request, "build_in_text_editor.jsp", 1001, 1)%>">
            <%=((TextDocumentDomainObject)Imcms.getServices().getDocumentMapper().getDocument(1001)).getText(1).getText()%>
        </a>
    <p>

    <p>
        <a href="<%=BuildInEditorSupport.createImageEditorURL(request, "build_in_text_editor.jsp", 1001, 3)%>">
            <img src="
                <%=((TextDocumentDomainObject)Imcms.getServices().getDocumentMapper().getDocument(1001)).getImage(3).getSource().getUrlPathRelativeToContextPath()%>
                "/>
        </a>
    <p>

</body>
</html>