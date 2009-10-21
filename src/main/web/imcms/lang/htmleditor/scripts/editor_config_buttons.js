/* **********************************
 *   By: Tommy Ullberg, imCode       
 *   www.imcode.com                  
 ********************************* */


/* ***** AKTIVE BUTTONS - PER ROW STRUCTURE/SORTORDER *****
 * Select the buttons from the "arrBtns" array below
 * 
 * Default settings:
 * arrButtonsRowOne = new Array(10,15,16,17,0,18,19,0,20,21,0,22,23,0,0,10,24,0,0,10,25);
 * arrButtonsRowTwo = new Array(10,1,2,3,4,0,5,6,7,0,8,9,0,11,12,0,13,14);
 * 
 * Alternative SIMPLE buttonlayout suggestion: 
 * arrButtonsRowOne = new Array(10,20,21,0,22,23,0,0,10,15,16,17,0,1,2,3);
 * arrButtonsRowTwo = new Array();
 * 
 * Alternative FULL buttonlayout suggestion: 
 * arrButtonsRowOne = new Array(10,15,16,17,0,18,19,0,20,21,0,10,1,2,3,4,0,5,6,7,0,8,9,0,11,12,0,13,14);
 * arrButtonsRowTwo = new Array(10,26,27,0,0,10,24,0,0,10,25,0,10,22,23);
 * 
 */


if (isAdmin) {
	arrButtonsRowOne = new Array(10,15,16,17,0,18,19,0,20,21,0,10,1,2,3,4,0,5,6,7,0,8,9,0,11,12,0,13,14);
	arrButtonsRowTwo = new Array(10,26,27,0,0,10,24,0,0,10,25,0,10,22,23);
} else {
	arrButtonsRowOne = new Array(10,20,21,0,22,23,0,0,10,15,16,17,0,1,2,3,0,11,12,0,13,14,0,18);
	arrButtonsRowTwo = new Array();
	//arrButtonsRowOne = new Array(10,15,16,17,0,18,19,0,20,21,0,10,1,2,3,4,0,5,6,7,0,8,9,0,11,12,0,13,14);
	//arrButtonsRowTwo = new Array(10,26,27,0,0,10,24,0,0,10,25,0,10,22,23);
}





/* ******************** AVAILABLE BUTTONS ******************** */

arrBtns = new Array();

/* ******************** FILLERS ******************** */

// spacer
arrBtns[0] = '<img src="images/1x1.gif" width="5" height="1">';

// handle (vertical bars)
arrBtns[10] = '<img src="images/btn_0handle.gif" width="3" height="21"></td>';
arrBtns[10] += '<td><img src="images/1x1.gif" width="1" height="1"></td>';
arrBtns[10] += '<td><img src="images/btn_0handle.gif" width="3" height="21"></td>';
arrBtns[10] += '<td><img src="images/1x1.gif" width="3" height="1">';


/* ******************** BUTTONS ******************** */

