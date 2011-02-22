<%@taglib prefix="vel" uri="imcmsvelocity"%>
<vel:velocity>
<html>
<head>


<title><? install/htdocs/imcms/html/link_editor.jsp/1 ?></title>

<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js.jsp" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF" marginwidth="0" marginheight="0" leftmargin="0" topmargin="0" class="imcmsAdmBgCont" style="border:0; margin:0" onLoad="focusField(1,'createLinkDesc')">

#gui_outer_start_noshade()
#gui_head_noshade("<? global/imcms_administration ?>")
<table border="0" cellspacing="0" cellpadding="0">
<form>
<tr>
	<td><input type="button" class="imcmsFormBtn" value="<? install/htdocs/imcms/html/link_editor.jsp/2001 ?>" onClick="window.close(); return false"></td>
</tr>
</form>
</table>
#gui_mid_noshade()
<table border="0" cellspacing="0" cellpadding="2" width="400">
<form name="createLinkForm" onSubmit="return false">
<tr>
	<td>
	<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td colspan="2">#gui_heading( "<? install/htdocs/imcms/html/link_editor.jsp/4/1 ?>" )</td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? install/htdocs/imcms/html/link_editor.jsp/5 ?></td>
		<td><input type="text" name="createLinkDesc" size="54" maxlength="255" style="width: 100%" value="<? install/htdocs/imcms/html/link_editor.jsp/6/1 ?>"></td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? install/htdocs/imcms/html/link_editor.jsp/7 ?></td>
		<td class="form">
		<select class="form" name="createLinkType" onChange="changeLinkType(document.forms.createLinkForm.createLinkType.options[document.forms.createLinkForm.createLinkType.selectedIndex].value)">
			<option value="GetDoc"><? install/htdocs/imcms/html/link_editor.jsp/8 ?></option>
			<option value="http"><? install/htdocs/imcms/html/link_editor.jsp/9 ?></option>
			<option value="mailto"><? install/htdocs/imcms/html/link_editor.jsp/10 ?></option>
			<option value="ftp"><? install/htdocs/imcms/html/link_editor.jsp/11 ?></option>
			<option value="#"><? install/htdocs/imcms/html/link_editor.jsp/12 ?></option>
			<option value="NAME"><? install/htdocs/imcms/html/link_editor.jsp/13 ?></option>
		</select></td>
	</tr>
	<tr>
		<td class="imcmsAdmText" nowrap><? install/htdocs/imcms/html/link_editor.jsp/1001 ?>&nbsp;</td>
		<td>
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td nowrap>
			<select name="createLinkTargetTemp" onChange="document.forms.createLinkForm.createLinkTarget.value = document.forms.createLinkForm.createLinkTargetTemp.options[document.forms.createLinkForm.createLinkTargetTemp.selectedIndex].value; document.forms.createLinkForm.createLinkTarget.focus(); document.forms.createLinkForm.createLinkTarget.select()">
				<option value="" selected><? install/htdocs/imcms/html/link_editor.jsp/14 ?></option>
				<option value="_blank"><? install/htdocs/imcms/html/link_editor.jsp/15 ?></option>
				<option value="_top"><? install/htdocs/imcms/html/link_editor.jsp/16 ?></option>
				<option value="_self"><? install/htdocs/imcms/html/link_editor.jsp/17 ?></option>
				<option value="_parent"><? install/htdocs/imcms/html/link_editor.jsp/18 ?></option>
				<option value="Skriv namn!"><? install/htdocs/imcms/html/link_editor.jsp/19 ?></option>
			</select></td>
			<td>&nbsp;</td>
			<td class="form" align="right"><input type="text" name="createLinkTarget" size="12" maxlength="50" style="width: 100" value=""></td>
		</tr>
		</table></td>
	</tr>
	<tr>
		<td class="imcmsAdmText" nowrap>
		<? install/htdocs/imcms/html/link_editor.jsp/1002 ?> &nbsp;</td>
		<td><input type="text" name="createLinkValue" size="54" maxlength="100" style="width: 100%" value="<? install/htdocs/imcms/html/link_editor.jsp/21/1 ?>"></td>
	</tr>
	<tr>
		<td class="imcmsAdmText" nowrap><? edit/link/cssClass ?></td>
		<td><input type="text" name="createLinkCss" size="20" maxlength="50" style="width: 150" value=""></td>
	</tr>
	<tr>
		<td colspan="2">#gui_hr( "blue" )</td>
	</tr>
	<tr>
		<td colspan="2" align="right">
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><input type="submit" value="<? install/htdocs/imcms/html/link_editor.jsp/2002 ?>" class="imcmsFormBtnSmall" width="120" style="width:120" onClick="createLink();"></td>
			<td>&nbsp;</td>
			<td><input type="reset" value="<? install/htdocs/imcms/html/link_editor.jsp/2003 ?>" class="imcmsFormBtnSmall" onClick="hideLinkExample(0);"></td>
		</tr>
		</table></td>
	</tr>
	<tr>
		<td colspan="2" nowrap>&nbsp;<br>#gui_heading( "<? install/htdocs/imcms/html/link_editor.jsp/25/1 ?>" )</td>
	</tr>
	<tr>
		<td colspan="2"><input type="text" name="theLinkCodeField" size="76" maxlength="255" style="width: 100%" value=""><div id="theLinkCodeFieldDiv"></div></td>
	</tr>
	<tr>
		<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="105" height="1"></td>
		<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="1" height="1"></td>
	</tr>
	</table></td>
