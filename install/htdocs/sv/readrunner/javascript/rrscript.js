/* ********************************************************************
	RRscript.js 2002-03-08
	Copyright (c) 2002 ReadRunner™. All Rights Reserved.
	You may not reverse engineer, decompile, or disassemble this product
	You may not redistribute this software, code or design without
	the written consent of the copyright owner.
	Coding by imCode AB. www.imcode.com
******************************************************************** */

/* ******************** CSS for the panel ******************** */

with (document) {
	write('<sty' + 'le type=\'text/css\'>\n');
	write('Q { filter: progid:DXImageTransform.Microsoft.Blinds(Direction=\'right\', bands=1); padding-right: 0.4em\; }\n');
	write('#RR1 { whiteSpace: nowrap }\n');
	write('#oRRn { padding-right: 0px\; }\n');
	write('.RR_field { font: 11px Verdana, Geneva, sans-serif\; color: #000066\; background-color: #f5f5f5\; visibility: visible\; border: 2px Inset #f0f0f0 }\n');
	write('.RR_field_right { font: 11px Verdana, Geneva, sans-serif\; color: #000066\; background-color: #f5f5f5\; visibility: visible\; border: 2px Inset #f0f0f0\; text-align:right }\n');
	write('.RR_field2 { font: 11px Verdana, Geneva, sans-serif\; color: #000066\; background-color: #f5f5f5\; visibility: visible\; }\n');
	write('.RR_button { background-color: #e0e0e0\; visibility: visible\; border: 1px outset #c0c0c0\; cursor: hand }\n');
	write('.RR_norm { font: 11px Verdana, Geneva, sans-serif\; color: #000066 }\n');
	write('</style>\n');
}

/* *************************************
 *           Misc / initiation         *
 ************************************* */

/* ********* To prevent double fire *********** */

var doFunctionCheck = false;

/* *************************************
 *           The Worm thing            *
 ************************************* */

/* ******************** Init ******************** */

var blnRemoveRightPaddning = 1; //                ***********  TOGGLE RemoveRightPaddning **********

var X3 = 0;
var Y3 = 0;
var X4 = 0;
var Y4 = 0;
var scrollDistans = 10;
var RRcounter = 1;
var RRobj = 0;
var runMode="void";
var RRarray = new Array(RRlayers+1);

function RRonResize() {
	setTimeout("history.go(0)", 1000);
}

function redirectMouseClick(parm){
	if (doFunctionCheck){
		//alert("DO");
		doFunctionCheck = false;
		document.all.RRwaitDiv.style.top = (parseInt(document.body.scrollTop) + (parseInt(document.body.offsetHeight) / 2) - 150);
		document.all.RRwaitDiv.style.display = 'block';
		if (parm == 'PlayButton'){
			RRdisableBtn('reset');
			RRdisableBtn('start');
			RRplay();
		}else{
			RRcheckKey();
			RRcheckButton();
		}
	}
}
function enableMouseClick(){
	doFunctionCheck = true;
	document.all.RRwaitDiv.style.display = 'none';
}
function RRinit() {
	var itemX,itemY
	RRdisableBtn('reset');
	RRdisableBtn('stop');
	RRdisableBtn('start');
	RRcheckLineBreaks();
	window.scrollTo(0,0);
	RRsetDefaultSettings();
	RRgetCookies();
	RRenableBtn('reset');
	RRdisableBtn('stop');
	RRenableBtn('start');
	RRremovePaddings();
}

function RRremovePaddings() {
	if (blnRemoveRightPaddning) {
		var Qt = document.all.tags("Q");
		for(i = 0; i < RRlayers; i++) {
			if (parseInt(Qt.item(i + 1).offsetTop) != parseInt(Qt.item(i).offsetTop)) {
				Qt.item(i).id = 'oRRn';
			}
		}
	}
	window.setTimeout("RRhideWait()", 500); // How long banner is shown after pageload...
}

function RRhideWait() {
	document.getElementById("RRloadingDiv").style.visibility = 'hidden';
	document.getElementById("RRcolorBgField").style.visibility = 'visible';
	document.getElementById("RRcolorWormField").style.visibility = 'visible';
	document.getElementById("RRcolorTextField").style.visibility = 'visible';
	document.getElementById("RRopacityLevelField").style.visibility = 'visible';
	RRcheckSpeed();
	enableMouseClick();
}

