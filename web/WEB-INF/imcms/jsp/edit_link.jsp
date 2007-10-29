<%@ page
	
	import="com.imcode.imcms.flow.Page,
	        static com.imcode.imcms.servlet.admin.LinkEditPage.Parameter.*,
	        com.imcode.imcms.api.ContentManagementSystem, imcode.server.Imcms"
	
	contentType="text/html; charset=UTF-8"
	
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui"
%><%

response.setContentType( "text/html; charset=" + Imcms.DEFAULT_ENCODING );

boolean isSwe = false ;
try {
	isSwe =	ContentManagementSystem.fromRequest(request).getCurrentUser().getLanguage().getIsoCode639_2().equals("swe");
} catch (Exception e) {}

pageContext.setAttribute("linkEditPage", Page.fromRequest(request));

%>
<jsp:useBean id="linkEditPage" type="com.imcode.imcms.servlet.admin.LinkEditPage" />

<ui:dialog titlekey="edit/link/dialog/title" helpid="EditLink">

    <ui:labeled idref="DEFVAL" key="install/htdocs/imcms/html/link_editor.jsp/7">
        <select id="typeSelect" onchange="setValue(this.selectedIndex);">
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
		var confirmed = true ;
		if (oHref.value != "") {
			confirmed = confirm("<%= isSwe ? "Vill du skriva \u00F6ver det gamla l\u00e4nkadress-v\u00e4rdet med ett exempel?" :
			                                 "Do you want to overwrite the old link address value with an example?" %>") ;
		}
		if (confirmed) oHref.value = arrValues[idx-1] ;
	}
	checkSearchEnabled() ;
}
function checkSearchEnabled() {
	var objType = document.getElementById("typeSelect") ;
	var objBtn  = document.getElementById("searchBtn") ;
	if (objType.selectedIndex != 1) {
		objBtn.className = "imcmsFormBtnSmallDisabled" ;
		objBtn.disabled  = true ;
	} else {
		objBtn.className = "imcmsFormBtnSmall" ;
		objBtn.disabled  = false ;
	}
}
function setSelectToLinkType() {
	var objType = document.getElementById("typeSelect") ;
	var objHref = document.getElementById("<%= HREF.toString() %>") ;
	var href = objHref.value ;
	if (/^#.*/.test(href)) {
		objType.selectedIndex = 5 ;
	} else if (/^ftp:.*/.test(href)) {
		objType.selectedIndex = 4 ;
	} else if (/^mailto:.*/.test(href)) {
		objType.selectedIndex = 3 ;
	} else if (href.indexOf(":") != -1) {
		objType.selectedIndex = 2 ;
	} else if (/^\/.*/.test(href)) {
		objType.selectedIndex = 1 ;
	}
	checkSearchEnabled() ;
}
setSelectToLinkType() ;
</script>