</tr>
</form>
</table>
#gui_bottom_noshade()
#gui_outer_end_noshade()
<script language="javascript">
<!--
var ie4 = (document.all) ? 1 : 0;
var ns4 = (document.layers) ? 1 : 0;
var ns6 = (document.getElementById) ? 1 : 0;
var moz = (document.getElementById) ? 1 : 0;

var arrTheFieldValues = new Array("<? install/htdocs/imcms/html/link_editor.jsp/21/1 ?>", "http:/\/", "<? install/htdocs/imcms/html/link_editor.jsp/3002 ?>", "<? install/htdocs/imcms/html/link_editor.jsp/3003 ?>", "<? install/htdocs/imcms/html/link_editor.jsp/3003 ?>", "ftp:/\/");
var defValDesc = "<? install/htdocs/imcms/html/link_editor.jsp/3004 ?>";

function changeLinkType(what) {
	var theField     = document.forms.createLinkForm.createLinkValue;
	var theFieldDesc = document.forms.createLinkForm.createLinkDesc;
	switch (what) {
		case "GetDoc":
			theField.value     = getFieldVals(0,0);
			theFieldDesc.value = getFieldVals(0,1);
		break
		case "http":
			theField.value     = getFieldVals(1,0);
			theFieldDesc.value = getFieldVals(1,1);
		break
		case "mailto":
			theField.value     = getFieldVals(2,0);
			theFieldDesc.value = getFieldVals(2,1);
		break
		case "NAME":
			theField.value     = getFieldVals(3,0);
			theFieldDesc.value = getFieldVals(3,1);
		break
		case "#":
			theField.value     = getFieldVals(4,0);
			theFieldDesc.value = getFieldVals(4,1);
		break
		case "ftp":
			theField.value     = getFieldVals(5,0);
			theFieldDesc.value = getFieldVals(5,1);
		break
	}
	theField.focus();
	theField.select();
}

function getFieldVals(iNum,isDesc) {
	var theField     = document.forms.createLinkForm.createLinkValue;
	var theFieldDesc = document.forms.createLinkForm.createLinkDesc;
	if (isDesc) {
		if (iNum == 3 && (theFieldDesc.value == "" || theFieldDesc.value == defValDesc)) {
			return "";
		} else if (theFieldDesc.value == "" || theFieldDesc.value == defValDesc) {
			return defValDesc;
		} else {
			return theFieldDesc.value;
		}
		return (iNum == 3) ? "" : defValDesc;
	} else {
		var doChange = false;
		for (var i = 0; i < arrTheFieldValues.length; i++) {
			if (theField.value == arrTheFieldValues[i]) {
				doChange = true;
			}
		}
		if (doChange || theField.value == "") {
			return arrTheFieldValues[iNum];
		} else {
			return theField.value;
		}
	}
}

