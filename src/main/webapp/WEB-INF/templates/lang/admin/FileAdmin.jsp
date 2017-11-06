<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<html>
<head>
<title><? templates/sv/AdminManager_adminTask_element.htm/5 ?></title>

    <link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/imcms_admin.css.jsp">
    <script src="${contextPath}/js/imcms/imcms_admin.js.jsp" type="text/javascript"></script>

    <script language="JavaScript">
<!--

/* *******************************************************************
 *           SETTINGS                                                *
 ******************************************************************* */

var imC_PATH = "${contextPath}"; // check behaviour of this expression language in scriptlet
var JSP_PATH = "${contextPath}/imcms/${language}/jsp"; // check behaviour of this expression language in scriptlet

/* *******************************************************************
 *           FUNCTIONS                                               *
 ******************************************************************* */

var ns    = (document.layers) ? 1 : 0;
var ie    = (document.all) ? 1 : 0;
var moz   = (document.getElementById) ? 1 : 0;
var gecko = (moz && !ie) ? 1 : 0;
var isMac = (/mac/i.test(navigator.platform)) ? 1 : 0;

wDir = new Array(3); // webdir[iside]
wDir[0] = "";
wDir[1] = "";
wDir[2] = "";

/* ***** REGEXP PATTERNS ***** */
/* isImage - image types (change in "FileAdmin_preview.jsp" too) */
var pai = "\\.(GIF|JPE?G|PNG)$";
/* isViewFile - viewable file types (change in "FileAdmin_preview.jsp" too, if image) */
var pa  = "(\\.(GIF|JPE?G|PNG|BMP|AVI|MPE?G|HTML?|CSS|JS|VBS|TXT|INC|JSP|ASP|FRAG)$)|(\\.LOG)";
/* isEditFileText - viewable file types that will open in readonly mode in "FileAdmin_edit.jsp" */
var pat = "(\\.(HTML?|CSS|JS|VBS|TXT|INC|JSP|ASP|FRAG)$)|(\\.LOG)";
/* isEditFile - editable file types (change in "FileAdmin_edit.jsp" too) */
var pae = "\\.(HTML?|CSS|JS|VBS|TXT|INC|JSP|ASP|FRAG)$";
/* isDir */
var isDirectoryPattern = "(/|\\.|\\\\)$";
/* isSysFold */
var pas = "webapps|[\\.]{2}";
/* isHiddenFold */
var pao = "WEB-INF";
/* isHtmlFile */
var pah = "\\.html?$";
/* isStatFile */
var par = "(/|\\\\)stats(/|\\\\).+\\.html?";

function checkFileType(iside, doReset) {
    if (!(isMac && ie)) {
        var f = document.forms[0];
        /* Viewable file */
        if (!doReset && isFile(iside) && (isViewFile(iside) || isStatFile(iside) || (isHtmlFile && !isHiddenFold(iside)))) {
            if (iside == 2) {
                f.preview2.disabled  = 0;
                f.preview2.className = "imcmsFormBtnSmall";
            } else {
                f.preview1.disabled  = 0;
                f.preview1.className = "imcmsFormBtnSmall";
            }
        } else {
            if (iside == 2) {
                f.preview2.disabled  = 1;
                f.preview2.className = "imcmsFormBtnSmallDisabled";
            } else {
                f.preview1.disabled  = 1;
                f.preview1.className = "imcmsFormBtnSmallDisabled";
            }
        }
        /* All files */
        if (!doReset && isFile(iside)) {
            if (iside == 2) {
                f.download2.disabled  = 0;
                f.download2.className = "imcmsFormBtnSmall";
            } else {
                f.download1.disabled  = 0;
                f.download1.className = "imcmsFormBtnSmall";
            }
        } else {
            if (iside == 2) {
                f.download2.disabled  = 1;
                f.download2.className = "imcmsFormBtnSmallDisabled";
            } else {
                f.download1.disabled  = 1;
                f.download1.className = "imcmsFormBtnSmallDisabled";
            }
        }
        /* Editable files */
        if (!doReset && isEditFile(iside)) {
            if (iside == 2) {
                f.edit2.disabled  = 0;
                f.edit2.className = "imcmsFormBtnSmall";
            } else {
                f.edit1.disabled  = 0;
                f.edit1.className = "imcmsFormBtnSmall";
            }
        } else {
            if (iside == 2) {
                f.edit2.disabled  = 1;
                f.edit2.className = "imcmsFormBtnSmallDisabled";
            } else {
                f.edit1.disabled  = 1;
                f.edit1.className = "imcmsFormBtnSmallDisabled";
            }
        }
        /* System folders (default installed & locked) */
        if (doReset || isSysFold(iside) || getFile(iside) == "") {
            if (iside == 2) {
                f.copy2.disabled    = 1;
                f.move2.disabled    = 1;
                //f.delete2.disabled  = 1;
                f.copy2.className   = "imcmsFormBtnSmallDisabled";
                f.move2.className   = "imcmsFormBtnSmallDisabled";
                //f.delete2.className = "imcmsFormBtnSmallDisabled";
            } else {
                f.copy1.disabled    = 1;
                f.move1.disabled    = 1;
                //f.delete1.disabled  = 1;
                f.copy1.className   = "imcmsFormBtnSmallDisabled";
                f.move1.className   = "imcmsFormBtnSmallDisabled";
                //f.delete1.className = "imcmsFormBtnSmallDisabled";
            }
        } else {
            if (iside == 2) {
                f.copy2.disabled    = 0;
                f.move2.disabled    = 0;
                //f.delete2.disabled  = 0;
                f.copy2.className   = "imcmsFormBtnSmall";
                f.move2.className   = "imcmsFormBtnSmall";
                //f.delete2.className = "imcmsFormBtnSmall";
            } else {
                f.copy1.disabled    = 0;
                f.move1.disabled    = 0;
                //f.delete1.disabled  = 0;
                f.copy1.className   = "imcmsFormBtnSmall";
                f.move1.className   = "imcmsFormBtnSmall";
                //f.delete1.className = "imcmsFormBtnSmall";
            }
        }
    }
}

