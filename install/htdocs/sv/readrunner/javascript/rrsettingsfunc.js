/* ********************************************************************
	RRsettingsFunc.js 2002-05-02
	Copyright (c) 2002 ReadRunner™. All Rights Reserved.
	You may not reverse engineer, decompile, or disassemble this product
	You may not redistribute this software, code or design without
	the written consent of the copyright owner.
	Coding by imCode AB. www.imcode.com
******************************************************************** */


/* *************************************
 *           Misc / initiation         *
 ************************************* */

function RRinit0() {
	RRsetDefaultSettings();
	var iMetaId = unescape(getParam('meta_id'));
	var actLay = unescape(getParam('lay'));
	var f = document.forms.form1;
	if (parseInt(iMetaId) > 1000) {
		f.MetaId.value = iMetaId;
	}
	if (actLay != '') showDiv(actLay);
}


/* *************************************
 *           Get URL-parameter         *
 ************************************* */

function getParam(attrib) {
	var idx = document.URL.indexOf('?');
	var retVal = '';
	var params = new Array();
	if (idx != -1) {
		var pairs = document.URL.substring(idx+1, document.URL.length).split('&');
		for (var i=0; i<pairs.length; i++) {
			nameVal = pairs[i].split('=');
			if (nameVal[0] == attrib) {
				retVal = nameVal[1];
			}
		}
		return retVal;
	}
}




/* *************************************
 *           Play button               *
 ************************************* */

function startRR() {
	var f = document.forms.form1;
	var iMetaId = f.MetaId.value;
	var sTempl = (unescape(getParam('template')) != '') ? '&template=' + unescape(getParam('template')) : '';
	//var sNoPausStop = (!f.cbp2.checked || parseFloat(f.p2.value) == 0) ? '&readrunner_no_stops=1' : '';
	//var sNoPausSep = (!f.cbp3.checked || parseFloat(f.p3.value) == 0) ? '&readrunner_no_separators=1' : '';
	if (parseInt(iMetaId) > 1000) document.location.href = '@servleturl@/GetDoc?meta_id=' + iMetaId + sTempl;
}


/* *************************************
 *           Default Settings          *
 ************************************* */

var iDefaultSpeed = 10;
var bDefaultPausRow = 1;
var iDefaultPausRow = 0.2;
var bDefaultPausStop = 1;
var iDefaultPausStop = 0.0;
var bDefaultPausDiv = 1;
var iDefaultPausDiv = 0.0;
var bDefaultPausScroll = 1;
var iDefaultPausScroll = 0.8;


/* ************ Reset Default Settings ********** */

function RRsetDefaultSettings() {
	var f = document.forms.form1;
	f.RRspeed.value = iDefaultSpeed;
	f.cbp1.checked = bDefaultPausRow;
	f.p1.value = iDefaultPausRow;
	f.cbp2.checked = bDefaultPausStop;
	f.p2.value = iDefaultPausStop;
	f.cbp3.checked = bDefaultPausDiv;
	f.p3.value = iDefaultPausDiv;
	f.cbp4.checked = bDefaultPausScroll;
	f.p4.value = iDefaultPausScroll;
	
	if (active11Td) form11Click(active11Td);
	if (active12Td) form12Click(active12Td);
	
	RRgetCookies(); // set values from cookie if exists
}


/* *************************************
 *           Save settings ?           *
 ************************************* */


/* ***************** "Save" Button ************** */

function RRsaveSettings(what) {
	if (!window.navigator.cookieEnabled) {
		alert('Du måste ha inställt att du accepterar cookies.\n(Verktyg - Internet-alternativ - Sekretess - Avancerat)');
	} else {
		var f = document.forms.form1;
		
		if (what == 'Speed') {        // Save speed
			var iSpeed = f.RRspeed.value;  // nuvarande/verklig
			var iSpeed0 = f.RRspeed0.value;// initial/jämförande
			setCookie('RRspeed', (iSpeed + '/' + iSpeed0));
		}
		if (what == 'Settings') {     // Save settings
			var iColorBg = 0;
			var iColorWorm = 0;
			arrColorWormRadio = f.RRcolorWorm;
			for (var i = 0; i < arrColorWormRadio.length; i++) {
				if (arrColorWormRadio[i].checked) iColorWorm = i;
			}
			var iColorText = 0;
			arrColorTextRadio = f.RRcolorText;
			for (var i = 0; i < arrColorTextRadio.length; i++) {
				if (arrColorTextRadio[i].checked) iColorText = i;
			}
			var bOpacity = '1';
			var iOpacityLev = 0;
			arrOpacityLevRadio = f.RRopacityLevel;
			for (var i = 0; i < arrOpacityLevRadio.length; i++) {
				if (arrOpacityLevRadio[i].checked) iOpacityLev = i;
			}
			var bPausRow = f.cbp1.checked;
			var iPausRow = f.p1.value;
			var bPausStop = f.cbp2.checked;
			var iPausStop = f.p2.value;
			var bPausDiv = f.cbp3.checked;
			var iPausDiv = f.p3.value;
			var bPausScroll = f.cbp4.checked;
			var iPausScroll = f.p4.value;
			var sSettings = bPausRow+'/'+iPausRow+',';
				sSettings += bPausStop+'/'+iPausStop+',';
				sSettings += bPausDiv+'/'+iPausDiv+',';
				sSettings += bPausScroll+'/'+iPausScroll+',';
				sSettings += iColorBg+',';
				sSettings += iColorText+','+iColorWorm+','+bOpacity+','+iOpacityLev;
			setCookie('RRsettings', escape(sSettings));
			//alert(sSettings);
		}
	}
}