function createLink() {
	var f  = document.forms.createLinkForm;
	var po;
	if (parent.opener) {
		if (parent.opener.document.forms[0]) {
			po = parent.opener.document.forms[0] ;
		}
	}
	var linkDesc = f.createLinkDesc.value;
	var linkType = f.createLinkType.options[f.createLinkType.selectedIndex].value;
	var linkTarget = f.createLinkTarget.value;
	var linkValue = f.createLinkValue.value;
	var linkClass = f.createLinkCss.value;
	var theLinkCode = (linkType == "NAME") ? "<A NAME=\"" : "<A HREF=\"";
	if (linkType == "NAME") {
		theLinkCode += linkValue + "\"";
		theLinkCode += ">" + linkDesc + "</A>";
	} else {
		if (linkType == "GetDoc") {
			theLinkCode += "GetDoc?meta_id=" + linkValue + "\"";
		} else if (linkType == "mailto") {
			theLinkCode += "mailto:" + linkValue + "\"";
		} else if (linkType == "#") {
			if (linkValue.indexOf("#") == -1) linkValue = "#" + linkValue;
			theLinkCode += linkValue + "\"";
		} else {
			theLinkCode += linkValue + "\"";
		}
		if (linkTarget != "") {
			theLinkCode += " TARGET=\"" + linkTarget + "\"";
		}
		if (linkClass != "") {
			theLinkCode += " CLASS=\"" + linkClass + "\"";
		}
		theLinkCode += ">" + linkDesc + "</A>";
	}
	f.theLinkCodeField.value = theLinkCode;
	/* Check for HTML-mode */
	if (po) {
		var poEl = (po.format_type) ? po.format_type[1] : po.type[1] ;
		if (!(poEl.checked == 1)) {
			if(confirm("<? install/htdocs/imcms/html/link_editor.jsp/3005 ?>")){
				poEl.checked = 1;
			}
		}
	}
	f.theLinkCodeField.focus();
	f.theLinkCodeField.select();
	/* Make target="_blank" for preview */
	var previewHref = theLinkCode ;
	var str1, str2, str3 ;
	if (previewHref.indexOf("TARGET=") != -1) {
		str1 = previewHref.substring(0, previewHref.indexOf("TARGET=") + 8) ;
		str2 = previewHref.substring(previewHref.indexOf("TARGET=") + 8, previewHref.length) ;
		previewHref = str1 + "_blank" + str2.substring(str2.indexOf("\""), str2.length) ;
	} else {
		previewHref = previewHref.substring(0, previewHref.indexOf(">")) + " TARGET=\"_blank\"" + previewHref.substring(previewHref.indexOf(">"), previewHref.length) ;
	}
	if (moz) {
		if (document.getElementById("theLinkCodeFieldDiv")) {
			hideLinkExample(1) ;
			document.getElementById("theLinkCodeFieldDiv").innerHTML = "&nbsp;<br><span class=\"imcmsAdmText\"><b><? install/htdocs/imcms/html/link_editor.jsp/3006 ?>: &nbsp;</b>" + previewHref + "</span>" ;
		}
	}
}

function hideLinkExample(show) {
	if (moz) {
		if (document.getElementById("theLinkCodeFieldDiv")) {
			document.getElementById("theLinkCodeFieldDiv").innerHTML = "" ;
			document.getElementById("theLinkCodeFieldDiv").style.display = (show) ? "block" : "none" ;
		}
	}
}

hideLinkExample(0) ;
//-->
</script>

</body>
</html>
</vel:velocity>