function isViewFile(iside) {
    re = new RegExp(pa, 'gi');
    var sfile = getFile(iside);
    return re.test(sfile);
}

function isImageFile(iside) {
    re = new RegExp(pai, 'gi');
    var sfile = getFile(iside);
    return re.test(sfile);
}

function isEditFile(iside) {
    re = new RegExp(pae, 'gi');
    var sfile = getFile(iside);
    var retVal = re.test(sfile);
    /* don't allow edit of statfiles */
    if (isStatFile(iside)) {
        retVal = false;
    }
    return retVal;
}

function isFile(iside) {
    re = new RegExp(isDirectoryPattern, 'gi');
    var sfile = getFile(iside);
    return !re.test(sfile);
}

function isSysFold(iside) {
    re = new RegExp(pas, 'gi');
    var sfile = getFile(iside);
    return re.test(sfile);
}

function isHiddenFold(iside) {
    re = new RegExp(pao, 'g');
    var sfile = (getFile(iside).indexOf(":") != -1) ? getFile(iside) : wDir[iside] + "\\" + getFile(iside);
    return re.test(sfile);
}

function isHtmlFile(iside) {
    re = new RegExp(pah, 'gi');
    var sfile = getFile(iside);
    return re.test(sfile);
}

function isEditFileText(iside) {
    re = new RegExp(pat, 'gi');
    var sfile = getFile(iside);
    return re.test(sfile);
}

function isStatFile(iside) {
    re = new RegExp(par, 'gi');
    var sfile = (getFile(iside).indexOf(":") != -1) ? getFile(iside) : wDir[iside] + "\\" + getFile(iside);
    return re.test(sfile);
}

function dblClickAction(iside) {
    if (!(isMac && ie)) {
        if (isImageFile(iside) || isStatFile(iside)) {
            //alert("1") ;
            previewFile(iside);
        } else if (isHtmlFile(iside) && !isHiddenFold(iside)) {
            //alert("2") ;
            openFile(iside);
        } else if (isEditFileText(iside)) {
            //alert("3") ;
            editFile(iside,1);
        } else if (isFile(iside) || isViewFile(iside)) {
            //alert("4") ;
            var fBtn = eval('document.forms[0].download' + iside);
            fBtn.click();
        } else {
            //alert("5") ;
            var fBtn = eval('document.forms[0].change' + iside);
            fBtn.click();
        }
    }
}

