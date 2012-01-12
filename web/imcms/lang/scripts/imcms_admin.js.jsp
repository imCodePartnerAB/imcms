<%@ page
	
	contentType="text/javascript"
	pageEncoding="UTF-8"
	
%><%@ taglib prefix="vel" uri="imcmsvelocity" %><%

String cp = request.getContextPath() ;

%>

<jsp:include page="imcms_jquery_1.7.1.js" />

<jsp:include page="imcms_jquery-ui_1.8.16.js" />


jQ(document).ready(function($) {
	toolTip($, 'body') ;
}) ;

function imLog(mess) {
	try {
		if (window && window.console) {
			window.console.log(mess) ;
		}
	} catch (e) {}
}

<%--
/* *******************************************************************************************
 *         ToolTip - Mouse over white plate with (i) icon or doc format icon (PDF, ZIP etc.) *
 ******************************************************************************************* */
--%>
/*
 * Tooltip script 
 * powered by jQuery (http://www.jquery.com)
 * 
 * written by Alen Grakalic (http://cssglobe.com)
 * modified by Tommy Ullberg, imCode Partner AB (http://www.imcms.net/)
 * 
 * for more info visit http://cssglobe.com/post/1695/easiest-tooltip-and-image-preview-using-jquery
 *
 */

var oToolTipTimer = null ;

this.toolTip = function($, selector){
	if (oToolTipTimer) {
		window.clearTimeout(oToolTipTimer) ;
	}
	xOffset = 10;
	yOffset = 20;
	$(selector + ' .toolTip,' + selector + ' .toolTipHide').hover(function(e){
		this.t = this.title;
		this.title = "";
		var fileData = $(this).attr("rel") ;
		this.iconClass = "" ;
		if (null != fileData && fileData.length > 7 && fileData.indexOf("FIL") != -1) {
			var fileExt = fileData.substring(4,7) ;
			if (/^(PDF|DOC|ZIP|JPG|PNG|GIF|MP3|AVI|MPG)$/i.test(fileExt)) {
				this.iconClass = "toolTipIcon_" + fileExt.toUpperCase() ;
			}
		} else if (null != fileData && fileData.indexOf("URL") != -1) {
			this.iconClass = "toolTipIcon_EXT_LINK" ;
		}
		$("body").append('<div id="toolTipPop">'+ this.t.replace(/&lt;/g, "<").replace(/&gt;/g, ">").replace(/#LT#/g, "&lt;").replace(/#GT#/g, "&gt;") + '</div>');
		$("#toolTipPop")
			.css("top",(e.pageY + yOffset) + "px")
			.css("left",(e.pageX + xOffset) + "px")
			.fadeIn(document.all ? "fast" : "slow");
		if ($(this).hasClass('toolTipHide')) {
			$("#toolTipPop").delay(1000).fadeTo(500, 0) ;
			//this.title = this.t;
		}
	}, function(){
		this.title = this.t;
		$("#toolTipPop").remove();
	});
	$(".toolTip,.toolTipHide").mousemove(function(e){
		$("#toolTipPop")
			.css("top",(e.pageY + yOffset) + "px")
			.css("left",(e.pageX + xOffset) + "px") ;
		if ("" != this.iconClass) {
			$("#toolTipPop").addClass(this.iconClass) ;
		}
	});
};

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

var isIE55    = (isWindows && hasDocumentAll && hasGetElementById && /MSIE \d+/.test(ua) && !isOpera);

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
