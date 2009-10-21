<%@ page
	contentType="text/javascript"
%>
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
			'ContextMenu',
			'TableOperations',
			'CharacterMap',
			'Stylist',
			'ListType',
			'InsertAnchor',
			'SuperClean'
		];
    // THIS BIT OF JAVASCRIPT LOADS THE PLUGINS, NO TOUCHING  :)
    if(!Xinha.loadPlugins(xinha_plugins, xinha_init)) return;

    xinha_config = xinha_config ? xinha_config() : new Xinha.Config();
    
    xinha_config.pageStyleSheets = [
        "<%= request.getContextPath() %>/css/editor_default.css",
        "<%= request.getContextPath() %>/css/editor_default_classes.css"
    ] ;
    
    xinha_config.stylistLoadStylesheet("<%= request.getContextPath() %>/css/editor_default_classes.css") ;
		
		xinha_config.SuperClean.show_dialog = true ;
		xinha_config.SuperClean.filters = {
			'remove_some_formatting': {label:Xinha._lc('Clean up Word formatting, but save text formatting', 'SuperClean'), checked:true},
			'remove_word_formatting': {label:Xinha._lc('Clean up Word formatting only', 'SuperClean'), checked:false},
			'remove_formatting': {label:Xinha._lc('Remove all formatting', 'SuperClean'), checked:false}
		} ;
		
		xinha_config.toolbar = [
			["popupeditor"],
			["separator","formatblock","separator","bold","italic","underline","strikethrough"],
			["separator","subscript","superscript"],
			["separator","justifyleft","justifycenter","justifyright","justifyfull"],
			["separator","insertorderedlist","insertunorderedlist","outdent","indent","separator","createlink","insertimage"],
			["separator","toggleborders","selectall"], (Xinha.is_gecko ? [] : ["saveas"]),
			["print","separator","showhelp","about","linebreak"],
			["htmlmode","separator","undo","redo"], (Xinha.is_gecko ? [] : ["separator","cut","copy","paste","overwrite"]),
			["separator","killword","clearfonts"]
		] ;
		
		xinha_config.formatblock = {
			"&mdash; format &mdash;": "",
			"Normal"   : "p",
			"Heading 1": "h1",
			"Heading 2": "h2",
			"Heading 3": "h3",
			"Heading 4": "h4",
			"Heading 5": "h5",
			"Heading 6": "h6"
		} ;
		
		xinha_config.CharacterMap.mode = "panel" ;
		xinha_config.ListType.mode = "panel" ;
		
		xinha_config.panel_dimensions = {
	    left:   "180px",
	    right:  "180px",
	    top:    "100px",
	    bottom: "100px"
	  } ;
		
		xinha_config.showLoading = true ;
		xinha_config.flowToolbars = false ;
		xinha_config.killWordOnPaste = true ;

    var serverBase = location.href.replace(/(https?:\/\/[^\/]*)\/.*/, '$1') ;
    xinha_config.baseHref = serverBase;
    xinha_editors = xinha_editors ? xinha_editors : Xinha.makeEditors([ 'text' ], xinha_config, xinha_plugins);
    <% if (null!=request.getParameter("html")) { %>
    Xinha.startEditors(xinha_editors);

    setTimeout(function() {
            try {
                xinha_editors.text.activateEditor();
                xinha_editors.text.focusEditor() ;
            } catch (e) {}
        }, 500);
    <% } %>
}

window.onload = xinha_init;



