/* **********************************
 *   By: Tommy Ullberg, imCode
 *   Copyright © imCode AB
 *   www.imcode.com
 ********************************* */


/* Write extra JS's */

if (isWordEnabled) document.writeln('<SCR'+'IPT LANGUAGE="JavaScript" TYPE="text/javascript" SRC="scripts/editor_functions_wordsupport.js"></SCR'+'IPT>');

/* Write the CSS's */

for (var i = 0; i < arrCssPaths.length; i++) {
	if (arrCssPaths[i] != '') document.writeln('<li' + 'nk rel="STYLESHEET" type="text/css" href="' + arrCssPaths[i] + '">');
}
if (isWordEnabled) {
	for (var i = 0; i < arrCssWordPaths.length; i++) {
		if (arrCssWordPaths[i] != '') document.writeln('<li' + 'nk rel="STYLESHEET" type="text/css" href="' + arrCssWordPaths[i] + '">');
	}
}


/* Functions */

var metaId, txtNr, sLabel
if (isModal) {
	var oArgs = window.dialogArguments;
	var metaId = oArgs.metaId;
	var txtNr = oArgs.txtNr;
	var sLabel = '';

} else {
	var metaId = getParam('meta_id');
	var txtNr = getParam('txt');
	var sLabel = getParam('label');
}




var numberOfTables = 0;
var tableInfoWindow;

function init() {
	//alert(document.frames['changeTextFrame'].document.forms[0]);
	document.getElementById("javascriptDisabled").style.display = "none";
	//var textToEdit = ((directEditEnabled && !directEditViaChangeText) || isModal) ? document.frames['changeTextFrame'].document.forms[0].text.value : parent.opener.document.forms[0].text.value;
	var textToEdit = theSavedCodeDiv.innerHTML ;

	/* fix links and other paths */
	textToEdit = fixOnLoad(textToEdit);

	/* mode */

	if(textToEdit.indexOf('<') < 0 && textToEdit.indexOf('>') < 0){
		//alert('TEXT');
		editorDiv.innerText = textToEdit;
	} else {
		//alert('HTML');
		editorDiv.innerHTML = textToEdit;
	}
	editorDiv.focus();

	var tableNodeList = document.getElementsByTagName('table');
	numberOfTables = tableNodeList.length;
	for ( i = 0 ; i < numberOfTables ; i++ ) {
		tableNodeList[i].name = "table" + (i+1);
	}
	document.forms[0].execState.value = '1';
	if (showSimple && document.forms[0].simpleSelector) document.forms[0].simpleSelector.selectedIndex = 0;
	if (showAdv && document.forms[0].advSelector) document.forms[0].advSelector.selectedIndex = 0;
	editorDiv.focus();
	setDefaultColors();
	editorDiv.style.width = 525;
	if (!showSimple && !showAdv) { // nothing is showing
		editorDiv.style.width = 675;
	}
	checkPlacementForLayers();
	if (showSimple) {
		if (showSimpleLinkDiv) {
			showSimpleFunction('modeSimpleLinkDiv')
		} else if (showSimpleListDiv) {
			showSimpleFunction('modeSimpleListDiv')
		} else if (showSimplePixelDiv) {
			showSimpleFunction('modeSimplePixelDiv')
		}/*
		moveLayer('modeSimpleDiv',null,null,1,null);
		moveLayer('modeSimpleLinkDiv',null,null,1,null);*/
	} else {
		showAdvanced();
		document.getElementById("advBtn").disabled = 1;
	}
	/* do a "Save" */
	theSavedCodeDiv.innerHTML = editorDiv.innerHTML;
	if (document.frames['changeTextFrame']) {
		if (document.frames['changeTextFrame'].document.forms[0]) {
			if (document.frames['changeTextFrame'].document.forms[0].text)
				document.frames['changeTextFrame'].document.forms[0].text.value = editorDiv.innerHTML
		}
	}

	/* focus */
	if (editorDiv) editorDiv.focus();
}

// top 80/50:
arrMain80Layers = new Array('editorOuterDiv','editorDiv','helpDiv','helpTextAllDiv','helpTextAllUpperDiv','helpTextAllRightDiv','helpDescRightDiv','helpAllDescRightDiv','modeHtmlCodeDiv');
arrRight80Layers = new Array('modeSimpleDiv','modeAdvancedDiv');
// top 120/90:
arrMain120Layers = new Array('helpSubjectDiv');
// top 140/110:
arrRight140Layers = new Array('modeColorDiv');
// top 150/120:
arrRight150Layers = new Array('modeAdvStandardDiv');
// top 155/125:
arrRight155Layers = new Array('modeSimpleLinkDiv','modeSimpleListDiv','modeSimplePixelDiv','modeAdvFontClassDiv','modeAdvFontStyleDiv','modeAdvCodeStringDiv','modeAdvSettingsDiv');