function previewFile(iside) {
    var form = document.forms[0];
    var sdir = (iside == 2) ? form.dir2.value : form.dir1.value;
    var sfile = getFile(iside);
    sfile = wDir[iside] + sfile;
    if (isStatFile(iside)) { // stat
        //alert("1") ;
        popWinOpen(800,570,JSP_PATH + "/FileAdmin_preview.jsp?isStat=1&file=" + escape(sfile),"filePreview",1,0);
    } else if (isHtmlFile(iside) && !isHiddenFold(iside)) { // html
        //alert("2") ;
        openFile(iside);
    } else if (isEditFileText(iside)) { // view textfiles "readonly"
        //alert("3") ;
        editFile(iside,1);
    } else if (isImageFile(iside)) { // file
        //alert("4") ;
        popWinOpen(800,570,JSP_PATH + "/FileAdmin_preview.jsp?file=" + escape(sfile),"filePreview",1,0);
    } else if (isFile(iside)) { // other files
        //alert("5") ;
        var fBtn = eval('document.forms[0].download' + iside);
        fBtn.click();
    }
}

function openFile(iside) {
    var f = document.forms[0];
    var sdir = (iside == 2) ? f.dir2.value : f.dir1.value;
    var sfile = getFile(iside);
    sfile = wDir[iside] + sfile;
    window.open(imC_PATH + "/" + sfile);
}

function editFile(iside,readonlyFlag) {
    var f = document.forms[0];
    var sdir = (iside == 2) ? f.dir2.value : f.dir1.value;
    var sfile = getFile(iside);
    sfile = wDir[iside] + sfile;
    readonlyFlag = (readonlyFlag) ? "&readonly=1" : "";
    popWinOpen(800,570,JSP_PATH + "/FileAdmin_edit.jsp?file=" + escape(sfile) + readonlyFlag,"fileEdit",1,0);
}

function getFile(iside) {
    var f = document.forms[0];
    var theVal = "";
    switch (iside) {
        case 1:
            if (f.files1.selectedIndex > -1) theVal = f.files1.options[f.files1.selectedIndex].value;
            break;
        case 2:
            if (f.files2.selectedIndex > -1) theVal = f.files2.options[f.files2.selectedIndex].value;
            break;
    }
    return theVal;
}

function setSelectedFolders() {
    var f = document.forms[0];
    for (var i = 0; i < f.files1.length; i++) {
        var option = f.files1.options[i] ;
        var dir = f.dir1.value ;
        if (option.value == dir) {
            option.style.backgroundColor = "#FFFF99"; // the Dir
        } else if ( dir.length > option.value.length && dir.substring(0, option.value.length) == option.value ) {
            option.style.backgroundColor = "#FFFFDD"; // a subDir
        }
    }
    for (var i = 0; i < f.files2.length; i++) {
        var option = f.files2.options[i] ;
        var dir = f.dir2.value ;
        if (option.value == dir) {
            option.style.backgroundColor = "#FFFF99"; // the Dir
        } else if ( dir.length > option.value.length && dir.substring(0, option.value.length) == option.value ) {
            option.style.backgroundColor = "#FFFFDD"; // a subDir
        }
    }
}
//-->
</script>


</head>
<body bgcolor="#FFFFFF" onLoad="checkFileType(1,true); checkFileType(2,true); setSelectedFolders();">

<ui:imcms_gui_outer_start/>
<c:set var="heading">
    <fmt:message key="templates/sv/AdminManager_adminTask_element.htm/5"/>
</c:set>
<ui:imcms_gui_head heading="${heading}"/>
<form method="post" action="FileAdmin" enctype="multipart/form-data">
<table border="0" cellspacing="0" cellpadding="0" width="100%">
    <input type="hidden" name="dir1" value="${dir1}">
    <input type="hidden" name="dir2" value="${dir2}">

    <tr>
        <td><input class="imcmsFormBtn" height="22" type="submit" name="cancel" value="<? templates/sv/FileAdmin.html/2022 ?>">
            <input class="imcmsFormBtn" type="button" value="<? global/help ?>" onClick="openHelpW('FileManager'); return false;"></td>
</tr>
</table>
    <ui:imcms_gui_mid/>

    <table border="0" cellspacing="0" cellpadding="0" width="760" align="center">
<tr>
    <td colspan="2" class="small"><? templates/sv/FileAdmin.html/3 ?>${dir1}</td>
    <td colspan="2">&nbsp;</td>
    <td colspan="2" align="right" class="small"><? templates/sv/FileAdmin.html/4 ?>${dir2}</td>
