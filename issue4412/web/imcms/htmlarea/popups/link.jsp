<%@ page
	
	import="java.util.Map,
	        java.util.HashMap"
	
	contentType="text/html; charset=windows-1252"
	
%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"
%><%@ include file="../_editor_settings.jsp"
%><%!

Map langMap = new HashMap() ;

private String lang( String string, User user ) {
	try {
		boolean isLangSwe = user.getLanguage().getIsoCode639_2().equals("swe") ;
		return (isLangSwe && langMap.containsKey(string)) ? (String) langMap.get(string) : string ;
	} catch (Exception ex) {
		return string ;
	}
}

%><%

langMap.put("Create/edit link"          , "Skapa/ändra länk") ;
langMap.put("Write number"              , "Skriv nummer") ;
langMap.put("Write the address"         , "Skriv adressen") ;
langMap.put("Write anchor name"         , "Skriv ankarnamn") ;
langMap.put("The old link"              , "Gamla länken") ;
langMap.put("Linktype"                  , "Länktyp") ;
langMap.put("Internal link (page-ID)"   , "Intern länk (meta_id)") ;
langMap.put("External link or a path"   , "Annan webbsida eller en sökväg") ;
langMap.put("Mail address"              , "Mailadress") ;
langMap.put("FTP link"                  , "FTP länk") ;
langMap.put("URL/address/meta_id"       , "URL/adress/meta_id") ;
langMap.put("Title/tooltip"             , "Titel/beskrivning") ;
langMap.put("Show link in"              , "Visa länken i") ;
langMap.put("Same window (default)"     , "Samma fönster (Standard)") ;
langMap.put("New window"                , "Nytt fönster") ;
langMap.put("Replace window (no frames)", "Hela fönstret (utan frames)") ;
langMap.put("Same frame"                , "Samma frame") ;
langMap.put("Cancel"                    , "Avbryt") ;

%>
<vel:velocity>
<html>
<head>
<title><%= lang("Create/edit link",user) %></title>

<base href="http://<%= request.getHeader("Host") %>$contextPath/" id="baseUrl">

<script language="javascript">
<!--
var ie4 = (document.all) ? 1 : 0;
var ns4 = (document.layers) ? 1 : 0;
var ns6 = (document.getElementById) ? 1 : 0;
var moz = (document.getElementById) ? 1 : 0;

var arrTheFieldValues = new Array("<%= lang("Write number",user) %>!", "http:/\/", "<%= lang("Write the address",user) %>!", "<%= lang("Write anchor name",user) %>!", "<%= lang("Write anchor name",user) %>!", "ftp:/\/");
var defValDesc = "";

function changeLinkType(what) {
	var theField = document.forms[0].f_href;
	switch (what) {
		case "GetDoc":
			theField.value = getFieldVals(0);
		break
		case "http":
			theField.value = getFieldVals(1);
		break
		case "mailto":
			theField.value = getFieldVals(2);
		break
		case "NAME":
			theField.value = getFieldVals(3);
		break
		case "#":
			theField.value = getFieldVals(4);
		break
		case "ftp":
			theField.value = getFieldVals(5);
		break
	}
	theField.focus();
	theField.select();
}