/* *************************************
 *            Set Cookie ?             *
 ************************************* */

function setCookie(name, value) {
	var RRpath = '/';
	var today = new Date();
	var expire = new Date();
	expire.setTime(today.getTime() + 1000*60*60*24*365); // 365 days
	var sCookieCont = name + "=" + value;
	sCookieCont += (expire == null) ? "" : "\; expires=" + expire.toGMTString();
	sCookieCont += "; path=" + RRpath;
	document.cookie = sCookieCont;
}


/* *************************************
 *            Get Cookie ?             *
 ************************************* */


/* ****** Gets Cookie And Sets The Values. ****** *
 * ******* Sets Default if not existing ********* */

function RRgetCookies() {
	var f = document.forms.form1;
	var blnCookieName = false;
	var blnCookieValues = false;
	var theCookieString = unescape(document.cookie);
	var theCookieSpeed = getCookie('RRspeed');
	if (theCookieSpeed != undefined) {
		if (theCookieSpeed.indexOf('/') != -1) {
			f.RRspeed.value = (theCookieSpeed.split("/")[0] >= 0) ? theCookieSpeed.split("/")[0] : iDefaultSpeed;
			//f.RRspeed0.value = (theCookieSpeed.split("/")[1] >= 0) ? theCookieSpeed.split("/")[1] : iDefaultSpeed;
		}
	}
	var theCookieSettings = unescape(getCookie('RRsettings'));
	if (theCookieSettings.indexOf('/') != -1) {
		//alert(theCookieSettings);
		arrSettings = theCookieSettings.split(",")
		//f.RRcolorBg.selectedIndex = (arrSettings[4] >= 0) ? arrSettings[4] : 0;
		
		sColorTextRadioValue = arrSettings[5];
		arrColorTextRadio = f.RRcolorText;
		for (var i = 0; i < arrColorTextRadio.length; i++) {
			if (i == sColorTextRadioValue) {
				form11Click('form11' + (i + 1));
			}
		}
		
		sColorWormRadioValue = arrSettings[6];
		arrColorWormRadio = f.RRcolorWorm;
		for (var i = 0; i < arrColorWormRadio.length; i++) {
			if (i == sColorWormRadioValue) {
				form12Click('form12' + (i + 1));
			}
		}
		
		f.cbp1.checked = (arrSettings[0].split("/")[0] == 'true') ? 1 : 0;
		f.p1.value = (arrSettings[0].split("/")[1] >= 0) ? arrSettings[0].split("/")[1] : iDefaultPausRow;
		f.cbp2.checked = (arrSettings[1].split("/")[0] == 'true') ? 1 : 0;
		f.p2.value = (arrSettings[1].split("/")[1] >= 0) ? arrSettings[1].split("/")[1] : iDefaultPausStop;
		f.cbp3.checked = (arrSettings[2].split("/")[0] == 'true') ? 1 : 0;
		f.p3.value = (arrSettings[2].split("/")[1] >= 0) ? arrSettings[2].split("/")[1] : iDefaultPausDiv;
		f.cbp4.checked = (arrSettings[3].split("/")[0] == 'true') ? 1 : 0;
		f.p4.value = (arrSettings[3].split("/")[1] >= 0) ? arrSettings[3].split("/")[1] : iDefaultPausScroll;
		//f.blnOpacity.checked = (arrSettings[7] == 'true') ? 1 : 0;
		sOpacityLevelRadioValue = arrSettings[8];
		arrOpacityLevelRadio = f.RRopacityLevel;
		for (var i = 0; i < arrOpacityLevelRadio.length; i++) {
			if (i == sOpacityLevelRadioValue) {
				form13Click('form13' + (i + 1));
			}
		}
		//f.RRopacityLevel.selectedIndex = (arrSettings[8] >= 0) ? arrSettings[8] : 0;
	}
}


