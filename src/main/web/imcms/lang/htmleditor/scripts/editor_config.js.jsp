<%@page contentType="text/javascript"%>
<%@taglib prefix="vel" uri="imcmsvelocity"%>
<vel:velocity><%
if (true == false) {
	%><script><%
} %>
/* **********************************
 *   By: Tommy Ullberg, imCode
 *   www.imcode.com
 *   Copyright © imCode AB
 ********************************* */


/* *******************************************************************************************
 *         Check for ?admin=1 in URL                                                         *
 ******************************************************************************************* */

function getParam2(attrib) {
	var idx = document.URL.indexOf('?');
	var retVal = '';
	var params = new Array();
	if (idx != -1) {
		var pairs = document.URL.substring(idx+1, document.URL.length).split('&');
		for (var i=0; i < pairs.length; i++) {
			nameVal = pairs[i].split('=');
			if (nameVal[0] == attrib) {
				retVal = nameVal[1];
			}
		}
		return retVal;
	}
}
var isAdmin = (getParam2('admin')) ? 1 : 0;

/* ****************************************************************************************************
 *                                                                                                    *
 *         CONFIGURATION                                                                              *
 *                                                                                                    *
 **************************************************************************************************** */

/* *******************************************************************************************
 *         PATH TO THE SERVLETS:   include "/imcms/" if in use.                              *
 ******************************************************************************************* */

var imcRootPath = "$contextPath";
var servletPath = "$contextPath/servlet/";

    /* ------------------------------------------------------------------- *
     *         the host. may not be empty. include "/imcms/" if in use.    *
     * ------------------------------------------------------------------- */

var strRightPath = "http://" + location.host + "$contextPath/";

/* *******************************************************************************************
 *         PATHS TO REPLACE ON LOAD AND SAVE:   *
 ******************************************************************************************* */

	/* leave one empty ("") if there are no "dev-domains" */
arrWrongPaths = new Array();
arrWrongPaths[0] = "http://" + location.host + "$contextPath/";
arrWrongPaths[1] = "http://" + location.host + "/";
//arrWrongPaths[2] = "http://mydev.domain.com:8080$contextPath/";


	/* servlets that will be "HREF-fixed" in the editor - (ie:  href="BackDoc"  ->  href="$contextPath/servlet/BackDoc") */
arrServletNames = new Array();
arrServletNames[0] = "GetDoc";
arrServletNames[1] = "AdminDoc";
arrServletNames[2] = "BackDoc";
arrServletNames[3] = "SearchDocuments";

/* Examples:
        <a href="GetDoc?meta_id=1234">link</a> AND
		    <img src="../images/image.gif" ... >
		    ...is replaced by MS-HTML (Microsoft Stupid HTML-functions) to...
		    <a href="http://www.Editor-Domain.com/htmleditor/GetDoc?meta_id=1234">link</a> AND
		    <img src="http://www.Editor-Domain.com/htmleditor/images/image.gif" ... >
		    ...because it "fixes" the link "to work".
		    All the "Wrong" paths will be replaced with the right one.
		    On [Save] and on [Preview] the right path is replaced with "$contextPath/" or $contextPath/servlet/GetDoc....
		    The result:
		    <a href="$contextPath/servlet/GetDoc?meta_id=1234">link</a> AND
		    <img src="$contextPath/images/image.gif" ... >
*/

/* *******************************************************************************************
 *         PATH TO THE STYLESHEET(S) OF THE SITE:                                            *
 ******************************************************************************************* */

arrCssPaths = new Array();
arrCssPaths[0] = "$contextPath/css/style_win_ie.css";
arrCssPaths[1] = "";

/* *******************************************************************************************
 *         PATH TO THE WORD-SPECIFIC STYLESHEET(S):                                          *
 ******************************************************************************************* */

arrCssWordPaths = new Array();
arrCssWordPaths[0] = "$contextPath/imcms/$language/htmleditor/css/default_word.css";
arrCssWordPaths[1] = "";

