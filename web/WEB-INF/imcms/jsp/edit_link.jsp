<%@ page
	
	import="com.imcode.imcms.flow.Page,
	        static com.imcode.imcms.servlet.admin.LinkEditPage.Parameter.*,
	        com.imcode.imcms.api.ContentManagementSystem"
	
	contentType="text/html"
	
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui"
%><%

boolean isSwe = false ;
try {
	isSwe =	ContentManagementSystem.fromRequest(request).getCurrentUser().getLanguage().getIsoCode639_2().equals("swe");
} catch (Exception e) {}

pageContext.setAttribute("linkEditPage", Page.fromRequest(request));

%>
<jsp:useBean id="linkEditPage" type="com.imcode.imcms.servlet.admin.LinkEditPage" />

<ui:dialog titlekey="edit/link/dialog/title" helpid="EditLink">

    <ui:labeled idref="DEFVAL" key="install/htdocs/imcms/html/link_editor.jsp/7">
        <select onchange="setValue(this.selectedIndex);">
	        <option value="" selected="selected">-</option>
	        <option value="1"><fmt:message key="install/htdocs/imcms/html/link_editor.jsp/8" /></option>
	        <option value="2"><fmt:message key="install/htdocs/imcms/html/link_editor.jsp/9" /></option>
	        <option value="3"><fmt:message key="install/htdocs/imcms/html/link_editor.jsp/10" /></option>
	        <option value="4"><fmt:message key="install/htdocs/imcms/html/link_editor.jsp/11" /></option>
	        <option value="5"><fmt:message key="install/htdocs/imcms/html/link_editor.jsp/12" /></option>
        </select>
    </ui:labeled>
	
    <ui:labeled idref="<%= HREF.toString() %>" key="edit/link/href">
        <ui:text id="<%= HREF.toString() %>" value="${linkEditPage.link.href}" maxlength="300" size="60"/>
        <input type="submit" class="imcmsFormBtnSmall" name="SEARCH" id="searchBtn" value="Search" />
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
window.resizeTo(800,520);
document.getElementById("searchBtn").value = "<%= isSwe ? "S\u00F6k" : "Search" %>" ;
function setValue(idx) {
	var oHref = document.getElementById("<%= HREF.toString() %>") ;
	var oldVal = oHref.value ;
	var arrValues = [
		"<%= request.getContextPath() %>/MetaOrAlias",
		"http://www.domain.com/path/",
		"mailto:whoever@domain.com",
		"ftp://ftp.domain.com/path/",
		"#anchorName"
	] ;
	if (idx > 0) {
		oHref.value = arrValues[idx-1] ;
	}
}
</script>
