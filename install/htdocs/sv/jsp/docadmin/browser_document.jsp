<%@ page contentType="text/html" import="com.imcode.imcms.servlet.admin.DocumentComposer,
                                         imcode.server.ApplicationServer,
                                         imcode.server.document.DocumentMapper,
                                         imcode.server.document.DocumentDomainObject,
                                         imcode.server.document.BrowserDocumentDomainObject,
                                         org.apache.commons.lang.ObjectUtils,
                                         org.apache.commons.lang.StringEscapeUtils,
                                         com.imcode.imcms.servlet.admin.BrowserDocumentComposer,
                                         java.util.*"%>
<%!
    public static final String PARAMETER__BROWSERS = "browsers";
%>
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
<input type="hidden" name="<%= DocumentComposer.PARAMETER__ACTION %>" value="<%= DocumentComposer.ACTION__CREATE_NEW_BROWSER_DOCUMENT %>">
<input type="hidden" name="<%= DocumentComposer.REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME %>" value="<%= request.getAttribute(DocumentComposer.REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME) %>">
<input type="hidden" name="<%= DocumentComposer.REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME %>" value="<%= request.getAttribute(DocumentComposer.REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME) %>">
<tr>
	<td colspan="3"><script>imcHeading("<? install/htdocs/sv/jsp/docadmin/browser_document.jsp/4/1 ?>",656);</script></td>
</tr>
<tr>
	<td align="right">
	<select name="<%= PARAMETER__BROWSERS %>" size="7" multiple>
		<%
            DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();

            Set addedBrowsers = new TreeSet() ;
            String[] addedBrowserIdStrings = request.getParameterValues( PARAMETER__BROWSERS ) ;

            for ( int i = 0; null != addedBrowserIdStrings && i < addedBrowserIdStrings.length; i++ ) {
                int addedBrowserId = Integer.parseInt(addedBrowserIdStrings[i]);
                BrowserDocumentDomainObject.Browser browser = documentMapper.getBrowserById(addedBrowserId) ;
                addedBrowsers.add(browser) ;
            }

            BrowserDocumentDomainObject.Browser[] allBrowsers = documentMapper.getAllBrowsers() ;
            Arrays.sort(allBrowsers) ;

            for ( int i = 0; i < allBrowsers.length; i++ ) {
                BrowserDocumentDomainObject.Browser browser = allBrowsers[i];
                if (!addedBrowsers.contains( browser )) {
                    %><option value="<%= browser.getId() %>"><%= StringEscapeUtils.escapeHtml( browser.getName() ) %></option><%
                }
            }

		%>
	</select></td>
	<td align="center"><input type="submit" class="imcmsFormBtnSmall" name="<%= BrowserDocumentComposer.PARAMETER_BUTTON__ADD_BROWSERS %>" value="<? install/htdocs/sv/jsp/docadmin/browser_document.jsp/2004 ?>"></td>
	<td>
        <table width="50%" border="0">
            <%
                for ( Iterator iterator = addedBrowsers.iterator(); iterator.hasNext(); ) {
                    BrowserDocumentDomainObject.Browser browser = (BrowserDocumentDomainObject.Browser)iterator.next();
                    %><tr>
                        <td><%= browser.getName() %></td>
                        <td><input type="text" name="browser_<%= browser.getId() %>" value=""></td>
                    </tr><%
                }
            %>
            <tr>
                <td></td>
                <td><input type="text" name="browser_0" value=""></td>
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
