// I18N constants

// LANG: "sv", ENCODING: UTF-8 | ISO-8859-1
// Author: Markus Berg http://kelvin.nu pat<pat@engvall.nu>

// FOR TRANSLATORS:
//
//   1. PLEASE PUT YOUR CONTACT INFO IN THE ABOVE LINE
//      (at least a valid email address)
//
//   2. PLEASE TRY TO USE UTF-8 FOR ENCODING;
//      (if this is not possible, please include a comment
//       that states what encoding is necessary.)

HTMLArea.I18N = {

	// the following should be the filename without .js extension
	// it will be used for automatically load plugin language.
	lang: "se",

	tooltips: {
		bold:           "Fet",
		italic:         "Kursiv",
		underline:      "Understruken",
		strikethrough:  "Genomstruken",
		subscript:      "Nedsänkt",
		superscript:    "Upphöjd",
		justifyleft:    "Vänsterjustera",
		justifycenter:  "Centrera",
		justifyright:   "Högerjustera",
		justifyfull:    "Marginaljustera",
		orderedlist:    "Numrerade lista",
		unorderedlist:  "Punktlista",
		outdent:        "Minska indrag",
		indent:         "Öka indrag",
		forecolor:      "Textfärg",
		hilitecolor:    "Backgrundsfärg",
		horizontalrule: "Vågrät linje",
		createlink:     "Infoga länk",
		insertimage:    "Infoga/ändra bild",
		inserttable:    "Infoga tabell",
		htmlmode:       "Visa källkod",
		popupeditor:    "Visa i eget fönster",
		about:          "Om denna editor",
		showhelp:       "Hjälp",
		textindicator:  "Nuvarande stil",
		undo:           "Ångra senaste ändring",
		redo:           "Gör om senaste ändring",
		cut:            "Klipp ut",
		copy:           "Kopiera",
		paste:          "Klistra in",
		lefttoright:    "Textriktning vänster till höger",
		righttoleft:    "Textriktning höger till vänster",
		killword:       "Rensa Word-kod"
	},

	buttons: {
		"ok":           "Ok",
		"cancel":       "Avbryt"
	},

	msg: {
		"Path":         "Sökväg",
		"TEXT_MODE":    "Du befinner dig i TEXT-läge.  Använd knappen märkt med [<>] för att byta tillbaka till WYSIWYG.",
		"Select_text_first": "Du mÃ¥ste markera text innan du kan skapa en lÃ¤nk",
		
		"IE-sucks-full-screen" :
		// translate here
		"På grund av buggar i Internet Explorer så brukar helskärmsläget vålla problem om man använder " +
		"denna browser.  De problem som brukar uppstå är skräptecken på skärmen, problem med vissa " +
		"funktioner samt blandade krascher.  Om du använder Windows 95 eller 98 så kommer du troligtvis " +
		"att råka ut för ett 'General Protection Fault' varpå du behöver starta om din dator.\n\n" +
		"Detta är den officiella varningen.  Tryck på Ok om du vill starta helskärmsläget ändå."
	},

	dialogs: {
		"Cancel"                                            : "Avbryt",
		"Insert/Modify Link"                                : "Infoga/ändra länk",
		"New window (_blank)"                               : "Nytt fönster (_blank)",
		"None (use implicit)"                               : "Ingen (använd implicit)",
		"OK"                                                : "OK",
		"Other"                                             : "Annan",
		"Same frame (_self)"                                : "Samma ram (_self)",
		"Target:"                                           : "Mål:",
		"Title (tooltip):"                                  : "Titel (tooltip):",
		"Top frame (_top)"                                  : "Toppram (_top)",
		"URL:"                                              : "URL:",
		"You must enter the URL where this link points to"  : "Du måste ange en URL som den här länken ska peka på"
	}
};

/*
Swedish characters:
(å) &aring; = Ã¥
(Å) &Aring; = ÃE
(ä) &auml;  = Ã¤
(Ä) &Auml;  = ÃD
(ö) &ouml;  = Ã¶
(Ö) &Ouml;  = ÃV
*/
