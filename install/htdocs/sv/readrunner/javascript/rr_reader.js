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
	if (isActive) setTimeout("history.go(0)", 1000);
}

function redirectMouseClick(parm){
	if (doFunctionCheck){
		//alert("DO");
		doFunctionCheck = false;
		document.all.RRwaitDiv.style.top = (parseInt(document.body.scrollTop) + (parseInt(document.body.offsetHeight) / 2) - 150);
		document.all.RRwaitDiv.style.display = 'block';
		if (parm == 'PlayButton'){
			//RRdisableBtn('reset');
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
	if (document.URL.indexOf('flags') == -1 || document.URL.indexOf('SaveText') == -1) {
		var itemX,itemY
		preloadImages();
		
		//RRdisableBtn('reset');
		RRdisableBtn('stop');
		RRdisableBtn('start');
		RRdisableBtn('RRmenu');
		RRcheckLineBreaks();
		window.scrollTo(0,0);
		RRsetDefaultSettings();
		//RRenableBtn('reset');
		RRenableBtn('start');
		RRenableBtn('RRmenu');
		RRremovePaddings();
	}
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
	document.styleSheets[0].addRule("Q", "height:0");
	RRcheckSpeed();
	RRpanelInit();
	
	window.setTimeout("infoPopHide()", 1000);
}

function infoPopHide() {
	if (infoWin) infoWin.close();
}

/*function RRhideWait() {
	document.getElementById("RRloadingDiv").style.visibility = 'hidden';
	RRcheckSpeed();
	enableMouseClick();
}*/

/* *************** Copy to buffer ************** */

function RRinitWorm(){
	document.getElementById("RRbufferDiv").innerHTML = document.getElementById("RRcontentDiv").innerHTML;
	//document.body.onScroll = "updateScreenSize();"
}


/* **************** "Play" button *************** */

function RRplay() {
	setTimeout("RRprepare()", 1000);
}

function RRprepare() {
	//RRdisableBtn('reset');
	RRdisableBtn('start');
	RRenableBtn('stop');
	RRcheckSpeed();
	/* Settings */
	var RRform = document.forms.form1;
	charsPerSec = RRform.RRspeed.value;
	theColorWorm = 'rgb(' + RRform.RRcolorWorm.value + ')';
	theColorBg = 'rgb(' + RRform.RRcolorBg.value + ')';
	theColorText = 'rgb(' + RRform.RRcolorText.value + ')';
	
	/* Build semi-transparent text - 80% of theColorWorm */
	theColorTextTransp = RRform.RRcolorWorm.value;
	var transpVal = RRform.RRopacityLevel.value;
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
			eval("paus" + n + " = (RRform.cbp" + n + ".checked) ? paus" + n + " : 0");
		}
		cps = charsPerSec;
		if (RRcounter > 1) { // right click start
			for (n = 1; n <= RRlayers; n++) {
				RRobj = document.all.tags("Q").item(n-1);
				RRarray[n] = RRobj.innerText.length;
				if (n < RRcounter) {
					//RRobj.style.backgroundColor = theColorBg;
					RRobj.style.color = theColorText;
				} else {
					RRobj.style.backgroundColor = theColorWorm;
					//RRobj.style.color = (RRform.blnOpacity.checked) ? theColorTextTransp : theColorWorm;
					RRobj.style.color = theColorTextTransp;
				}
			}
		} else { // original
			for (n = 1; n <= RRlayers; n++) {
				RRobj = document.all.tags("Q").item(n-1);
				RRarray[n] = RRobj.innerText.length;
				RRobj.style.backgroundColor = theColorWorm;
				//RRobj.style.color = (RRform.blnOpacity.checked) ? theColorTextTransp : theColorWorm;
				RRobj.style.color = theColorTextTransp;
			}
		}
		//RRform.blnOpacity.disabled = 1;
		//RRform.RRopacityLevel.disabled = 1;
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
	//RRdisableBtn('reset');
	RRenableBtn('start');
	RRdisableBtn('stop');
}

function RRnothing() {
	//RRenableBtn('reset');
	RRenableBtn('start');
	RRdisableBtn('stop');
	//setTimeout("showPanel()", 500);
}

function RRquit() {
	//RRenableBtn('reset');
	RRdisableBtn('start');
	RRdisableBtn('stop');
	//setTimeout("showPanel()", 500);
}


/* **************** Reset function ************** */

function RRreset_OLD() {
	//RRenableBtn('reset');
	RRenableBtn('start');
	RRdisableBtn('stop');
	//form1.blnOpacity.disabled = 0;
	//form1.RRopacityLevel.disabled = 0;
	window.scrollTo(0,0);
	//showPanel();
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

function RRreset() {
	RRenableBtn('start');
	RRdisableBtn('stop');
	window.scrollTo(0,0);
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
	} else {
		RRwait(RRcounter,didScroll);
	}
}

/* ***************** The Pauses ***************** */

function RRwait(divID,didScroll) {
	var f = document.forms.form1;
	var thePause = 0;
	if (runMode == "stop") {
		call = "RRnothing()";
		enableMouseClick();
	} else if (RRobj.filters[0].status == 2) {
	
		call = "RRwait(" + divID + "," + didScroll + ")";
		enableMouseClick();
	} else {
		call = (RRcounter > RRlayers) ? "RRquit()" : "RRworm()";
		enableMouseClick();
	}
	if (divID != null && (f.cbp1.checked || f.cbp2.checked || f.cbp3.checked || f.cbp4.checked)) {
		RRobj = document.all.tags("Q").item(divID-1);
		var theText = RRobj.innerText;
		
		if (f.cbp3.checked) { // ***** skiljetecken ****
			if (/[,;:–\/-]\s*$/g.test(theText)) thePause = paus3;
		}
		if (f.cbp2.checked) { //   ***** stopptecken *****
			if (/[\.!\?]\s*$/g.test(theText)) thePause = paus2;
		}
		
		if (f.cbp1.checked && divID < RRlayers) { // ***** radbrytning *****
			var thisY = parseInt(RRobj.offsetTop);
			var nextY = parseInt(document.all.tags("Q").item(divID).offsetTop);
			var thisH = parseInt(RRobj.offsetHeight);
			var nextH = parseInt(document.all.tags("Q").item(divID).offsetHeight);
			thePause = (nextY != thisY || nextH != thisH) ? paus1 : thePause;
		}
		if (f.cbp4.checked) { // ***** scrollning *****
			if (didScroll) thePause = paus4;
		}
		thePause *= 1.6; //                                  ***** Adjust factor *****
	}
	
	if (parseInt(thePause) > 0) {
		setTimeout(call, parseInt(thePause)+10);
	} else {
		setTimeout(call, 0);
	}
	//window.status = 'Pause: ' + thePause + '  divID: ' + divID + ' theText: ' + theText + ' pauses: ' + paus1 + '/' + paus2 + '/' + paus3 + '/' + paus4 + '' + f.cbp1.checked + f.cbp2.checked + f.cbp3.checked + f.cbp4.checked;
}


/* ********* Check User Key/Mouse Events ******** */

/* Checks keyboard clicks and left-mouse-clicks */

function RRcheckKey() {
	//if (!RRhelpOut) {
		kc = window.event.keyCode; //37 - 39 / 83 (S)
		mb = window.event.button;
		ctrlKey = window.event.ctrlKey;
		cps = parseInt(form1.RRspeed.value);
		cps += (kc==39)-(kc==37);
		cps = (cps > 150) ? 150 : cps;
		cps = (cps < 1) ? 1 : cps;
		form1.RRspeed.value=cps;
		if (ctrlKey || mb==1 || kc==83) {
			if (!document.getElementById("start").disabled) {
				//RRdisableBtn('reset');
				RRdisableBtn('start');
				RRplay();
			} else if (!document.getElementById("stop").disabled) {
				RRstop();
			}
		}
		RRcheckSpeed();
	//}
}

/* Checks right-clicks in Q-tags */

function RRcheckButton() {
	//if (!RRhelpOut) {
		mb = window.event.button;
		srcE = window.event.srcElement;
		if (mb == 2 && srcE.tagName == 'Q' && !document.getElementById("start").disabled) {
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
				//hidePanel();
				setTimeout("RRprepare()", 500);
			}
		}
	//}
}


