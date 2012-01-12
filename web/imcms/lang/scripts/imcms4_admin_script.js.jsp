<%@ page
	
	import="com.imcode.imcms.servlet.AjaxServlet,
	        imcode.server.Imcms,
	        java.net.URL,
	        java.net.URLConnection,
	        java.io.InputStream,
	        java.io.InputStreamReader,
	        java.io.BufferedReader,
	        org.apache.oro.text.perl.Perl5Util,
	        org.apache.commons.lang.StringUtils"
	
	contentType="text/javascript"
	pageEncoding="UTF-8"
	
%><%@ taglib uri="imcmsvelocity" prefix="vel"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%!

private final static int CONNECTION_TIMEOUT_MILLIS = 3000 ;
private final static String USE_INLINE_EDITING           = "imcms_text_use_inline_editing" ;
private final static String USE_INLINE_EDITING_FORMATTED = "imcms_text_use_inline_editing_formatted" ;
private final static String USE_WIDTH                    = "imcms_text_use_width" ;

public static String getURLcontent( String urlString, String encoding ) {
		try {
				URL url = new URL(urlString) ;
				URLConnection con = url.openConnection() ;
				con.setConnectTimeout(CONNECTION_TIMEOUT_MILLIS);
				con.connect() ;
				InputStream is = (InputStream) con.getContent() ;
				InputStreamReader isr = new InputStreamReader(is, encoding) ;
				BufferedReader br = new BufferedReader(isr) ;
				String line = br.readLine() ;
				StringBuffer retVal = new StringBuffer();
				while (line != null) {
						retVal.append( line ).append( "\n" ) ;
						line = br.readLine() ;
				}
				br.close() ;
				return retVal.toString();
		} catch (Exception ex) {
				return null ;
		}
}

%><%

boolean isTextMode = ("true".equals(StringUtils.defaultString(request.getParameter("textMode")))) ;

// Tured off in edit_link.jsp:
boolean loadJq = (!"false".equals(StringUtils.defaultString(request.getParameter("loadJq")))) ;

String cp = request.getContextPath() ;

