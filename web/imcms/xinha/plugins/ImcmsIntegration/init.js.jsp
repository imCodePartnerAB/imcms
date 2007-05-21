<%@page contentType="text/javascript"  %>
xinha_editors = null;
xinha_init    = null;
xinha_config  = null;
xinha_plugins = null;

// This contains the names of textareas we will make into Xinha editors
xinha_init = xinha_init ? xinha_init : function()
{
    xinha_plugins = xinha_plugins ? xinha_plugins :
                    [
                            'ImcmsIntegration',
                            'CharCounter',
                            'CharacterMap',
                            'ContextMenu',
                            'Stylist'
                            ];
    // THIS BIT OF JAVASCRIPT LOADS THE PLUGINS, NO TOUCHING  :)
    if(!Xinha.loadPlugins(xinha_plugins, xinha_init)) return;

    xinha_config = xinha_config ? xinha_config() : new Xinha.Config();
    
    xinha_config.pageStyle = "@import url(<%= request.getContextPath() %>/css/editor_default.css);";
    
    xinha_config.toolbar =
		[
			["popupeditor"],
			["separator","formatblock","separator","bold","italic","underline","strikethrough"],
			["separator","subscript","superscript"],
			["linebreak","separator","justifyleft","justifycenter","justifyright"],
			["separator","insertorderedlist","insertunorderedlist","outdent","indent"],
			["separator","createlink","insertimage"],
			["linebreak","separator","undo","redo","separator","selectall"], (Xinha.is_gecko ? [] : ["cut","copy","paste","overwrite"]),
			["separator","killword","clearfonts","removeformat"],
			["separator","htmlmode","showhelp","about"]
		];
		
		xinha_config.formatblock =
		{
			"&mdash; format &mdash;": "",
			"Normal"   : "p",
			"Heading 1": "h1",
			"Heading 2": "h2",
			"Heading 3": "h3",
			"Heading 4": "h4",
			"Heading 5": "h5",
			"Heading 6": "h6"
		};

    var serverBase = location.href.replace(/(https?:\/\/[^\/]*)\/.*/, '$1') ;
    xinha_config.baseHref = serverBase;
    xinha_editors = xinha_editors ? xinha_editors : Xinha.makeEditors([ 'text' ], xinha_config, xinha_plugins);
    <% if (null!=request.getParameter("html")) { %>
    Xinha.startEditors(xinha_editors);

    setTimeout(function() {
            xinha_editors.text.activateEditor();
            xinha_editors.text.focusEditor() ;
        }, 500);
    <% } %>
}

window.onload = xinha_init;




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