/* *************************************
 *           Default Settings          *
 ************************************* */

var bDefaultSaveSpeed = 1;
var bDefaultSaveSettings = 1;
var iDefaultSpeed = 10;
var bDefaultPausRow = 1;
var iDefaultPausRow = 0.2;
var bDefaultPausStop = 1;
var iDefaultPausStop = 0.0;
var bDefaultPausDiv = 1;
var iDefaultPausDiv = 0.0;
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
	RRgetCookies();
}


/* *************************************
 *            Get Cookie ?             *
 ************************************* */


arrRROpacityLevels = new Array('0','0.3','0.375','0.450','0.525','0.6','0.675','0.750','0.825','0.9','0.975');

arrRRColorText = new Array('102,102,102','0,0,0','0,0,102','0,102,0');
arrRRColorWorm = new Array('153,204,255','153,204,204','153,204,153','204,204,204','153,153,153');


/* ****** Gets Cookie And Sets The Values. ****** *
 * ******* Sets Default if not existing ********* */
 
 /*
 Remade this function to work with the new "offline reader"
 It now checks for URL-parameters instead of existing cookie.
		...doc.html?s=60/10&tc=0&wc=0&ol=0.2&p1=true/0.2&p2=true/0.5&p3=false/0&p4=true/0.8
 */


