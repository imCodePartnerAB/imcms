
var ns = (document.layers) ? 1 : 0;
var ie = (document.all) ? 1 : 0;
var moz = (document.getElementById) ? 1 : 0;


var activeTd = '';
var active11Td = 'form112';
var active12Td = 'form123';
var active13Td = 'form139';


/* ***** Layers in use (short form) / Settings ***** */

arrLinks = new Array('nr1','nr2','nr3','nr4');
arrDivs = new Array('nr1','nr2','nr3','nr4','help');
arrSubDivs = new Array('s1','s2','s3','s4');

arrFormMainDivs = new Array('form1');
arrFormSubDivs = new Array('form11', 'form12', 'form13', 'form14', 'form15', 'form21', 'form22', 'form23', 'form24', 'form25', 'form26', 'form31', 'form32', 'form33');
arrForm11SubDivs = new Array('form111', 'form112', 'form113', 'form114');
arrForm12SubDivs = new Array('form121', 'form122', 'form123', 'form124', 'form125');

arrForm13SubDivs = new Array('form131', 'form132', 'form133', 'form134', 'form135', 'form136', 'form137', 'form138', 'form139', 'form1310', 'form1311');

arrForm13Values = new Array('0','0.3','0.375','0.450','0.525','0.6','0.675','0.750','0.825','0.9','0.975');
arrForm13Descriptions = new Array('100%','90%','80%','70%','60%','50%','40%','30%','20%','10%','5%');



/* ***** START onLoad ***** */

function setDefaultVis() {
	var vis = 0;
	arrSubLayVis = new Array('0','1','1','1'); // what sublayers should be visible by default?
	for (var i = 0; i < arrSubLayVis.length; i++) {
		vis = (arrSubLayVis[i] == '1') ? '3' : '0';
		showHideLay('s' + (i+1),vis);
		document.getElementById(arrSubDivs[i] + 'Div').style.display = (vis == '3') ? 'block' : 'none';
	}
}

function preloadImages() {
	btnEye = new Array(3);
	for (i=0; i<=2; i++) {
		btnEye[i] = new Image();
	}
	btnEye[0].src = 'images/btn_eye_0.gif';
	btnEye[1].src = 'images/btn_eye_1.gif';
	btnEye[2].src = 'images/btn_eye_2.gif';
}

/* ***** END onLoad ***** */


function showHideLay(lay,vis) {
	var el = '';
	if (ie) {
		el = eval('document.all.' + lay + 'Div.style');
	} else if (moz) {
		el = eval('document.getElementById("' + lay + 'Div").style');
	} else if (ns) {
		el = eval('document.' + lay + 'Div');
	}
	if (lay.indexOf('s') != -1) {
		document.getElementById(lay + 'Div').style.display = (el.visibility == 'hidden') ? 'block' : 'none';
	}
	switch (vis) {
		case '0':
			el.visibility = 'hidden';
		break
		case '1':
			el.visibility = 'visible';
		break
		case '3':
			el.visibility = 'inherit';
		break
		default:
			el.visibility = (el.visibility == 'hidden') ? 'inherit' : 'hidden';
		break
	}	
}


function formTdClick(layShort) {
	activeTd = layShort;
	hideAllFormSubLays();
	formTdOver(layShort);
	var el = '';
	var elTD = '';
	if (ie) {
		el = eval('document.all.' + layShort + 'Div');
		elTD = eval('document.all.' + layShort + 'TD');
	} else if (moz) {
		el = eval('document.getElementById("' + layShort + 'Div")');
		elTD = eval('document.getElementById("' + layShort + 'TD")');
	}
	el.style.visibility = 'visible';
	elTD.className = 'blackBold';
}


function form11Click(layShort) { // form111, form 112 ...
	f = eval('document.getElementById("' + layShort + 'Radio")');
	f.checked = 1;
	active11Td = layShort;
	for (var i = 0; i < arrForm11SubDivs.length; i++) {
		elTD = (ie) ? eval('document.all.' + arrForm11SubDivs[i] + 'TD') : eval('document.getElementById("' + arrForm11SubDivs[i] + 'TD")');
		elTD.className = 'grey';
	}
	elTD = (ie) ? eval('document.all.' + layShort + 'TD') : eval('document.getElementById("' + layShort + 'TD")');
	elTD.className = 'blackBold';
	//alert(layShort + ' - ' + active11Td);
}

function form12Click(layShort) { // form121, form 122 ...
	f = eval('document.getElementById("' + layShort + 'Radio")');
	f.checked = 1;
	active12Td = layShort;
	for (var i = 0; i < arrForm12SubDivs.length; i++) {
		elTD = (ie) ? eval('document.all.' + arrForm12SubDivs[i] + 'TD') : eval('document.getElementById("' + arrForm12SubDivs[i] + 'TD")');
		elTD.className = 'grey';
	}
	elTD = (ie) ? eval('document.all.' + layShort + 'TD') : eval('document.getElementById("' + layShort + 'TD")');
	elTD.className = 'blackBold';
	//alert(layShort + ' - ' + active12Td);
}

