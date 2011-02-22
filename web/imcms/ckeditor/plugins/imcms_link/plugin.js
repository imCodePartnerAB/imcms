
(function() {
	var commandName = 'imcms_link' ;
	
	var commandDef  = {
		exec : function(editor) {
			
			var selection = editor.getSelection() ;
			var linkElement = null ;
			var selectedText = '' ;
			if (CKEDITOR.env.ie) {
				selection.unlock(true) ;
				selectedText = selection.getNative().createRange().text ;
			} else {
				selectedText = selection.getNative() ;
			}

			// Fill in all the relevant fields if there's already one link selected.
			if ( ( linkElement = getSelectedLink(editor)) && linkElement.hasAttribute( 'href' ) ) {
				selection.selectElement( linkElement );
				//console.log('selection.selectElement( linkElement );') ;
			} else if ( ( linkElement = selection.getSelectedElement() ) && linkElement.is('img')
					&& linkElement.getAttribute( '_cke_real_element_type' )
					&& linkElement.getAttribute( '_cke_real_element_type' ) == 'anchor' ) {
				this.fakeObj = linkElement;
				linkElement = editor.restoreRealElement( this.fakeObj );
				selection.selectElement( this.fakeObj );
				//console.log('linkElement.is(\'img\')') ;
			} else {
				linkElement = null;
				//console.log('linkElement = null;') ;
				//console.log('selectedText = ' + selectedText + ';') ;
			}

			//element = parseLink(editor, element) ;
			
			var outparam = null ;
			if (linkElement) {
				var oldHref = linkElement.getAttribute('href') ;
				var otherParams = '' ;
				//console.log('linkElement: ' + linkElement.getOuterHtml()) ;
				for (var o in CKEDITOR_otherLinkParams) {
					var otherLinkParamName  = CKEDITOR_otherLinkParams[o] ;
					var otherLinkParamValue = linkElement.getAttribute(otherLinkParamName) || linkElement.getAttribute('_cke_pa_' + otherLinkParamName) ;
					//var otherLinkParamValue = linkElement.$.attributes.getNamedItem( otherLinkParamName ) ;
					//console.log('param: ' + otherLinkParamName + ' = ' + otherLinkParamValue) ;
					if (null != otherLinkParamValue && '' != otherLinkParamValue) {
						otherParams += ' ' + otherLinkParamName + '="' + otherLinkParamValue + '"' ;
					}
				}
				outparam = {
					'TYPE'   : (/^\//.test(oldHref) ? 1 : oldHref.indexOf('://') != -1 ? 2 : 0),
					'HREF'   : oldHref,
					'TARGET' : linkElement.getAttribute('target'),
					'TITLE'  : linkElement.getAttribute('title'),
					'CLASS'  : linkElement.getAttribute('class'),
					'STYLE'  : linkElement.getAttribute('style'),
					'OTHER'  : otherParams
				} ;
			}
			
			var queryString = '' ;
			
			for (var p in outparam) {
				if (outparam[p]) {
					queryString += '&' + p + '=' + encodeURIComponent(outparam[p]) ;
				}
			}
			
			//console.log('queryString: ' + queryString) ;
			
			var returnLink = window.showModalDialog(CKEDITOR_imcmsLinkEditPath + queryString,null,'dialogWidth:800px;dialogHeight:600px;center:yes;resizable:yes;help:no') ;
			
			if (!returnLink || '' == returnLink.HREF) { // user must have pressed Cancel
				return false ;
			}
			
			//console.log('linkElement: ' + linkElement + '\nreturnLink.HREF: ' + returnLink.HREF) ;
			
			var link = linkElement ;
			
			//console.log('link.href (before): ' + (!link ? 'null' : link.getAttribute('href'))) ;
			
			if (!link) { // If the <a/> tag doesn't exists in the editor - create it.
				if ('' == selectedText) {
					selectedText = returnLink.TITLE || returnLink.HREF ;
				}
				link = CKEDITOR.dom.element.createFromHtml('<a>' + selectedText + '</a>') ;
				link.setAttribute('href', returnLink.HREF) ;
				editor.insertElement(link) ;
				//console.log('link (not exist) - Created: ' + link) ;
			} else { // If the <a/> tag exists in the editor (var link = linkElement ;) - change it.
				link.setAttribute('href', returnLink.HREF) ;
				//console.log('link (exist): ' + link) ;
			}
			link.removeAttribute('_cke_saved_href') ;
			
			//console.log('link.href (after): ' + link.getAttribute('href')) ;
			
			var linkType = returnLink.TYPE ;
			var hasTarget = (1 == linkType || 2 == linkType) ;
			
			for (var parameter in returnLink) {
				var parameterValue = returnLink[parameter] ;
				//console.log(parameter + ' : ' + parameterValue) ;
				switch (parameter) {
					case 'TITLE':
						if ('' != parameterValue) link.setAttribute('title', parameterValue) ;
						break ;
					case 'TARGET':
						if (hasTarget && null != parameterValue) {
							link.setAttribute('target', parameterValue) ;
						}
						break ;
					case 'CLASS':
						if ('' != parameterValue) link.setAttribute('class', parameterValue) ;
						break ;
					case 'STYLE':
						if ('' != parameterValue) link.setAttribute('style', parameterValue) ;
						break ;
					case 'OTHER':
						if ('' != parameterValue) {
							for (var i in CKEDITOR_otherLinkParams) {
								var returnOtherLinkParamName  = CKEDITOR_otherLinkParams[i] ;
								var returnOtherLinkParamValue = paramFromString(returnOtherLinkParamName, returnLink.OTHER) ;
								//console.log('PARSE OTHER: ' + returnOtherLinkParamName + ':' + returnOtherLinkParamValue) ;
								if (null != returnOtherLinkParamValue && '' != returnOtherLinkParamValue) {
									//console.log('SET OTHER: ' + returnOtherLinkParamName + '="' + returnOtherLinkParamValue + '"') ;
									link.setAttribute(returnOtherLinkParamName, returnOtherLinkParamValue) ;
									link.removeAttribute('_cke_pa_' + returnOtherLinkParamName) ;
								}
							}
						}
						break ;
				}
			}
			if ('null' == link.getAttribute('target') || '' == link.getAttribute('target')) {
				link.removeAttribute('target') ;
			}
			//console.log('link.getOuterHtml():\n' + link.getOuterHtml()) ;
			//console.log('editor.getSnapshot():\n' + editor.getSnapshot()) ;
			//console.log('editor.getData():\n' + editor.getData()) ;
			
		}
	} ;
	
	CKEDITOR.plugins.add(commandName, {
		init : function(editor) {
			editor.addCommand(commandName, commandDef) ;
			editor.ui.addButton('ImcmsLink',{
				label   : editor.lang.link.toolbar, 
				icon    : this.path + 'images/toolBarButton.png',
				command : commandName
			}) ;
			
			editor.addCommand('anchor', new CKEDITOR.dialogCommand('anchor')) ;
			editor.addCommand('unlink', new CKEDITOR.unlinkCommand()) ;
			
			editor.ui.addButton('Unlink', {
				label   : editor.lang.unlink,
				command : 'unlink'
			}) ;
			editor.ui.addButton('Anchor', {
				label   : editor.lang.anchor.toolbar,
				command : 'anchor'
			}) ;
			
			CKEDITOR.dialog.add('anchor', this.path + 'dialogs/anchor.js') ;
			
			// Add the CSS styles for anchor placeholders.
			editor.addCss(
			'img.cke_anchor {' +
			' background-image: url(' + CKEDITOR.getUrl(this.path + 'images/anchor.gif' ) + ');' +
			' background-position: center center;' +
			' background-repeat: no-repeat;' +
			' border: 1px solid #a9a9a9;' +
			' width: 18px !important;' +
			' height: 18px !important;' +
			'}\n' +
			'a.cke_anchor {' +
			' background-image: url(' + CKEDITOR.getUrl(this.path + 'images/anchor.gif' ) + ');' +
			' background-position: 0 center;' +
			' background-repeat: no-repeat;' +
			' border: 1px solid #a9a9a9;' +
			' padding-left: 18px;' +
			'}'
			) ;
			
			editor.on('selectionChange', function(evt) {
				var command = editor.getCommand('unlink'),
				element = evt.data.path.lastElement && evt.data.path.lastElement.getAscendant('a', true) ;
				if (element && element.getName() == 'a' && element.getAttribute('href')) {
					command.setState( CKEDITOR.TRISTATE_OFF );
				} else {
					command.setState( CKEDITOR.TRISTATE_DISABLED );
				}
			}) ;
			
			editor.on('doubleclick', function(evt) {
				var element = CKEDITOR.plugins.link.getSelectedLink( editor ) || evt.data.element;
				if (element.is('a')) {
					if (element.getAttribute('name') && !element.getAttribute('href')) {
						evt.data.dialog = 'anchor'
					} else {
						editor.execCommand(commandName) ;
					}
				} else if ( element.is('img') && element.getAttribute('_cke_real_element_type') == 'anchor') {
					evt.data.dialog = 'anchor';
				}
			}) ;
			
			if (editor.addMenuItems) {
				editor.addMenuItems({
					anchor : {
						label   : editor.lang.anchor.menu,
						command : 'anchor',
						group   : 'anchor'
					},
					link : {
						label   : editor.lang.link.menu,
						command : commandName,
						group   : 'link',
						order   : 1
					},
					unlink : {
						label   : editor.lang.unlink,
						command : 'unlink',
						group   : 'link',
						order   : 5
					}
				}) ;
			}
			
			if (editor.contextMenu) {
				editor.contextMenu.addListener(function(element, selection) {
						if (!element || element.isReadOnly()) {
							return null ;
						}
						var isAnchor = (element.is('img') && element.getAttribute('_cke_real_element_type') == 'anchor') ;
						if (!isAnchor) {
							if (!(element = CKEDITOR.plugins.link.getSelectedLink(editor))) {
								return null ;
							}
							isAnchor = (element.getAttribute('name') && !element.getAttribute('href')) ;
						}
						return isAnchor ?
								{ anchor : CKEDITOR.TRISTATE_OFF } :
								{ link : CKEDITOR.TRISTATE_OFF, unlink : CKEDITOR.TRISTATE_OFF } ;
				}) ;
			}
		},
		
		afterInit : function(editor) {
			var dataProcessor = editor.dataProcessor,
			dataFilter = dataProcessor && dataProcessor.dataFilter ;
			if (dataFilter) {
				dataFilter.addRules({
					elements : {
						a : function( element ) {
							var attributes = element.attributes;
							if (attributes.name && !attributes.href) {
								return editor.createFakeParserElement(element, 'cke_anchor', 'anchor') ;
							}
						}
					}
				}) ;
			}
		},
		
		requires : ['fakeobjects']/*
		}*/
	}) ;
	
})() ;

function getSelectedLink(editor ) {
	try {
		var selection = editor.getSelection() ;
		if (selection.getType() == CKEDITOR.SELECTION_ELEMENT) {
			var selectedElement = selection.getSelectedElement() ;
			if (selectedElement.is('a')) {
				return selectedElement ;
			}
		}
		var range = selection.getRanges(true)[0] ;
		range.shrink(CKEDITOR.SHRINK_TEXT) ;
		var root = range.getCommonAncestor() ;
		return root.getAscendant('a', true) ;
	} catch(e) {
		return null ;
	}
}

function paramFromString(paramName, inputString) {
	var pattern = paramName + "=([\\\"'])([^\\1]+?)\\1" ;
	var re      = new RegExp(pattern, 'gi') ;
	var results = re.exec(inputString) ;
	if (null != results) {
		return results[2] ;
	}
	return '' ;
}

CKEDITOR.unlinkCommand = function(){};
CKEDITOR.unlinkCommand.prototype = {
	exec : function (editor) {
		var selection = editor.getSelection(),
			bookmarks = selection.createBookmarks(),
			ranges = selection.getRanges(),
			rangeRoot,
			element;

		for ( var i = 0 ; i < ranges.length ; i++ ) {
			rangeRoot = ranges[i].getCommonAncestor( true );
			element = rangeRoot.getAscendant( 'a', true );
			if ( !element )
				continue;
			ranges[i].selectNodeContents( element );
		}

		selection.selectRanges( ranges );
		editor.document.$.execCommand( 'unlink', false, null );
		selection.selectBookmarks( bookmarks );
	},

	startDisabled : true
} ;


	// Loads the parameters in a selected link to the link dialog fields.
	var javascriptProtocolRegex = /^javascript:/,
		emailRegex = /^mailto:([^?]+)(?:\?(.+))?$/,
		emailSubjectRegex = /subject=([^;?:@&=$,\/]*)/,
		emailBodyRegex = /body=([^;?:@&=$,\/]*)/,
		anchorRegex = /^#(.*)$/,
		urlRegex = /^((?:http|https|ftp|news):\/\/)?(.*)$/,
		selectableTargets = /^(_(?:self|top|parent|blank))$/,
		encodedEmailLinkRegex = /^javascript:void\(location\.href='mailto:'\+String\.fromCharCode\(([^)]+)\)(?:\+'(.*)')?\)$/,
		functionCallProtectedEmailLinkRegex = /^javascript:([^(]+)\(([^)]+)\)$/;

	var popupRegex =
		/\s*window.open\(\s*this\.href\s*,\s*(?:'([^']*)'|null)\s*,\s*'([^']*)'\s*\)\s*;\s*return\s*false;*\s*/;
	var popupFeaturesRegex = /(?:^|,)([^=]+)=(\d+|yes|no)/gi;

	function parseLink( editor, element ) {
		var href = ( element  && ( element.getAttribute( '_cke_saved_href' ) || element.getAttribute( 'href' ) ) ) || '',
		 	javascriptMatch,
			emailMatch,
			anchorMatch,
			urlMatch,
			retval = {};

		if ( ( javascriptMatch = href.match( javascriptProtocolRegex ) ) )
		{
			if ( emailProtection == 'encode' )
			{
				href = href.replace( encodedEmailLinkRegex,
						function ( match, protectedAddress, rest )
						{
							return 'mailto:' +
							       String.fromCharCode.apply( String, protectedAddress.split( ',' ) ) +
							       ( rest && unescapeSingleQuote( rest ) );
						});
			}
			// Protected email link as function call.
			else if ( emailProtection )
			{
				href.replace( functionCallProtectedEmailLinkRegex, function( match, funcName, funcArgs )
				{
					if ( funcName == compiledProtectionFunction.name )
					{
						retval.type = 'email';
						var email = retval.email = {};

						var paramRegex = /[^,\s]+/g,
							paramQuoteRegex = /(^')|('$)/g,
							paramsMatch = funcArgs.match( paramRegex ),
							paramsMatchLength = paramsMatch.length,
							paramName,
							paramVal;

						for ( var i = 0; i < paramsMatchLength; i++ )
						{
							paramVal = decodeURIComponent( unescapeSingleQuote( paramsMatch[ i ].replace( paramQuoteRegex, '' ) ) );
							paramName = compiledProtectionFunction.params[ i ].toLowerCase();
							email[ paramName ] = paramVal;
						}
						email.address = [ email.name, email.domain ].join( '@' );
					}
				} );
			}
		}

		if ( !retval.type )
		{
			if ( ( anchorMatch = href.match( anchorRegex ) ) )
			{
				retval.type = 'anchor';
				retval.anchor = {};
				retval.anchor.name = retval.anchor.id = anchorMatch[1];
			}
			// Protected email link as encoded string.
			else if ( ( emailMatch = href.match( emailRegex ) ) )
			{
				var subjectMatch = href.match( emailSubjectRegex ),
					bodyMatch = href.match( emailBodyRegex );

				retval.type = 'email';
				var email = ( retval.email = {} );
				email.address = emailMatch[ 1 ];
				subjectMatch && ( email.subject = decodeURIComponent( subjectMatch[ 1 ] ) );
				bodyMatch && ( email.body = decodeURIComponent( bodyMatch[ 1 ] ) );
			}
			// urlRegex matches empty strings, so need to check for href as well.
			else if (  href && ( urlMatch = href.match( urlRegex ) ) )
			{
				retval.type = 'url';
				retval.url = {};
				retval.url.protocol = urlMatch[1];
				retval.url.url = urlMatch[2];
			}
			else
				retval.type = 'url';
		}

		// Load target and popup settings.
		if ( element )
		{
			var target = element.getAttribute( 'target' );
			retval.target = {};
			retval.adv = {};

			// IE BUG: target attribute is an empty string instead of null in IE if it's not set.
			if ( !target )
			{
				var onclick = element.getAttribute( '_cke_pa_onclick' ) || element.getAttribute( 'onclick' ),
					onclickMatch = onclick && onclick.match( popupRegex );
				if ( onclickMatch )
				{
					retval.target.type = 'popup';
					retval.target.name = onclickMatch[1];

					var featureMatch;
					while ( ( featureMatch = popupFeaturesRegex.exec( onclickMatch[2] ) ) )
					{
						if ( featureMatch[2] == 'yes' || featureMatch[2] == '1' )
							retval.target[ featureMatch[1] ] = true;
						else if ( isFinite( featureMatch[2] ) )
							retval.target[ featureMatch[1] ] = featureMatch[2];
					}
				}
			}
			else
			{
				var targetMatch = target.match( selectableTargets );
				if ( targetMatch )
					retval.target.type = retval.target.name = target;
				else
				{
					retval.target.type = 'frame';
					retval.target.name = target;
				}
			}

			var me = this;
			var advAttr = function( inputName, attrName )
			{
				var value = element.getAttribute( attrName );
				if ( value !== null )
					retval.adv[ inputName ] = value || '';
			};
			advAttr( 'advId', 'id' );
			advAttr( 'advLangDir', 'dir' );
			advAttr( 'advAccessKey', 'accessKey' );
			advAttr( 'advName', 'name' );
			advAttr( 'advLangCode', 'lang' );
			advAttr( 'advTabIndex', 'tabindex' );
			advAttr( 'advTitle', 'title' );
			advAttr( 'advContentType', 'type' );
			advAttr( 'advCSSClasses', 'class' );
			advAttr( 'advCharset', 'charset' );
			advAttr( 'advStyles', 'style' );
		}

		// Find out whether we have any anchors in the editor.
		// Get all IMG elements in CK document.
		var elements = editor.document.getElementsByTag( 'img' ),
			realAnchors = new CKEDITOR.dom.nodeList( editor.document.$.anchors ),
			anchors = retval.anchors = [];

		for ( var i = 0; i < elements.count() ; i++ )
		{
			var item = elements.getItem( i );
			if ( item.getAttribute( '_cke_realelement' ) && item.getAttribute( '_cke_real_element_type' ) == 'anchor' )
				anchors.push( editor.restoreRealElement( item ) );
		}

		for ( i = 0 ; i < realAnchors.count() ; i++ )
			anchors.push( realAnchors.getItem( i ) );

		for ( i = 0 ; i < anchors.length ; i++ )
		{
			item = anchors[ i ];
			anchors[ i ] = { name : item.getAttribute( 'name' ), id : item.getAttribute( 'id' ) };
		}

		// Record down the selected element in the dialog.
		//this._.selectedElement = element;

		return retval;
	}