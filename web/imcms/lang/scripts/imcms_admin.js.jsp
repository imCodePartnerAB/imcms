<%@page contentType="text/javascript"%>
<%@taglib prefix="vel" uri="imcmsvelocity"%>
<vel:velocity>

/* *******************************************************************************************
 *         Browser sniffer                                                                   *
 ******************************************************************************************* */

var platf     = navigator.platform;
var ua        = navigator.userAgent;

var hasDocumentLayers = (document.layers);
var hasDocumentAll    = (document.all);
var hasGetElementById = (document.getElementById);

var isGecko   = inStr(ua,"Gecko");
var isOpera   = inStr(ua,"Opera");
var isSafari  = inStr(ua,"Safari");
var isWindows = inStr(platf,"Win32");
var isMac     = inStr(platf,"Mac");

var isIE55    = (isWindows && hasDocumentAll && hasGetElementById && (inStr(ua,"MSIE 5.5") || inStr(ua,"MSIE 6.0") || inStr(ua,"MSIE 6.5") || inStr(ua,"MSIE 7.0") || !/MSIE \d+/.test(ua)) && !isOpera);

function inStr(str,val,cas) {
	var ret;
	if (cas) { /* Case sensitive */
		ret = (str.indexOf(val) != -1);
	} else { /* Not Case sensitive */
		str = str.toUpperCase();
		val = val.toUpperCase();
		ret = (str.indexOf(val) != -1);
	}
	return ret;
}

/* *******************************************************************************************
 *         Functions                                                                         *
 ******************************************************************************************* */

function singleclicked() {
	if (isMac && isSafari) return true ;
	if (!("clicked" in this)) {
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
	if (sParams.indexOf('?') != -1) {
		var pairs = sParams.substring(1, sParams.length).split('&');
		for (var i=0; i<pairs.length; i++) {
			var nameVal = pairs[i].split('=');
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

function openHelpW(helpDocName){
    window.open("@documentationurl@/Help?name=" + helpDocName + "&lang=$language" ,"help");
}

function popWinOpen(winW,winH,sUrl,sName,iResize,iScroll) {
	var winX, winY ;
	if (screen) {
		if ((screen.height - winH) < 150) {
			winX = (screen.width - winW) / 2;
			winY = 0;
		} else {
			winX = (screen.width - winW) / 2;
			winY = (screen.height - winH) / 2;
		}
		var popWindow = window.open(sUrl,sName,"resizable=" + iResize + ",menubar=0,scrollbars=" + iScroll + ",width=" + winW + ",height=" + winH + ",top=" + winY + ",left=" + winX + "");
		if (popWindow) popWindow.focus();
	} else {
		window.open(sUrl,sName,"resizable=" + iResize + ",menubar=0,scrollbars=" + iScroll + ",width=" + winW + ",height=" + winH);
	}
}
</vel:velocity>