function getFieldVals(iNum) {
	var theField = document.forms[0].f_href;
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

function checkLinkType(sHref) {
	var fObj  = document.forms[0].createLinkType ;
	if (/GetDoc/i.test(sHref) || (/\d{4,6}/.test(sHref) && !/^http/i.test(sHref))) {
		fObj.selectedIndex = 0 ;
		sHref = /\d{4,6}/.exec(sHref) ;
		document.getElementById("f_href").value = sHref ;
	} else if (/^mailto:/i.test(sHref)) {
		fObj.selectedIndex = 2 ;
		sHref = sHref.replace(/mailto:/i, "") ;
		document.getElementById("f_href").value = sHref ;
	} else if (/^ftp:/i.test(sHref)) {
		fObj.selectedIndex = 3 ;
	} else {
		fObj.selectedIndex = 1 ;
	}
}

function buildHref(sHref) {
	var fObj     = document.forms[0].createLinkType ;
	var linkType = fObj.options[fObj.selectedIndex].value ;
	// if the old link wasn't a "/1234" or "1234" link and it's an internal link
	if (!/^\/?\d{4,6}$/.test(oldHref) && linkType == "GetDoc" && !/GetDoc/gi.test(sHref)) {
		sHref = "<%= request.getContextPath() %>/servlet/GetDoc?meta_id=" + sHref ;
	} else if (!/^\/?\d{4,6}$/.test(oldHref) && linkType == "GetDoc") {
		sHref = "<%= request.getContextPath() %>/" + sHref ;
	} else if (linkType == "mailto" && !/mailto:/gi.test(sHref)) {
		sHref = "mailto:" + sHref ;
	}
	return sHref ;
}
//-->
</script>

<script type="text/javascript" src="<%= EDITOR_URL %>popups/popup.js"></script>

<script type="text/javascript">
window.resizeTo(410, 200);

I18N = window.opener.HTMLArea.I18N.dialogs;

function i18n(str) {
  return (I18N[str] || str);
};

function onTargetChanged() {
  var f = document.getElementById("f_other_target");
  if (this.value == "_other") {
    f.style.visibility = "visible";
    f.select();
    f.focus();
  } else f.style.visibility = "hidden";
};

var oldHref = "" ;

function Init() {
	__dlg_translate(I18N);
	__dlg_init();
	var param = window.dialogArguments;
	var target_select = document.getElementById("f_target");
	if (param) {
		document.getElementById("f_href").value = param["f_href"];
		oldHref = param["f_href"] ;
		document.getElementById("f_title").value = param["f_title"];
		comboSelectValue(target_select, param["f_target"]);
		if (target_select.value != param.f_target) {
			var opt = document.createElement("option");
			opt.value = param.f_target;
			opt.innerHTML = opt.value;
			target_select.appendChild(opt);
			opt.selected = true;
		}
		checkLinkType(param["f_href"]) ;
		if (oldHref != "" && /^\d{4,6}$/.test(oldHref)) {
			document.getElementById("bottomLeftTd").innerHTML = "<%= lang("The old link",user) %>:<br>" +
			    "<a href=\"http://<%= request.getHeader("Host") + request.getContextPath() + "/" %>" + oldHref + "\" target=\"_blank\">" + oldHref + "</a>" ;
		} else if (oldHref != "") {
			document.getElementById("bottomLeftTd").innerHTML = "<%= lang("The old link",user) %>:<br>" +
			    "<a href=\"" + oldHref + "\" target=\"_blank\">" + oldHref + "</a>" ;
		}
	}
	var opt = document.createElement("option");
	opt.value = "_other";
	opt.innerHTML = i18n("Other");
	target_select.appendChild(opt);
	target_select.onchange = onTargetChanged;
	document.getElementById("f_href").focus();
	document.getElementById("f_href").select();
};

function onOK() {
  var required = {
    "f_href": i18n("You must enter the URL where this link points to")
  };
  for (var i in required) {
    var el = document.getElementById(i);
    if (!el.value) {
      alert(required[i]);
      el.focus();
      return false;
    }
  }
  // pass data back to the calling window
  var fields = ["f_href", "f_title", "f_target" ];
  var param = new Object();
	var sHref = document.getElementById("f_href").value ;
	document.getElementById("f_href").value = buildHref(sHref) ;
  for (var i in fields) {
    var id = fields[i];
    var el = document.getElementById(id);
    param[id] = el.value;
  }
  if (param.f_target == "_other") param.f_target = document.getElementById("f_other_target").value;
  __dlg_close(param);
	try {
		window.close() ;
		parent.opener.focus() ;
  } catch(e){}
	return false;
};

function onCancel() {
  __dlg_close(null);
  return false;
};

</script>

<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/swe/scripts/imcms_admin.js.jsp" type="text/javascript"></script>

</head>
<body class="imcmsAdmBgCont" style="border:0; margin:0" onLoad="Init();">


<form name="createLinkForm" onSubmit="return false">
<table border="0" cellspacing="0" cellpadding="10" width="100%">
<tr>
	<td class="imcmsAdmBgHead">
	<span class="imcmsAdmHeadingTop"><%= lang("Create/edit link",user) %></span></td>
</tr>
<tr>
	<td>
	<table border="0" cellspacing="0" cellpadding="2" width="374">
	<tr>
		<td>
		<table border="0" cellpadding="0" cellspacing="0" width="100%">
		<tr>
			<td class="imcmsAdmText"><%= lang("Linktype",user) %>:</td>
			<td class="form">
			<select class="form" name="createLinkType" onChange="changeLinkType(document.forms[0].createLinkType.options[document.forms[0].createLinkType.selectedIndex].value)">
				<option value="GetDoc"><%= lang("Internal link (page-ID)",user) %></option>
				<option value="http"><%= lang("External link or a path",user) %></option>
				<option value="mailto"><%= lang("Mail address",user) %></option>
				<option value="ftp"><%= lang("FTP link",user) %></option>
			</select></td>
		</tr>
		<tr>
			<td class="imcmsAdmText" nowrap>
			<%= lang("URL/address/meta_id",user) %>: &nbsp;</td>
			<td><input type="text" name="f_href" id="f_href" size="54" maxlength="100" style="width:260px" value="<%= lang("Write number",user) %>!"></td>
		</tr>
		<tr>
			<td class="imcmsAdmText" nowrap>
			<%= lang("Title/tooltip",user) %>: &nbsp;</td>
			<td><input type="text" name="f_title" id="f_title" size="54" maxlength="100" style="width:260px" value=""></td>
		</tr>
		<tr>
			<td class="imcmsAdmText" nowrap><%= lang("Show link in",user) %>:&nbsp;</td>
			<td>
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td nowrap>
				<select name="f_target" id="f_target">
					<option value="" selected><%= lang("Same window (default)",user) %></option>
					<option value="_blank"><%= lang("New window",user) %></option>
					<option value="_top"><%= lang("Replace window (no frames)",user) %></option>
					<option value="_self"><%= lang("Same frame",user) %></option>
					<option value="_parent">Parent frameset</option>
				</select></td>
				<td class="form" align="right"><input type="text" name="f_other_target" id="f_other_target" size="12" maxlength="50" style="width:96px; visibility:hidden;" value=""></td>
			</tr>
			</table></td>
		</tr>
		<tr>
			<td colspan="2"><img src="$contextPath/imcms/swe/images/admin/1x1_20568d.gif" width="100%" height="1" vspace="8"></td>
		</tr>
		<tr>
			<td colspan="2">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td id="bottomLeftTd" class="imcmsAdmText">&nbsp;</td>
				<td align="right" nowrap>
				<button type="button" class="imcmsFormBtnSmall" style="width:60px" name="ok"<%
				%> onclick="return onOK();">OK</button><%
				%>&nbsp;<%
				%><button type="button" class="imcmsFormBtnSmall" style="width:60px" name="cancel"<%
				%> onclick="return onCancel();"><%= lang("Cancel",user) %></button></td>
			</tr>
			</table></td>
		</tr>
		<tr>
			<td><img src="$contextPath/imcms/swe/images/admin/1x1.gif" width="110" height="1"></td>
			<td><img src="$contextPath/imcms/swe/images/admin/1x1.gif" width="1" height="1"></td>
		</tr>
		</table></td>
	</tr>
	</table></td>
</tr>
</table>
</form>

</body>
</html>
</vel:velocity>