/* *************** Add rule to css ************** */

function RRinitWorm(){
	document.styleSheets[0].addRule("Q", "height:0");
	document.getElementById("RRbufferDiv").innerHTML = document.getElementById("RRcontentDiv").innerHTML;
	//document.body.onScroll = "updateScreenSize();"
}


/* **************** "Play" button *************** */

function RRplay() {
	hidePanel();
	setTimeout("RRprepare()", 1000);
}

function RRprepare() {
	RRdisableBtn('reset');
	RRdisableBtn('start');
	RRenableBtn('stop');
	RRcheckSpeed();
	/* Settings */
	var RRform = document.forms.form1;
	charsPerSec = RRform.RRspeed.value;
	theColorWorm = 'rgb(' + RRform.RRcolorWorm.options[RRform.RRcolorWorm.selectedIndex].value + ')';
	theColorBg = 'rgb(' + RRform.RRcolorBg.options[RRform.RRcolorBg.selectedIndex].value + ')';
	theColorText = 'rgb(' + RRform.RRcolorText.options[RRform.RRcolorText.selectedIndex].value + ')';
	
	/* Build semi-transparent text - 80% of theColorWorm */
	theColorTextTransp = RRform.RRcolorWorm.options[RRform.RRcolorWorm.selectedIndex].value;
	var transpVal = RRform.RRopacityLevel.options[RRform.RRopacityLevel.selectedIndex].value;
	arrTransp = theColorTextTransp.split(',');
	for (var i=0; i<3; i++) {
		arrTransp[i] = (arrTransp[i] > 0) ? parseInt(arrTransp[i] * transpVal) : 0;
	}
	theColorTextTransp = 'rgb(' + arrTransp[0] + ',' + arrTransp[1] + ',' + arrTransp[2] + ')';
	
	paus1 = parseFloat(RRform.p1.value) * 1400;
	paus2 = parseFloat(RRform.p2.value) * 1000;
	paus3 = parseFloat(RRform.p3.value) * 1000;
	paus4 = parseFloat(RRform.p4.value) * 1000;
	if(runMode == "stop") { 
		runMode = "start";
		updateScreenSize();
		RRworm(); 
	} else {
		for(n=1;n<4;n++) {
			eval("paus" + n + " = (form1.cbp" + n + ".checked) ? paus" + n + " : 0");
		}
		cps = charsPerSec;
		if (RRcounter > 1) { // right click start
			for (n = 1; n <= RRlayers; n++) {
				RRobj = document.all.tags("Q").item(n-1);
				RRarray[n] = RRobj.innerText.length;
				if (n < RRcounter) {
					RRobj.style.backgroundColor = theColorBg;
					RRobj.style.color = theColorText;
				} else {
					RRobj.style.backgroundColor = theColorWorm;
					RRobj.style.color = (RRform.blnOpacity.checked) ? theColorTextTransp : theColorWorm;
				}
			}
		} else { // original
			for (n = 1; n <= RRlayers; n++) {
				RRobj = document.all.tags("Q").item(n-1);
				RRarray[n] = RRobj.innerText.length;
				RRobj.style.backgroundColor = theColorWorm;
				RRobj.style.color = (RRform.blnOpacity.checked) ? theColorTextTransp : theColorWorm;
			}
		}
		RRform.blnOpacity.disabled = 1;
		RRform.RRopacityLevel.disabled = 1;
		updateScreenSize();
		if (RRcounter > 1) {
			setTimeout("RRworm()", 1000);
		} else {
			RRworm();
		}
	}
}


/* ****************** When done ***************** */

function RRstop() {
	runMode = "stop";
	RRdisableBtn('reset');
	RRenableBtn('start');
	RRdisableBtn('stop');
}

function RRnothing() {
	RRenableBtn('reset');
	RRenableBtn('start');
	RRdisableBtn('stop');
	setTimeout("showPanel()", 500);
}

function RRquit() {
	RRenableBtn('reset');
	RRdisableBtn('start');
	RRdisableBtn('stop');
	setTimeout("showPanel()", 500);
}


/* **************** Reset function ************** */