// bold
arrBtns[1] = '<button unselectable="on" class="button" id="btn13" onClick="doExecCommand(\'Bold\',false,null);" title="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/1 ?>"><img src="images/btn_format_f.gif"></button>';
// italic
arrBtns[2] = '<button unselectable="on" class="button" id="btn14" onClick="doExecCommand(\'Italic\',false,null)\;" title="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/2 ?>"><img src="images/btn_format_k.gif"></button>';
// underline
arrBtns[3] = '<button unselectable="on" class="button" id="btn15" onClick="doExecCommand(\'Underline\',false,null)\;" title="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/3 ?>"><img src="images/btn_format_u.gif"></button>';
// strike
arrBtns[4] = '<button unselectable="on" class="button" id="btn16" onClick="doExecCommand(\'StrikeThrough\',false,null)\;" title="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/4 ?>"><str'+'ike style="position:relative\; top:-1\; font: bold 14px Times New Roman">S</str'+'ike></button>';
// justify left
arrBtns[5] = '<button unselectable="on" class="button" id="btn17" onClick="doExecCommand(\'JustifyLeft\',false,null)\;" title="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/5 ?>"><img src="images/btn_justify_left.gif"></button>';
// justify center
arrBtns[6] = '<button unselectable="on" class="button" id="btn18" onClick="doExecCommand(\'JustifyCenter\',false,null)\;" title="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/6 ?>"><img src="images/btn_justify_center.gif"></button>';
// justify right
arrBtns[7] = '<button unselectable="on" class="button" id="btn19" onClick="doExecCommand(\'JustifyRight\',false,null)\;" title="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/7 ?>"><img src="images/btn_justify_right.gif"></button>';
// sup
arrBtns[8] = '<button unselectable="on" class="button" id="btn20" onClick="doExecCommand(\'SuperScript\',false,null)\;" title="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/8 ?>"><img src="images/btn_superscript.gif"></button>';
// sub
arrBtns[9] = '<button unselectable="on" class="button" id="btn21" onClick="doExecCommand(\'SubScript\',false,null)\;" title="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/9 ?>"><img src="images/btn_subscript.gif"></button>';
// ol
arrBtns[11] = '<button unselectable="on" class="button" id="btn22" onClick="doExecCommand(\'InsertOrderedList\',true,null)\;" title="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/10 ?>"><img src="images/btn_list_ordered.gif"></button>';
// ul
arrBtns[12] = '<button unselectable="on" class="button" id="btn23" onClick="doExecCommand(\'InsertUnorderedList\',true,null)\;" title="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/11 ?>"><img src="images/btn_list_unordered.gif"></button>';
// outdent
arrBtns[13] = '<button unselectable="on" class="button" id="btn24" onClick="doExecCommand(\'Outdent\',false,null)\;" title="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/12 ?>"><img src="images/btn_outdent.gif"></button>';
// indent
arrBtns[14] = '<button unselectable="on" class="button" id="btn25" onClick="doExecCommand(\'Indent\',false,null)\;" title="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/13 ?>"><img src="images/btn_indent.gif"></button>';
// cut
arrBtns[15] = '<button unselectable="on" class="button" id="btn1" unselectable="On" onClick="doExecCommand(\'Cut\',true,null)\;" title="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/14 ?>"><img src="images/btn_cut.gif"></button>';
// copy
arrBtns[16] = '<button unselectable="on" class="button" id="btn2" unselectable="On" onClick="doExecCommand(\'Copy\',true,null)\;" title="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/15 ?>"><img src="images/btn_copy.gif"></button>';
// paste
arrBtns[17] = '<button unselectable="on" class="button" id="btn3" unselectable="On" onClick="doExecCommand(\'Paste\',true,null)\; if (isWordEnabled && preview) checkWordCode(editorDiv.innerHTML)" title="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/16 ?>"><img src="images/btn_paste.gif"></button>';
// preview in browser
arrBtns[18] = '<button unselectable="on" class="button" id="btn4" unselectable="On" title="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/17 ?>" onClick="if(document.forms[0].execState.value == \'2\'){ showHelpLayer(\'Preview\')\; } else { popWinOpen(\'100%\',\'100%\',\'preview.html\',\'Preview\',0,1,1)\; }"><img src="images/btn_preview.gif"></button>';
// find/search
arrBtns[19] = '<button unselectable="on" class="button" id="btn5" onClick="vbscript:findBtnClick" unselectable="On" name="findBtn" title="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/18 ?>"><img src="images/btn_find.gif"></button>';
// undo
arrBtns[20] = '<button unselectable="on" class="button" id="btn6" onClick="doExecCommand(\'Undo\',null,null)\;" title="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/19 ?>"><img src="images/btn_undo.gif" border="0"></button>';
// redo
arrBtns[21] = '<button unselectable="on" class="button" id="btn7" onClick="doExecCommand(\'Redo\',null,null)\;" title="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/20 ?>"><img src="images/btn_redo.gif" border="0"></button>';
// reset
arrBtns[22] = '<button unselectable="on" class="button" id="btn8" onClick="doExecCommand(\'Refresh\',null,null)\;" title="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/21 ?>" style="height:23"><img src="images/btn_refresh.gif" border="0"></button>';
// del format
arrBtns[23] = '<button unselectable="on" class="button" id="btn9" onClick=\'if(document.forms[0].execState.value == "2"){ showHelpLayer("Erase")\; } else { removeHTML()\; }\' title="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/22 ?>"><img src="images/btn_eraser.gif" border="0"></button>';

/* ******************** SELECTORS ******************** */