function RRgetCookies() {
	var f = document.forms.form1;
	var s = getParam('s');
	var tc = getParam('tc');
	var wc = getParam('wc');
	var ol = getParam('ol');
	var p1 = getParam('p1');
	var p2 = getParam('p2');
	var p3 = getParam('p3');
	var p4 = getParam('p4');
	
	if (s.indexOf('/') != -1) f.RRspeed.value = (s.split("/")[0] >= 0) ? s.split("/")[0] : iDefaultSpeed;
	if (s.indexOf('/') != -1) f.RRspeed0.value = (s.split("/")[1] >= 0) ? s.split("/")[1] : iDefaultSpeed;

	//f.RRcolorBg.value = (arrSettings[4] >= 0) ? arrSettings[4] : 0;
	f.RRcolorText.value = (tc >= 0) ? arrRRColorText[tc] : arrRRColorText[0];
	f.RRcolorWorm.value = (wc >= 0) ? arrRRColorWorm[wc] : arrRRColorWorm[0];
	if (p1.indexOf('/') != -1) f.cbp1.checked = (p1.split("/")[0] == 1) ? 1 : 0;
	if (p1.indexOf('/') != -1) f.p1.value = (p1.split("/")[1] >= 0) ? p1.split("/")[1] : iDefaultPausRow;
	if (p2.indexOf('/') != -1) f.cbp2.checked = (p2.split("/")[0] == 1) ? 1 : 0;
	if (p2.indexOf('/') != -1) f.p2.value = (p2.split("/")[1] >= 0) ? p2.split("/")[1] : iDefaultPausStop;
	if (p3.indexOf('/') != -1) f.cbp3.checked = (p3.split("/")[0] == 1) ? 1 : 0;
	if (p3.indexOf('/') != -1) f.p3.value = (p3.split("/")[1] >= 0) ? p3.split("/")[1] : iDefaultPausDiv;
	if (p4.indexOf('/') != -1) f.cbp4.checked = (p4.split("/")[0] == 1) ? 1 : 0;
	if (p4.indexOf('/') != -1) f.p4.value = (p4.split("/")[1] >= 0) ? p4.split("/")[1] : iDefaultPausScroll;
	
	//f.blnOpacity.checked = (arrSettings[7] == 'true') ? 1 : 0;
	
	f.RRopacityLevel.value = (ol >= 0) ? arrRROpacityLevels[ol] : arrRROpacityLevels[0];
	//alert(f + ';' + s + ';' + tc + ';' + wc + ';' + ol + ';' + p1 + ';' + p2 + ';' + p3 + ';' + p4);
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
	cps = (f == 'RRspeed0') ? parseInt(form1.RRspeed0.value) : parseInt(form1.RRspeed.value);
	cps = (cps < 1 || isNaN(cps)) ? 1 : cps;
	cps = (cps > 150) ? 150 : cps;
	dWords = RRcalcSpeed();
	window.status = 'Hastighet: ' + cps + ' tkn/s  -  ' + dWords + ' ord/min';
	if (document.getElementById("rrSpeedLay") && document.getElementById("rrSpeedWordsLay")) {
		document.getElementById("rrSpeedLay").innerText = cps;
		document.getElementById("rrSpeedWordsLay").innerText = dWords;
	}
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
	var dWords = parseInt(60 * cps / avWordLen);
	return dWords;
}





/* *************************************
 *        Disable/Enable buttons       *
 ************************************* */

var RRstopDisabled = 1;
var RRstartDisabled = 1;



function RRdisableBtn(id) {
	var elBtn = eval('document.all.' + id);
	var elBtnImg = eval('document.all.' + id + 'Image');
	var elBtnCont = eval('document.all.' + id + 'Cont');
	if (elBtn && elBtnImg && elBtnCont) {
		elBtn.disabled = 1;
		if (id == 'stop') RRstopDisabled = 1;
		if (id == 'start') RRstartDisabled = 1;
		elBtn.style.cursor = 'default';
		elBtnImg.style.cursor = 'default';
		elBtnCont.style.cursor = 'default';
	}
}