function RRreset() {
	RRenableBtn('reset');
	RRenableBtn('start');
	RRdisableBtn('stop');
	form1.blnOpacity.disabled = 0;
	form1.RRopacityLevel.disabled = 0;
	window.scrollTo(0,0);
	showPanel();
	RRcounter = 1;
	RRobj = 0;
	runMode = "start";
	document.getElementById("RRcontentDiv").innerHTML = document.getElementById("RRbufferDiv").innerHTML;
	if (blnRemoveRightPaddning) {
		var Qt = document.all.tags("Q");
		for(i = 0; i < RRlayers; i++) {
			if (parseInt(Qt.item(i + 1).offsetTop) != parseInt(Qt.item(i).offsetTop)) {
				Qt.item(i).id = 'oRRn';
			}
		}
	}
	RRarray = null;
	RRarray = new Array(RRlayers+1);
}


/* ****************** Main Loop ***************** */


function RRworm() {
	var dSpeed = 0;
	dSpeed = parseFloat(RRarray[RRcounter]/cps); // *****  Hastighet  *****
	RRobj = document.all.tags("Q").item(RRcounter-1);
	
	// AUTO-SCROLLING
	//if (form1.cbp4.checked) {
	//}
	var didScroll = doScroll(RRobj);
	if (!didScroll){
		RRobj.filters[0].Apply();
		RRobj.style.backgroundColor = theColorBg; // Text bg-color
		RRobj.style.color = theColorText;
		RRobj.filters[0].Play(dSpeed);
		RRwait(RRcounter,didScroll);
		RRcounter++;
	}else{
		RRwait(RRcounter,didScroll);
	}
}

/* ***************** The Pauses ***************** */

function RRwait(divID,didScroll) {
	var thePause = 0;
	if (runMode == "stop") {
		call = "RRnothing()";
		enableMouseClick();
	} else if (RRobj.filters[0].status == 2) {
	
		call = "RRwait()";
		enableMouseClick();
	} else {
		call = (RRcounter > RRlayers) ? "RRquit()" : "RRworm()";
		enableMouseClick();
	}
	if (divID != null && (form1.cbp1.checked || form1.cbp2.checked || form1.cbp3.checked)) {
		RRobj = document.all.tags("Q").item(divID-1);
		var theText = RRobj.innerText;
		
		if (form1.cbp3.checked && /[,;:–\/-]\s*$/g.test(theText)) thePause = paus3; // ***** skiljetecken ****
		if (form1.cbp2.checked && /[\.!\?]\s*$/g.test(theText)) thePause = paus2; //   ***** stopptecken *****
		
		if (form1.cbp1.checked && divID < RRlayers) { //                               ***** radbrytning *****
			var thisY = parseInt(RRobj.offsetTop);
			var nextY = parseInt(document.all.tags("Q").item(divID).offsetTop);
			var thisH = parseInt(RRobj.offsetHeight);
			var nextH = parseInt(document.all.tags("Q").item(divID).offsetHeight);
			thePause = (nextY != thisY || nextH != thisH) ? paus1 : thePause;
			//document.all.dummyField.style.top = 180+thisY;// ful-hack
		}
	}
	if (divID != null && form1.cbp4.checked && didScroll) {
		thePause = paus4;
	}
	thePause *= 1.6; //                                      ***** Adjust factor *****
	
	if (parseInt(thePause) > 0) {
		setTimeout(call, parseInt(thePause)+10);
	} else {
		setTimeout(call, 0);
	}
}


/* ********* Check User Key/Mouse Events ******** */

/* Checks keyboard clicks and left-mouse-clicks */

function RRcheckKey() {
		if (!RRhelpOut) {
			kc = window.event.keyCode; //37 - 39 / 83 (S)
			mb = window.event.button;
			ctrlKey = window.event.ctrlKey;
			cps = parseInt(form1.RRspeed.value);
			cps += (kc==39)-(kc==37);
			cps = (cps > 150) ? 150 : cps;
			cps = (cps < 1) ? 1 : cps;
			form1.RRspeed.value=cps;
			if (ctrlKey || mb==1 || kc==83) {
				if (!form1.start.disabled) {
					RRdisableBtn('reset');
					RRdisableBtn('start');
					RRplay();
				} else if (!form1.stop.disabled) {
					RRstop();
				}
			}
			RRcheckSpeed();
		}
}

/* Checks right-clicks in Q-tags */