// color selector
arrBtns[24] = '<img id="btn10" src="images/btn_color_text.gif" width="16" height="16" alt="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/23 ?>:" hspace="3" onClick="if(document.forms[0].execState.value == \'2\'){ showHelpLayer(\'FontColor\')\; }"></td>';
arrBtns[24] += '<td><span id="fontcolorClickDiv" onClick="if(document.forms[0].execState.value == \'2\'){ showHelpLayer(\'FontColor\')\; }"><select unselectable="on" name="fontcolor" onChange="changeFontColor(this.options[this.selectedIndex].value)\;">';
arrBtns[24] += '	<option value="#000000" selected>- <? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/24 ?> -</option>';
var i,j,sInv;
for (var i = 0; i < arrColorSelectValues.length; i++) {
	sInv = '';
	re = new RegExp('\,' + i + '\,','g');
	var sTest = "," + arrColorSelectDescInverted.join(",") + ",";
	if (re.test(sTest)) {
		sInv = '\; color:white';
	}
	arrBtns[24] += '<option value="' + arrColorSelectValues[i] + '" style="background-color:' + arrColorSelectValues[i] + sInv + '">';
	arrBtns[24] += arrColorSelectDesc[i] + '</option>';
}
arrBtns[24] += '</select></span>';

// background-color selector
arrBtns[25] = '<img id="btn11" src="images/btn_color_background.gif" width="16" height="15" alt="<? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/26 ?>:" hspace="3" onClick="if(document.forms[0].execState.value == \'2\'){ showHelpLayer(\'FontBgColor\')\; }"></td>';
arrBtns[25] += '<td><span id="backgroundcolorClickDiv" onClick="if(document.forms[0].execState.value == \'2\'){ showHelpLayer(\'FontBgColor\')\; }"><select unselectable="on" name="backgroundcolor" onChange="changeBackgroundColor(this.options[this.selectedIndex].value)\;">';
arrBtns[25] += '	<option selected value ="">- <? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/24 ?> -</option>';
var i,j,sInv;
for (var i = 0; i < arrBackgroundColorSelectValues.length; i++) {
	sInv = '';
	re = new RegExp('\,' + i + '\,','g');
	var sTest = "," + arrBackgroundColorSelectDescInverted.join(",") + ",";
	if (re.test(sTest)) {
		sInv = '\; color:white';
	}
	arrBtns[25] += '<option value="' + arrBackgroundColorSelectValues[i] + '" style="background-color:' + arrBackgroundColorSelectValues[i] + sInv + '">';
	arrBtns[25] += arrBackgroundColorSelectDesc[i] + '</option>';
}
arrBtns[25] += '</select></span>';


/* *** TIP! ***		Place 26 and 27 next to each other.
									If there are any Word-code pasted into the editor, 27 will be visible instead of 26 */

// font class selector
arrBtns[26] = '<span id="topClassSelectClickDiv" style="display:block" onClick="if(document.forms[0].execState.value == \'2\'){ showHelpLayer(\'FontClass\')\; }"><select unselectable="on" name="topClassSelect">';
arrBtns[26] += '	<option value="" selected>- <? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/25 ?></option>';
var i,sDesc;
for (var i = 0; i < arrFavClassTop.length; i++) {
	sDesc = (arrFavClassTopDesc[i]) ? arrFavClassTopDesc[i] : arrFavClassTop[i];
	arrBtns[26] += '<option value="' + arrFavClassTop[i] + '">' + sDesc + '</option>';
}
arrBtns[26] += '</select></span>';

// font class selector - MS-Word classes
arrBtns[27] = '<span id="topClassWordSelectClickDiv" style="display:none" onClick="if(document.forms[0].execState.value == \'2\'){ showHelpLayer(\'FontClass\')\; }"><select unselectable="on" name="topClassWordSelect">';
arrBtns[27] += '	<option value="" selected>- <? install/htdocs/sv/htmleditor/scripts/editor_config_buttons.js/27 ?></option>';
var i,sDesc;
for (var i = 0; i < arrWordClassesSelect.length; i++) {
	sDesc = (arrWordClassesDesc[i]) ? arrWordClassesDesc[i] : arrWordClassesSelect[i];
	arrBtns[27] += '<option value="' + arrWordClassesSelect[i] + '">' + sDesc + '</option>';
}
arrBtns[27] += '</select></span>';



