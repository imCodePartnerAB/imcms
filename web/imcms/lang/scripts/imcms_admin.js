/* *******************************************************************************************
 *         Functions                                                                         *
 ******************************************************************************************* */

function focusField(theFormName, theElementName) {
	var f = (!isNaN(theFormName)) ? eval("document.forms[" + theFormName + "]") : eval("document.forms." + theFormName);
	if (f) {
		var el = eval("f." + theElementName);
		if (el) el.focus();
	}
}

function getParam(attrib) {			// get querystring-parameters from document.location
	var sParams = location.search;
	var retVal = "";
	var params = new Array();
	if (sParams.indexOf('?') != -1) {
		var pairs = sParams.substring(1, sParams.length).split('&');
		for (var i=0; i<pairs.length; i++) {
			nameVal = pairs[i].split('=');
			if (nameVal[0] == attrib) {
				retVal = nameVal[1];
			}
		}
	}
	return retVal;
}

/* *******************************************************************************************
 *         POPUP functions                                                                   *
 ******************************************************************************************* */

function openHelpW(id){
    var helpMetaId = parseInt(<? install/htdocs/helpDocumentationMetaIdStartIndex ?>) + id;
	window.open("@documentationurl@/GetDoc?meta_id=" + helpMetaId,"help");
}

function popWinOpen(winW,winH,sUrl,sName,iResize,iScroll) {
	if (screen) {
		if ((screen.height - winH) < 150) {
			var winX = (screen.width - winW) / 2;
			var winY = 0;
		} else {
			var winX = (screen.width - winW) / 2;
			var winY = (screen.height - winH) / 2;
		}
		var popWindow = window.open(sUrl,sName,"resizable=" + iResize + ",menubar=0,scrollbars=" + iScroll + ",width=" + winW + ",height=" + winH + ",top=" + winY + ",left=" + winX + "");
		if (popWindow) popWindow.focus();
	} else {
		window.open(sUrl,sName,"resizable=yes,menubar=0,scrollbars=" + iScroll + ",width=" + winW + ",height=" + winH);
	}
}