function checkPlacementForLayers() {
	if (arrButtonsRowTwo.length == 0) { // no second row of buttons
		for (var i = 0; i < arrMain80Layers.length; i++) {
			if (document.getElementById(arrMain80Layers[i])) document.getElementById(arrMain80Layers[i]).style.top = 50;
			if (document.getElementById(arrMain80Layers[i])) document.getElementById(arrMain80Layers[i]).style.height = 410;
		}
		for (var i = 0; i < arrRight80Layers.length; i++) {
			if (document.getElementById(arrRight80Layers[i])) document.getElementById(arrRight80Layers[i]).style.top = 50;
		}
		for (var i = 0; i < arrMain120Layers.length; i++) {
			if (document.getElementById(arrMain120Layers[i])) document.getElementById(arrMain120Layers[i]).style.top = 90;
			if (document.getElementById(arrMain120Layers[i])) document.getElementById(arrMain120Layers[i]).style.height = 368;
		}
		for (var i = 0; i < arrRight140Layers.length; i++) {
			if (document.getElementById(arrRight140Layers[i])) document.getElementById(arrRight140Layers[i]).style.top = 110;
		}
		for (var i = 0; i < arrRight150Layers.length; i++) {
			if (document.getElementById(arrRight150Layers[i])) document.getElementById(arrRight150Layers[i]).style.top = 120;
		}
		for (var i = 0; i < arrRight155Layers.length; i++) {
			if (document.getElementById(arrRight155Layers[i])) document.getElementById(arrRight155Layers[i]).style.top = 125;
		}
		if (document.getElementById("disableTopBtnDiv")) document.getElementById("disableTopBtnDiv").style.height = 27;
		if (document.getElementById("disableRightBtnDiv")) document.getElementById("disableRightBtnDiv").style.top = 72;
		if (document.getElementById("disableRightBtnDiv")) document.getElementById("disableRightBtnDiv").style.height = 385;
	}
}


function setDefaultColors(){
	var editorLayer = document.getElementById("editorDiv");
	editorLayer.style.width = cssDefaultWidth;
	editorLayer.style.color = cssDefaultColorFont;
	editorLayer.style.backgroundColor = cssDefaultColorBackground;
	editorLayer.style.fontFamily = cssDefaultFontFamily;
	editorLayer.style.fontSize = cssDefaultFontSize;
	document.forms.editorForm.previewWidth.value = cssDefaultWidth;
	document.forms.editorForm.previewColor.value = cssDefaultColorFont;
	document.forms.editorForm.previewBackground.value = cssDefaultColorBackground;
	document.forms.editorForm.previewFontFamily.value = cssDefaultFontFamily;
	document.forms.editorForm.previewFontSize.value = cssDefaultFontSize;
	getCookies();
}


/* ******************************** */
/*                SAVE              */
/* ******************************** */

var arrTabBeforeElements = new Array('<TD','<LI','<DT','<DD','<BLOCKQUOTE');
var arrOneBreakBeforeElements = new Array('</TABLE>','</TBODY>','</TR>','</BLOCKQUOTE>','</DL>','</OL>','</UL>');
var arrTwoBreakBeforeElements = new Array();
var arrOneBreakAfterElements = new Array('<BR>','</DL>','</OL>','</UL>');
var arrTwoBreakAfterElements = new Array('</P>','</BLOCKQUOTE>');

var timer;

function doSend() {
	var f = document.forms.saveForm ;
	var doSave = (confirmSave) ? 0 : 1;
	if (confirmSave) {
		doSave = confirm('<? install/htdocs/sv/htmleditor/scripts/editor_functions.js/1 ?>');
	}
	if (doSave) {
		var editedCode            = editorDiv.innerHTML ;
		var originalCode          = theOriginalCodeDiv.innerHTML ;
		theSavedCodeDiv.innerHTML = editedCode ;
		editedCode                = fixHTML(editedCode) ;
		editedCode                = replaceParagraphs(editedCode) ;
		f.orgContent.value        = originalCode ;
		f.txtContent.value        = editedCode ;
		f.submit() ;
	}
}

function checkSaved() {
	if (document.frames['changeTextFrame'] && parent.opener) {
		if (document.frames['changeTextFrame'].document.URL.indexOf('SaveText') != -1) {
			if (timer) clearInterval(timer);
			window.close();
			if (/\d{4,}/.test(metaId)) {
				parent.opener.location.href = servletPath + "AdminDoc?meta_id=" + metaId + "&flags=65536";
			} else {
				parent.opener.location.reload();
			}
		}
	}
}

/* fix on load and refresh */

function fixOnLoad(theString) {
	//alert(theString);
	for (var i = 0; i < arrWrongPaths.length; i++) {
		if (arrWrongPaths[i] != "") {
			re = new RegExp(arrWrongPaths[i] + "user\\\/editor\\\/(GetDoc\\\?)", "gi");
			theString = theString.replace(re,strRightPath + "servlet/$1");
			re = new RegExp(arrWrongPaths[i] + "user\\\/editor\\\/", "gi");
			theString = theString.replace(re,strRightPath);
			re = new RegExp(arrWrongPaths[i], "gi");
			theString = theString.replace(re,strRightPath);
		}
	}
	for (var i = 0; i < arrServletNames.length; i++) {
		if (arrServletNames[i] != "") {
			if (/(GetDoc|AdminDoc)/.test(arrServletNames[i])) {
				re = new RegExp("(['\\\"])(" + arrServletNames[i] + "\\\?meta_id=)", "gi"); // "GetDoc?meta_id=
			} else {
				re = new RegExp("(['\\\"])(" + arrServletNames[i] + ")", "gi"); // "SearchDocuments
			}
			theString = theString.replace(re,"$1" + strRightPath + "servlet/$2");
		}
	}
	re = new RegExp("(['\\\"])\\\.+\\\/", "gi"); // "../
	theString = theString.replace(re,"$1" + strRightPath);
	return theString;
}

