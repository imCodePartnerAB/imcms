<%@ page import="com.imcode.imcms.flow.Page, static com.imcode.imcms.servlet.admin.LinkEditPage.Parameter.*" contentType="text/html"%> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>

<% pageContext.setAttribute("linkEditPage", Page.fromRequest(request)); %>
<jsp:useBean id="linkEditPage" type="com.imcode.imcms.servlet.admin.LinkEditPage" />

<ui:dialog titlekey="edit/link/dialog/title" helpid="EditLink">

    <ui:labeled idref="<%= HREF.toString() %>" key="edit/link/href">
        <ui:text id="<%= HREF.toString() %>" value="${linkEditPage.link.href}" maxlength="300" size="60"/>
        <ui:smallsubmit name="<%= SEARCH.toString() %>" value="..."/>
    </ui:labeled>
    
    <ui:labeled idref="<%= TITLE.toString() %>" key="edit/link/title">
        <ui:text id="<%= TITLE.toString() %>" value="${linkEditPage.link.title}" maxlength="300" size="60"/>
    </ui:labeled>
    
    <c:if test="${linkEditPage.targetEditable}">
        <ui:separator/>
        <ui:labeled idref="<%= TARGET.toString() %>" key="edit/link/target">
            <ui:linktarget name="<%= TARGET.toString() %>" target="${linkEditPage.link.target}"/>
        </ui:labeled>
    </c:if>

</ui:dialog>
<script type="text/javascript">
    window.resizeTo(720,450);
</script>
