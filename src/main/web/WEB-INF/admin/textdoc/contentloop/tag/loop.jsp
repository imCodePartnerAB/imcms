<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page import="com.imcode.imcms.web.admin.ContentLoopController"%>
<%@ page import="imcode.server.document.textdocument.TextDocumentDomainObject, imcode.server.user.UserDomainObject, imcode.util.Utility, imcode.server.parser.ParserParameters"%>

<%
    String viewFragment = (String)request.getAttribute("viewFragment");

%>
<table border="1" bgcolor="BLUE">
  <tr>
    <td>
        <td bgcolor="RED">
          <form:form action="${pageContext.servletContext.contextPath}/newadmin/contentloop" method="POST">
            <input type="hidden" name="docId" value="${contentLoop.docId}"/>
            <input type="hidden" name="no" value="${contentLoop.no}"/>
            <input type="hidden" name="contentNo" value="0"/>
            <input type="hidden" name="flags" value="${flags}"/>
            <input type="hidden" name="cmd" value="<%=ContentLoopController.Command.ADD_FISRT.ordinal()%>"/>
            <input type="submit" value="Add first"/>
          </form:form>
        </td>
    </td>
  </tr>

    <%
    if (viewFragment.length() > 0) {
        out.println(viewFragment);
    }
    %>

  <tr>
    <td>
        <td bgcolor="RED">
          <form:form action="${pageContext.servletContext.contextPath}/newadmin/contentloop" method="POST">
            <input type="hidden" name="docId" value="${contentLoop.docId}"/>
            <input type="hidden" name="no" value="${contentLoop.no}"/>
              <input type="hidden" name="contentNo" value="0"/>
            <input type="hidden" name="flags" value="${flags}"/>
            <input type="hidden" name="cmd" value="<%=ContentLoopController.Command.ADD_LAST.ordinal()%>"/>
            <input type="submit" value="Add last"/>
          </form:form>
        </td>
    </td>
  </tr>
</table>