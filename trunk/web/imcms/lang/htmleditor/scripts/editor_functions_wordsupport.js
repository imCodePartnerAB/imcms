/* **********************************
 *   By: Tommy Ullberg, imCode       
 *   Copyright © imCode AB           
 *   www.imcode.com                  
 ********************************* */


/* NOTE!
   Once Word-code is pasted in to the editor - it will not be able to use other classes or style-code.
   ...if you use the Fix-Word-Code button that is! Because the fix-function replaces all unknow code and classes/styles */



var removeAllStyles = 1;
var isHtml;
var askedAboutWord = 0;



/* ******************************************************************************** * 
 *                       Check for Word-specific code                               * 
 * ******************************************************************************** */

function checkWordCode(str) {
	if ((/\bmso-/gi.test(str) || /:office\b/gi.test(str) || /\bo:p\b/gi.test(str)) && !askedAboutWord) {
		/* If there are Word-code - Make some changes to the editor, to prevent misusing */
		if (document.getElementById("bottomLeftLayer")) document.getElementById("bottomLeftLayer").style.display = 'none';
		document.getElementById("btnFixWord").style.visibility = 'visible';
		if (document.getElementById("topClassSelectClickDiv")) document.getElementById("topClassSelectClickDiv").style.display = 'none';
		if (document.getElementById("topClassWordSelectClickDiv")) document.getElementById("topClassWordSelectClickDiv").style.display = 'block';
		
		/* Remove FONT class="" and FONT style="" from Advanced-panel-selectbox */
		var f = document.forms.editorForm;
		if (showAdvStandardDiv && f.advSelector) {
			for (var i = 0; i < f.advSelector.length; i++) {
				if (f.advSelector.options[i].value == 'modeAdvStandardDiv') f.advSelector.options[i] = null;
			}
		}
		if (showAdvFontClassDiv && f.advSelector) {
			for (var i = 0; i < f.advSelector.length; i++) {
				if (f.advSelector.options[i].value == 'modeAdvFontClassDiv') f.advSelector.options[i] = null;
			}
		}
		if (showAdvFontStyleDiv && f.advSelector) {
			for (var i = 0; i < f.advSelector.length; i++) {
				if (f.advSelector.options[i].value == 'modeAdvFontStyleDiv') f.advSelector.options[i] = null;
			}
		}
		showAdvStandardDiv = 0;
		showAdvFontClassDiv = 0;
		showAdvFontStyleDiv = 0;
		if (!showAdvCodeStringDiv && !showAdvSettingsDiv && showAdv) {
			showAdv = 0;
			if (document.getElementById("advBtn")) document.getElementById("advBtn").style.display = 'none';
		}
		
		/* Ask once, if Word-fixing are to be executed directly */
		var sMess = '<? install/htdocs/sv/htmleditor/scripts/editor_functions_wordsupport.js/1 ?>\n\n';
		sMess += '<? install/htdocs/sv/htmleditor/scripts/editor_functions_wordsupport.js/2 ?>.\n\n';
		sMess += '<? install/htdocs/sv/htmleditor/scripts/editor_functions_wordsupport.js/3 ?>\n\n';
		sMess += '<? install/htdocs/sv/htmleditor/scripts/editor_functions_wordsupport.js/4 ?>.\n';
		sMess += '<? install/htdocs/sv/htmleditor/scripts/editor_functions_wordsupport.js/5 ?>.\n\n';
		sMess += '<? install/htdocs/sv/htmleditor/scripts/editor_functions_wordsupport.js/6 ?>\n\n';
		sMess += '<? install/htdocs/sv/htmleditor/scripts/editor_functions_wordsupport.js/7 ?>.';
		
		askedAboutWord = 1;
		if (confirm(sMess)){
			doFixWordHTML();
		}
	}
}

function doFixWordHTML() {
	if (document.getElementById("waitingDiv")) document.getElementById("waitingDiv").style.display = 'block';
	setTimeout("fixWordHTML(1)", 100);
}


/* ******************************************************************************** * 
 *                       Remove Word-specific and obsolete code                     * 
 * ******************************************************************************** */