function RRcheckButton() {
	if (!RRhelpOut) {
		mb = window.event.button;
		srcE = window.event.srcElement;
		if (mb == 2 && srcE.tagName == 'Q' && !form1.start.disabled) {
			srcE.style.cursor = 'e-resize';
			var Qsel = -1;
			for (var i = 0; i < document.all.tags("Q").length; i++) {
				if (Qsel == -1) {
					Qsel = (document.all.tags("Q").item(i).style.cursor == 'e-resize') ? i : -1;
				}
			}
			document.all.tags("Q").item(Qsel).style.cursor = '';
			if (Qsel != -1) {
				RRreset('');
				RRcounter = Qsel+1;
				hidePanel();
				setTimeout("RRprepare()", 500);
			}
		}
	}
}


/* *************************************
 *           Default Settings          *
 ************************************* */

var bDefaultSaveSpeed = 1;
var bDefaultSaveSettings = 1;
var iDefaultSpeed = 30;
var bDefaultPausRow = 1;
var iDefaultPausRow = 0.5;
var bDefaultPausStop = 1;
var iDefaultPausStop = 0.5;
var bDefaultPausDiv = 1;
var iDefaultPausDiv = 0.5;
var bDefaultPausScroll = 0;
var iDefaultPausScroll = 0.8;


/* ************ Reset Default Settings ********** */

function RRsetDefaultSettings() {
	var f = document.forms.form1;
	f.blnSaveSpeed.checked = bDefaultSaveSpeed;
	f.blnSaveSettings.checked = bDefaultSaveSettings;
	f.RRspeed.value = iDefaultSpeed;
	f.cbp1.checked = bDefaultPausRow;
	f.p1.value = iDefaultPausRow;
	f.cbp2.checked = bDefaultPausStop;
	f.p2.value = iDefaultPausStop;
	f.cbp3.checked = bDefaultPausDiv;
	f.p3.value = iDefaultPausDiv;
	f.cbp4.checked = bDefaultPausScroll;
	f.p4.value = iDefaultPausScroll;
}


/* *************************************
 *           Save settings ?           *
 ************************************* */


/* ***************** "Save" Button ************** */

function RRsaveSettings() {
	if (!window.navigator.cookieEnabled) {
		alert('Du måste ha inställt att du accepterar cookies.\n(Verktyg - Internet-alternativ - Sekretess - Avancerat)');
	} else {
		var f = document.forms.form1;
		var bSaveSpeed = f.blnSaveSpeed.checked;
		var bSaveSettings = f.blnSaveSettings.checked;
		
		if (bSaveSpeed) {        // Save speed
			var iSpeed = f.RRspeed.value;  // nuvarande/verklig
			var iSpeed0 = f.RRspeed0.value;// initial/jämförande
			setCookie('RRspeed', (iSpeed + '/' + iSpeed0));
		} else {
			document.cookie = "RRspeed=" + null;
		}
		if (bSaveSettings) {     // Save settings
			var iColorBg = f.RRcolorBg.selectedIndex;
			var iColorMask = f.RRcolorWorm.selectedIndex;
			var iColorText = f.RRcolorText.selectedIndex;
			var bOpacity = f.blnOpacity.checked;
			var iOpacityLev = f.RRopacityLevel.selectedIndex;
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
				sSettings += iColorBg+','+iColorMask+','+iColorText+',';
				sSettings += bOpacity+','+iOpacityLev;
			setCookie('RRsettings', escape(sSettings));
			//alert(sSettings);
		} else {
			document.cookie = "RRsettings=" + null;
		}
	}
}


/* *************************************
 *            Set Cookie ?             *
 ************************************* */