</tr>
<tr>
    <td colspan="8"><ui:imcms_gui_hr wantedcolor="blue"/></td>
</tr>
<tr>
    <td colspan="2">
        <table border="0" cellpadding="0" cellspacing="0" width="100%">
            <tr>
                <td align="right"><input
                        type="submit" name="change1" class="imcmsFormBtnSmall" height="22" value="<? templates/sv/FileAdmin.html/2001 ?>"></td>
            </tr>
            <tr>
                <td><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1" height="2" alt=""></td>
            </tr>
            <tr>
                <td align="right"><span class="imcmsAdmForm">
		<select name="files1" id="fileSel_1" size="20" class="imcmsAdmForm" style="width:330px"
                onChange="checkFileType(1,false)"
                onDblClick="dblClickAction(1)" multiple>
            ${files1}
		</select></span></td>
            </tr>
        </table></td>

    <td colspan="2" align="center">
        <table border="0" cellpadding="2" cellspacing="0">
            <tr>
                <td colspan="2" align="center" class="small"><? templates/sv/FileAdmin.html/6 ?></td>
            </tr>
            <tr>
                <td align="right"><input class="imcmsFormBtnSmall" height="22" type="submit" name="delete1" value="&nbsp;<? templates/sv/FileAdmin.html/2002 ?>&nbsp;"></td>
                <td><input class="imcmsFormBtnSmall" height="22" type="submit" name="delete2" value="&nbsp;<? templates/sv/FileAdmin.html/2003 ?>&nbsp;"></td>
            </tr>
            <tr>
                <td colspan="2" align="center" class="small">
                    <img src="${contextPath}/imcms/${language}/images/admin/line_hr2.gif" width="50" height="6"
                         alt=""><? templates/sv/FileAdmin.html/7 ?></td>
            </tr>
            <tr>
                <td align="right"><input class="imcmsFormBtnSmall" height="22" type="submit" name="move2" value="<? templates/sv/FileAdmin.html/2004 ?>"></td>
                <td><input class="imcmsFormBtnSmall" height="22" type="submit" name="move1" value="<? templates/sv/FileAdmin.html/2005 ?>"></td>
            </tr>
            <tr>
                <td colspan="2" align="center" class="small">
                    <img src="${contextPath}/imcms/${language}/images/admin/line_hr2.gif" width="50" height="6"
                         alt=""><? templates/sv/FileAdmin.html/8 ?></td>
            </tr>
            <tr>
                <td align="right"><input class="imcmsFormBtnSmall" height="22" type="submit" name="copy2" value="&nbsp;<? templates/sv/FileAdmin.html/2006 ?>&nbsp;"></td>
                <td><input class="imcmsFormBtnSmall" height="22" type="submit" name="copy1" value="&nbsp;<? templates/sv/FileAdmin.html/2007 ?>&nbsp;"></td>
            </tr>
            <tr>
                <td colspan="2" align="center" class="small">
                    <img src="${contextPath}/imcms/${language}/images/admin/line_hr2.gif" width="50" height="6"
                         alt=""><? templates/sv/FileAdmin.html/1002 ?></td>
            </tr>
            <tr>
                <td align="right"><input class="imcmsFormBtnSmall" height="22" type="submit" name="download1" value="&nbsp;<? templates/sv/FileAdmin.html/2008 ?>&nbsp;"></td>
                <td><input class="imcmsFormBtnSmall" height="22" type="submit" name="download2" value="&nbsp;<? templates/sv/FileAdmin.html/2009 ?>&nbsp;"></td>
            </tr>
            <tr>
                <td colspan="2" align="center" class="small">
                    <img src="${contextPath}/imcms/${language}/images/admin/line_hr2.gif" width="50" height="6"
                         alt=""><? templates/sv/FileAdmin.html/9 ?></td>
            </tr>
            <tr>
                <td align="right"><input class="imcmsFormBtnSmall" height="22" type="button" name="preview1" value="&nbsp;<? templates/sv/FileAdmin.html/2010 ?>&nbsp;" onClick="previewFile(1)"></td>
                <td><input class="imcmsFormBtnSmall" height="22" type="button" name="preview2" value="&nbsp;<? templates/sv/FileAdmin.html/2011 ?>&nbsp;" onClick="previewFile(2)"></td>
            </tr>
            <tr>
                <td colspan="2" align="center" class="small">
                    <img src="${contextPath}/imcms/${language}/images/admin/line_hr2.gif" width="50" height="6"
                         alt=""><? templates/sv/FileAdmin.html/10 ?></td>
            </tr>
            <tr>
                <td align="right"><input class="imcmsFormBtnSmall" height="22" type="button" name="edit1" value="&nbsp;<? templates/sv/FileAdmin.html/2012 ?>&nbsp;" onClick="editFile(1,0);"></td>
                <td><input class="imcmsFormBtnSmall" height="22" type="button" name="edit2" value="&nbsp;<? templates/sv/FileAdmin.html/2013 ?>&nbsp;" onClick="editFile(2,0);"></td>
            </tr>
        </table></td>

    <td colspan="2" align="right">
        <table border="0" cellpadding="0" cellspacing="0" width="100%">
            <tr>
                <td><input type="submit" name="change2"
                           class="imcmsFormBtnSmall" height="22" value="<? templates/sv/FileAdmin.html/2014 ?>"></td>
            </tr>
            <tr>
                <td><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1" height="2" alt=""></td>
            </tr>
            <tr>
                <td>
		<span class="imcmsAdmForm">
		<select name="files2" id="fileSel_2" size="20" class="imcmsAdmForm" style="width:330px"
                onChange="checkFileType(2,false)"
                onDblClick="dblClickAction(2)" multiple>
            ${files2}
		</select></span></td>
            </tr>
        </table></td>
