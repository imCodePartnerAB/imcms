<%@ page
	
	import="imcode.server.document.textdocument.TextDocumentDomainObject,
	        imcode.server.user.UserDomainObject,
	        imcode.server.parser.ParserParameters"
	
	pageEncoding="UTF-8"
	
%><%

ParserParameters parserParameters = ParserParameters.fromRequest(request);
TextDocumentDomainObject document = (TextDocumentDomainObject) parserParameters.getDocumentRequest().getDocument();
Integer menuIndex = (Integer) request.getAttribute("menuIndex");
String label = (String) request.getAttribute("label") ;
Integer defaultUserCount = (Integer) request.getAttribute("defaultUserCount");
Integer userCount = (Integer) request.getAttribute("userCount");
String content = (String) request.getAttribute("content") ;
UserDomainObject user = parserParameters.getDocumentRequest().getUser();

int metaId = document.getId() ;
String cp = request.getContextPath() ;
String lang = user.getLanguageIso639_2() ;

%>
<a href="<%= cp %>/servlet/ChangeMenu?documentId=<%= metaId %>&amp;menuIndex=<%= menuIndex %>" class="imcms_label"><%-- 
    --%><%= label %> [<%= defaultUserCount %>/<%= userCount %>]&nbsp;<%-- 
    --%><img src="<%= cp %>/imcms/<%= lang %>/images/admin/red.gif" alt="edit menu <%= menuIndex%>" align="bottom" style="border:0 !important;" /></a>
<%= content %>
    <a href="<%= cp %>/servlet/ChangeMenu?documentId=<%= metaId %>&amp;menuIndex=<%= menuIndex %>"><%-- 
    --%><img src="<%= cp %>/imcms/<%= lang %>/images/admin/ico_txt.gif" alt="edit menu <%= menuIndex%>" style="border:0 !important;" /></a>
