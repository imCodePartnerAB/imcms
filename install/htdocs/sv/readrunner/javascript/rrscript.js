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
	setTimeout("history.go(0)", 1000);
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
		/*if (document.URL.indexOf('readrunner_no_') == -1) { // if there are no parameters - check if there should be...
			var doNotParseStop = RRcheckCookieForPauses()[0];
			var doNotParseDiv = RRcheckCookieForPauses()[1];
			var theURL = document.URL;
			if (doNotParseStop) theURL += '&readrunner_no_stops=1';
			if (doNotParseDiv) theURL += '&readrunner_no_separators=1';
			if (doNotParseStop || doNotParseDiv) document.location = theURL;
			//alert('StopAv: ' + doNotParseStop + ' / DivAv: ' + doNotParseDiv + '\n\nURL: ' + theURL);
		}*/
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
	//window.setTimeout("RRhideWait()", 500); // How long banner is shown after pageload...
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
			eval("paus" + n + " = (form1.cbp" + n + ".checked) ? paus" + n + " : 0");
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

function RRreset() {
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
	if (divID != null && (form1.cbp1.checked || form1.cbp2.checked || form1.cbp3.checked || form1.cbp4.checked)) {
		RRobj = document.all.tags("Q").item(divID-1);
		var theText = RRobj.innerText;
		
		if (form1.cbp3.checked) { // ***** skiljetecken ****
			if (/[,;:–\/-]\s*$/g.test(theText)) thePause = paus3;
		}
		if (form1.cbp2.checked) { //   ***** stopptecken *****
			if (/[\.!\?]\s*$/g.test(theText)) thePause = paus2;
		}
		
		if (form1.cbp1.checked && divID < RRlayers) { // ***** radbrytning *****
			var thisY = parseInt(RRobj.offsetTop);
			var nextY = parseInt(document.all.tags("Q").item(divID).offsetTop);
			var thisH = parseInt(RRobj.offsetHeight);
			var nextH = parseInt(document.all.tags("Q").item(divID).offsetHeight);
			thePause = (nextY != thisY || nextH != thisH) ? paus1 : thePause;
		}
		if (form1.cbp4.checked) { // ***** scrollning *****
			if (didScroll) thePause = paus4;
		}
		thePause *= 1.6; //                                  ***** Adjust factor *****
	}
	
	if (parseInt(thePause) > 0) {
		setTimeout(call, parseInt(thePause)+10);
	} else {
		setTimeout(call, 0);
	}
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

function RRgetCookies() {
	var f = document.forms.form1;
	var blnCookieName = false;
	var blnCookieValues = false;
	var theCookieString = unescape(document.cookie);
	var theCookieSpeed = getCookie('RRspeed');
	if (theCookieSpeed != undefined) {
		if (theCookieSpeed.indexOf('/') != -1) {
			f.RRspeed.value = (theCookieSpeed.split("/")[0] >= 0) ? theCookieSpeed.split("/")[0] : iDefaultSpeed;
			f.RRspeed0.value = (theCookieSpeed.split("/")[1] >= 0) ? theCookieSpeed.split("/")[1] : iDefaultSpeed;
		}
	}
	var theCookieSettings = unescape(getCookie('RRsettings'));
	if (theCookieSettings.indexOf('/') != -1) {
		arrSettings = theCookieSettings.split(",")
		//f.RRcolorBg.value = (arrSettings[4] >= 0) ? arrSettings[4] : 0;
		f.RRcolorText.value = (arrSettings[5] >= 0) ? arrRRColorText[arrSettings[5]] : arrRRColorText[0];
		f.RRcolorWorm.value = (arrSettings[6] >= 0) ? arrRRColorWorm[arrSettings[6]] : arrRRColorWorm[0];
		f.cbp1.checked = (arrSettings[0].split("/")[0] == 'true') ? 1 : 0;
		f.p1.value = (arrSettings[0].split("/")[1] >= 0) ? arrSettings[0].split("/")[1] : iDefaultPausRow;
		f.cbp2.checked = (arrSettings[1].split("/")[0] == 'true') ? 1 : 0;
		f.p2.value = (arrSettings[1].split("/")[1] >= 0) ? arrSettings[1].split("/")[1] : iDefaultPausStop;
		f.cbp3.checked = (arrSettings[2].split("/")[0] == 'true') ? 1 : 0;
		f.p3.value = (arrSettings[2].split("/")[1] >= 0) ? arrSettings[2].split("/")[1] : iDefaultPausDiv;
		f.cbp4.checked = (arrSettings[3].split("/")[0] == 'true') ? 1 : 0;
		f.p4.value = (arrSettings[3].split("/")[1] >= 0) ? arrSettings[3].split("/")[1] : iDefaultPausScroll;
		//f.blnOpacity.checked = (arrSettings[7] == 'true') ? 1 : 0;
		f.RRopacityLevel.value = (arrSettings[8] >= 0) ? arrRROpacityLevels[arrSettings[8]] : arrRROpacityLevels[0];
	}
}
/*
function RRcheckCookieForPauses() {
	var retStop = 0;
	var retDiv = 0;
	var f = document.forms.form1;
	var theCookieSettings = unescape(getCookie('RRsettings'));
	if (theCookieSettings.indexOf('/') != -1) {
		arrSettings = theCookieSettings.split(",")
		var StopCheck = (arrSettings[1].split("/")[0] == 'true') ? 1 : 0;
		var StopVal = (arrSettings[1].split("/")[1] >= 0) ? parseFloat(arrSettings[1].split("/")[1]) : parseFloat(iDefaultPausStop);
		var DivCheck = (arrSettings[2].split("/")[0] == 'true') ? 1 : 0;
		var DivVal = (arrSettings[2].split("/")[1] >= 0) ? parseFloat(arrSettings[2].split("/")[1]) : parseFloat(iDefaultPausDiv);
		retStop = (StopCheck == 0 || StopVal == 0) ? 1 : 0;
		retDiv = (DivCheck == 0 || DivVal == 0) ? 1 : 0;
	}
	arrRet = new Array(retStop,retDiv)
	return arrRet;
}*/


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
	/*var cps0 = parseInt(form1.RRspeed0.value);
	var elProc = document.getElementById("RRpercent");
	var elWords = document.getElementById("RRwords");
	
	var dProc = (parseInt(cps / cps0 * 1000) / 10) - 100;
	dProc = Math.round(dProc * 10) / 10;*/
	var dWords = parseInt(60 * cps / avWordLen);
	
	/*var isPos = (dProc > 0) ? '+' : '';
	if (dProc == 0) isPos = String.fromCharCode(177); // "+/-"
	elProc.innerText = isPos + dProc + '%'; // writes diff-percentage
	elWords.innerHTML = 'ca.<b>' + dWords.toString().replace(/\./, ',') + '</b>'; // writes words/min.*/
	return dWords;
}





