<%@ page contentType="text/html; charset=UTF-8"
         import="imcode.server.Imcms,
                 imcode.server.document.BrowserDocumentDomainObject,
                 org.apache.commons.lang.ObjectUtils,
                 org.apache.commons.lang.StringEscapeUtils,
                 java.util.*,
                 imcode.util.HttpSessionUtils,
                 com.imcode.imcms.flow.PageFlow,
                 com.imcode.imcms.flow.EditBrowserDocumentPageFlow,
                 com.imcode.imcms.flow.*"%>
<%@taglib prefix="vel" uri="imcmsvelocity"%>
<vel:velocity>
<html>
<head>

<title><? install/htdocs/sv/jsp/docadmin/browser_document.jsp/1 ?></title>

<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js.jsp" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(1,'new_browsers')">
#gui_outer_start()
#gui_head("<? global/imcms_administration ?>")
<table border="0" cellspacing="0" cellpadding="0">
<form method="POST" action="DocumentPageFlowDispatcher">
<tr>
	<td><input type="submit" name="cancel" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/browser_document.jsp/2001 ?>"></td>
	<td>&nbsp;</td>
    <td><input type="button" value="<? install/htdocs/sv/jsp/docadmin/browser_document.jsp/2002 ?>" title="<? install/htdocs/sv/jsp/docadmin/browser_document.jsp/2003 ?>" class="imcmsFormBtn" onClick="openHelpW('LinkBrowserControl')"></td>
</tr>
</table>
#gui_mid()
<table border="0" cellspacing="0" cellpadding="2" width="660">
<input type="hidden" name="<%= PageFlow.REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW %>"
    value="<%= HttpSessionUtils.getSessionAttributeNameFromRequest(request,PageFlow.REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW) %>">
<input type="hidden" name="<%= PageFlow.REQUEST_PARAMETER__PAGE %>"
    value="<%= EditDocumentPageFlow.PAGE__EDIT %>">
<tr>
	<td colspan="3">
        #gui_heading( "<? install/htdocs/sv/jsp/docadmin/browser_document.jsp/4/1 ?>" )
    </td>
</tr>
<tr>
	<td align="right">
	<select name="<%= EditBrowserDocumentPageFlow.REQUEST_PARAMETER__BROWSERS %>" size="7" multiple>
        <%
            Map addedBrowsers = (Map)request.getAttribute( EditBrowserDocumentPageFlow.REQUEST_ATTRIBUTE__ADDED_BROWSERS );

            BrowserDocumentDomainObject.Browser[] allBrowsers = Imcms.getServices().getDocumentMapper().getAllBrowsers() ;
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
        <input type="submit" class="imcmsFormBtnSmall" name="<%= EditBrowserDocumentPageFlow.REQUEST_PARAMETER__ADD_BROWSERS_BUTTON %>"
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
                        <td><input type="text" name="<%= EditBrowserDocumentPageFlow.REQUEST_PARAMETER_PREFIX__DESTINATION %><%= browser.getId() %>" size="5" maxlength="9" value="<%= ObjectUtils.defaultIfNull( addedBrowsers.get(browser),"") %>"></td>
                    </tr><%
                }
            %>
            <tr>
                <td><? install/htdocs/sv/jsp/docadmin/browser_document.jsp/other_browsers ?>:</td>
                <td><input type="text" name="<%= EditBrowserDocumentPageFlow.REQUEST_PARAMETER_PREFIX__DESTINATION %><%= BrowserDocumentDomainObject.Browser.DEFAULT.getId() %>" size="5" maxlength="9" value="<%= ObjectUtils.defaultIfNull( addedBrowsers.get(BrowserDocumentDomainObject.Browser.DEFAULT),"") %>"></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
	<td colspan="3"><? install/htdocs/sv/jsp/docadmin/browser_document.jsp/7 ?> </td>
</tr>
<tr>
	<td colspan="3">#gui_hr( "blue" )</td>
</tr>
<tr>
	<td colspan="3" align="right">
	<input type="SUBMIT" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/browser_document.jsp/2005 ?>" name="<%= PageFlow.REQUEST_PARAMETER__OK_BUTTON %>" onClick="return singleclicked();">
	<input type="RESET" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/browser_document.jsp/2006 ?>" name="reset">
	<input type="SUBMIT" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/browser_document.jsp/2007 ?>" name="<%= PageFlow.REQUEST_PARAMETER__CANCEL_BUTTON %>"></td>
</tr>
</form>
</table>
#gui_bottom()
#gui_outer_end()
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
</vel:velocity>
