<%@ page import="imcode.server.document.textdocument.TextDocumentDomainObject, imcode.server.user.UserDomainObject, imcode.util.Utility, imcode.server.parser.ParserParameters"%>
<%
    /*
    ParserParameters parserParameters = ParserParameters.fromRequest(request);
    TextDocumentDomainObject document = (TextDocumentDomainObject) parserParameters.getDocumentRequest().getDocument();
    Integer menuIndex = (Integer) request.getAttribute("menuIndex");
    String label = (String) request.getAttribute("label") ;
    Integer defaultUserCount = (Integer) request.getAttribute("defaultUserCount");
    Integer userCount = (Integer) request.getAttribute("userCount");
    String content = (String) request.getAttribute("content") ;
    UserDomainObject user = parserParameters.getDocumentRequest().getUser();
    */
    
    String content = (String) request.getAttribute("content");
    String url = request.getRequestURL().toString();
%>

<table border="1">
  <tr>
    <td>
      <form method="get" action="<%=request.getRequestURL().toString()%>">
        <input type="submit" value="Add last"/>
        <input type="hidden" name="cmd" value="addLast"/>
        <input type="hidden" name="meta_id" value="<%=request.getParameter("meta_id")%>"/>
        <input type="hidden" name="flags" value="<%=request.getParameter("flags")%>"/>
      </form>  
    </td>
    <td>
      <form method="get" action="<%=request.getRequestURL().toString()%>">
        <input type="submit" value="Remove last"/>
        <input type="hidden" name="cmd" value="removeLast"/>
        <input type="hidden" name="meta_id" value="<%=request.getParameter("meta_id")%>"/>
        <input type="hidden" name="flags" value="<%=request.getParameter("flags")%>"/>
      </form>  
    </td>      
  </tr>
  <tr>
    <td colspan="2">${content}</td>
  </tr>
</table>