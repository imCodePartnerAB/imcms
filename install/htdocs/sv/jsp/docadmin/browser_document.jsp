<%@ page contentType="text/html" import="com.imcode.imcms.servlet.admin.DocumentComposer,
                                         imcode.server.ApplicationServer,
                                         imcode.server.document.DocumentMapper,
                                         imcode.server.document.DocumentDomainObject,
                                         imcode.server.document.BrowserDocumentDomainObject,
                                         org.apache.commons.lang.ObjectUtils,
                                         org.apache.commons.lang.StringEscapeUtils,
                                         com.imcode.imcms.servlet.admin.BrowserDocumentComposer,
                                         java.util.*,
                                         org.apache.commons.lang.StringUtils"%>
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
<form method="POST" action="BrowserDocumentComposer">
<%
    DocumentComposer.NewDocumentParentInformation newDocumentParentInformation = (DocumentComposer.NewDocumentParentInformation)DocumentComposer.getObjectFromSessionWithKeyInRequest(request, DocumentComposer.REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME);
    boolean creatingNewDocument = null != newDocumentParentInformation;

    if (creatingNewDocument) { %>
        <input type="hidden"
            name="<%= DocumentComposer.REQUEST_ATTR_OR_PARAM__ACTION %>"
            value="<%= DocumentComposer.ACTION__CREATE_NEW_BROWSER_DOCUMENT %>">
        <input type="hidden"
            name="<%= DocumentComposer.REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME %>"
            value="<%= DocumentComposer.getSessionAttributeNameFromRequest( request, DocumentComposer.REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME) %>">
<% } else {%>
        <input type="hidden"
            name="<%= DocumentComposer.REQUEST_ATTR_OR_PARAM__ACTION %>"
            value="<%= DocumentComposer.ACTION__PROCESS_EDITED_BROWSER_DOCUMENT %>">
<% } %>
        <input type="hidden"
            name="<%= DocumentComposer.REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME %>"
            value="<%= DocumentComposer.getSessionAttributeNameFromRequest(request, DocumentComposer.REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME) %>">
<tr>
	<td colspan="3"><script>imcHeading("<? install/htdocs/sv/jsp/docadmin/browser_document.jsp/4/1 ?>",656);</script></td>
</tr>
<tr>
	<td align="right">
	<select name="<%= BrowserDocumentComposer.PARAMETER__BROWSERS %>" size="7" multiple>
        <%
            Map addedBrowsers = (Map)request.getAttribute( BrowserDocumentComposer.REQUEST_ATTRIBUTE__ADDED_BROWSERS );

            BrowserDocumentDomainObject.Browser[] allBrowsers = ApplicationServer.getIMCServiceInterface().getDocumentMapper().getAllBrowsers() ;
            Arrays.sort(allBrowsers) ;

            for ( int i = 0; i < allBrowsers.length; i++ ) {
                BrowserDocumentDomainObject.Browser browser = allBrowsers[i];
                if (!addedBrowsers.containsKey( browser )) {
                    %><option value="<%= browser.getId() %>"><%= StringEscapeUtils.escapeHtml( browser.getName() ) %></option><%
                }
            }
        %>
	</select>
    </td>
	<td align="center">
        <input type="submit" class="imcmsFormBtnSmall" name="<%= BrowserDocumentComposer.PARAMETER_BUTTON__ADD_BROWSERS %>"
                value="<? install/htdocs/sv/jsp/docadmin/browser_document.jsp/2004 ?>">
    </td>
	<td>
        <table border="0">
            <%

                for ( Iterator iterator = new TreeSet(addedBrowsers.keySet()).iterator(); iterator.hasNext(); ) {
                    BrowserDocumentDomainObject.Browser browser = (BrowserDocumentDomainObject.Browser)iterator.next();
                    if ( browser.equals( BrowserDocumentDomainObject.Browser.DEFAULT )) {
                        continue ;
                    }
                    %><tr>
                        <td><%= browser.getName() %>:</td>
                        <td><input type="text" name="<%= BrowserDocumentComposer.PARAMETER_PREFIX__DESTINATION %><%= browser.getId() %>" size="5" maxlength="9" value="<%= ObjectUtils.defaultIfNull( addedBrowsers.get(browser),"") %>"></td>
                    </tr><%
                }
            %>
            <tr>
                <td><? install/htdocs/sv/jsp/docadmin/browser_document.jsp/other_browsers ?>:</td>
                <td><input type="text" name="<%= BrowserDocumentComposer.PARAMETER_PREFIX__DESTINATION %><%= BrowserDocumentDomainObject.Browser.DEFAULT.getId() %>" size="5" maxlength="9" value="<%= ObjectUtils.defaultIfNull( addedBrowsers.get(BrowserDocumentDomainObject.Browser.DEFAULT),"") %>"></td>
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
	<input type="SUBMIT" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/browser_document.jsp/2005 ?>" name="<%= BrowserDocumentComposer.PARAMETER_BUTTON__OK %>">
	<input type="RESET" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/browser_document.jsp/2006 ?>" name="reset">
	<input type="SUBMIT" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/browser_document.jsp/2007 ?>" name="<%= BrowserDocumentComposer.PARAMETER_BUTTON__CANCEL %>"></td>
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
