<%@ page
	
	import="imcode.server.Imcms,
	        java.net.URL,
	        java.net.URLConnection,
	        java.io.InputStream,
	        java.io.InputStreamReader,
	        java.io.BufferedReader,
	        org.apache.oro.text.perl.Perl5Util, org.apache.commons.lang.StringUtils"
	
	contentType="text/javascript"
	pageEncoding="UTF-8"
	
%><%@ taglib uri="imcmsvelocity" prefix="vel"
%><%!

private final static int CONNECTION_TIMEOUT_MILLIS = 3000 ;

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

boolean loadJq = (!"false".equals(StringUtils.defaultString(request.getParameter("loadJq")))) ;

if (loadJq) { %>

<jsp:include page="imcms_jquery_1.4.2.js" />

<jsp:include page="imcms_jquery-ui_1.8.5.js" />

jQ(document).ready(function($) {
	$('a.imcms_text_admin').live('click', function(event) {
		event.preventDefault() ;
		var $this = $(this) ;
		var uniqueId = $this.attr('rev') ;
		var linkHref = $this.attr('href') ;
		var $textFieldDummy = $('#imcms_text_field_dummy_' + uniqueId) ;
		if (1 == $textFieldDummy.length) {
			$textFieldDummy.show(0, function() {
				var textW = $textFieldDummy.width() ;
				if (textW >= 150 && textW <= 600) {
					linkHref += '&amp;width=' + textW ;
				}
				$textFieldDummy.hide(0) ;
			}) ;
		}
		imcmsOpenPath(event, linkHref.replace(/&amp;/g, '&')) ;
	}) ;
}) ;

function imcmsOpenPath(event, path) {
	if (event && (event.ctrlKey || event.shiftKey)) {
		window.open(path) ;
	} else {
		document.location = path ;
	}
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