/* ************* Get Cookie By Name ************* */

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


/* *************************************
 *                SPEED                *
 ************************************* */

var avWordLen = 5.34; // average chr/words

function RRinitSpeedCalc() {
	RRcheckSpeed();
}

function RRcheckSpeed(f) {
	cps = form1.RRspeed.value;
	cps = (parseInt(cps) < 1 || isNaN(cps)) ? 1 : cps;
	cps = (parseInt(cps) > 150) ? 150 : cps;
	//dWords = RRcalcSpeed();
	//window.status = 'Hastighet: ' + cps + ' tkn/s  - ca. ' + dWords + ' ord/min';
}

function RRcheckSpeedVal(f) {
	cps = parseInt(form1.RRspeed.value);
	cps = (cps > 150) ? 150 : cps;
	cps = (cps < 1 || isNaN(cps)) ? '' : cps;
	form1.RRspeed.value = cps;
	RRcheckSpeed(f);
}


function RRcheckSpeedValEmpty() {
	cps = form1.RRspeed.value;
	if (cps == '' || isNaN(cps)) {
		cps = 10;
	} else {
		cps = (parseInt(cps) < 1) ? 1 : cps;
		cps = (parseInt(cps) > 150) ? 150 : cps;
	}
	form1.RRspeed.value = cps;
	RRcheckSpeed(f);
}

function RRcalcSpeed() {
	var cps = parseInt(form1.RRspeed.value);
	var cps0 = parseInt(form1.RRspeed0.value);
	var elProc = document.getElementById("RRpercent");
	var elWords = document.getElementById("RRwords");
	
	var dProc = (parseInt(cps / cps0 * 1000) / 10) - 100;
	dProc = Math.round(dProc * 10) / 10;
	var dWords = parseInt(60 * cps / avWordLen);
	
	var isPos = (dProc > 0) ? '+' : '';
	if (dProc == 0) isPos = String.fromCharCode(177); // "+/-"
	//elProc.innerText = isPos + dProc + '%'; // writes diff-percentage
	//elWords.innerHTML = 'ca.<b>' + dWords.toString().replace(/\./, ',') + '</b>'; // writes words/min.
	return dWords;
}


/* *************************************
 *            Value Arrows             *
 ************************************* */

var mouseActiveUp = 0;
var mouseActiveDn = 0;
var iVal = 0;
var timeUp = null;
var timeDn = null;


function RRcheckPauseVal(what) {
	theField = eval('form1.' + what);
	thePause = theField.value;
	thePause = (thePause > 3) ? 3 : thePause;
	thePause = (thePause < 0) ? 0 : thePause;
	thePause = (thePause >= 0) ? thePause : 0;
	form1[what].value = thePause;
}

function fArrowUp(f){
	var fv = eval('form1.' + f);
	iVal = parseFloat(fv.value) * 10;
	if(iVal == ''){
		iVal = 0;
	} else {
		iVal = parseInt(iVal);
	}
	if(iVal < 30){
		iVal += 1;
	} else {
		iVal = 30;
	}
	fv.value = (iVal > 0) ? iVal / 10 : 0;
	fv.focus();
}

function fArrowDn(f){
	var fv = eval('form1.' + f);
	iVal = parseFloat(fv.value) * 10;
	if(iVal == ''){
		iVal = 0;
	} else {
		iVal = parseInt(iVal);
	}
	if(iVal > 1){
		iVal -= 1;
	} else {
		iVal = 0;
	}
	fv.value = (iVal > 0) ? iVal / 10 : 0;
	fv.focus();
}

/*function fSpeedArrowUp(f){
	var fv = eval('form1.' + f);
	iVal = parseInt(fv.value);
	if(iVal == ''){
		iVal = 1;
	} else {
		iVal = parseInt(iVal);
	}
	if(iVal < 150){
		iVal += 1;
	} else {
		iVal = 150;
	}
	fv.value = (iVal > 0) ? iVal : 150;
	fv.focus();
	RRcheckSpeed(f);
}

function fSpeedArrowDn(f){
	var fv = eval('form1.' + f);
	iVal = parseInt(fv.value);
	if(iVal == ''){
		iVal = 1;
	} else {
		iVal = parseInt(iVal);
	}
	if(iVal > 1){
		iVal -= 1;
	} else {
		iVal = 1;
	}
	fv.value = (iVal > 0) ? iVal : 1;
	fv.focus();
	RRcheckSpeed(f);
}*/
