    <%@ page
        import="imcode.server.document.textdocument.TextDocumentDomainObject, imcode.server.user.UserDomainObject, imcode.util.Utility, imcode.server.parser.ParserParameters" %>
<%
    ParserParameters parserParameters = ParserParameters.fromRequest(request);
    TextDocumentDomainObject document = (TextDocumentDomainObject) parserParameters.getDocumentRequest().getDocument();
    Integer menuIndex = (Integer) request.getAttribute("menuIndex");
    String label = (String) request.getAttribute("label");
    Integer defaultUserCount = (Integer) request.getAttribute("defaultUserCount");
    Integer userCount = (Integer) request.getAttribute("userCount");
    String content = (String) request.getAttribute("content");
    UserDomainObject user = parserParameters.getDocumentRequest().getUser();

    String editorUrl = String.format("%s/docadmin/menu?meta_id=%d&menu_no=%d", request.getContextPath(), document.getId(), menuIndex);
%>

<a href="<%=editorUrl%>" class="imcms_label">
    <%= label %> [<%= defaultUserCount %>/<%= userCount %>]&nbsp;
    <img src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/red.gif"
         border="0" alt="edit menu <%= menuIndex%>" align="bottom"/>
</a>

<%= content %>

<a href="<%=editorUrl%>">
    <img src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/ico_txt.gif"
         border="0" alt="edit menu <%= menuIndex%>"/>
</a>