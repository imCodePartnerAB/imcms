<%@ page contentType="text/html" import="com.imcode.imcms.servlet.admin.DocumentInformation,
                                         imcode.server.ApplicationServer,
                                         imcode.server.document.DocumentMapper,
                                         imcode.server.document.DocumentDomainObject,
                                         java.util.Map,
                                         imcode.server.document.BrowserDocumentDomainObject,
                                         java.util.Iterator,
                                         org.apache.commons.lang.ObjectUtils,
                                         java.util.Set,
                                         org.apache.commons.lang.StringEscapeUtils,
                                         java.util.Arrays"%>
<html>
<head>

<title><? install/htdocs/sv/jsp/docadmin/browser_document.jsp/1 ?></title>

<link rel="stylesheet" href="@imcmscssurl@/imcms_admin_ns.css" type="text/css">
<script src="@imcmsscripturl@/imcms_admin.js" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(1,'new_browsers')">

<script>
imcmsGui("outer_start", null);
imcmsGui("head", null);
</script>
<table border="0" cellspacing="0" cellpadding="0">
<form action="BackDoc">
<tr>
	<td><input type="submit" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/browser_document.jsp/2001 ?>"></td>
	<td>&nbsp;</td>
    <td><input type="button" value="<? install/htdocs/sv/jsp/docadmin/browser_document.jsp/2002 ?>" title="<? install/htdocs/sv/jsp/docadmin/browser_document.jsp/2003 ?>" class="imcmsFormBtn" onClick="openHelpW(46)"></td>
</tr>
</form>
</table>
<script>
imcmsGui("mid", null);
</script>
<table border="0" cellspacing="0" cellpadding="2" width="660">
<form method="POST" action="DocumentInformation">
<input type="hidden" name="<%= DocumentInformation.PARAMETER__ACTION %>" value="<%= DocumentInformation.ACTION__CREATE_NEW_BROWSER_DOCUMENT %>">
<input type="hidden" name="<%= DocumentInformation.REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME %>" value="<%= request.getAttribute(DocumentInformation.REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME) %>">
<input type="hidden" name="<%= DocumentInformation.REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME %>" value="<%= request.getAttribute(DocumentInformation.REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME) %>">
<tr>
	<td colspan="3"><script>imcHeading("<? install/htdocs/sv/jsp/docadmin/browser_document.jsp/4/1 ?>",656);</script></td>
</tr>
<tr>
	<td align="right">
	<select name="browsers" size="7" multiple>
		<%
            BrowserDocumentDomainObject document = (BrowserDocumentDomainObject)DocumentInformation.getObjectFromSessionWithKeyInRequest( request, DocumentInformation.REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME ) ;
            Map documentBrowserMap = document.getBrowserDocumentIdMap() ;
            Set documentBrowserMapKeySet = documentBrowserMap.keySet();
            DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
            BrowserDocumentDomainObject.Browser[] allBrowsers = documentMapper.getAllBrowsers() ;
            Arrays.sort(allBrowsers) ;
            for ( int i = 0; i < allBrowsers.length; i++ ) {
                BrowserDocumentDomainObject.Browser browser = allBrowsers[i];
                if (!documentBrowserMapKeySet.contains( browser )) {
                    %><option value="<%= browser.getId() %>">
                        <%= StringEscapeUtils.escapeHtml( browser.getName() ) %>
                    </option><%
                }
            }

		%>
	</select></td>
	<td align="center"><input type="submit" class="imcmsFormBtnSmall" name="add_browsers" value="<? install/htdocs/sv/jsp/docadmin/browser_document.jsp/2004 ?>"></td>
	<td>
        <table width="50%" border="0">
            <%
                for ( Iterator iterator = documentBrowserMapKeySet.iterator(); iterator.hasNext(); ) {
                    BrowserDocumentDomainObject.Browser browser = (BrowserDocumentDomainObject.Browser)iterator.next();
                    Integer toMetaId = (Integer)documentBrowserMap.get(browser) ;
                    %><tr>
                        <td><%= browser.getName() %></td>
                        <td><input type="text" name="browser_<%= browser.getId() %>" value="<%= toMetaId %>"></td>
                    </tr><%
                }
            %>
            <tr>
                <td></td>
                <td><input type="text" name="browser_0" value="<%= ObjectUtils.defaultIfNull( document.getDefaultBrowserDocumentId(), "") %>"></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
	<td colspan="3"><? install/htdocs/sv/jsp/docadmin/browser_document.jsp/7 ?> </td>
</tr>
<tr>
	<td colspan="3"><script>hr("100%",656,"blue");</script></td>
</tr>
<tr>
	<td colspan="3" align="right">
	<input type="SUBMIT" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/browser_document.jsp/2005 ?>" name="ok">
	<input type="RESET" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/browser_document.jsp/2006 ?>" name="reset">
	<input type="SUBMIT" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/browser_document.jsp/2007 ?>" name="cancel"></td>
</tr>
</form>
</table>
<script>
imcmsGui("bottom", null);
imcmsGui("outer_end", null);
</script>

<script language="JavaScript">
<!--
var el = eval("document.forms[1].elements");
if (el) {
	for (var i = 0; i < el.length; i++) {
		if (el[i].type.toUpperCase() == "TEXT" && el[i].name.indexOf("bid") != -1) el[i].size = 5;
	}
}
//-->
</script>

</body>
</html>