/* *******************************************************************************************
 *         FAVORITE STYLESHEET CLASSES:                                                      *
 ******************************************************************************************* */

    /* ------------------------------------------------------------------- *
     *         Only classes that you refer to with CLASS="theClassName"    *
     *         The first array as many CSS classes as you want             *
     *         - For the dropdown in the top panel (button-panel)          *
     * ------------------------------------------------------------------- */

    /* classname */
arrFavClassTop = new Array("headingBig","heading","norm","small");

    /* classdescription (leave empty for the same name as the classname) */
arrFavClassTopDesc = new Array("Big heading","Subheading","Normal","Small");

    /* ------------------------------------------------------------------- *
     *         This array is for the advanced panel:                       *
     *         'font class="X"' with 10 textfields                         *
     *         (Must be 10. Leave empty "" if necessary)                   *
     *         the first one becomes prechecked                            *
     * ------------------------------------------------------------------- */

arrFavClass = new Array("headingBig","heading","norm","small","navLink","dim","info","codered","codeblack","codeblue");

    /* ------------------------------------------------------------------- *
     *         The first array as many CSS classes as you want             *
     *          - For the dropdowns in the right panels                    *
     *         (Only classes that you refer to with CLASS="theClassName")  *
     * ------------------------------------------------------------------- */

arrAllClass = new Array("norm","headingBig","heading","small","navLink","dim","info","codered","codeblack","codeblue");

    /* ------------------------------------------------------------------- *
     *         Classes for MS-Word texts (with leading dot                 *
     *         (ie: '.class','.class'))                                    *
     *         - Any classes that aren't here will be replaced             *
     *         with the first one in the array - Sholud be ".MsoNormal"    *
     * ------------------------------------------------------------------- */

arrWordClassesName = new Array('.MsoNormal','.MsoBodyText','.MsoBrdtext','.MsoH1','.MsoRubrik1','.MsoH2','.MsoRubrik2','.MsoH3','.MsoRubrik3','.MsoH4','.MsoRubrik4','.MsoH5','.MsoRubrik5','.MsoH6','.MsoRubrik6','.MsoHeading7','.MsoRubrik7','.MsoHeading8','.MsoRubrik8','.MsoHeading9','.MsoRubrik9','.MsoTabell','.MsoList');

    /* ------------------------------------------------------------------- *
     *         classes for the Word-dropdown that appears when Word-code   *
     *         is pasted (No leading dot)                                  *
     * ------------------------------------------------------------------- */

    /* classname */
arrWordClassesSelect = new Array('MsoNormal','MsoH1','MsoH2','MsoH3','MsoH4','MsoH5','MsoH6','MsoTabell','MsoList');

    /* classdescription (leave empty for the same name as the classname) */
arrWordClassesDesc = new Array('Normal','Heading 1','Heading 2','Heading 3','Heading 4','Heading 5','Heading 6','Tabell','Lista');


/* ****************************************************************************************************
 *                                                                                                    *
 *         DEFAULT SETTINGS - FOR THE EDITOR:                                                         *
 *                                                                                                    *
 **************************************************************************************************** */

	/* Default width - Editor-view/preview: */
var cssDefaultWidth = '525'; // 10-525 or NN%

	/* Default colors - Editor-view/preview: */
var cssDefaultColorFont = 'rgb(0,0,0)'; // default: 'rgb(0,0,0)'
var cssDefaultColorBackground = 'rgb(255,255,255)'; // default: 'rgb(255,255,255)'

	/* Default font - Editor-view/preview: */
var cssDefaultFontSize = '10px'; // default: '10px'
var cssDefaultFontFamily = 'Verdana,Geneva,sans-serif'; // default: 'Verdana,Geneva,sans-serif'

	/* Default font - Code-view: */
var cssCodeFontSize = '11px'; // default: '11px'
var cssCodeFontFamily = 'Courier New,Courier,monospace'; // default: 'Courier New,Courier,monospace'

/* *******************************************************************************************
 *         COLOR DROPDOWNS                                                                   *
 ******************************************************************************************* */

	/* font-colors */