function form13Click(layShort) { // form131, form 132 ...
	f = eval('document.getElementById("' + layShort + 'Radio")');
	f.checked = 1;
	active13Td = layShort;
	for (var i = 0; i < arrForm13SubDivs.length; i++) {
		elTD = (ie) ? eval('document.all.' + arrForm13SubDivs[i] + 'TD') : eval('document.getElementById("' + arrForm13SubDivs[i] + 'TD")');
		elTD.className = 'grey';
	}
	elTD = (ie) ? eval('document.all.' + layShort + 'TD') : eval('document.getElementById("' + layShort + 'TD")');
	elTD.className = 'blackBold';
	//alert(layShort + ' - ' + active13Td);
}


function hideAllFormSubLays() {
	var el = '';
	for (var i = 0; i < arrFormSubDivs.length; i++) {
		formTdOut(arrFormSubDivs[i]);
		showHideLay(arrFormSubDivs[i],'0');
		if (ie) {
			el = eval('document.all.' + arrFormSubDivs[i] + 'TD');
		} else if (moz) {
			el = eval('document.getElementById("' + arrFormSubDivs[i] + 'TD")');
		}
		el.className = 'grey';
	}
}


function formTdOver(layShort) {
	var el = '';
	if (ie) {
		el = eval('document.all.' + layShort + 'TD.style');
	} else if (moz) {
		el = eval('document.getElementById("' + layShort + 'TD").style');
	}
	if (active11Td != layShort) el.background = '#f0f0f0';
}

function formTdOut(layShort) {
	var el = '';
	if (ie) {
		el = eval('document.all.' + layShort + 'TD.style');
	} else if (moz) {
		el = eval('document.getElementById("' + layShort + 'TD").style');
	}
	if (activeTd != layShort) el.background = '#ffffff';
}






/*
function regExpTest(string,pattern) {
	var blnRetVal = (pattern.test(string)) ? true : false;
	return blnRetVal;
}
*/

function moveLay(lay,x,y,vis) {
	var el = '';
	if (ie) {
		el = eval('document.all.' + lay + '.style');
	} else if (moz) {
		el = eval('document.getElementById("' + lay + '").style');
	} else if (ns) {
		el = eval('document.' + lay);
	}
	if (x != '') el.left = getCoords(lay,'x') + x;
	if (y != '') el.top = getCoords(lay,'y') + y;
	el.visibility = (!vis) ? 'hidden' : 'visible';
}


/*
function showHideLay(lay,vis) {
	var el = '';
	if (ie) {
		el = eval('document.all.' + lay + 'Div.style');
	} else if (moz) {
		el = eval('document.getElementById("' + lay + 'Div").style');
	} else if (ns) {
		el = eval('document.' + lay + 'Div');
	}
	if (lay.indexOf('s') != -1) {
		document.getElementById(lay + 'Div').style.display = (el.visibility == 'hidden') ? 'block' : 'none';
	}
	switch (vis) {
		case '0':
			el.visibility = 'hidden';
		break
		case '1':
			el.visibility = 'visible';
		break
		case '3':
			el.visibility = 'inherit';
		break
		default:
			el.visibility = (el.visibility == 'hidden') ? 'inherit' : 'hidden';
		break
	}	
}*/


function resetLinks() {
	if (moz) {
		for (var i = 0; i < arrLinks.length; i++) {
			document.getElementById(arrLinks[i] + 'Li'+'nk').style.color = '';
		}
	}
}

function activateLink(id) {
	resetLinks();
	if (moz) {
		document.getElementById(id + 'Li'+'nk').style.color = '#ffffcc';
	}
}

function showDiv(id) {
	hideAllLays();
	hideAllFormSubLays();
	showHideLay(id,'3');
	if (id.indexOf('s') != -1) {
		document.getElementById(id + 'Div').style.display = 'block';
	}
	if (id == 'help') {
		resetLinks();
	} else {
		activateLink(id);
	}
}

function hideAllLays() {
	for (var i = 0; i < arrDivs.length; i++) {
		moveLay(arrDivs[i] + 'Div','','',0);
	}
}


function eyeOver() {
	if (document.layers) {
	  setTimeout('document.layers.navDiv.document.images.eyeBtn.src = btnEye[1].src', 0);
	  setTimeout('document.layers.navDiv.document.images.eyeBtn.src = btnEye[2].src', 100);
	} else {
	  setTimeout('document.eyeBtn.src = btnEye[1].src', 0);
	  setTimeout('document.eyeBtn.src = btnEye[2].src', 100);
	}
}

function eyeOut() {
	if (document.layers) {
	  setTimeout('document.layers.navDiv.document.images.eyeBtn.src = btnEye[1].src', 0);
	  setTimeout('document.layers.navDiv.document.images.eyeBtn.src = btnEye[0].src', 100);
	} else {
	  setTimeout('document.eyeBtn.src = btnEye[1].src', 0);
	  setTimeout('document.eyeBtn.src = btnEye[0].src', 100);
	}
}
