<%@ page
	
	import="java.io.File,
	        imcode.server.Imcms,
	        com.imcode.imcms.servlet.admin.EditImage,
	        com.imcode.imcms.servlet.admin.ImageEditPage"
	
	contentType="text/javascript"
	
	pageEncoding="UTF-8"
	
%><%

boolean editorActive = (null != request.getParameter("html")) ;

String cp = request.getContextPath() ;

String ckEditorPath      = cp + "/imcms/ckeditor/" ;
String ckPluginImcmsPath = ckEditorPath + "plugins/imcms_integration/" ;

File webRoot = Imcms.getPath() ;
boolean hasOwnCss = false ;

if (new File(webRoot, "/css/editor_site.css").exists()) {
	hasOwnCss = true ;
}

%>

var CKEDITOR_imcmsImageEditPath                       = '<%= EditImage.linkTo(request, "/imcms/ckeditor/plugins/imcms_image/return_image.jsp")%>' ;
var contextPath                                       = '<%= cp %>' ;


function initCkEditor(id, lang, toolBarSet) {
	<% if (editorActive) { %>
	CKEDITOR.replace(id,
		{
			customConfig : '<%= ckPluginImcmsPath + "imcms_config.js?" + System.currentTimeMillis() %>',
			contentsCss  : '<%= cp + "/css/" + (hasOwnCss ? "editor_site.css" : "editor_default.css") %>',<%--
			height       : height,--%>
			language     : lang,
			toolbar      : toolBarSet
		}
	) ;
	CKEDITOR.on('instanceReady', function(event) {
		setSizeOfEditor($) ;
		<%-- Fix indentation on commen tags --%>
		var tags = ['p', 'li', 'table', 'tbody', 'tr'] ;
		for (var key in tags) {
			event.editor.dataProcessor.writer.setRules(tags[key], {
					indent           : false,
					breakBeforeOpen  : true,
					breakAfterOpen   : false,
					breakBeforeClose : false,
					breakAfterClose  : true
			}) ;
		}
		tags = ['td'] ;
		for (key in tags) {
			event.editor.dataProcessor.writer.setRules(tags[key], {
					indent           : true,
					breakBeforeOpen  : true,
					breakAfterOpen   : false,
					breakBeforeClose : false,
					breakAfterClose  : true
			}) ;
		}
		tags = ['h1', 'h2', 'h3', 'h4', 'h5', 'h6'] ;
		for (key in tags) {
			event.editor.dataProcessor.writer.setRules(tags[key], {
					indent           : false,
					breakBeforeOpen  : true,
					breakAfterOpen   : false,
					breakBeforeClose : false,
					breakAfterClose  : true
			}) ;
		}
		<%-- Delete hasbox parameter --%>
		event.editor.dataProcessor.htmlFilter.addRules({
			elements : {
				p : function( element ) {
					if (element.attributes.hasbox) delete element.attributes.hasbox ;
				}
			}
		}) ;
	}) ;
	<% } %>
}
