/* **********************************
 *   By: Tommy Ullberg, imCode       
 *   www.imcode.com                  
 ********************************* */

/* ***** HELP FUNCTIONS ***** */

function showHelp(what){
	if(what != ''){
		hideAllLayers();
		disableButtons('help');
		document.getElementById("disableTopBtnDiv").style.width = 674;
		if (parseInt(editorDiv.style.width) > 525) editorDiv.style.width = 525;
		moveLayer('helpDiv',null,null,0,null);
		switch(what){
			case 'all':
				hideHelp('allLay');
				moveLayer('helpDiv',null,null,0,null);
				moveLayer('helpTextAllDiv',null,null,1,null);
				break;
			case 'upperbuttons':
				hideHelp('allLay');
				moveLayer('helpDiv',null,null,0,null);
				moveLayer('helpTextAllUpperDiv',null,null,1,null);
				break;
			case 'rightbuttons':
				hideHelp('allLay');
				moveLayer('helpDiv',null,null,0,null);
				moveLayer('helpTextAllRightDiv',null,null,1,null);
				break;
		}
	}
}

function hideHelp(what){
	moveLayer('helpTextAllDiv',null,null,0,null);
	moveLayer('helpTextAllUpperDiv',null,null,0,null);
	moveLayer('helpTextAllRightDiv',null,null,0,null);
	moveLayer('helpDescRightDiv',null,null,0,null);
	showHelpLayer('DefaultText');
	moveLayer('helpSubjectDiv',null,null,0,null);
	document.forms[0].execState.value = '1';
	editorDiv.focus();
	//showDefaultPane();
	
	for(var i=1; i<=30; i++){
		if (document.getElementById("btn" + i)) document.getElementById("btn" + i).style.cursor = 'hand';
		if (document.getElementById("btn" + i)) document.getElementById("btn" + i).disabled = 0;
	}
	
	if(what == 'allLay'){
		moveLayer('helpDiv',null,null,0,null);
	} else if(what == 'all'){
		if (!showSimple && !showAdv) {
			editorDiv.style.width = 675;
		} else {
			editorDiv.style.width = 525;
		}
		moveLayer('helpDiv',null,null,0,null);
		enableButtons();
		document.getElementById("disableTopBtnDiv").style.width = 535;
		if (document.getElementById("topClassSelect")) document.getElementById("topClassSelect").disabled = 0;
		if (document.getElementById("topClassWordSelect")) document.getElementById("topClassWordSelect").disabled = 0;
		if (document.getElementById("fontcolor")) document.getElementById("fontcolor").disabled = 0;
		if (document.getElementById("backgroundcolor")) document.getElementById("backgroundcolor").disabled = 0;
	}
}

function showHelpSubjects(){
	hideHelp('all');
	hideAllLayers();
	if(document.getElementById("helpDiv").style.visibility == 'hidden'){
		if (parseInt(editorDiv.style.width) > 525) editorDiv.style.width = 525;
		moveLayer('helpDiv',null,null,1,null);
		moveLayer('helpSubjectDiv',null,null,1,null);
		moveLayer('helpDescRightDiv',null,null,1,null);
		showHelpLayer('DefaultText');
		document.forms[0].execState.value = '2';
		document.getElementById("hideHelpBtn").focus();
		for(var i=1; i<=30; i++){
			if(i != 5){
				if (document.getElementById("btn" + i)) document.getElementById("btn" + i).style.cursor = 'help';
			} else {
				if (document.getElementById("btn" + i)) document.getElementById("btn" + i).style.cursor = 'default';
				if (document.getElementById("btn" + i)) document.getElementById("btn" + i).disabled = 1;
			}
		}
		if (document.getElementById("topClassSelect")) document.getElementById("topClassSelect").disabled = 1;
		if (document.getElementById("topClassWordSelect")) document.getElementById("topClassWordSelect").disabled = 1;
		if (document.getElementById("fontcolor")) document.getElementById("fontcolor").disabled = 1;
		if (document.getElementById("backgroundcolor")) document.getElementById("backgroundcolor").disabled = 1;
		if (document.getElementById("topClassSelectClickDiv")) document.getElementById("topClassSelectClickDiv").style.cursor = 'help';
		if (document.getElementById("topClassWordSelectClickDiv")) document.getElementById("topClassWordSelectClickDiv").style.cursor = 'help';
		if (document.getElementById("fontcolorClickDiv")) document.getElementById("fontcolorClickDiv").style.cursor = 'help';
		if (document.getElementById("backgroundcolorClickDiv")) document.getElementById("backgroundcolorClickDiv").style.cursor = 'help';
	}
}


/* ***** HELP TEXTS ***** */