function setCookie(name, value) {
	var today = new Date();
	var expire = new Date();
	expire.setTime(today.getTime() + 1000*60*60*24*365); // 365 days
	document.cookie = name + "=" + escape(value) + ((expire == null) ? "" : ("; expires=" + expire.toGMTString()));
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
	if (theCookieSpeed != undefined)
	{
	if (theCookieSpeed.indexOf('/') != -1) {
		f.RRspeed.value = (theCookieSpeed.split("/")[0] >= 0) ? theCookieSpeed.split("/")[0] : iDefaultSpeed;
		f.RRspeed0.value = (theCookieSpeed.split("/")[1] >= 0) ? theCookieSpeed.split("/")[1] : iDefaultSpeed;
	}
	}
	var theCookieSettings = unescape(getCookie('RRsettings'));
	if (theCookieSettings.indexOf('/') != -1) {
		arrSettings = theCookieSettings.split(",")
		f.RRcolorBg.selectedIndex = (arrSettings[4] >= 0) ? arrSettings[4] : 0;
		f.RRcolorWorm.selectedIndex = (arrSettings[5] >= 0) ? arrSettings[5] : 0;
		f.RRcolorText.selectedIndex = (arrSettings[6] >= 0) ? arrSettings[6] : 0;
		f.cbp1.checked = (arrSettings[0].split("/")[0] == 'true') ? 1 : 0;
		f.p1.value = (arrSettings[0].split("/")[1] >= 0) ? arrSettings[0].split("/")[1] : iDefaultPausRow;
		f.cbp2.checked = (arrSettings[1].split("/")[0] == 'true') ? 1 : 0;
		f.p2.value = (arrSettings[1].split("/")[1] >= 0) ? arrSettings[1].split("/")[1] : iDefaultPausStop;
		f.cbp3.checked = (arrSettings[2].split("/")[0] == 'true') ? 1 : 0;
		f.p3.value = (arrSettings[2].split("/")[1] >= 0) ? arrSettings[2].split("/")[1] : iDefaultPausDiv;
		f.cbp4.checked = (arrSettings[3].split("/")[0] == 'true') ? 1 : 0;
		f.p4.value = (arrSettings[3].split("/")[1] >= 0) ? arrSettings[3].split("/")[1] : iDefaultPausScroll;
		f.blnOpacity.checked = (arrSettings[7] == 'true') ? 1 : 0;
		f.RRopacityLevel.selectedIndex = (arrSettings[8] >= 0) ? arrSettings[8] : 0;
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
 *            Move Layers              *
 ************************************* */


function hidePanel() {
	if (parseInt(document.all.RRpanelDiv.style.top) >= 0 && form1.blnHidePanel.checked) {
		moveLay('RRpanelDiv',0,-118,1);
		moveLay('RRcontentDiv',0,-118,1);
		moveLay('RRlogoSmallDiv','','',1);
	}
}

function showPanel() {
	if (parseInt(document.all.RRpanelDiv.style.top) < -100 && form1.blnHidePanel.checked) {
		moveLay('RRpanelDiv',0,118,1);
		moveLay('RRcontentDiv',0,118,1);
		moveLay('RRlogoSmallDiv','','',0);
	}
}

function moveLay(lay,x,y,vis) {
	var el = eval('document.all.' + lay + '.style');
	if (x != '') el.left = getCoords(lay,'x') + x;
	if (y != '') el.top = getCoords(lay,'y') + y;
	el.visibility = (!vis) ? 'hidden' : 'visible';
}

function getCoords(lay,coord) {
	var el = eval('document.all.' + lay + '.style');
	if (coord == 'x') return Number(parseInt(el.left));
	if (coord == 'y') return Number(parseInt(el.top));
}


/* *************************************
 *            SPEED SLIDER             *
 ************************************* */

var avWordLen = 5.34; // average chr/words

function RRinitSpeedCalc() {
	RRcheckSpeed();
}

function RRcheckSpeed(f) {
	cps = (f == 'RRspeed0') ? parseInt(form1.RRspeed0.value) : parseInt(form1.RRspeed.value);
	cps = (cps < 1 || isNaN(cps)) ? 1 : cps;
	cps = (cps > 150) ? 150 : cps;
	dWords = RRcalcSpeed();
	window.status = 'Hastighet: ' + cps + ' tkn/s  -  ' + dWords + ' ord/min';
	enableMouseClick();
}

function RRcheckSpeedVal(f) {
	cps = (f == 'RRspeed0') ? parseInt(form1.RRspeed0.value) : parseInt(form1.RRspeed.value);
	cps = (cps < 1 || isNaN(cps)) ? 1 : cps;
	cps = (cps > 150) ? 150 : cps;
	if (f == 'RRspeed0') {
		form1.RRspeed0.value = cps;
	} else {
		form1.RRspeed.value = cps;
	}
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
	elProc.innerText = isPos + dProc + '%'; // writes diff-percentage
	elWords.innerHTML = 'ca.<b>' + dWords.toString().replace(/\./, ',') + '</b>'; // writes words/min.
	return dWords;
}

function getClipVal(lay,dir) {
	var el = eval('document.all.' + lay);
	var clipVal = el.style.clip.split('rect(')[1].split(')')[0].split('px');
	if (dir == 't') return Number(clipVal[0]);
	if (dir == 'r') return Number(clipVal[1]);
	if (dir == 'b') return Number(clipVal[2]);
	if (dir == 'l') return Number(clipVal[3]);
}

function setClipVal(lay,t,r,b,l) {
	var el = eval('document.all.' + lay);
	el.style.clip = 'rect(' + t + 'px ' + r + 'px ' + b + 'px ' + l + 'px)';
	/*if (lay == 'RRsliderDiv') {
		if (r >= 100) {
			RRmax.innerText = r;
			r = 100;
		} else if (r <= 0) {
			r = 0;
		} else {
			RRmax.innerText = 100;
		}
	}*/
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
	enableMouseClick();
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

function fSpeedArrowUp(f){
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
}


/* *************************************
 *        Disable/Enable buttons       *
 ************************************* */

function RRdisableBtn(id) {
	var elBtn = eval('document.all.' + id);
	var elBtnImg = eval('document.all.' + id + 'img');
	elBtn.disabled = 1;
	elBtn.style.border = '1px inset #c0c0c0';
	elBtn.style.filter = 'Alpha(Opacity=30)';
	elBtn.style.cursor = 'default';
	elBtnImg.style.position = 'relative';
	elBtnImg.style.top = 1;
	elBtnImg.style.left = 1;
	elBtnImg.style.filter = 'Alpha(Opacity=30)';
}

function RRenableBtn(id) {
	var elBtn = eval('document.all.' + id);
	var elBtnImg = eval('document.all.' + id + 'img');
	elBtn.disabled = 0;
	elBtn.style.border = '1px outset #c0c0c0';
	elBtn.style.filter = '';
	elBtn.style.cursor = 'hand';
	elBtnImg.style.position = 'relative';
	elBtnImg.style.top = 0;
	elBtnImg.style.left = 0;
	elBtnImg.style.filter = '';
}

function RRoverBtn(elBtn) {
	if (!elBtn.disabled) {
		elBtn.style.border = '1px solid #333333';
	}
}

function RRoutBtn(elBtn) {
	if (!elBtn.disabled) {
		elBtn.style.border = '1px outset #c0c0c0';
	}
}


/* *************************************
 *        Show / Hide Help             *
 ************************************* */

var RRhelpOut = 0;

function RRshowHelp() {
	if (form1.stop.disabled) {
		var helpLay = document.all.RRhelpDiv;
		var helpBtnLay = document.all.RRhelpBtnHref;
		if (helpLay.style.visibility == 'hidden') {
			helpLay.style.visibility = 'visible';
			helpBtnLay.style.visibility = 'hidden';
			RRhelpOut = 1;
		} else {
			helpLay.style.visibility = 'hidden';
			helpBtnLay.style.visibility = 'visible';
			RRhelpOut = 0;
		}
	}
}

function RRshowHelpFull() {
	var helpLay = document.all.RRhelpDiv;
	var helpFullLay = document.all.RRhelpFullDiv;
	if (getClipVal('RRhelpDiv','b') == 105) {
		helpLay.style.height = 305;
		setClipVal('RRhelpDiv',0,460,305,0);
		helpFullLay.style.display = 'block';
	} else {
		helpLay.style.height = 105;
		setClipVal('RRhelpDiv',0,460,105,0);
		helpFullLay.style.display = 'none';
	}
}

function RRcheckLineBreaks() {
	var addText;
	var myText;
	var resultString;
	var oObject = document.all.item("RR1");
	if (oObject != null) {
		if (document.all("RR1").length == undefined) {
			antalLayers = 1;
			isOnlyOneLayer = 1;
		} else {
			antalLayers = document.all("RR1").length;
			isOnlyOneLayer = 0;
		}
		for (iCount1=0;iCount1<antalLayers;iCount1++) {
			resultString = "";
			oTextRange = document.body.createTextRange();
			if (isOnlyOneLayer) {
				oTextRange.moveToElementText(RR1);
			} else {
				oTextRange.moveToElementText(eval("RR1[" + iCount1 + "]"));
			}
			window.scrollTo(0,0);
			oRect = oTextRange.getClientRects();
			oldHTML = RRremoveDIV(oTextRange.htmlText);
			iCount=0;
			while ((oRect.length) > iCount) {
				window.scrollTo(oRect[iCount].left-10,oRect[iCount].top-10);
				oTextRange.moveToPoint(oRect[iCount].left-document.body.scrollLeft,oRect[iCount].top-document.body.scrollTop);
				tRange = -1;
				oDivH = oTextRange.boundingHeight;
				while (oDivH == oTextRange.boundingHeight && tRange != oTextRange.boundingWidth) {
					tRange = oTextRange.boundingWidth;
					oTextRange.moveEnd("word");
				}
				if (tRange != oTextRange.boundingWidth || oDivH != oTextRange.boundingHeight) {
					oTextRange.moveEnd("word",-1);
				}
				oldHTML = oldHTML.replace(/\r/gi,"");
				oldHTML = oldHTML.replace(/\n/gi,"");
				oldHTML = oldHTML.replace(/\t/gi,"");
				myText = "";
				myText = oTextRange.text;
				myTextLength = myText.length;
				test = 0;
				myCount = 0;
				do {
					myRe = /</g;
					myRe.lastIndex=test;
					myRe.exec(oldHTML.substr(0,(myTextLength+myCount)));
					test=myRe.lastIndex;
					last=test;
					if (test>0) {
						myRe = />/g;
						myRe.lastIndex=test;
						myRe.exec(oldHTML);
						test=myRe.lastIndex;
						myCount = myCount + (test - last + 1);
					}
				} while (test>0);
				resultString = resultString + oldHTML.substr(0,myTextLength+myCount)
				oldHTML = oldHTML.substr(myTextLength+myCount)
				if (/ $/.exec(resultString)) {
					resultString = resultString.substr(0,(resultString.length-1));
				}
				if (!/^<\/q/i.exec(oldHTML)) {
					if ((myTextLength+myCount) != 0) {
						if (/[\.,]$/.exec(resultString)) { // || 
							resultString = resultString + "\n";
						} else {
							resultString = resultString + "</Q><br>\n<Q>";
							RRlayers++;
						}
					}
				}
				iCount++;
			}
			resultString = resultString + oldHTML;
			if (isOnlyOneLayer) {
				document.all.RR1.innerHTML = resultString;
			} else {
				document.all.RR1[iCount1].innerHTML = resultString;
			}
			window.scrollTo(0,0);
		}
		RRinitSpeedCalc();
		RRinitWorm();
	}
}
function RRremoveDIV(myText) {
	var theLastValue,test,myRe;
	if (/</.exec(myText.substr(0,15))) {
		myArray1 = /</.exec(myText.substr(0,20));
		myArray2 = />/.exec(myText.substr(0,20));
		if (/RR1/.exec(myText.substr(myArray1.index,(myArray2.index-myArray1.index+1)))) {
			myText = myText.substr(0,myArray1.index) + myText.substr(myArray2.index+1);
			myRe = /</g;
			do {
				theLastValue = test;
				myRe.exec(myText);
				test = myRe.lastIndex;
			} while (test>0);
			theLastValue--;
			if (/div/i.exec(myText.substr(theLastValue))) {
				myText = myText.substr(0,theLastValue);
			}
		}
	}
	return myText;
}

function doScroll(obj){
	var Y1 = loopTop(obj);
	var X1 = loopLeft(obj);
	var Y2 = (Y1 + obj.offsetHeight);
	var X2 = (X1 + obj.offsetWidth);
	if (((X1 - scrollDistans) < X3) || ((Y1 - scrollDistans) < Y3) || ((X2 + (scrollDistans*2)) > X4) || ((Y2 + (scrollDistans*2)) > Y4)) {
		window.scrollTo(X1-scrollDistans, Y1-scrollDistans);
		updateScreenSize();
		return true;
	}else{
		return false;
	}
	//updateScreenSize();
}
function loopTop(obj){
	var prevPos = 0;
	if (obj.tagName != "BODY"){
		prevPos = loopTop(obj.offsetParent);}
	return (obj.offsetTop + prevPos);
}
function loopLeft(obj){
	var prevPos = 0;
	if (obj.tagName != "BODY"){
		prevPos = loopLeft(obj.offsetParent);}
	return (obj.offsetLeft + prevPos);
}
function updateScreenSize(){
	Y3 = document.body.scrollTop;
	Y4 = Y3 + document.body.offsetHeight;
	X3 = document.body.scrollLeft;
	X4 = X3 + document.body.offsetWidth;
}
