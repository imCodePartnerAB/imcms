<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@page import="com.imcode.imcms.web.admin.ContentLoopController"%>
<%@page import="com.imcode.imcms.api.ContentLoop"%>
<%@page import="java.util.List"%>
<%@page import="com.imcode.imcms.api.Content"%>

<% 
int contentsCount = (Integer)request.getAttribute("contentsCount");
boolean isFirstContent = (Boolean)request.getAttribute("isFirstContent");
boolean isLastContent = (Boolean)request.getAttribute("isLastContent");
boolean isSingleContent = contentsCount == 1;

// single content: +addBefor,+ addAfter (2 columns)
// multipleContent: +moveUp, +moveDown, +delete (5 columns)
// multipleContent, 1st or last content: -moveUp or -moveDown (4 columns) 
int columnCount = isSingleContent
	? 2
	: (isFirstContent || isLastContent)
		? 4
		: 5;

pageContext.setAttribute("columnCount", columnCount);
pageContext.setAttribute("isSingleContent", isSingleContent);
%>

<tr>
  <td>
    <table>
      <tr>
        <td bgcolor="#CCCCCC" colspan="${columnCount}">${viewFragment}</td>
      </tr>
      <tr>
        <c:if test="${!isSingleContent && !isFirstContent}">
        <td bgcolor="RED">
          <form:form action="${pageContext.servletContext.contextPath}/newadmin/contentloop" method="POST">
            <input type="hidden" name="docId" value="${contentLoop.docId}"/>
            <input type="hidden" name="no" value="${contentLoop.no}"/>
            <input type="hidden" name="contentNo" value="${content.no}"/>
            <input type="hidden" name="flags" value="${flags}"/>
            <input type="hidden" name="cmd" value="<%=ContentLoopController.Command.MOVE_UP.ordinal()%>"/>
            <input type="submit" value="Move up"/>
          </form:form>   
        </td>
        </c:if>
        
        <c:if test="${!isSingleContent && !isLastContent}">
        <td bgcolor="RED">
          <form:form action="${pageContext.servletContext.contextPath}/newadmin/contentloop" method="POST">
            <input type="hidden" name="docId" value="${contentLoop.docId}"/>
            <input type="hidden" name="no" value="${contentLoop.no}"/>
            <input type="hidden" name="contentNo" value="${content.no}"/>
            <input type="hidden" name="flags" value="${flags}"/>
            <input type="hidden" name="cmd" value="<%=ContentLoopController.Command.MOVE_DOWN.ordinal()%>"/>
            <input type="submit" value="Move down"/>
          </form:form>   
        </td>
        </c:if>
        
        <td bgcolor="RED">
          <form:form action="${pageContext.servletContext.contextPath}/newadmin/contentloop" method="POST">
            <input type="hidden" name="docId" value="${contentLoop.docId}"/>
            <input type="hidden" name="no" value="${contentLoop.no}"/>
            <input type="hidden" name="contentNo" value="${content.no}"/>
            <input type="hidden" name="flags" value="${flags}"/>
            <input type="hidden" name="cmd" value="<%=ContentLoopController.Command.ADD_BEFORE.ordinal()%>"/>
            <input type="submit" value="Add before"/>
          </form:form>   
        </td> 
        
        <td bgcolor="RED">
          <form:form action="${pageContext.servletContext.contextPath}/newadmin/contentloop" method="POST">
            <input type="hidden" name="docId" value="${contentLoop.docId}"/>
            <input type="hidden" name="no" value="${contentLoop.no}"/>
            <input type="hidden" name="contentNo" value="${content.no}"/>
            <input type="hidden" name="flags" value="${flags}"/>
            <input type="hidden" name="cmd" value="<%=ContentLoopController.Command.ADD_AFTER.ordinal()%>"/>
            <input type="submit" value="Add after"/>
          </form:form>   
        </td> 
        
        <c:if test="${!isSingleContent}">
        <td bgcolor="RED">
          <form:form action="${pageContext.servletContext.contextPath}/newadmin/contentloop" method="POST">
            <input type="hidden" name="docId" value="${contentLoop.docId}"/>
            <input type="hidden" name="no" value="${contentLoop.no}"/>
            <input type="hidden" name="contentNo" value="${content.no}"/>
            <input type="hidden" name="flags" value="${flags}"/>
            <input type="hidden" name="cmd" value="<%=ContentLoopController.Command.DELETE.ordinal()%>"/>
            <input type="submit" value="Delete"/>
          </form:form>   
        </td> 
        </c:if>
      </tr>      
    </table>
  </td>
</tr>