var btnRow1W = 100;
var btnRow2W = 400;
var btnOffsetX1 = 10;
var btnOffsetX2 = 10;

var isTop = 1;									// if visible frame is the uppermost frame
var isActive = 1;
var isAltered = 0;


/* *************************************
 *           INIT                      *
 ************************************* */

function initBtnsSettings() {		// settings - online
	document.getElementById("RRpanelDiv").style.width = document.body.clientWidth - 1;	// fixes bottom-scroll bug
	document.getElementById("RRpanelBtnDiv").style.left = document.body.clientWidth - btnRow1W - btnOffsetX1;
	isTop = 1;
	RRpanelInit();
	document.getElementById("RRpanelDiv").style.width = document.body.clientWidth;			// fixes line 16 bug
}

function initBtns() {						// player - offline
	document.getElementById("RRpanelDiv").style.width = document.body.clientWidth;
	document.getElementById("RRpanelTopLineDiv").style.width = document.body.clientWidth;
	document.getElementById("RRpanelBtnDiv").style.left = document.body.clientWidth - btnRow2W - btnOffsetX2;
}

function initBtnsGenerator() {	// generator - online
	document.getElementById("RRpanelDiv").style.width = document.body.clientWidth;
	document.getElementById("textDiv").style.height = document.body.clientHeight - 220;
	isTop = 1;
	RRpanelInit();
}

/* *************************************
 *           BUTTONS                   *
 ************************************* */

function overBtn(what) {
	var el = document.getElementById(what);
	var elCont = document.getElementById(what + 'Cont');
	el.className = 'btnUp';
	elCont.className = what + 'Cont';
}

function outBtn(what) {
	var el = document.getElementById(what);
	var elCont = document.getElementById(what + 'Cont');
	el.className = 'btnNorm';
	elCont.className = 'btnDim';
}

function downBtn(what) {
	var el = document.getElementById(what);
	var elCont = document.getElementById(what + 'Cont');
	el.className = 'btnDown';
	elCont.className = what + 'ContAct';
}

function clickBtn(what) {
	var el = document.getElementById(what);
	var elCont = document.getElementById(what + 'Cont');
	switch (what) {
		case 'reset': // rr-page
			RRreset();
		break
		case 'stop': // rr-page
			outBtn('stop');
			RRstop();
		break
		case 'start': // rr-page
			outBtn('start');
			RRplay();
		break;
		case 'menu': // settings-page
			top.location = top.location.toString().replace(/(.*?)menu\=[1](.*?)/i, '$1menu=0$2');
		break
		case 'save':
			document.forms.rrdownl.submit();
			return false;
		break
	}
	el.blur();
}

/* *************************************
 *        The floating panel           *
 ************************************* */

var iObjOffsetX = 0;
var iObjOffsetY = 0;

/*
code mady by www.bratta.com can be used
freely as long as this msg is intact
*/

var RRpageWidth,RRpageHeight

function RRpanelInit() {
	document.getElementById("RRpanelDiv").style.display = 'block';
	RRpanelObj = new RRmakeObj('RRpanelDiv');
	RRpageWidth = document.body.offsetWidth - 4;
	RRpageHeight = document.body.offsetHeight - 4;
	RRcheckIt();
	window.onscroll = RRcheckIt;
}

function RRcheckIt() {
	var X_left = document.body.scrollLeft + iObjOffsetX;
	var Y_top = (isTop) ? document.body.scrollTop + iObjOffsetY - 2 : document.body.scrollTop + iObjOffsetY;
	RRpanelObj.RRmoveIt(X_left,Y_top);
}

function RRmakeObj(obj,nest) {
	nest = (!nest) ? '' : 'document.' + nest + '.';
	this.css = eval(obj + '.style');
	this.evnt = eval(obj);						
	this.RRmoveIt = RRmoveIt;
}

function RRmoveIt(x,y) {
	this.x = x;
	this.y = y;
	this.css.left = this.x;
	this.css.top = this.y;
}

/* *************************************
 *        Misc Functions               *
 ************************************* */

function getParam(attrib) {			// get querystring-parameters from document.location
	var sParams = location.search;
	var retVal = -1;
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

function getParamTop(attrib) {	// get querystring-parameters from top.document.location
	var sParams = top.location.search;
	var retVal = -1;
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

function RRopenHelp() {					// opens the help-page
	var iMetaId = unescape(getParam('meta_id'));
	document.location = '/readrunner/readrunner.html?meta_id=' + iMetaId + '&lay=help';
}
