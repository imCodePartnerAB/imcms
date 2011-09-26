<%@ page contentType="text/css" pageEncoding="UTF-8" %>
* {
    margin: 0;
    padding: 0;
}

html {
    height: 100%;
}

input, select, th, td {
    font-size: 1em;
}

.right {
    float: right !important;
}

.left {
    float: left !important;
}

.clearboth {
    clear: both !important;
}

.center {
    text-align: center !important;
}

.clearheight {
    clear: both;
    height: 0;
    overflow: hidden;
}

/* slightly enhanced, universal clearfix hack */
.clearfix:after {
     visibility: hidden;
     display: block;
     font-size: 0;
     content: " ";
     clear: both;
     height: 0;
     }
.clearfix { display: inline-block; }
/* start commented backslash hack \*/
* html .clearfix { height: 1%; }
.clearfix { display: block; }
/* close commented backslash hack */

.m10t {
    margin-top: 10px !important;
}

.m15t {
    margin-top: 15px !important;
}

.m10b {
    margin-bottom: 10px !important;
}

.h10 {
    height: 10px !important;
}

.red {
    color: red;
}

a {
    color: #06f;
    cursor: pointer;
    text-decoration: underline;
}

img, table {
    border: 0 none;
}

.inBtnGroup {
    margin-right: 5px;
}

a.imcmsFormBtnSmall.disabled, input.imcmsFormBtnSmall.disabled {
    background-color: #B8C6D5;
    border: 1px outset #DAE4EF;
    border-color: #DAE4EF #999999 #999999 #DAE4EF;
}

.btnBack {
	float: left !important;
}

.imcmsFormBtn, .imcmsFormBtnSmall {
    text-decoration:none;
}

.btnBack span {
	display: block;
	text-align: center;
	
	min-width: 50px;
	width: auto !important;
	width: 50px;
}

#containerTop, #containerContent {
    margin: 0 auto !important;
    padding-right: 20px;
    padding-left: 20px;
}

#containerContent {
    clear: both !important;
    padding-top: 10px;
    padding-bottom: 20px;
    width:850px;
}

#containerTop {
    background-color: #20568D;
    color: #fff;
}

#backButton {
    margin-bottom: 15px;
}

ul.tabs {
    list-style-type:none;
    font-size: 0.7916em;
    font-weight: 600;
}
ul.tabs li {
    float: left !important;
    font-family: Tahoma, Arial, sans-serif;
    text-align: center;
    color: #fff;
    background-color: #4076ad;
    border: 1px outset #668DB6;
    border-color: #fff #002f5f #333333 #fff;
    border-bottom: 1px solid #20568D;
    padding: 2px 6px;
    cursor: pointer;
    
    min-width: 80px;
    width: auto !important;
    width: 80px;
}
ul.tabs li.sel {
    background-color: #f5f5f7;
    border: 1px solid #f5f5f7;
    border-color: #f5f5f7 #bababd #f5f5f7 #f5f5f7;
    color: #4076ad;
    cursor: default;
}
ul.tabs a {
    text-decoration: none;
    color: #fff;
}
li.sel a {
	color: #4076ad;
}

.pageHeading {
    font-family: Tahoma,Arial,Verdana,sans-serif;
    font-weight: bold;
    font-size: 1.416em;
}

.minH30 {
	min-height: 30px;
	height: auto !important;
	height: 30px;
}

.infoRow {
	padding:3px 2px;
}

.minW60 {
	min-width: 60px;
	width: auto !important;
	width: 60px;
}

.detailedTooltipThumb {
    background: white;
    border:1px solid #888;
    width:160px;
    margin-right:10px;
    padding:5px 0;
}

.roleTable th, .roleTable td {
    padding: 5px;
}

.libraryCategoriesTable th, .libraryCategoriesTable td {
    padding: 5px;
    text-align: left;
}

.editCategoryTable input[disabled] {
    border: none;
    background: white;
}

#freetext.placeholder {
    color: gray;
    font-style: italic;
}

.imcmsAdmHeading {
    padding-bottom: 15px;
    border-bottom:1px solid #20568D;
}


/* uploadify button */
div.UploadifyButtonWrapper{
    position:relative;
    float: left;
    margin-right: 5px;
}

/* fake button */
div.UploadifyButtonWrapper button {
    position:absolute; /* relative to UploadifyButtonWrapper */
    top:0;
    left:0;
    z-index:0;
    display:block;
    float:left;
}

/* pass hover effects to button */
div.UploadifyButtonWrapper a.Hover {
    background:orange;
    color:white;
}

/* position flash button above css button */
div.UploadifyObjectWrapper {
    position:relative;
    z-index:10;
}


/* external files libraries and file table */
#listOfLibraries, #listOfLibraries ul {
    list-style-type: none;
}

#listOfLibraries li {
    cursor: pointer;
    padding-top:2px;
    padding-bottom:2px;
}

#listOfLibraries li img {
    padding-right: 5px;
}

#listOfLibraries {
    padding:0 5px 5px 5px;
}

#listOfLibraries ul {
    padding-left:40px;
}

.currentLibrary {
    font-weight: bold;
}

/* export dialog */
#exportOverlay {
    background-color:#fff;
    display:none;
    border:1px solid black;
    padding-top:10px;
}

#exportOverlay .imcmsAdmHeading {
    padding-left:10px;
}

.fixedWidth {
    width:80px;
}

.fixedWidthInput {
    width:100px;margin-left:5px;
}

#exportImage {
    padding:50px 45px 30px 45px;
}

.exportBtns {
    text-align:right;
    margin-top:25px;
}

table.tablesorter tr.odd td, div.odd, .editCategoryTable tr.odd td, .editCategoryTable tr.odd input.disabled {
    background-color: white;
}

table.tablesorter tr.odd td, div.odd, .editCategoryTable tr.odd td, .editCategoryTable tr.odd {
    background-color: rgb(211, 234, 255);
}

#externalFilesUpload {
    margin-top:15px;
    margin-bottom:15px;
}

#externalFiles {
    margin-top:15px;
}

.externalFilesLibrariesAndEntries {
    width: 100%;
}

.externalFilesLibrariesAndEntries .tableSeparatorTop {
    background: url(${pageContext.request.contextPath}/images/grayLine.png) 50% 150% no-repeat;width:20px;
}

.externalFilesLibrariesAndEntries .tableSeparatorMiddle {
    background: url(${pageContext.request.contextPath}/images/grayDot.png) 50% 0 repeat-y;
}

.listOfLibrariesCell {
    vertical-align:top;
    width:230px;
}

.hint {
    color:gray;
    font-style: italic;
    font-weight: normal;
}

.addImageControls {
    margin-bottom: 15px;
}

.tablesorter .labelCell {
    width:60%;
}

.tablesorter td.useCell, .tablesorter td.editCell {
    text-align: center;
}

.preferencesSection {
    margin-bottom: 15px;
}

.editCategoryTable td {
    padding: 3px 2px;
}

.editCategoryTable th.header {
    width:320px;
}

.editCategoryTable input {
    width:99%;
}
