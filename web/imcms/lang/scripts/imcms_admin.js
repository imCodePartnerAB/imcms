/* *******************************************************************************************
 *         Browser sniffer                                                                   *
 ******************************************************************************************* */

var platf     = navigator.platform;
var ua        = navigator.userAgent;

var isNS      = (document.layers) ? 1 : 0;
var isIE      = (document.all) ? 1 : 0;
var isMoz     = (document.getElementById) ? 1 : 0;

var isGecko   = inStr(ua,'Gecko');
var isOpera   = inStr(ua,'Opera');
var isWindows = inStr(platf,'Win32');
var isMac     = inStr(platf,'Mac');

var isIE55    = (isWindows && isIE && isMoz && (inStr(ua,'MSIE 5.5') || inStr(ua,'MSIE 6.0') || inStr(ua,'MSIE 6.5') || inStr(ua,'MSIE 7.0')) && !isOpera) ? 1 : 0;

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
 *         INIT                                                                              *
 ******************************************************************************************* */

if (isMac && isNS) { // Mac NS 4.X
	document.writeln("<link rel=\"stylesheet\" href=\"@imcmscssurl@/imcms_admin_ns_mac.css\" type=\"text/css\">");
} else if (isMac) {  // Mac IE/Moz
	document.writeln("<link rel=\"stylesheet\" href=\"@imcmscssurl@/imcms_admin_mac.css\" type=\"text/css\">");
} else if (!isNS) {  // Win / NS 4.X already has the CSS via LINK REL before the script
	document.writeln("<link rel=\"stylesheet\" href=\"@imcmscssurl@/imcms_admin.css\" type=\"text/css\">");
}

/* *******************************************************************************************
 *         Functions                                                                         *
 ******************************************************************************************* */

function getNsFormSize(theVal) {
	/* OBS! Dessa värden gäller vid nuvarande CSS */
	theVal = parseInt(theVal);
	if (isNS && isMac) {
		theVal = (theVal < 12) ? parseInt(theVal * 1.0) : parseInt(theVal * .86);
	} else if (isNS) {
		theVal = (theVal < 5) ? parseInt(theVal * .55) : parseInt(theVal * .46);
	}
	return theVal;
}

function writeFormField(theType,theName,theSize,theMaxLength,theWidth,theValue) {
	var retVal = "<input";
	var sType, sName, sSize, sMaxLength, sWidth, sValue ;
	sType = " type=\"" + theType + "\"";
	sName = (theName.indexOf("id=") != -1) ? " name=\"" + theName + "\"" : " name=\"" + theName + "\" id=\"" + theName + "\"";
	sSize = (theType.toUpperCase() == "TEXTAREA") ? " cols=\"" + getNsFormSize(theSize) + "\"" : " size=\"" + getNsFormSize(theSize) + "\"";
	if (theMaxLength == null)     sMaxLength = "";
	if (isNS || theWidth == null) sWidth = "";
	if (theValue == null)         sValue = "";

	switch (theType.toUpperCase()) {
		case "FILE":
			retVal += sType + sName + sSize;
			retVal += (sValue != "") ? " value=\"" + theValue + "\"" : "";
			retVal += ">";
		break;
		case "HIDDEN":
			retVal += sType + sName;
			retVal += (sValue != "") ? " value=\"" + theValue + "\"" : "";
			retVal += ">";
		break;
		case "TEXTAREA": // use "theMaxLength" for "ROWS="
			retVal  = "<textarea" + sName + sSize;
			retVal += (sMaxLength != "") ? " rows=\"" + theMaxLength + "\"" : " rows=\"3\"";
			if (theWidth != "") retVal += " style=\"overflow:auto; width:" + theWidth + "\"";
			retVal += " wrap=\"VIRTUAL\">";
		break;
		default: // TEXT / PASSWORD
			retVal += sType + sName + sSize;
			retVal += (sMaxLength != "") ? " maxlength=\"" + theMaxLength + "\"" : "";
			retVal += (sValue != "") ? " value=\"" + theValue + "\"" : "";
			if (sWidth != "") retVal += " style=\"width:" + theWidth + "\"";
			retVal += ">";
	}
	document.write(retVal);
}

function hr(theWidth, theWidthNs, theColor) {
	if (theColor == "blue") theColor = "20568d";
	theWidth = (isMoz) ? theWidth : theWidthNs;
	document.write("<img src=\"@imcmsimageurl@/admin/1x1_" + theColor + ".gif\" width=\"" + theWidth + "\" height=\"1\" vspace=\"8\">");
}

function imcHeading(theHeading, theWidthNs) {
	theColor = "20568d";
	theWidth = (isMoz) ? "100%" : theWidthNs;
	document.writeln("<span class=\"imcmsAdmHeading\">" + theHeading + "</span><br>");
	document.write("<img src=\"@imcmsimageurl@/admin/1x1_" + theColor + ".gif\" width=\"" + theWidth + "\" height=\"1\" vspace=\"8\">");
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
		window.open(sUrl,sName,"resizable=yes,menubar=0,scrollbars=" + iScroll + ",width=" + winW + ",height=" + winH);
	}
}