if (loadJq) { %>

<jsp:include page="imcms_jquery_1.7.1.js" />

<jsp:include page="imcms_jquery-ui_1.8.16.js" />

jQ.fn.outerHTML = function() {
		return $('<div>').append( this.eq(0).clone() ).html();
};

function imLog(mess) {
	try {
		if (window && window.console) {
			window.console.log(mess) ;
		}
	} catch (e) {}
}

var private_USE_INLINE_EDITING_FORMATTED = false;//('false' != imcmsGetCookie('<%= USE_INLINE_EDITING_FORMATTED %>')) ;
var private_USE_INLINE_EDITING           = (!private_USE_INLINE_EDITING_FORMATTED && 'false' != imcmsGetCookie('<%= USE_INLINE_EDITING %>')) ;
var private_USE_WIDTH                    = ('false' != imcmsGetCookie('<%= USE_WIDTH %>')) ;

jQ(document).ready(function($) {
	
	<% if (isTextMode) { %>
	<%--
	Inline edit help dialog
	--%>
	
	var $dialogHelpDiv = $('<div id="imcmsInlineEditHelpDiv" style="display:none; text-align:left;" title="<fmt:message key="global/help" /> - Inline Editing" />')
					.load('<%= AjaxServlet.getPath(cp) + "?action=getHelpTextInlineEditing" %>')
					.appendTo('body') ;
	
	$dialog = $dialogHelpDiv.dialog({
		width: 400,
		minHeight: 300,
		modal: false,
		autoOpen: false,
		dialogClass: 'imcmsAdmin',
		buttons: {
			'<fmt:message key="global/close" />' : function() {
				$(this).dialog("close");
			}
		}
	}) ;
	<% } %>
	
	<%--
	Add &width=NNN to ChangeText?......
	--%>
	$('a.imcms_text_admin').live('click', function(event) {
		event.preventDefault() ;
		var $this = $(this) ;
		var uniqueId = $this.attr('rev') ;
		openTextEditNormal($, $this, uniqueId, event) ;
	}) ;
	
	<%--
	ContextMenu UL - HTML
	--%>
	$('body').append('' +
        '<ul id="imcmsContextMenuTextField" class="imcmsContextMenu" style="display:none;">\n' +
        '	<li id="li_OPEN_INLINE_EDITING" class="' + (!private_USE_INLINE_EDITING ? 'disabled ' : '') + 'action"><a href="#OPEN_INLINE_EDITING"><fmt:message
						key="scripts/imcms4_admin_script.js.jsp/context_menu/open_inline" /></a></li>\n' +
        '	<li class="action"><a href="#OPEN_NORMAL"><fmt:message
						key="scripts/imcms4_admin_script.js.jsp/context_menu/open_normal" /></a></li>\n' +<%--
        '	<li id="li_USE_INLINE_EDITING_FORMATTED" class="' + (private_USE_INLINE_EDITING_FORMATTED ? 'active ' : '') + 'separator"><a href="#USE_INLINE_EDITING_FORMATTED"><fmt:message
						key="scripts/imcms4_admin_script.js.jsp/context_menu/use_inline_formatted" /></a></li>\n' +--%>
        '	<li id="li_USE_INLINE_EDITING" class="' + (private_USE_INLINE_EDITING ? 'active' : '') + '"><a href="#USE_INLINE_EDITING"><fmt:message
						key="scripts/imcms4_admin_script.js.jsp/context_menu/use_inline" /></a></li>\n' +
        '	<li id="li_USE_WIDTH"' + (private_USE_WIDTH ? ' class="active"' : '') + '><a href="#USE_WIDTH"><fmt:message
						key="scripts/imcms4_admin_script.js.jsp/context_menu/use_width" /></a></li>\n' +
        '	<li id="li_SHOW_HELP" class="separator"><a href="#SHOW_HELP"><fmt:message
						key="scripts/imcms4_admin_script.js.jsp/context_menu/show_help" /></a></li>\n' +
        '</ul>') ;
	
	<%--
	Set right width on contextMenu UL
	--%>
	try {
		var aTagWidest = 0 ;
		$('#imcmsContextMenuTextField').css('left', -1000).show() ;
		$('#imcmsContextMenuTextField li a').each(function() {
			var thisW = $(this).css('display', 'inline').width() ;
			aTagWidest = Math.max(aTagWidest, thisW) ;
			$(this).css('display', 'block') ;
		}) ;
		$('#imcmsContextMenuTextField').css('width', (aTagWidest + 30) + 'px').hide() ;
	} catch (e) {}
	
	<%--
	Init contextMenu
	--%>
	try {
		if ($(".imcms_text_admin").length > 0) {
			$(".imcms_text_admin").contextMenu(
				{
					menu: 'imcmsContextMenuTextField'
				}, function abort(id, el) {<%--
					console.log(
						'ABORT:\n' +
						'  id:     ' + id + '\n' +
						'  href:   ' + $(el).attr('href')
					) ;--%>
				}, function callback(id, action, $el, pos) {<%--
					console.log(
						'CALLBACK:\n' +
						'  id:     ' + id + '\n' +
						'  action: ' + action + '\n' +
						'  href:   ' + $el.attr('href') + '\n' +
						'  rev:    ' + $el.attr('rev') + '\n' +
						'  html:   ' + $el.outerHTML()
					) ;--%>
					switch (action) {
						case 'OPEN_NORMAL':
							window.setTimeout(function() {
								openTextEditNormal($, $el, id, null) ;
							}, 1000) ;
							break ;
						case 'OPEN_INLINE_EDITING':
							$('#' + id + '_container').trigger('dblclick') ;
							break ;<%--
						case 'USE_INLINE_EDITING_FORMATTED':
							private_USE_INLINE_EDITING_FORMATTED = !private_USE_INLINE_EDITING_FORMATTED ;
							imcmsSetCookie('<%= USE_INLINE_EDITING_FORMATTED %>', private_USE_INLINE_EDITING_FORMATTED + '') ;
							$('#li_USE_INLINE_EDITING_FORMATTED').removeClass('active') ;
							if (private_USE_INLINE_EDITING_FORMATTED) {
								$('#li_USE_INLINE_EDITING_FORMATTED').addClass('active') ;
								if (private_USE_INLINE_EDITING) {
									$('#li_USE_INLINE_EDITING').removeClass('active') ;
									$('#li_OPEN_INLINE_EDITING').removeClass('disabled') ;
									private_USE_INLINE_EDITING = false ;
									imcmsSetCookie('<%= USE_INLINE_EDITING %>', private_USE_INLINE_EDITING + '') ;
									$('#li_OPEN_INLINE_EDITING').addClass('disabled') ;
									editablePluginDestroy($) ;
								}
							}
							break ;--%>
						case 'USE_INLINE_EDITING':
							private_USE_INLINE_EDITING = !private_USE_INLINE_EDITING ;
							imcmsSetCookie('<%= USE_INLINE_EDITING %>', private_USE_INLINE_EDITING + '') ;
							$('#li_USE_INLINE_EDITING').removeClass('active') ;
							$('#li_OPEN_INLINE_EDITING').removeClass('disabled') ;
							if (private_USE_INLINE_EDITING) {
								$('#li_USE_INLINE_EDITING').addClass('active') ;
								editablePluginActivate($) ;
								if (private_USE_INLINE_EDITING_FORMATTED) {
									$('#li_USE_INLINE_EDITING_FORMATTED').removeClass('active') ;
									private_USE_INLINE_EDITING_FORMATTED = false ;
								}
							} else {
								$('#li_OPEN_INLINE_EDITING').addClass('disabled') ;
								editablePluginDestroy($) ;
							}
							break ;
						case 'USE_WIDTH':
							private_USE_WIDTH = !private_USE_WIDTH ;
							imcmsSetCookie('<%= USE_WIDTH %>', private_USE_WIDTH + '') ;
							$('#li_USE_WIDTH').removeClass('active') ;
							if (private_USE_WIDTH) {
								$('#li_USE_WIDTH').addClass('active') ;
							}
							break ;
						case 'SHOW_HELP':
							$dialog.dialog('open') ;
							break ;
					}
				}
			) ;
		}
	} catch (e) {}
	
}) ;

function openTextEditNormal($, $this, uniqueId, event) {
	var linkHref = $this.attr('href') ;
	if (private_USE_WIDTH) {
		var $textFieldDummy = $('#' + uniqueId + '_dummy') ;
		if (1 == $textFieldDummy.length) {
			$textFieldDummy.show(0, function() {
				var textW = $textFieldDummy.width() ;
				if (textW >= 150 && textW <= 600) {
					linkHref += '&amp;width=' + textW ;
				}
				$textFieldDummy.hide(0) ;
			}) ;
		}
	}
	imcmsOpenPath(event, linkHref.replace(/&amp;/g, '&')) ;
}

function imcmsOpenPath(event, path) {
	if (event && (event.ctrlKey || event.shiftKey)) {
		window.open(path) ;
	} else {
		document.location = path ;
	}
}

/* *******************************************************************************************
 *         Set Cookie                                                                        *
 ******************************************************************************************* */

function imcmsSetCookie(name, value) {
	var sPath = '/';
	var today = new Date();
	var expire = new Date();
	expire.setTime(today.getTime() + 1000*60*60*24*365); // 365 days
	var sCookieCont = name + "=" + escape(value);
	sCookieCont += (expire == null) ? "" : "; expires=" + expire.toGMTString();
	sCookieCont += "; path=" + sPath;
	document.cookie = sCookieCont;
}

/* *******************************************************************************************
 *         Get Cookie                                                                        *
 ******************************************************************************************* */

function imcmsGetCookie(Name) {
	var search = Name + "=";
	if (document.cookie.length > 0) {
		var offset = document.cookie.indexOf(search);
		if (offset != -1) {
			offset += search.length;
			end = document.cookie.indexOf(";", offset);
			if (end == -1) {
				end = document.cookie.length;
			}
			return unescape(document.cookie.substring(offset, end));
		}
	}
	return null ;
}

<% } %><%--


