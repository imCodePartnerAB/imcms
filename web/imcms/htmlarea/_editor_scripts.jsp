<%@ page
	
%><%@ include file="_editor_methods.jsp" %><%

if (isHtmlAreaSupported) { %>
<script type="text/javascript">
_editor_url = "<%= EDITOR_URL %>";
_editor_lang = "<%= isLangSwe ? "se" : "en" %>";
var editorTextareaId = "txtCont" ;
</script>

<script type="text/javascript" src="<%= EDITOR_URL %>htmlarea.js"></script>

<script type="text/javascript">
HTMLArea.loadPlugin("TableOperations");
HTMLArea.loadPlugin("ContextMenu");

var editor = null;
openEditor = true ;

function initEditor() {
  editor = new HTMLArea(editorTextareaId);
	editor.config.metaId      = <%= EDITED_META %> ;
	editor.config.servletPath = "<%= SERVLET_PATH %>" ;
	editor.config.contextPath = "<%= request.getContextPath() %>" ;
	editor.registerPlugin(TableOperations);
	editor.registerPlugin("ContextMenu");
	editor.config.pageStyle =
	'body { background-color:#ffffff; color:#000000; font: x-small verdana,sans-serif }';

	editor.config.toolbar = [
	[ "copy", "cut", "paste", "separator",
	  "space", "undo", "redo", "separator",
	  "bold", "italic", "underline", "separator", "subscript", "superscript", "separator",
	  "justifyleft", "justifycenter", "justifyright", "justifyfull", "separator",
	  "insertorderedlist", "insertunorderedlist", "outdent", "indent", "separator",
	  "createlink", "inserttable", "separator",
	  "insertimage", "separator",
	  "showhelp", "about" ]
	];
  setTimeout(function() {
    editor.generate();
		lockMode(true,2) ;
  }, 300);
  return false;
}

function showHideHtmlArea(show) {
	if (document.getElementById) {
		if (document.getElementById(editorTextareaId)) {
			var isOn = (document.getElementById(editorTextareaId).style.display == "none") ;
			if (!show && isOn && document.getElementById("htmlarea")) {
				document.getElementById("htmlarea").innerHTML     = "" ;
				document.getElementById("htmlarea").style.display = "none" ;
				var o = document.getElementById("htmlarea") ;
				//alert(o.outerHTML) ;
				document.getElementById("htmlarea").id            = "" ;
				//alert(o.outerHTML) ;
				document.getElementById(editorTextareaId).style.display = "block" ;
				document.getElementById(editorTextareaId).value         = fixHTML(editor.getHTML()) ;
				lockMode(false,1) ;
			} else if (show && !isOn) {
				if (eval("editor")) {
					editor.generate() ;
					window.status = "editor.generate()" ;
				} else {
					initEditor() ;
					window.status = "initEditor()" ;
				}
				lockMode(true,2) ;
				setEditorSize() ;
			}
		}
	}
}
/*
function showHideHtmlArea() {
	if (document.getElementById) {
		//alert(document.getElementById(editorTextareaId) +"\n\n"+ document.getElementById("htmlarea")) ;
		if (document.getElementById(editorTextareaId) && document.getElementById("htmlarea")) {
			var isOn = (document.getElementById(editorTextareaId).style.display == "none") ;
			if (isOn) {
				document.getElementById("htmlarea").style.display       = "none" ;
				document.getElementById(editorTextareaId).style.display = "block" ;
				document.getElementById(editorTextareaId).value = fixHTML(editor.getHTML()) ;
				lockMode(false) ;
			} else {
				document.getElementById("htmlarea").style.display       = "block" ;
				document.getElementById(editorTextareaId).style.display = "none" ;
				editor.setHTML(document.getElementById(editorTextareaId).value) ;
				editor.generate() ;
				lockMode(true) ;
			}
		}
	}
}*/

function toggleEditorOnOff(on) {
	if (on) {
		setCookie("imcms_hide_editor", "true") ;
		document.getElementById("editorOnOffBtn1").style.display = "none" ;
		document.getElementById("editorOnOffBtn0").style.display = "block" ;
	} else {
		setCookie("imcms_hide_editor", "") ;
		document.getElementById("editorOnOffBtn1").style.display = "block" ;
		document.getElementById("editorOnOffBtn0").style.display = "none" ;
	}
}

function lockMode(doLock,mode) {
	var f = document.forms[0] ;
	f.format_type[mode].checked = true ;
	f.format_type[0].disabled   = (doLock) ? true : false ;
}

/* *******************************************************************************************
 *         Set Cookie                                                                        *
 ******************************************************************************************* */

function setCookie(name, value) {
	var sPath = '/';
	var today = new Date();
	var expire = new Date();
	expire.setTime(today.getTime() + 1000*60*60*24*365); // 365 days
	var sCookieCont = name + "=" + escape(value);
	sCookieCont += (expire == null) ? "" : "\; expires=" + expire.toGMTString();
	sCookieCont += "; path=" + sPath;
	document.cookie = sCookieCont;
}

/* *******************************************************************************************
 *         Get Cookie                                                                        *
 ******************************************************************************************* */

function getCookie(Name) {
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
}
</script><%
} %>