function showHelpLayer(what){
	var helpImageIcon = '';
	var helpImageIconNew = '';
	var helpHeading = '';
	var helpContent = '';
	var helpHeadingLayer = document.getElementById("helpHeadingDiv");
	var helpContentLayer = document.getElementById("helpContentDiv");
	moveLayer('helpSubjectDiv',null,null,1,null);
	switch(what){
		// * Help texts for the click-button-help
		case 'Cut':
			helpImageIcon = 'images/btn_cut.gif';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/1 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/2 ?>';
			break;
		case 'Copy':
			helpImageIcon = 'images/btn_copy.gif';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/3 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/4 ?>';
			break;
		case 'Paste':
			helpImageIcon = 'images/btn_paste.gif';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/5 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/6 ?>';
			break;
		case 'Preview':
			helpImageIcon = 'images/btn_preview.gif';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/7 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/8/1 ?><br><br><? install/htdocs/sv/htmleditor/scripts/editor_help.js/8/2 ?><br><br><? install/htdocs/sv/htmleditor/scripts/editor_help.js/8/3 ?>';
			break;
		case 'Undo':
			helpImageIcon = 'images/btn_undo.gif';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/9 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/10 ?>';
			break;
		case 'Redo':
			helpImageIcon = 'images/btn_redo.gif';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/11 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/12 ?>';
			break;
		case 'Refresh':
			helpImageIcon = 'images/btn_refresh.gif';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/13 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/14 ?>';
			break;
		case 'Erase':
			helpImageIcon = 'images/btn_eraser.gif';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/15 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/16 ?>';
			break;
		case 'FontClass':
			helpImageIcon = '';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/17 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/18 ?><br><br>';
			helpContent += '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/19 ?><br><br>';
			helpContent += '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/20 ?>';
			break;
		case 'FontColor':
			helpImageIcon = 'images/btn_color_text.gif';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/21 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/22 ?>';
			break;
		case 'FontBgColor':
			helpImageIcon = 'images/btn_color_background.gif';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/23 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/24 ?>';
			break;
		case 'EditCode':
			helpImageIconNew = '<button disabled style="width:55; height:21; cursor:default"><img src="images/btn_preview_editor.gif"></button>&nbsp;<button disabled style="width:55; height:21; cursor:default"><img src="images/btn_preview_html.gif"></button>';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/25 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/26 ?>';
			break;
		case 'Bold':
			helpImageIcon = 'images/btn_format_f.gif';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/27 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/28 ?>';
			break;
		case 'Italic':
			helpImageIcon = 'images/btn_format_k.gif';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/29 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/30 ?>';
			break;
		case 'Underline':
			helpImageIcon = 'images/btn_format_u.gif';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/31 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/32 ?>';
			break;
		case 'StrikeThrough':
			helpImageIconNew = '<button class="button" onClick="return false" style="cursor:default"><strike style="position:relative; top:-1; font: bold 14px Times New Roman">S</strike></button>';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/33 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/34 ?>';
			break;
		case 'JustifyLeft':
			helpImageIcon = 'images/btn_justify_left.gif';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/35 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/36 ?>';
			break;
		case 'JustifyCenter':
			helpImageIcon = 'images/btn_justify_center.gif';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/37 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/38 ?>';
			break;
		case 'JustifyRight':
			helpImageIcon = 'images/btn_justify_right.gif';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/39 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/40 ?>';
			break;
		case 'SuperScript':
			helpImageIcon = 'images/btn_superscript.gif';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/41 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/42 ?>';
			break;
		case 'SubScript':
			helpImageIcon = 'images/btn_subscript.gif';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/43 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/44 ?>';
			break;
		case 'InsertOrderedList':
			helpImageIcon = 'images/btn_list_ordered.gif';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/45 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/46 ?>';
			break;
		case 'InsertUnorderedList':
			helpImageIcon = 'images/btn_list_unordered.gif';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/47 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/48 ?>';
			break;
		case 'Outdent':
			helpImageIcon = 'images/btn_outdent.gif';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/49 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/50 ?>';
			break;
		case 'Indent':
			helpImageIcon = 'images/btn_indent.gif';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/51 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/52 ?>';
			break;
			
		case 'DefaultText':
			helpImageIcon = '';
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/53 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/54 ?>';
			break;
		default:
			helpHeading = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/55 ?>';
			helpContent = '<? install/htdocs/sv/htmleditor/scripts/editor_help.js/56 ?>';
	}
	
	
	if(helpHeading != ''){
		if(helpImageIcon != ''){
			sHeading = '<table border="0" cellpadding="0" cellspacing="0"><tr><td class="imEditHelpHeading">' + helpHeading + '</td><td>&nbsp;&nbsp;</td><td height="23"><button disabled class=button style="cursor:default"><img src="' + helpImageIcon + '"></button></td></tr></table>';
		} else if(helpImageIconNew != ''){
			sHeading = '<table border="0" cellpadding="0" cellspacing="0"><tr><td class="imEditHelpHeading">' + helpHeading + '</td><td>&nbsp;&nbsp;</td><td height="23">' + helpImageIconNew + '</td></tr></table>';
		} else {
			sHeading = '<table border="0" cellspacing="0" cellpadding="0"><tr><td height="23" class="imEditHelpHeading">' + helpHeading + '</td></tr></table>';
		}
		helpHeadingLayer.innerHTML = sHeading;
		helpContentLayer.innerHTML = helpContent;
	}
}