/* fix on save and preview */

function fixOnUnload(theString) {
	// same as fixHTML()
}

function fixHTML(inStr) {
	inStr = inStr.replace(/<STRONG>/gi,"<B>");
	inStr = inStr.replace(/<\/STRONG>/gi,"</B>");
	inStr = inStr.replace(/<EM>/gi,"<I>");
	inStr = inStr.replace(/<\/EM>/gi,"</I>");
	inStr = inStr.replace(/style=\"BORDER-RIGHT: #c0c0c0 4px solid[^\"]*?\"/gi,"border=0");
	inStr = inStr.replace(/\sdir=ltr/gi,"");
	inStr = inStr.replace(/\sstyle=\"MARGIN-RIGHT: 0px\"/gi,"");
	inStr = inStr.replace(/\sid=null/gi,"");

	for (var i = 0; i < arrOneBreakBeforeElements.length; i++) {
		var re = new RegExp(arrOneBreakBeforeElements[i], 'gi')
		inStr = inStr.replace(re,"\n" + arrOneBreakBeforeElements[i]);
	}
	for (var i = 0; i < arrTwoBreakBeforeElements.length; i++) {
		var re = new RegExp(arrTwoBreakBeforeElements[i], 'gi')
		inStr = inStr.replace(re,"\n\n" + arrTwoBreakBeforeElements[i]);
	}
	for (var i = 0; i < arrOneBreakAfterElements.length; i++) {
		var re = new RegExp(arrOneBreakAfterElements[i], 'gi')
		inStr = inStr.replace(re,arrOneBreakAfterElements[i] + "\n");
	}
	for (var i = 0; i < arrTwoBreakAfterElements.length; i++) {
		var re = new RegExp(arrTwoBreakAfterElements[i], 'gi')
		inStr = inStr.replace(re,arrTwoBreakAfterElements[i] + "\n\n");
	}

	for (var i = 0; i < arrTabBeforeElements.length; i++) {
		var re = new RegExp(arrTabBeforeElements[i], 'gi')
		inStr = inStr.replace(re,"\t" + arrTabBeforeElements[i]);
	}

	inStr = inStr.replace(/<BR>[\n\r]<BR>/gi,"<BR><BR>\n");
	inStr = inStr.replace(/<BR>[\n\r]{1,}<\/{1}/gi,"<BR></");

	/* replace all MS-HTML absolute paths */

	for (var i = 0; i < arrWrongPaths.length; i++) {
		if (arrWrongPaths[i] != "") {
			re = new RegExp(arrWrongPaths[i], "gi");
			inStr = inStr.replace(re, imcRootPath + "/");
		}
	}
	re = new RegExp("(" + imcRootPath + ")+", "gi");
	inStr = inStr.replace(re, imcRootPath);
	if (strRightPath != "") {
		re = new RegExp(strRightPath, "gi");
		inStr = inStr.replace(re, "/");
	}

	/* Fix all anchor jumps */

	re = new RegExp("#", "gi") ;
	if (re.test(inStr)) {
		re = /"[^"]*?#[^"]*?"/gi ;
		arrMatches = inStr.match(re) ;
		if (arrMatches) {
			for (var i = 0; i < arrMatches.length; i++) {
				var sTemp = arrMatches[i] ;
				re = new RegExp("http:|GetDoc", "gi") ;
				if (!re.test(sTemp)) {
					sTemp = "\"#" + sTemp.split("#")[1] ;
					inStr = inStr.replace(arrMatches[i], sTemp) ;
				}
			}
		}
	}

	return inStr
}

function removeHTML() {
	var sLay,sCode,re
	sLay = document.getElementById("editorDiv");
	sCode = sLay.innerHTML;
	re = /(<BR*>)|(<\/P>[^\n\r])|(<\/[OUD]L>)|(<\/BLOCKQUOTE>)/gi;
		sCode = sCode.replace(re, "\r");
	re = /<\/?[A-Z]{1,}.?[^<]*>/gi;
		sCode = sCode.replace(re, "");
	re = /\ \;/gi;
		sCode = sCode.replace(re, " ");
	sLay.innerText = sCode;
	sLay.focus();
}


function fixOfficeHTML() {
	// replace <v:shape WHATEVER </v:shape>				=> ''
	// replace <A name=_Toc[1-9]+></A>						=> ''
	// replace  style="mso-bookmark: _Toc[\d]+"		=> ''
	// replace <?xml:namespace WHATEVER />				=> ''
	// replace <w:wrap WHATEVER></w:wrap>					=> ''
}

/* ***** Replace first and last paragraph-container ***** */

function replaceParagraphs(inString) {
	var retStr = inString;
	re = /^\s*<P\b[^>]*?>([\s\S]*?)<\/P>/i;
	retStr = retStr.replace(re, "$1");
	retStr = strRev(retStr);
	re = /^\s*>P\/<([\s\S]*?)(>[^<]*?\bP<)/i;
	retStr = retStr.replace(re, "$1$2");
	retStr = strRev(retStr);

	return retStr;
}

/* ***** String inverter ***** */

function strRev(str) {
	if (!str) return '';
	var revstr='';
	for (i = str.length-1; i>=0; i--)
		revstr+=str.charAt(i)
	return revstr;
}


/* ******************************** */
/*               CLOSE              */
/* ******************************** */

function doClose() {
	var closeMess = "<? install/htdocs/sv/htmleditor/scripts/editor_functions.js/2 ?>";
	if (confirmClose && directEditEnabled) {
		if (document.frames['changeTextFrame'].document.forms[0].text.value != editorDiv.innerHTML) {
			if (confirm(closeMess)) window.close();
		} else {
			window.close();
		}
	} else if (confirmClose && !directEditEnabled) {
		if (editorDiv.innerHTML != theSavedCodeDiv.innerHTML) {
			if (confirm(closeMess)) window.close();
		} else {
			window.close();
		}
	} else {
		window.close();
	}
}


/* ******************************** */
/*               CLEAR              */
/* ******************************** */

function Clear() {
	editorDiv.innerHTML = '';
	editorDiv.focus();
}

function fix(number) {
	return (number < 10) ? '0' + number : number;
}

function fileUpLoad(){
	popWinOpen(380,125,'input_link.asp?winW=380&winH=125&winDesc=Intern+l%E4nk','insLink',0,0,0);
}

function createFileLink( file , nn ){
	var link = ("A");
	var aElement=document.createElement(link);
	eval("aElement." + "innerText" + "='" + nn + "'");
	aElement.href=file;
	editorDiv.appendChild(aElement);
	editorDiv.focus();
}

function collectTableInfo() {
	popWinOpen(380,150,'input_table.html','TableInfo',0,0,0);
}

function createTable(iRows,iCols,iSpacing,iPadding,iBorder,iWidth){
	//alert(iRows + ' - ' + iCols + ' - ' + iSpacing + ' - ' + iPadding + ' - ' + iBorder + ' - ' + iWidth);
	numberOfTables++;
	var oTable = document.createElement("TABLE");
	var oTBody = document.createElement("TBODY");
	oTable.id = "table" + numberOfTables;
	oTable.cellspacing = iSpacing;
	oTable.cellpadding = iPadding;
	oTable.border = iBorder;
	oTable.width = iWidth;
	var iVal = 0;
	for (i=0; i<iRows; i++) {
		var oRow = document.createElement("TR");
		for (j=0; j<iCols; j++) {
			iVal++;
			var oCell = document.createElement("TD");
			var cellContent = document.createTextNode("cell_" + iVal); //  \u00A0 = generates a Non-breaking space
			oCell.appendChild(cellContent);
			oRow.appendChild(oCell);
		}
		oTBody.appendChild(oRow);
	}
	oTable.appendChild(oTBody);
	editorDiv.appendChild(oTable);
	editorDiv.innerText = editorDiv.innerHTML; // * Reloads the editor - to render the tables correctly
	editorDiv.innerHTML = editorDiv.innerText;
	editorDiv.innerHTML = editorDiv.innerHTML.replace(/border=0/g,"style='BORDER-RIGHT: #c0c0c0 4px solid'");
	editorDiv.focus();
}




function previewHtml(){
	var d = document.all.editorDiv;
	var f = document.forms.previewForm;
	f.html.value = 0;
	f.theCode.value = d.innerHTML;
	f.submit();
}

function OLDpreviewCode() {
	var d = document.all.editorDiv;
	var f = document.forms.previewForm;
	f.html.value = 1;
	f.theCode.value = d.innerHTML;
	alert(f.theCode.value);
	//f.submit();
}



function dummyText(){
	var sDummy = '<p>Lorem ipsum, dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>\n\n<p>Duis autem, vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi.</p>';
	var nod = document.createElement("");
	editorDiv.insertBefore( nod );
	nod.innerHTML = sDummy;
	editorDiv.focus();
}

function popWinOpen(iWidth,iHeight,sUrl,sName,sMenu,sScroll,sStatus) {
	//var window_width = iWidth;
	//var window_height = iHeight;
	var window_width = (iWidth == '100%')? screen.width-10 : iWidth;
	var window_height = (iHeight == '100%')? screen.height-100 : iHeight;
	if(screen.height < 700){
		var window_top = 0;
		var window_left = (screen.width-window_width)/2;
	} else {
		var window_top = (screen.height-window_height)/2;
		var window_left = (screen.width-window_width)/2;
	}
	if(iWidth == '100%' && iHeight == '100%'){
		popWindow = window.open(''+ sUrl + '',''+ sName + '','width=' + window_width + ',height=' + window_height + ',top=0,left=0,resizable=yes,scrollbars=' + sScroll + ',status=' + sStatus + ',menubar=' + sMenu + ',location=0,directories=0');
	} else {
		popWindow = window.open(''+ sUrl + '',''+ sName + '','resizable=no,menubar=' + sMenu + ',scrollbars=' + sScroll + ',status=' + sStatus + ',width=' + window_width + ',height=' + window_height + ',top=' + window_top + ',left=' + window_left + ',location=0,directories=0');
	}
	popWindow.focus();
}

/*function winOpen(sUrl,sName){

}*/

function changeFontColor(theValue){
	if(document.forms[0].execState.value == '1'){
		document.execCommand("ForeColor",false,theValue);
		document.forms[0].fontcolor.selectedIndex = 0;
		editorDiv.focus();
	}
}

function changeBackgroundColor(theValue){
	if(document.forms[0].execState.value == '1'){
		document.execCommand("BackColor",false,theValue);
		document.forms[0].backgroundcolor.selectedIndex = 0;
		editorDiv.focus();
	}
}

function doFormatBlock(){
	if(document.forms[0].execState.value == '1'){
		var blockCode = document.forms.editorForm.FormatBlockCode.value;
		if(blockCode != ''){
			blockCode = '<' + blockCode + '>';
			if(blockCode == '<BLOCKQUOTE>'){
				doExecCommand('Indent',false,null);
			} else {
				doExecCommand('FormatBlock',true,blockCode);
			}
		}
	}
}

function doExecCommand(what,gui,avalue){
	if(document.forms[0].execState.value == '1'){
		if(what == 'Refresh'){ // Reload saved content
			var textToEdit = (directEditEnabled) ? document.frames['changeTextFrame'].document.forms[0].text.value : parent.opener.document.forms[0].text.value;
			if(textToEdit.indexOf('<') < 0 && textToEdit.indexOf('>') < 0){ // TEXT
				editorDiv.innerText = textToEdit;
				editorDiv.innerHTML = editorDiv.innerHTML.replace(/^/g,"<P>");
				editorDiv.innerHTML = editorDiv.innerHTML.replace(/<BR><BR>/g,"</p>");
				editorDiv.innerHTML = editorDiv.innerHTML.replace(/<P><\/P>/g,"</P><P>");
				editorDiv.innerHTML = editorDiv.innerHTML.replace(/<P><\/P>/g,"");
				editorDiv.innerHTML = editorDiv.innerHTML.replace(/\s<\/P>/g,"</P>");
				editorDiv.focus();
				document.execCommand("SelectAll",false);
				document.execCommand("RemoveFormat",false);
				document.execCommand("FormatBlock",false,"<P>");
				document.execCommand("Unselect",false);
			} else { // HTML
				textToEdit = fixOnLoad(textToEdit);
				editorDiv.innerHTML = textToEdit;
				editorDiv.innerHTML = editorDiv.innerHTML.replace(/border=0/g,"style='BORDER-RIGHT: #c0c0c0 4px solid'");
				editorDiv.focus();
			}

		} else { // document.execCommand("x")
			if(gui == true){
				if(avalue != ''){
					document.execCommand(what,true,avalue);
				} else {
					document.execCommand(what,true);
				}
			} else {
				if(avalue != ''){
					document.execCommand(what,false,avalue);
				} else {
					document.execCommand(what);
				}
			}
		}
		editorDiv.focus();
	} else if(document.forms[0].execState.value == '2'){ // HELP subjects
		showHelpLayer(what);
	}
}

function insertSpan(){
	var objSelRange = document.selection.createRange;
	var objMyRange = objSelRange.duplicate;
	alert(objMyRange.text);
}


function doFind(){ // Not in use - runs by VBScript
	var textToFind = window.prompt("<? install/htdocs/sv/htmleditor/scripts/editor_functions.js/3 ?>", "");
	var r = document.body.createTextRange();
	r.findText(textToFind);
	r.select();
}

/* ***** Advanced / Simple mode ***** */

var advMode = 0;

function showAdvanced(){
	if(document.forms[0].execState.value == '1'){
		hideAllLayers();
		if(showAdv){
			var oadvBtn = document.getElementById("advBtn").style;
			var oadvBtnText = document.getElementById("advBtnText").style;
		}
		if(advMode){ // * Simple
			advMode = 0;
			if (showSimple){
				moveLayer('modeSimpleDiv',null,null,1,null);
				moveLayer('modeSimpleLinkDiv',null,null,1,null);
			}
			if(showAdv){
				oadvBtn.borderStyle = 'outset';
				oadvBtnText.top = 0;
				oadvBtnText.left = 0;
			}
			editorDiv.focus();

		} else { // * Advanced
			advMode = 1;
			if(showAdv){
				moveLayer('listCountDiv',null,-25,2,null);
				//moveLayer('modeAdvancedDiv',null,null,1,null);
				if (showAdvStandardDiv) {
					showAdvFunction('modeAdvStandardDiv');
				} else if (showAdvFontClassDiv) {
					showAdvFunction('modeAdvFontClassDiv');
				} else if (showAdvFontStyleDiv) {
					showAdvFunction('modeAdvFontStyleDiv');
				} else if (showAdvCodeStringDiv) {
					showAdvFunction('modeAdvCodeStringDiv');
				} else if (showAdvSettingsDiv) {
					showAdvFunction('modeAdvSettingsDiv');
				}
				oadvBtn.borderStyle = 'inset';
				oadvBtnText.top = 1;
				oadvBtnText.left = 1;
			}
			editorDiv.focus();
		}
		if (showSimple) document.forms[0].simpleSelector.selectedIndex = 0;
		if (showAdv) document.forms[0].advSelector.selectedIndex = 0;
	}
}

function showAdvFunction(what) {
	if(document.forms[0].execState.value == '1'){
		hideAllLayers();
		moveLayer('modeAdvancedDiv',null,null,1,null);
		switch(what){
			case 'modeAdvStandardDiv':
				moveLayer('modeAdvStandardDiv',null,null,1,null);
			break;
			case 'modeAdvFontClassDiv':
				moveLayer('modeAdvFontClassDiv',null,null,1,null);
			break;
			case 'modeAdvFontStyleDiv':
				moveLayer('modeAdvFontStyleDiv',null,null,1,null);
				if (!arrButtonsRowTwo.length > 0) {
					moveLayer('modeColorDiv',null,232,1,null);
				} else {
					moveLayer('modeColorDiv',null,262,1,null);
				}
			break;
			case 'modeAdvCodeStringDiv':
				moveLayer('modeAdvCodeStringDiv',null,null,1,null);
			break;
			case 'modeAdvSettingsDiv':
				moveLayer('modeAdvSettingsDiv',null,null,1,null);
			break;
		}
	editorDiv.focus();
	}
}

function showSimpleFunction(what){
	if(document.forms[0].execState.value == '1'){
		hideAllLayers();
		moveLayer('modeSimpleDiv',null,null,1,null);
		switch(what){
			case 'modeSimpleLinkDiv':
				moveLayer('modeSimpleLinkDiv',null,null,1,null);
			break;
			case 'modeSimpleListDiv':
				moveLayer('modeSimpleListDiv',null,null,1,null);
				document.forms[0].createListType.selectedIndex = 0;
			break;
			case 'modeSimplePixelDiv':
				moveLayer('modeSimplePixelDiv',null,null,1,null);
			break;
		}
	editorDiv.focus();
	}
}

function hideAllLayers(){
	moveLayer('modeSimpleDiv',null,null,0,null);
		moveLayer('modeSimpleLinkDiv',null,null,0,null);
		moveLayer('modeSimpleListDiv',null,null,0,null);
		moveLayer('modeSimplePixelDiv',null,null,0,null);
		moveLayer('listCountTypeDiv',null,null,0,null);
		moveLayer('listCountDiv',null,-25,2,null);
	moveLayer('modeAdvancedDiv',null,null,0,null);
		moveLayer('modeAdvStandardDiv',null,null,0,null);
		moveLayer('modeAdvFontClassDiv',null,null,0,null);
		moveLayer('modeAdvFontStyleDiv',null,null,0,null);
		moveLayer('modeAdvCodeStringDiv',null,null,0,null);
		moveLayer('modeAdvSettingsDiv',null,null,0,null);
		moveLayer('modeColorDiv',null,null,0,null);
	moveLayer('modeHtmlCodeDiv',null,null,0,null);
}

function changeLinkType(what){
	if(document.forms[0].execState.value == '1'){
		theDescr = document.getElementById("createLinkFieldText");
		theField = document.getElementById("createLinkField");
		switch(what){
			case 'GetDoc':
				theDescr.innerHTML = '<b><? install/htdocs/sv/htmleditor/scripts/editor_functions.js/10 ?>:</b>';
				theField.innerHTML = '<input type="text" name="createLinkValue" value="" size="5" maxlength="6" style="width:42\; text-align:right">';
			break;
			case 'http':
				theDescr.innerHTML = '<b><? install/htdocs/sv/htmleditor/scripts/editor_functions.js/11 ?>:</b>';
				theField.innerHTML = '<input type="text" name="createLinkValue" value="http://" size="12" maxlength="100" style="width:140">';
			break;
			case 'mailto':
				theDescr.innerHTML = '<b><? install/htdocs/sv/htmleditor/scripts/editor_functions.js/12 ?>:</b>';
				theField.innerHTML = '<input type="text" name="createLinkValue" value="" size="12" maxlength="100" style="width:140">';
			break;
			case 'ftp':
				theDescr.innerHTML = '<b><? install/htdocs/sv/htmleditor/scripts/editor_functions.js/13 ?>:</b>';
				theField.innerHTML = '<input type="text" name="createLinkValue" value="ftp://" size="12" maxlength="100" style="width:140">';
			break;
		}
	editorDiv.focus();
	}
}


function changeListType(what){
	if(what != 'OL') {
		document.getElementById("listCountTypeDiv").style.visibility = 'hidden';
		document.getElementById("listCountDiv").style.top = -25;
	} else {
		document.getElementById("listCountTypeDiv").style.visibility = 'visible';
		document.getElementById("listCountDiv").style.top = 5;
	}
}

/* ***** Sets the default values according to the settings-form ***** */

function setDefaultValues(what){
	var theEditor = document.getElementById("editorDiv");
	if(what == 'do'){ // set values
		var previewWidth = document.forms.editorForm.previewWidth.value;
		if(previewWidth.indexOf("%") < 0){
			previewWidth = (previewWidth > 525)? 525 : previewWidth
			previewWidth = (previewWidth < 10)? 10 : previewWidth
			theEditor.style.width = parseInt(previewWidth);// + 6;
		} else {
			theEditor.style.width = 525;
		}
		var previewBackground = document.forms.editorForm.previewBackground.value;
		theEditor.style.backgroundColor = previewBackground;
		var previewColor = document.forms.editorForm.previewColor.value;
		theEditor.style.color = previewColor;
	} else { // reset
		document.forms.editorForm.previewWidth.value = '100%';
		document.forms.editorForm.previewBackground.value = cssDefaultColorBackground;
		document.forms.editorForm.previewColor.value = cssDefaultColorFont;
		theEditor.style.width = '525';
		theEditor.style.backgroundColor = cssDefaultColorBackground;
		theEditor.style.color = cssDefaultColorFont;
	}
	editorDiv.focus();
}


/* ***** HTML / Editor mode ***** */

var preview = 1;

function previewCode(){
	var theEditor = document.getElementById("editorDiv");
	if (document.forms[0].simpleSelector) document.forms[0].simpleSelector.selectedIndex = 0;
	if (document.forms[0].advSelector) document.forms[0].advSelector.selectedIndex = 0;
	if (preview) {
		if (parseInt(editorDiv.style.width) > 525) editorDiv.style.width = 525;
		preview = 0;
		document.images.previewImg.src = 'images/btn_preview_editor.gif';
		//theEditor.innerText          = theEditor.innerHTML;
		theEditor.innerText            = fixHTML(theEditor.innerHTML);
		theEditor.style.fontSize       = cssCodeFontSize;
		theEditor.style.fontFamily     = cssCodeFontFamily;
		disableButtons();

	} else {
		if (!showSimple && !showAdv) {
			editorDiv.style.width = 675;
		} else {
			editorDiv.style.width = 525;
		}
		preview = 1;
		document.images.previewImg.src = 'images/btn_preview_html.gif';
		theEditor.innerHTML            = theEditor.innerText;
		theEditor.innerHTML            = fixOnLoad(theEditor.innerHTML);
		theEditor.style.fontSize       = cssDefaultFontSize;
		theEditor.style.fontFamily     = cssDefaultFontFamily;
		enableButtons();
	}
}

function disableButtons(what){
	document.forms[0].execState.value = '0';
	moveLayer('disableTopBtnDiv',null,null,1,null);
	moveLayer('disableBottomBtnDiv',null,null,1,null);
	moveLayer('modeSimpleDiv',null,null,0,null);
	moveLayer('modeSimpleLinkDiv',null,null,0,null);
	moveLayer('modeSimpleListDiv',null,null,0,null);
	moveLayer('modeSimplePixelDiv',null,null,0,null);
	moveLayer('modeAdvancedDiv',null,null,0,null);
	if (document.forms[0].advSelector) document.forms[0].advSelector.selectedIndex = 0;
	moveLayer('modeAdvStandardDiv',null,null,0,null);
	moveLayer('modeAdvFontClassDiv',null,null,0,null);
	moveLayer('modeAdvFontStyleDiv',null,null,0,null);
	moveLayer('modeAdvCodeStringDiv',null,null,0,null);
	moveLayer('modeAdvSettingsDiv',null,null,0,null);
	moveLayer('modeColorDiv',null,null,0,null);
	if (what == 'help'){
		moveLayer('helpAllDescRightDiv',null,null,1,null);
	} else {
		moveLayer('modeHtmlCodeDiv',null,null,1,null);
	}
	if (document.getElementById("topClassSelect")) document.getElementById("topClassSelect").style.backgroundColor = '#D6D3CE';
	if (document.getElementById("topClassWordSelect")) document.getElementById("topClassWordSelect").style.backgroundColor = '#D6D3CE';
	if (document.getElementById("fontcolor")) document.getElementById("fontcolor").style.backgroundColor = '#D6D3CE';
	if (document.getElementById("backgroundcolor")) document.getElementById("backgroundcolor").style.backgroundColor = '#D6D3CE';
	if (document.getElementById("topClassSelect")) document.getElementById("topClassSelect").disabled = 1;
	if (document.getElementById("topClassWordSelect")) document.getElementById("topClassWordSelect").disabled = 1;
	if (document.getElementById("fontcolor")) document.getElementById("fontcolor").disabled = 1;
	if (document.getElementById("backgroundcolor")) document.getElementById("backgroundcolor").disabled = 1;
	if(showAdv){
		var oadvBtn = document.getElementById("advBtn").style;
		var oadvBtnText = document.getElementById("advBtnText").style;
		oadvBtn.borderStyle = 'outset';
		oadvBtnText.top = 0;
		oadvBtnText.left = 0;
	}
	advMode = 0;
}
function enableButtons(){
	document.forms[0].execState.value = '1';
	showDefaultPane();
	moveLayer('modeHtmlCodeDiv',null,null,0,null);
	moveLayer('helpAllDescRightDiv',null,null,0,null);
	moveLayer('disableTopBtnDiv',null,null,0,null);
	moveLayer('disableBottomBtnDiv',null,null,0,null);
	if (document.getElementById("topClassSelect")) document.getElementById("topClassSelect").style.backgroundColor = '#FFFFFF';
	if (document.getElementById("topClassWordSelect")) document.getElementById("topClassWordSelect").style.backgroundColor = '#FFFFFF';
	if (document.getElementById("fontcolor")) document.getElementById("fontcolor").style.backgroundColor = '#FFFFFF';
	if (document.getElementById("backgroundcolor")) document.getElementById("backgroundcolor").style.backgroundColor = '#FFFFFF';
	if (document.getElementById("topClassSelect")) document.getElementById("topClassSelect").disabled = 0;
	if (document.getElementById("topClassWordSelect")) document.getElementById("topClassWordSelect").disabled = 0;
	if (document.getElementById("fontcolor")) document.getElementById("fontcolor").disabled = 0;
	if (document.getElementById("backgroundcolor")) document.getElementById("backgroundcolor").disabled = 0;
}


function showDefaultPane(){
	hideAllLayers();
	if (showSimple){
		moveLayer('modeSimpleDiv',null,null,1,null);
		moveLayer('modeSimpleLinkDiv',null,null,1,null);
	} else if (showAdv){
		moveLayer('listCountDiv',null,-25,2,null);
		moveLayer('modeAdvancedDiv',null,null,1,null);
		moveLayer('modeAdvStandardDiv',null,null,1,null);
	}
}

/* ***** Show / Hide layers ***** */

function moveLayer(id,posX,posY,show,zind){
  if (show >= 1){
		if (showSimple == 0 && id.indexOf("modeSimple") >= 0){
			show = 0;
			//alert('Hide modeSimple - ' + id);
		}
		if (showAdv == 0 && id.indexOf("modeAdv") >= 0){
			show = 0;
			//alert('Hide modeAdv - ' + id);
		}
  }

  if (show == 1){
    sShow = 'visible';
  } else if (show == 2){
    sShow = 'inherit';
  } else {
    sShow = 'hidden';
  }
	if (document.getElementById(id)) {
		document.getElementById(id).style.visibility = sShow;
		if (posX != null) document.getElementById(id).style.left = posX;
		if (posY != null) document.getElementById(id).style.top = posY;
		if (zind != null) document.getElementById(id).style.zIndex = zind;
	}
}

function insertVertSpace(height){
	var sSpace = '<br>\n<img src="images/' + pixelSrc + '" width="1" height="' + height + '"><br>\n';
	alert(sSpace);
}



/* ***************** Avancerat / Inställningar "Genomför" = Set Cookie ************** */

function saveSettings(what) {
	if (window.navigator.cookieEnabled) {
		var f = document.forms.editorForm;
		if (what == 'EditorSettings') {
			var sWidth = f.previewWidth.value;
			var sBackground = f.previewBackground.value;
			var sColor = f.previewColor.value;
			setCookie('EditorSettings', sWidth + '/' + sBackground + '/' + sColor);
		}
	}
}




/* ****** Gets Cookies And Sets The Values. ****** *
 * ******* Sets Default values if not existing ********* */

function getCookies() {
	var f = document.forms.editorForm;
	var editorLayer = document.getElementById("editorDiv");
	var cookieSettings = getCookie('EditorSettings');
	if (cookieSettings != null) {
		if (cookieSettings.indexOf('/') != -1) {
			f.previewWidth.value = (cookieSettings.split("/")[0]  != '') ? cookieSettings.split("/")[0] : cssDefaultWidth;
			f.previewBackground.value = (cookieSettings.split("/")[1]  != '') ? cookieSettings.split("/")[1] : cssDefaultColorBackground;
			f.previewColor.value = (cookieSettings.split("/")[2] != '') ? cookieSettings.split("/")[2] : cssDefaultColorFont;
			if (cookieSettings.split("/")[0].indexOf('%') < 0) editorLayer.style.width = (cookieSettings.split("/")[0]  != '') ? cookieSettings.split("/")[0] : cssDefaultWidth;
			editorLayer.style.backgroundColor = (cookieSettings.split("/")[1]  != '') ? cookieSettings.split("/")[1] : cssDefaultColorBackground;
			editorLayer.style.color = (cookieSettings.split("/")[2]  != '') ? cookieSettings.split("/")[2] : cssDefaultColorFont;
		}
	}
}






/* *************************************
 *            Set Cookie ?             *
 ************************************* */

function setCookie(name, value) {
	var sPath = '/';
	var today = new Date();
	var expire = new Date();
	expire.setTime(today.getTime() + 1000*60*60*24*365); // 365 days
	var sCookieCont = name + "=" + escape(value);
	sCookieCont += (expire == null) ? "" : "\; expires=" + expire.toGMTString();
	sCookieCont += "; path=" + sPath;
	document.cookie = sCookieCont;
}


/* *************************************
 *            Get Cookie ?             *
 ************************************* */

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


