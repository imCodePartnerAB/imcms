installation av HTML-editor:

zip'a upp filerna i mapen webapps/imcms/htmleditor/

1.  Byt ut flen WEB-INF\templates\change_text.html

2.  Ställ in rätt sökvägar i filerna: editor_config.vbs och editor.js

- editor_config.vbs
	raden   getDocPath = "@servleturl@/"      (path till servlet-mappen)

- editor.js

/* ****************************************************************************************************
 *                                                                                                    *
 *         CONFIGURATION                                                                              *
 *                                                                                                    *
 **************************************************************************************************** */

/* *******************************************************************************************
 *         PATH TO THE SERVLETS:   include "/imcms/" if in use.                              *
 ******************************************************************************************* */

var servletPath = "@servleturl@/";

    /* ------------------------------------------------------------------- *
     *         the host. may not be empty. include "/imcms/" if in use.    *
     * ------------------------------------------------------------------- */

var strRightPath = "http://" + location.host + "@rooturl@/";

/* *******************************************************************************************
 *         PATHS TO REPLACE ON LOAD AND SAVE:   *
 ******************************************************************************************* */

	/* leave one empty ("") if there are no "dev-domains" */
arrWrongPaths = new Array();
arrWrongPaths[0] = "http://" + location.host + "@rooturl@/";
/* arrWrongPaths[1] = "http://tommy.imcode.com:8080/"; */
