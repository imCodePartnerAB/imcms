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
List<DocumentVersion> documentVersions = dm.getDocumentVersionInfo(documentId).getVersions();
%>

<div>
  <table border="1">
    <tr><th colspan="3">Versions for document <%=documentId%></th></tr>
    <tr>
      <td>Version nr</td>
      <td>Version tag</td>
      <td>Text field <%=textIndex%> content</td>
    </tr>  
	<%	
	for (DocumentVersion version: documentVersions) {
		%>
		<tr>
		  <td><%=version.getNo()%></td>
		  <td>
		    <imcms:text no='<%=textIndex%>' version="<%=version.getNo()%>"/>
		  </td>
		</tr>
		<%	
	}
	%>
  </table>
</div>