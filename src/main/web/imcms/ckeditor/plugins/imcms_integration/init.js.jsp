<%@ page
	
	import="java.io.File,
	        imcode.server.Imcms,
	        com.imcode.imcms.servlet.admin.EditImage,
	        com.imcode.imcms.servlet.admin.EditLink,
	        org.apache.commons.lang.StringUtils"
	
	contentType="text/javascript"
	pageEncoding="UTF-8"
	
%><%

String cp = request.getContextPath() ;

String ckEditorPath      = cp + "/imcms/ckeditor/" ;
String ckPluginImcmsPath = ckEditorPath + "plugins/imcms_integration/" ;

File webRoot = Imcms.getPath() ;
boolean hasOwnCss = false ;

if (new File(webRoot, "/css/editor_site.css").exists()) {
	hasOwnCss = true ;
}

%>

var CKEDITOR_imcmsImageEditPath = '<%= EditImage.linkTo(request, "/imcms/ckeditor/plugins/imcms_image/return_image.jsp")%>' ;
var CKEDITOR_imcmsLinkEditPath  = '<%= EditLink.linkTo(request, "/imcms/ckeditor/plugins/imcms_link/return_link.jsp")%>' ;
var CKEDITOR_otherLinkParams    = [ '<%= StringUtils.join(EditLink.OTHER_PARAMETERS, "', '") %>' ] ;
var contextPath                 = '<%= cp %>' ;


function initCkEditor($, id, lang, width, toolBarSet, customConfig) {
	var isInlineEditing = (id.toLowerCase().indexOf('inline') != -1) ;
	var oConfig = {
		customConfig       : '<%= ckPluginImcmsPath + "imcms_config.js.jsp?" + System.currentTimeMillis() %>' + (isInlineEditing ? '&isInlineEditing=true' : ''),
		contentsCss        : '<%= cp + "/css/" + (hasOwnCss ? "editor_site.css" : "editor_default.css") %>',
		width              : width,
		language           : lang,
		toolbar            : toolBarSet,
		sharedSpaces       : { top : 'toolBar' }<%--,
		autoGrow_minHeight : 300,
		autoGrow_maxHeight : maxH--%>
	} ;
    oConfig = $.extend({}, oConfig, customConfig);
	if (isInlineEditing) {
		var iOffset = (/.*SIMPLE_[12]/.test(toolBarSet)) ? 200 : 300 ;
		var winH    = $(window).height() ;
		var wantedH = (winH - iOffset) ;
		var maxH    = (wantedH > 700) ? 700 : (wantedH < 100) ? 100 : wantedH ;
		if (maxH < 400) maxH = 400 ;<%-- Trying this value --%><%--
		console.log('winH: ' + winH + ', maxH: ' + maxH) ; --%>
		oConfig.autoGrow_minHeight = 100 ;
		oConfig.autoGrow_maxHeight = maxH ;
	}
	CKEDITOR.replace(id, oConfig) ;
	CKEDITOR.on('instanceReady', function(event) {
		setSizeOfEditor($) ;
		checkIframeScroll($) ;
		<%-- Fix indentation on common tags --%>
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
				},
				img : function( element ) {
					if (element.attributes.rel) delete element.attributes.rel ;
				}
			}
		}) ;
	}) ;
}