function RRenableBtn(id) {
	var elBtn = eval('document.all.' + id);
	var elBtnImg = eval('document.all.' + id + 'Image');
	var elBtnCont = eval('document.all.' + id + 'Cont');
	if (elBtn && elBtnImg && elBtnCont) {
		elBtn.disabled = 0;
		if (id == 'stop') RRstopDisabled = 0;
		if (id == 'start') RRstartDisabled = 0;
		elBtn.style.cursor = 'hand';
		elBtnImg.style.cursor = 'hand';
		elBtnCont.style.cursor = 'hand';
	}
}

function RRoverBtn(elBtn) {
	/*if (!elBtn.disabled) {
		elBtn.style.border = '1px solid #c03333';
	}*/
}

function RRoutBtn(elBtn) {
	/*if (!elBtn.disabled) {
		elBtn.style.border = '1px outset #c0c0c0';
	}*/
}


function RRdividersOffInCookie() { /* checks the cookie if there are "skiljetecken" pauses (1 / 0) */
	//var retStop = 0;
	var retDiv = 1; // Off by default. If there are no cookie
	var f = document.forms.form1;
	var theCookieSettings = unescape(getCookie('RRsettings'));
	if (theCookieSettings.indexOf('/') != -1) {
		arrSettings = theCookieSettings.split("&")
		//var StopCheck = (arrSettings[1].split("/")[0] == 'true') ? 1 : 0;
		//var StopVal = (arrSettings[1].split("/")[1] >= 0) ? parseFloat(arrSettings[1].split("/")[1]) : parseFloat(iDefaultPausStop);
		var DivCheck = (arrSettings[2].split("/")[0] == 'true') ? 1 : 0;
		var DivVal = (arrSettings[2].split("/")[1] >= 0) ? parseFloat(arrSettings[2].split("/")[1]) : parseFloat(iDefaultPausDiv);
		//retStop = (StopCheck == 0 || StopVal == 0) ? 1 : 0;
		retDiv = (DivCheck == 0 || DivVal == 0) ? 1 : 0; // Off if cookie says > 0. On other if it doesn't.
	}
	return retDiv;
}

/* *************************************
 *        Linebreak check              *
 ************************************* */


function RRcheckLineBreaks() {
	var addText;
	var myText;
	var resultString;
	var oObject = document.all.item("RR1");
	var bSeperator;
	//if (unescape(getParam('readrunner_no_separators')) == '1'){
	if (RRdividersOffInCookie()) {
		bSeperator = false;}
	else{
		bSeperator = true;}
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
				do {
					myRe = /&/g;
					myRe.lastIndex=test;
					myRe.exec(oldHTML.substr(0,(myTextLength+myCount)));
					test=myRe.lastIndex;
					last=test;
					if (test>0) {
						myRe = /;/g;
						myRe.lastIndex=test;
						myRe.exec(oldHTML);
						if((test - last) < 6){
						test=myRe.lastIndex;
						myCount = myCount + (test - last);}
					}
				} while (test>0);
				resultString = resultString + oldHTML.substr(0,myTextLength+myCount)
				oldHTML = oldHTML.substr(myTextLength+myCount)
				if (/ $/.exec(resultString)) {
					resultString = resultString.substr(0,(resultString.length-1));
				}
				if (!/^<\/q/i.exec(oldHTML)) {
					if ((myTextLength+myCount) != 0) {
						if (/[\.,]$/.exec(resultString) && bSeperator) { // ||
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
		//window.scrollTo(X1-scrollDistans, Y1-scrollDistans);
		window.scrollTo(X1-scrollDistans, Y1-scrollDistans-40);
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



/* ***** START IMAGES ***** */

function preloadImages() {
	btnEye = new Array(2);
	for (i=0; i<=1; i++) {
		btnEye[i] = new Image();
	}
	btnEye[0].src = '/readrunner/images/btn_eye_anim.gif';
	btnEye[1].src = '/readrunner/images/btn_eye_arrows.jpg';
}


function eyeOver() {
  setTimeout('document.eyeBtn.src = btnEye[1].src', 0);
}

function eyeOut() {
  setTimeout('document.eyeBtn.src = btnEye[0].src', 0);
}



/* ***** END ***** */


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