</tr>
<tr>
    <td colspan="6"><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1" height="8" alt=""></td>
</tr>
<tr>
    <td><input class="imcmsFormBtnSmall" height="22" style="width:90px" type="submit" name="upload1"
               value="<? templates/sv/FileAdmin.html/2015 ?>"></td>
    <td colspan="4" align="center"><span class="imcmsAdmForm"><? templates/sv/FileAdmin.html/1003 ?>&nbsp;
    <input type="file" name="file" class="imcmsAdmForm" size="20"></span></td>
    <td align="right"><input class="imcmsFormBtnSmall" height="22" style="width:90px" type="submit" name="upload2"
                             value="<? templates/sv/FileAdmin.html/2016 ?>"></td>
</tr>
<tr>
    <td colspan="6"><ui:imcms_gui_hr wantedcolor="blue"/></td>
</tr>
<tr>
    <td align="left"><input
            type="submit" name="rename1" class="imcmsFormBtnSmall" height="22" style="width:90px"
            value="<? templates/sv/FileAdmin.html/2017 ?>"><br><img
            src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1" height="2" alt=""><br><input
            type="submit" name="mkdir1" class="imcmsFormBtnSmall" height="22" style="width:90px"
            value="<? templates/sv/FileAdmin.html/2018 ?>"></td>
    <td colspan="4" align="center"><? templates/sv/FileAdmin.html/13 ?>
        <span class="imcmsAdmForm"><input type="text" name="name" class="imcmsAdmForm"></span></td>
    <td align="right"><input
            type="submit" name="rename2" class="imcmsFormBtnSmall" height="22" style="width:90px"
            value="<? templates/sv/FileAdmin.html/2019 ?>"><br><img
            src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1" height="2" alt=""><br><input
            type="submit" name="mkdir2" class="imcmsFormBtnSmall" height="22" style="width:90px"
            value="<? templates/sv/FileAdmin.html/2020 ?>"></td>
</tr>
<tr>
    <td><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="150" height="1" alt=""></td>
    <td><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="180" height="1" alt=""></td>
    <td colspan="2"><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="80" height="1" alt=""></td>
    <td><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="180" height="1" alt=""></td>
    <td><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="150" height="1" alt=""></td>
</tr>
</table>
</form>
<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>

<script language="JavaScript">
<!--
/*
 * Figure out the webpath when knowing the absolute path.
 * Put them in an easy accessable, array[what_side] array.
 */

wDir[1] = document.forms[0].dir1.value.replace(/\\\\/g, "/") ;
wDir[2] = document.forms[0].dir2.value.replace(/\\\\/g, "/") ;

/*
 * make the file-select's wider.
 * if supported and > 800 in screen resolution.
 */

if (document.getElementById) {
    if (parseInt(screen.width) > 800) {
        document.getElementById("fileSel_1").style.width = 400;
        document.getElementById("fileSel_2").style.width = 400;
    }
}
//-->
</script>

</body>
</html>
