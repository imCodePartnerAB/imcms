/* *******************************************************************************************
 *         Browser sniffer                                                                   *
 ******************************************************************************************* */

var platf     = navigator.platform;
var ua        = navigator.userAgent;

var hasDocumentLayers = (document.layers) ? 1 : 0;
var hasDocumentAll    = (document.all) ? 1 : 0;
var hasGetElementById = (document.getElementById) ? 1 : 0;

var isGecko   = inStr(ua,'Gecko');
var isOpera   = inStr(ua,'Opera');
var isWindows = inStr(platf,'Win32');
var isMac     = inStr(platf,'Mac');

var isIE55    = (isWindows && hasDocumentAll && hasGetElementById && (inStr(ua,'MSIE 5.5') || inStr(ua,'MSIE 6.0') || inStr(ua,'MSIE 6.5') || inStr(ua,'MSIE 7.0')) && !isOpera) ? 1 : 0;

function inStr(str,val,cas) {
	var ret;
	if (cas) { /* Case sensitive */
		ret = (str.indexOf(val) != -1) ? true : false;
	} else { /* Not Case sensitive */
		str = str.toUpperCase();
		val = val.toUpperCase();
		ret = (str.indexOf(val) != -1) ? true : false;
	}
	return ret;
}

/* *******************************************************************************************
 *         Functions                                                                         *
 ******************************************************************************************* */

function singleclicked() {
    if (!('clicked' in this)) {
        this.clicked = 1;
        return true;
    } else {
        return false ;
    }
}

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
		window.open(sUrl,sName,"resizable=" + iResize + ",menubar=0,scrollbars=" + iScroll + ",width=" + winW + ",height=" + winH);
	}
}

