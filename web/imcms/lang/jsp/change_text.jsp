<%@ page

	import="com.imcode.imcms.servlet.admin.ChangeText,
	        imcode.server.Imcms,
	        imcode.server.LanguageMapper, imcode.server.document.textdocument.TextDomainObject, imcode.util.Utility, org.apache.commons.lang.StringEscapeUtils, java.util.ArrayList, java.util.Arrays, java.util.List"

    contentType="text/html; charset=UTF-8"

%><%@taglib prefix="vel" uri="imcmsvelocity"
%><%
	response.setContentType( "text/html; charset=" + Imcms.DEFAULT_ENCODING );
ChangeText.TextEditPage textEditPage = (ChangeText.TextEditPage) request.getAttribute(ChangeText.TextEditPage.REQUEST_ATTRIBUTE__PAGE);

List<String> formats = new ArrayList<String>();
String[] formatParameterValues = request.getParameterValues("format");
if (null != formatParameterValues) {
    formats.addAll(Arrays.asList(formatParameterValues));
    formats.remove("");
}

boolean showModeEditor = formats.isEmpty();
boolean showModeText   = formats.contains("text") || showModeEditor;
boolean showModeHtml   = formats.contains("html") || formats.contains("none") || showModeEditor ;

%>
<vel:velocity>
<html>
<head>
<title><? templates/sv/change_text.html/1 ?></title>

<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/imcms/css/imcms_admin.css.jsp">
<script src="<%= request.getContextPath() %>/imcms/$language/scripts/imcms_admin.js.jsp" type="text/javascript"></script>
</head>
<body bgcolor="#FFFFFF" style="margin-bottom:0px;">
<% if (showModeEditor) { %>
<script type="text/javascript">
    _editor_url  = "<%=request.getContextPath()%>/imcms/xinha/"  // (preferably absolute) URL (including trailing slash) where Xinha is installed
    _editor_lang = "<%= LanguageMapper.convert639_2to639_1(Utility.getLoggedOnUser(request).getLanguageIso639_2()) %>";      // And the language we need to use in the editor.
</script>
<script type="text/javascript" src="<%= request.getContextPath() %>/imcms/xinha/XinhaCore.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/imcms/xinha/plugins/ImcmsIntegration/init.js.jsp<% if (TextDomainObject.TEXT_TYPE_HTML==textEditPage.getType()) { %>?html=true<% } %>"></script>
<% } %>
<form method="POST" action="<%= request.getContextPath() %>/servlet/SaveText">
<input type="hidden" name="meta_id"  value="<%= textEditPage.getDocumentId() %>">
<input type="hidden" name="txt_no"   value="<%= textEditPage.getTextIndex() %>">

#gui_outer_start()
#gui_head( "<? global/imcms_administration ?>" )

<table border="0" cellspacing="0" cellpadding="0" width="100%">
<tr>
	<td>
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td>
		<input type="button" tabindex="10" value="<? global/back ?>" name="cancel" class="imcmsFormBtn"></td>
	</tr>
	</table></td>

	<td align="right">
	<input type="button" tabindex="12" value="<? templates/sv/change_text.html/2004 ?>" title="<? templates/sv/change_text.html/2005 ?>" class="imcmsFormBtn" onClick="openHelpW('EditText')"></td>

</tr>
</table>

#gui_mid()
<div id="theLabel"><%= StringEscapeUtils.escapeHtml( textEditPage.getLabel() ) %></div>

<table border="0" cellspacing="0" cellpadding="2" width="720" align="center">
</vel:velocity>
<tr>
	<td colspan="2" class="imcmsAdmForm">
        <div id="editor">
            <textarea name="text" tabindex="1" id="text" cols="125" rows="25" style="overflow: auto; width: 100%" wrap="virtual"><%= StringEscapeUtils.escapeHtml( textEditPage.getTextString() ) %></textarea>
        </div>    
    </td>
</tr>
<vel:velocity>
<tr>
	<td>
        <% if (showModeEditor) { %>
        <script type="text/javascript">
            
            function getElementsByClassAttribute(node, tagname, class) {
                var result = new Array();
                var elements = node.getElementsByTagName(tagname);
                for (i = 0, j = 0; i < elements.length; ++i) {
                    var element = elements[i];
                    if (element.className == class) {
                        result[j++] = element;
                    }
                }
                return result;
            }
                    
            function setTextMode() {
                xinha_editors.text.deactivateEditor();
                var editors = getElementsByClassAttribute(document, 'table', 'htmlarea')
                var editor = editors[0];
                var textarea = document.getElementById('text');
                editor.parentNode.replaceChild(textarea,editor);
                textarea.style.width = editor.style.width;
                textarea.style.height = editor.style.height;
                textarea.style.display = 'block';
            }
            function setHtmlMode() {
                Xinha.startEditors(xinha_editors);
            }
        </script>
        <% } %>
        <% if (showModeText && showModeHtml) { %>
        <input type="radio" name="format_type" id="format_type_text" value="0" <% if (TextDomainObject.TEXT_TYPE_PLAIN==textEditPage.getType()) { %> checked<% } %>
               <% if (showModeEditor) { %>onclick="setTextMode()"<% } %>>
        <label for="format_type_text">Text</label>
        <input type="radio" name="format_type" id="format_type_html" value="1" <% if (TextDomainObject.TEXT_TYPE_HTML==textEditPage.getType()) { %> checked<% } %> 
               <% if (showModeEditor) { %>onclick="setHtmlMode()"<% } %>>
        <label for="format_type_html">HTML</label>
        <% } else if (showModeText) { %>
            <input type="hidden" name="format_type" value="<%= TextDomainObject.TEXT_TYPE_PLAIN %>">
        <% } else if (showModeHtml) { %>
            <input type="hidden" name="format_type" value="<%= TextDomainObject.TEXT_TYPE_HTML %>">
        <% } %>
    </td>
    <td align="right">
            <input tabindex="2" type="SUBMIT" class="imcmsFormBtn" name="ok" value="  <? templates/sv/change_text.html/2006 ?>  ">
            <input tabindex="3" type="SUBMIT" class="imcmsFormBtn" name="save" value="  <? templates/sv/change_text.html/save ?>  ">
            <input tabindex="4" type="RESET" class="imcmsFormBtn" value="<? templates/sv/change_text.html/2007 ?>">
            <input tabindex="5" type="SUBMIT" class="imcmsFormBtn" name="cancel" value=" <? templates/sv/change_text.html/2008 ?> ">
    </td>
</tr>
</table>
#gui_bottom()
#gui_outer_end()
</form>
</body>
</html>
</vel:velocity>
