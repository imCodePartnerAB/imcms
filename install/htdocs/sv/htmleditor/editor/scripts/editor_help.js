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
			helpHeading = 'Knappen &quot;Klipp ut&quot;';
			helpContent = 'Används för att klippa ut markerad text till <i>Urklipp/Klippbordet</i>. Innehållet kan sedan klistras in på annan plats eller i annat program.';
			break;
		case 'Copy':
			helpImageIcon = 'images/btn_copy.gif';
			helpHeading = 'Knappen &quot;Kopiera&quot;';
			helpContent = 'Används för att kopiera markerad text till <i>Urklipp/Klippbordet</i>. Innehållet kan sedan klistras in på annan plats eller i annat program.';
			break;
		case 'Paste':
			helpImageIcon = 'images/btn_paste.gif';
			helpHeading = 'Knappen &quot;Klistra in&quot;';
			helpContent = 'Används för att klistra in det senast kopierade eller urklippta innehållet i <i>Urklipp/Klippbordet</i>.<br><br>Innehållet klistras in där markören står.<br>Om någon text är markerad, byts den ut mot det inklistrade innehållet.';
			break;
		case 'Preview':
			helpImageIcon = 'images/btn_preview.gif';
			helpHeading = 'Knappen &quot;Förhandsgranska&quot;';
			helpContent = 'Öppnar och visar hur HTML-koden ser ut, i ett nytt fönster.<br><br>I förhandsgranskningsfönstret får man se hur koden verkligen genereras av en webläsare, när man väl har sparat sin kod.<br><br>Vidare kan man justera innehållsbredden, om man vet denna, för att återspegla den bredd man använder där själva texten/koden skall ligga. Därmed får man en exakt uppfattning om radbrytningar och ev. förskjutningar som kan uppstå på fel ställe.';
			break;
		case 'Undo':
			helpImageIcon = 'images/btn_undo.gif';
			helpHeading = 'Knappen &quot;Ångra&quot;';
			helpContent = 'Ångrar/återställer senast utförda åtgärd.<br><br><b>OBS! Gäller inte alla funktioner.</b><br><br>Vissa funktioner kan man bara återställa genom att göra något av följande:<ul><li>Ladda om senast sparade version<li>Manuellt editera bort koden i HTML-läge.<li>Välja att radera all formatering (tar bort all HTML)</ul>';
			break;
		case 'Redo':
			helpImageIcon = 'images/btn_redo.gif';
			helpHeading = 'Knappen &quot;Gör om&quot;';
			helpContent = 'Återställer senast ångrade åtgärd.';
			break;
		case 'Refresh':
			helpImageIcon = 'images/btn_refresh.gif';
			helpHeading = 'Knappen &quot;Ladda om texten&quot;';
			helpContent = 'Återställer orginalversionen, eller senast sparade version (om man sparat), från imCMS textinmatningsformulär.';
			break;
		case 'Erase':
			helpImageIcon = 'images/btn_eraser.gif';
			helpHeading = 'Knappen &quot;Radera all formatering&quot;';
			helpContent = 'Raderar alla HTML taggar i texten och återställer formateringen till ren text.<br><br><b>OBS!</b> Raderar inte radbrytningar men ev. styckeformatering bibehålls inte.';
			break;
		case 'FontClass':
			helpImageIcon = '';
			helpHeading = 'Formatväljare';
			helpContent = 'Ändrar stilen på markerad text till det valda formatet.<br><br>';
			helpContent += '<b>Notera!&nbsp;&nbsp;</b>Det är bättre att markera texten som skall ändras genom att dra med muspekaren, än att dubbel- eller trippelklicka som i ordbehandlingsprogrammen. I bland försvinner radbrytningen efter markeringen om man markerar så och använder funktionen. (Microsofts fel)<br><br>';
			helpContent += 'Det är ingen fara dock. Bara att ställa sig efter markeringen och göra en ny radbrytning.';
			break;
		case 'FontColor':
			helpImageIcon = 'images/btn_color_text.gif';
			helpHeading = 'Teckensnittsfärg';
			helpContent = 'Ändrar färgen på <font style="color:blue">markerad</font> text till den valda färgen.';
			break;
		case 'FontBgColor':
			helpImageIcon = 'images/btn_color_background.gif';
			helpHeading = 'Överstrykningsfärg';
			helpContent = 'Ändrar överstrykningsfärgen på <font style="background-color:#bbffbb">markerad</font> text till den valda färgen.<br>Simulerar en överstrykningspenna.';
			break;
		case 'EditCode':
			helpImageIconNew = '<button disabled style="width:55; height:21; cursor:default"><img src="images/btn_preview_editor.gif"></button>&nbsp;<button disabled style="width:55; height:21; cursor:default"><img src="images/btn_preview_html.gif"></button>';
			helpHeading = 'Knappen &quot;Växla editeringsläge&quot;';
			helpContent = 'Växlar mellan lägena WYSIWYG editor (What You See Is What You Get) och HTML editor.<br><br>I läget HTML editor kan man alltså finjustera sin HTML-kod, eller rätta till ev. felaktigheter, om man har den kunskapen.<br>I läget WYSIWYG editor fungerar editeringen nästan som en ordbehandlare.';
			break;
		case 'Bold':
			helpImageIcon = 'images/btn_format_f.gif';
			helpHeading = 'Knappen &quot;Fetstil&quot;';
			helpContent = 'Formaterar <b>markerad</b> text som fetstil.';
			break;
		case 'Italic':
			helpImageIcon = 'images/btn_format_k.gif';
			helpHeading = 'Knappen &quot;Kursiv stil&quot;';
			helpContent = 'Formaterar <i>markerad</i> text som kursiv (lutande).';
			break;
		case 'Underline':
			helpImageIcon = 'images/btn_format_u.gif';
			helpHeading = 'Knappen &quot;Understrykning&quot;';
			helpContent = 'Stryker under <u>markerad</u> text.<br><br>Bör användas med försiktighet, eftersom understruken text lätt kan förväxlas med en länk på Internet.';
			break;
		case 'StrikeThrough':
			helpImageIconNew = '<button class="button" onClick="return false" style="cursor:default"><strike style="position:relative; top:-1; font: bold 14px Times New Roman">S</strike></button>';
			helpHeading = 'Knappen &quot;Överstrykning&quot;';
			helpContent = 'Stryker över <strike>markerad</strike> text.';
			break;
		case 'JustifyLeft':
			helpImageIcon = 'images/btn_justify_left.gif';
			helpHeading = 'Knappen &quot;Vänster justera&quot;';
			helpContent = 'Vänster justerar markerat stycke. (standard)';
			break;
		case 'JustifyCenter':
			helpImageIcon = 'images/btn_justify_center.gif';
			helpHeading = 'Knappen &quot;Centrera&quot;';
			helpContent = 'Centrerar markerat stycke.';
			break;
		case 'JustifyRight':
			helpImageIcon = 'images/btn_justify_right.gif';
			helpHeading = 'Knappen &quot;Höger justera&quot;';
			helpContent = 'Höger justerar markerat stycke.';
			break;
		case 'SuperScript':
			helpImageIcon = 'images/btn_superscript.gif';
			helpHeading = 'Knappen &quot;Upphöjd text&quot;';
			helpContent = 'Formaterar markerad text som upphöjd text. Ex: m<sup>2</sup>.';
			break;
		case 'SubScript':
			helpImageIcon = 'images/btn_subscript.gif';
			helpHeading = 'Knappen &quot;Nersänkt text&quot;';
			helpContent = 'Formaterar markerad text som nersänkt text. Ex: H<sub>2</sub>O.';
			break;
		case 'InsertOrderedList':
			helpImageIcon = 'images/btn_list_ordered.gif';
			helpHeading = 'Knappen &quot;Skapa numrerad lista&quot;';
			helpContent = 'Skapar en numrerad lista (1,2,3...) av alla markerade stycken.<br><br>Ytterligare punkter kan läggas till genom att klicka på <i>Enter</i>.<br>För att byta rad utan att skapa ny punkt - klicka <i>Shift + Enter</i>.<br><br>För att börja skriva utanför listan:<br>Klicka utanför listtexten på det ställe du vill börja skriva - eller...<br>klicka på <i>Enter</i> och sedan på &quot;Skapa numrerad lista&quot; igen.';
			break;
		case 'InsertUnorderedList':
			helpImageIcon = 'images/btn_list_unordered.gif';
			helpHeading = 'Knappen &quot;Skapa punktlista&quot;';
			helpContent = 'Skapar en punktlista av alla markerade stycken.<br><br>Ytterligare punkter kan läggas till genom att klicka på <i>Enter</i>.<br>För att byta rad utan att skapa ny punkt - klicka <i>Shift + Enter</i>.<br><br>För att börja skriva utanför listan:<br>Klicka utanför listtexten på det ställe du vill börja skriva - eller...<br>klicka på <i>Enter</i> och sedan på &quot;Skapa punktlista&quot; igen.';
			break;
		case 'Outdent':
			helpImageIcon = 'images/btn_outdent.gif';
			helpHeading = 'Knappen &quot;Öka indrag&quot;';
			helpContent = 'Ökar indraget (vänstermarginalen) på markerade stycken med ett steg.<br><br>Hur stort indraget är beror på vilken webläsare man har. Det brukar vara ungefär 40 pixlar. Man kan justera detta värde genom att lägga in <nobr class="imEditHelpCode">BLOCKQUOTE { margin-left: <i>nytt värde</i> }</nobr> i sitt stylesheet.';
			break;
		case 'Indent':
			helpImageIcon = 'images/btn_indent.gif';
			helpHeading = 'Knappen &quot;Minska indrag&quot;';
			helpContent = 'Minskar indraget (vänstermarginalen) på markerade stycken med ett steg.<br><br>Hur stort indraget är beror på vilken webläsare man har. Det brukar vara ungefär 40 pixlar. Man kan justera detta värde genom att lägga in <nobr class="imEditHelpCode">BLOCKQUOTE { margin-left: <i>nytt värde</i> }</nobr> i sitt stylesheet.';
			break;
			
		case 'DefaultText':
			helpImageIcon = '';
			helpHeading = 'Funktions beskrivning';
			helpContent = 'Klicka på knappen/funktionen du vill veta mer om. Gäller för knappar/funktioner där muspekaren blir till ett frågetecken när man för musen över densamma.';
			break;
		default:
			helpHeading = 'Ingen hjälp till detta!';
			helpContent = 'Klicka på en knapp som har en hjälp-muspekare...';
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