function fixWordHTML(flag) {
	var f, lay, retStr, re, flag;
	lay = document.getElementById("editorDiv");
	retStr = lay.innerHTML.toString();
	
	if (flag) {
		
		// replace <A name=_Toc[\d]+></A> => ''
		re = /(.*?)<A name\=_Toc[\d]+>(.*?)<\/A>/gi;
		retStr = retStr.replace(re, "$1$2");
		// replace  lang=SV => ''
		re = /(.*?) lang=SV(.*?)/gi;
		retStr = retStr.replace(re, "$1$2");
		// replace <?xml:namespace WHATEVER /> => ''
		re = /(.*?)<\?xml:namespace\b[^\/>]*\/>(.*?)/gi;
		retStr = retStr.replace(re, "$1$2");
		// replace <o:p></o:p> => ''
		re = /(.*?)<o:p>(.*?)<\/o:p>/gi;
		retStr = retStr.replace(re, "$1$2");
		// replace ; mso-??: NN => ''
		re = /[;\s]{0,2}mso\-[A-Z0-9\-:\s']*[^";]/gi;
		retStr = retStr.replace(re, "");
		// replace ; tab-??: NN => ''
		re = /[;\s]{0,2}tab\-[A-Z0-9\-:\s\.]*[^";]/gi;
		retStr = retStr.replace(re, "");
		// delete all span and font
		re = /(<FONT[^>]*>)|(<\/?FONT>)|(<SPAN[^>]*>)|(<\/?SPAN>)/gi;
		retStr = retStr.replace(re, "");
		// delete empty style="" => ''
		re = /\sstyle=""/gi;
		retStr = retStr.replace(re, "");
		// replace style="nn" => ''
		if (removeAllStyles) {
			re = /\sstyle="[^"]*"/gi;
			retStr = retStr.replace(re, "");
		}
		// replace <[ovw]:XXX> and </[ovw]:XXX> => ''
		re = /(<[OVW]:[^>]*>)|(<\/[OVW]:[^>]*>)/gi;
		retStr = retStr.replace(re, "");
		// replace <H[1-6]> => '<H[1-6] class="MsoH[1-6]">'
		re = /(<)(H[1-6])([^>]*)(>)/gi;
		retStr = retStr.replace(re, "$1$2 class=Mso$2$3$4");
		
		// Add "Mso" in front of all classes
		re = /(\sclass="?)([\w]+)("?)/gi;
		retStr = retStr.replace(re, "$1Mso$2$3");
		// Remove Mso duplicates
		re = /(\sclass="?)Mso(Mso)([\w]+)("?)/gi;
		retStr = retStr.replace(re, "$1$2$3$4");
		// replace empty tags
		re = /(<)([A-Z1-9]+>)\s?(<\/\2)/gi;
		retStr = retStr.replace(re, "");
		
		// optional fixing before saving...
		// fix <td
		re = /([^\t]<td[^>]*>)[\s]+|([^\t]<td>)[\s]+/gi;
		retStr = retStr.replace(re, "\t$1");
		// fix </td
		re = /[\s]+(<\/td>)/gi;
		retStr = retStr.replace(re, "$1");
		// fix <li
		re = /(<li[^>]*>)[\s]*/gi;
		retStr = retStr.replace(re, "\t$1");
		// fix </?L
		re = /(.*?)(<\/[OUD]L>)(.*?)/gi;
		retStr = retStr.replace(re, "$1\n$2\n$3");
		
		
		
		/* ***** check if all the classes exists in the CSS (or in 'config/arrWordClassesName' actually).
							Otherwise they will bee replaced with the default class. ***** */
		
		re = /(\sclass="?)([\w]+)("?)/gi;
		arrMatches = retStr.match(re);
		var arrNotUsedClasses = 'foo';
		// * If classname doesn't appear in the "arrWordClassesName-array" - add it to a "arrNotUsedClasses-array"
		for (var i = 0; i < arrMatches.length; i++) {
			arrMatches[i] = arrMatches[i].replace(/(\sclass="?)([\w]+)("?)/gi, "$2");
			if (!isCssClass(arrMatches[i])) arrNotUsedClasses += ',' + arrMatches[i];
		}
		arrNotUsedClasses = arrNotUsedClasses.split(',');
		/* * Replace all classnames that aren't in the "arrWordClassesName-array"
		     - to the first post in the same array (MsoNormal) */
		var defClass = arrWordClassesName[0].substr(1);
		for (var i = 1; i < arrNotUsedClasses.length; i++) {
			re = new RegExp("(class=\"?)(" + arrNotUsedClasses[i] + ")", "gi");
			retStr = retStr.replace(re, "$1" + arrWordClassesName[0].substr(1));
		}
		if (document.getElementById("waitingDiv")) document.getElementById("waitingDiv").style.display = 'none';
	} else {
		askedAboutWord = 0;
	}
	
	lay.innerHTML = retStr;
}


function isCssClass(what) {
	var re = new RegExp("[.]?" + what + "[,]?","gi");
	var str = arrWordClassesName.join(',');
	return re.test(str);
	//alert(re.test(str));
}