/* *************************************
 *        Disable/Enable buttons       *
 ************************************* */

var RRstopDisabled = 1;
var RRstartDisabled = 1;



function RRdisableBtn(id) {
	var elBtn = eval('document.all.' + id);
	if (elBtn) {
		elBtn.disabled = 1;
		if (id == 'stop') RRstopDisabled = 1;
		if (id == 'start') RRstartDisabled = 1;
		//elBtn.style.border = '1px solid #c0c0c0';
		elBtn.style.filter = 'gray';
		elBtn.style.cursor = 'default';
	}
}

function RRenableBtn(id) {
	var elBtn = eval('document.all.' + id);
	if (elBtn) {
		elBtn.disabled = 0;
		if (id == 'stop') RRstopDisabled = 0;
		if (id == 'start') RRstartDisabled = 0;
		//elBtn.style.border = '1px outset #c0c0c0';
		elBtn.style.filter = '';
		elBtn.style.cursor = 'hand';
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


/* *************************************
 *        Linebreak check              *
 ************************************* */


function RRcheckLineBreaks() {
	var addText;
	var myText;
	var resultString;
	var oObject = document.all.item("RR1");
	var bSeperator;
	if (unescape(getParam('readrunner_no_separators')) == '1'){
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


/* *************************************
 *        The floating panel           *
 ************************************* */


var sPos = '3'; // 1-8 from top-left clockwise
var iObjW = 255;
var iObjH = 36;
var iObjOffsetX = 0;
var iObjOffsetY = 0;

//code mady by www.bratta.com can be 
//used freely as long as this msg is intact

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

var RRpageWidth,RRpageHeight

function RRpanelInit() {
	if (sPos == '2' || sPos == '6') { // horizontal panel
		iObjW = 249;
		iObjH = 36;
		document.getElementById("RRpanelStandingDiv").innerHTML = '';
		document.getElementById("RRpanelStandingDiv").style.display = 'none';
		document.getElementById("RRpanelDiv").style.display = 'block';
		document.getElementById("RRpanelDiv").innerHTML = '<table border="0" cellpadding="0" cellspacing="0" style="border-left: 1px solid black; border-bottom: 1px solid black; filter: progid:DXImageTransform.Microsoft.Gradient(StartColorStr=#ffFFFFFF, EndColorStr=#ffEEEEEE, GradientType=1)"><tr><td><img name="eyeBtn" src="@readrunnerimagesurl@/btn_eye_anim.gif" width="35" height="35" alt="" border="0" usemap="#RRposArrows" onMouseOver="eyeOver()" onMouseOut="eyeOut()"></td><td><img src="@readrunnerimagesurl@/1x1.gif" width="2" height="1"></td><td><div id="RRpanelBtn" style="display:block"><table border="0" cellpadding="0" cellspacing="0"><tr><td><input type="image" name="stop" id="stop" title="Stoppa" value="Stop" onClick="RRstop()" disabled src="@readrunnerimagesurl@/btn_panel_stopp.gif" width="57" height="13" alt="" border="0"></td><td><img src="@readrunnerimagesurl@/1x1.gif" width="2" height="1"></td><td><input type="image" name="start" id="start" disabled title="Starta" value="Go!" onClick="RRdisableBtn(\'start\'); RRplay()" src="@readrunnerimagesurl@/btn_panel_start.gif" width="57" height="13" alt="" border="0"></td><td><img src="@readrunnerimagesurl@/1x1.gif" width="2" height="1"></td><td><input type="image" name="RRmenu" id="RRmenu" onClick="RRopenSettings(); return false" src="@readrunnerimagesurl@/btn_panel_meny.gif" width="57" height="13" alt="" border="0"></td></tr></table></div><div id="RRpanelTxt" style="width:175; font: 10px/12px Verdana, sans-serif; display:none; text-align:center"></div></td><td><img src="@readrunnerimagesurl@/1x1.gif" width="2" height="1"></td><td><input type="image" onClick="RRopenHelp(); return false" src="@readrunnerimagesurl@/btn_panel_help.gif" width="34" height="33" alt="" border="0"></td></tr></table>';
		RRpanelObj = new RRmakeObj('RRpanelDiv');
	} else { // vertical panel
		iObjW = 62;
		iObjH = 116;
		document.getElementById("RRpanelDiv").innerHTML = '';
		document.getElementById("RRpanelDiv").style.display = 'none';
		document.getElementById("RRpanelStandingDiv").style.display = 'block';
		document.getElementById("RRpanelStandingDiv").innerHTML = '<table border="0" cellpadding="0" cellspacing="0" style="border-left: 1px solid black; border-bottom: 1px solid black; filter: progid:DXImageTransform.Microsoft.Gradient(StartColorStr=#ffFFFFFF, EndColorStr=#ffEEEEEE, GradientType=0)"><tr><td align="center"><img name="eyeBtn" src="@readrunnerimagesurl@/btn_eye_anim.gif" width="35" height="35" alt="" border="0" usemap="#RRposArrows" onMouseOver="eyeOver()" onMouseOut="eyeOut()"></td></tr><tr><td><img src="@readrunnerimagesurl@/1x1.gif" width="1" height="2"></td></tr><tr><td align="center"><div id="RRpanelBtn" style="display:block"><table border="0" cellpadding="0" cellspacing="0"><tr><td><input type="image" name="stop" id="stop2" title="Stoppa" value="Stop" onClick="RRstop()" disabled src="@readrunnerimagesurl@/btn_panel_stopp.gif" width="57" height="13" alt="" border="0" hspace="2"></td></tr><tr><td><img src="@readrunnerimagesurl@/1x1.gif" width="1" height="2"></td></tr><tr><td><input type="image" name="start" id="start" title="Starta" value="Go!" onClick="RRdisableBtn(\'start\'); RRplay()" disabled src="@readrunnerimagesurl@/btn_panel_start.gif" width="57" height="13" alt="" border="0" hspace="2"></td></tr><tr><td><img src="@readrunnerimagesurl@/1x1.gif" width="1" height="2"></td></tr><tr><td><input type="image" name="RRmenu" id="RRmenu" onClick="RRopenSettings(); return false" src="@readrunnerimagesurl@/btn_panel_meny.gif" width="57" height="13" alt="" border="0" hspace="2"></td></tr></table></div><div id="RRpanelTxt" style="width:61; height:43; font: 10px/12px Verdana, sans-serif; display:none; text-align:center"></div></td></tr><tr><td><img src="@readrunnerimagesurl@/1x1.gif" width="1" height="2"></td></tr><tr><td align="center"><input type="image" onClick="RRopenHelp(); return false" src="@readrunnerimagesurl@/btn_panel_help.gif" width="34" height="33" alt="" border="0"></td></tr></table>';
		RRpanelObj = new RRmakeObj('RRpanelStandingDiv');
	}
	RRpageWidth = document.body.offsetWidth - 4;
	RRpageHeight = document.body.offsetHeight - 4;
	RRcheckIt();
	window.onscroll = RRcheckIt;
	if (!RRstopDisabled) RRenableBtn('stop');
	if (!RRstartDisabled) RRenableBtn('start');
}

function RRcheckIt() {
	var X_left = document.body.scrollLeft + iObjOffsetX;
	var X_mid = ((RRpageWidth - iObjW) / 2) + document.body.scrollLeft;
	var X_right = document.body.scrollLeft + RRpageWidth - (iObjW + iObjOffsetX + 16);
	var Y_top = document.body.scrollTop + iObjOffsetY;
	var Y_mid = ((RRpageHeight - iObjH) / 2) + document.body.scrollTop;
	var Y_bottom = document.body.scrollTop + RRpageHeight - (iObjH + iObjOffsetY);
	switch(sPos) {
		case "1": // TL
			RRpanelObj.RRmoveIt(X_left,Y_top);
			break
		case "2": // T
			RRpanelObj.RRmoveIt(X_mid,Y_top);
			break
		case "4": // R
			RRpanelObj.RRmoveIt(X_right,Y_mid);
			break
		case "5": // BR
			RRpanelObj.RRmoveIt(X_right,Y_bottom);
			break
		case "6": // B
			RRpanelObj.RRmoveIt(X_mid,Y_bottom);
			break
		case "7": // BL
			RRpanelObj.RRmoveIt(X_left,Y_bottom);
			break
		case "8": // L
			RRpanelObj.RRmoveIt(X_left,Y_mid);
			break
		default: // TR
			RRpanelObj.RRmoveIt(X_right,Y_top);
	}
}


function RRpanelPrintHTML(txt) {
	if (txt != '') {
		arrTxt = new Array();
		arrTxt[0] = '<b>Flytta panelen till plats:</b><br>Övre vänstra';
		arrTxt[1] = '<b>Flytta:</b><br>Övre vänstra';
		arrTxt[2] = '<b>Flytta panelen till plats:</b><br>Övre mitten';
		arrTxt[3] = '<b>Flytta:</b><br>Övre mitten';
		arrTxt[4] = '<b>Flytta panelen till plats:</b><br>Övre höger';
		arrTxt[5] = '<b>Flytta:</b><br>Övre höger';
		arrTxt[6] = '<b>Flytta panelen till plats:</b><br>Höger mitten';
		arrTxt[7] = '<b>Flytta:</b><br>Höger mitten';
		arrTxt[8] = '<b>Flytta panelen till plats:</b><br>Botten höger';
		arrTxt[9] = '<b>Flytta:</b><br>Botten höger';
		arrTxt[10] = '<b>Flytta panelen till plats:</b><br>Botten mitten';
		arrTxt[11] = '<b>Flytta:</b><br>Botten mitten';
		arrTxt[12] = '<b>Flytta panelen till plats:</b><br>Botten vänster';
		arrTxt[13] = '<b>Flytta:</b><br>Botten vänster';
		arrTxt[14] = '<b>Flytta panelen till plats:</b><br>Vänster mitten';
		arrTxt[15] = '<b>Flytta:</b><br>Vänster mitten';
		var elBtn = document.getElementById("RRpanelBtn");
		var elTxt = document.getElementById("RRpanelTxt");
		elBtn.style.display = 'none';
		elTxt.style.display = 'block';
		elTxt.innerHTML = (sPos == '2' || sPos == '6') ? arrTxt[txt] : arrTxt[parseInt(txt) + 1];
		/*var theText = eval('sTxt' + txt + sLetter);
		alert(theText);
		elTxt.innerHTML = 'sTxt' + txt + sLetter;*/
	} else {
		var elBtn = document.getElementById("RRpanelBtn");
		var elTxt = document.getElementById("RRpanelTxt");
		elTxt.innerHTML = '';
		elTxt.style.display = 'none';
		elBtn.style.display = 'block';
	}
}


function RRopenSettings() {
	var iMetaId = unescape(getParam('meta_id'));
	var sTempl = (unescape(getParam('template')) != '') ? '&template=' + unescape(getParam('template')) : '';
	document.location = '@readrunnerurl@/readrunner.html?meta_id=' + iMetaId + sTempl;
}

function RRopenHelp() {
	var iMetaId = unescape(getParam('meta_id'));
	document.location = '@readrunnerurl@/readrunner.html?meta_id=' + iMetaId + '&lay=help';
}



/* ***** START IMAGES ***** */

function preloadImages() {
	btnEye = new Array(2);
	for (i=0; i<=1; i++) {
		btnEye[i] = new Image();
	}
	btnEye[0].src = '@readrunnerimagesurl@/btn_eye_anim.gif';
	btnEye[1].src = '@readrunnerimagesurl@/btn_eye_arrows.jpg';
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