arrColorSelectValues = new Array("#000000","#C0C0C0","#808080","#FFFFFF","#800000","#FF0000","#800080","#FF00FF","#008000","#00FF00","#808000","#FFFF00","#000080","#0000FF","#008080","#00FFFF");
arrColorSelectDesc = new Array("Black","Silver","Gray","White","Maroon","Red","Purple","Purple","Fuchsia","Green","Lime","Olive","Yellow","Navy","Blue","Teal","Aqua");
arrColorSelectDescInverted = new Array(0,2,4,6,8,12,13,14); // Inverted text-colors - white text on these (zero-based)

	/* background-colors */
arrBackgroundColorSelectValues = new Array("transparent","white","#FFFF88","#FFBBBB","#BBFFBB","#C0C0FF","#E0E0E0");
arrBackgroundColorSelectDesc = new Array("Transparent","Vit","Gul","Rosa","Ljusgrön","Ljusblå","Ljusgrå");
arrBackgroundColorSelectDescInverted = new Array(); // white text on these (zero-based)

/* *******************************************************************************************
 *         ACTIVE PANELS & FUNCTIONS - WHAT TO SHOW                                          *
 ******************************************************************************************* */

/* Show simple panels */
var showSimple = 1;

	/* Choose (if showSimple = 1) */
		/* Create link */
		var showSimpleLinkDiv = 1;
		/* Create list */
		var showSimpleListDiv = 1;
		/* Insert pixel */
		var showSimplePixelDiv = 0;


/* Show advanced panels */
var showAdv = 0;

	/* Choose (if showAdv = 1) */
		/* Standard */
		var showAdvStandardDiv = 1;
		/* Font Class="" */
		var showAdvFontClassDiv = 0;
		/* Font Style="" */
		var showAdvFontStyleDiv = 0;
		/* Valfri kod */
		var showAdvCodeStringDiv = 0;
		/* Inställningar */
		var showAdvSettingsDiv = 0;

/* *******************************************************************************************
 *         MISC FUNCTIONS                                                                    *
 ******************************************************************************************* */

	/* ***** ENABLE DIRECT TEXTEDIT - NO ChangeText PAGE: *****
			* DISABLED FUNCTIONS - DO NOT ALTER */
	var directEditEnabled       = 0;
	var directEditViaChangeText = 0;


	/* Modal(Modeless) Dialog Window (no parent.opener) NOT READY YET! */

	var isModal = 0;

	/* Enable Word-HTML cleaing */
	var isWordEnabled = 1;

	/* Show right-click-menu (Context Menu) */
	var showContext = 1; // NOT READY YET!

	/* Show keyboard-shortcuts in docfoot */
	var showKBshortcuts = 1;

	/* Show HTML mode button */
	var showHtmlBtn = 1;

	/* Show Close (Stäng) button */
	var showCloseBtn = 1;

	/* Show Reset (Rensa) button */
	var showResetBtn = 1;

	/* Show Save (Spara) button */
	var showSaveBtn = 1;

	/* Show Help (Original) button [Hjälp] */
	var showHelpFullBtn = 0;

	/* Show Help (Per btn) button [?] */
	var showHelpSubjectBtn = 1;

	/* Confirm when saving (save and close) */
	var confirmSave = 0;

	/* Confirm when closing (no save) */
	var confirmClose = 0;


/* Admin settings (if there are a parameter "?admin=1" in ChangeText mode) */

if (isAdmin) {

	showSimple = 1;
	showSimpleLinkDiv = 1;
	showSimpleListDiv = 1;
	showSimplePixelDiv = 1;

	showAdv = 1;
	showAdvStandardDiv = 1;
	showAdvFontClassDiv = 1;
	showAdvFontStyleDiv = 1;
	showAdvCodeStringDiv = 1;
	showAdvSettingsDiv = 1;

	isWordEnabled = 1;
	showContext = 1;
	showKBshortcuts = 1;
	showHtmlBtn = 1;
	showCloseBtn = 1;
	showResetBtn = 1;
	showSaveBtn = 1;
	showHelpFullBtn = 1;
	showHelpSubjectBtn = 1;
}<%
if (true == false) {
	%></script><%
} %>
</vel:velocity>



