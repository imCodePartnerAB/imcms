<%@page import="com.imcode.imcms.mapping.DocumentMapper"%>
<%@page import="imcode.server.Imcms"%>

<%@taglib prefix="imcms" uri="imcms"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>

<%@page import="java.util.List"%>
<%@page import="com.imcode.imcms.api.DocumentVersion"%>

<%
Integer documentId = 1001;
Integer textIndex = 1;
Integer imageIndex = 1;

DocumentMapper dm = Imcms.getServices().getDocumentMapper();
List<DocumentVersion> documentVersions = dm.getDocumentVersions(documentId);
%>

<div>
  <table border="1">
    <tr><th colspan="2">Versions for document <%=documentId%></th></tr>
    <tr><td>Version nr</td><td>Version tag</td></tr>  
	<%	
	for (DocumentVersion version: documentVersions) {
		%>
		<tr>
		  <td><%=version.getVersion()%></td>
		  <td><%=version.getVersionTag()%></td>
		</tr>
		<%	
	}
	%>
  </table>
</div>

<div>
  <table border="1">
    <tr><th colspan="2">Text <%=textIndex%> for document <%=documentId%></th></tr>
    <tr><td>Version nr</td><td>Text</td></tr>  
	<%
	for (DocumentVersion version: documentVersions) {
		%>
		<tr>
		  <td><%=version.getVersion()%></td>
		  <td><imcms:text no='<%=textIndex%>' version="<%=version.getVersion()%>"/></td>
		</tr>
		<%	
	}
	%>
  </table>
</div>