// Make new jQ version of jQuery: <%

Perl5Util re = new Perl5Util() ;

String jQueryMain = getURLcontent("http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js", Imcms.UTF_8_ENCODING) ;
String jQueryUi   = getURLcontent("http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js", Imcms.UTF_8_ENCODING) ;

jQueryMain = re.substitute("s#jQuery#jQ#g", jQueryMain).replace("* jQ", "* jQuery") ;
jQueryUi   = re.substitute("s#jQuery#jQ#g", jQueryUi).replace("* jQ", "* jQuery") ;

%>

<%= jQueryMain %>

<%= jQueryUi %>


//Did some playing. Thought I'd continue later.


jQ(document).ready(function($) {
	
	$('head').append('<link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" />\n') ;
	
	$('<div id="adminPanelFixed" />').css({
		'position' : 'fixed',
		'padding' : '0',
		'top' : '-2px',
		'left' : '500px',
		'width' : $('#adminPanelDiv .adminPanelTable').width() + 'px',
		'height' : ($('#adminPanelDiv .adminPanelTable').height()) + 'px'
	}).prependTo('body') ;
	
	$('#adminPanelDiv .adminPanelTable').show('transfer',{ to: '#adminPanelFixed', className: 'ui-effects-transfer' }, 2000, function() {
		$('#adminPanelFixed').html($('#adminPanelDiv').html()) ;
		$('#adminPanelDiv').html('').hide() ;
		//alert('transfered!') ;
	}) ;
	
	
}) ;


--%>
<vel:velocity>

<%-- Moved from inPage_admin.html --%>
function imcmsTargetNewWindow() {
	var exampelWindow = window.open("", "Exempelmall", "scrollbars=yes,toolbar=0,resizable=yes,location=0,directories=0,status=0,menubar=0,height=500,width=800") ;
	document.changePageForm.target = "Exempelmall" ;
	exampelWindow.focus() ;
}
function imcmsResetTarget() {
	document.changePageForm.target = "" ;
}

<%-- Moved from adminbuttons.jsp --%>
function openHelpW(helpDocName){
	window.open('@documentationurl@/Help?name=' + helpDocName + '&lang=$language', 'imcmsHelpWin') ;
}

</vel:velocity>