<%@ page
	
	import="java.util.Map,
	        java.util.HashMap"
	
	contentType="text/html; charset=windows-1252"
	
%><%@ include file="../_editor_settings.jsp" %><%!

Map langMap = new HashMap() ;

private String lang( String string ) {
	try {
		return (isLangSwe && langMap.containsKey(string)) ? (String) langMap.get(string) : string ;
	} catch (Exception ex) {
		return string ;
	}
}

%><%

langMap.put("Insert Table"                            , "Infoga tabell") ;
langMap.put("Rows"                                    , "Rader") ;
langMap.put("Cols"                                    , "Kolumner") ;
langMap.put("Width"                                   , "Bredd") ;
langMap.put("Percent"                                 , "Procent") ;
langMap.put("Pixels"                                  , "Pixlar") ;
langMap.put("Em"                                      , "Em") ;
langMap.put("Layout"                                  , "Utseende") ;
langMap.put("Alignment"                               , "Justering") ;
langMap.put("Positioning of this table"               , "Positionering av tabellen i förhållande till annat innehåll") ;
langMap.put("Not set"                                 , "- Inget") ;
langMap.put("Left"                                    , "Vänster") ;
langMap.put("Right"                                   , "Höger") ;
langMap.put("Texttop"                                 , "Texttopp") ;
langMap.put("Absmiddle"                               , "Abs.Mitten") ;
langMap.put("Baseline"                                , "Baslinje") ;
langMap.put("Absbottom"                               , "Abs.Botten") ;
langMap.put("Bottom"                                  , "Botten") ;
langMap.put("Middle"                                  , "Mitten") ;
langMap.put("Top"                                     , "Topp") ;
langMap.put("Border thickness"                        , "Ytterkantbredd") ;
langMap.put("Leave empty for no border"               , "Lämna tom för ingen kant") ;
langMap.put("Spacing"                                 , "Marginaler") ;
langMap.put("Cell spacing"                            , "Innerkantbredd") ;
langMap.put("Space between adjacent cells"            , "Tjockleken på kantlinjen emellan cellerna") ;
langMap.put("Cell padding"                            , "Cellmarginal") ;
langMap.put("Space between content and border in cell", "Marginal mellan innehållet och kanten på cellen") ;
langMap.put("Cancel"                                  , "Avbryt") ;
langMap.put("You must enter a number of rows"         , "Du måste ange antal rader") ;
langMap.put("You must enter a number of columns"      , "Du måste ange antal kolumner") ;

%><html>

<head>
  <title><%= lang("Insert Table") %></title>

<script type="text/javascript" src="popup.js"></script>

<script type="text/javascript">


function Init() {
  __dlg_init();
	window.resizeTo(400, 260) ;
  document.getElementById("f_rows").focus();
};

function onOK() {
  var required = {
    "f_rows": "You must enter a number of rows",
    "f_cols": "You must enter a number of columns"
  };
  for (var i in required) {
    var el = document.getElementById(i);
    if (!el.value) {
      alert(required[i]);
      el.focus();
      return false;
    }
  }
  var fields = ["f_rows", "f_cols", "f_width", "f_unit",
                "f_align", "f_border", "f_spacing", "f_padding"];
  var param = new Object();
  for (var i in fields) {
    var id = fields[i];
    var el = document.getElementById(id);
    param[id] = el.value;
  }
  __dlg_close(param);
  return false;
};

function onCancel() {
  __dlg_close(null);
  return false;
};

</script>

<link rel="stylesheet" type="text/css" href="/imcms/css/imcms_admin.css.jsp">
<script src="/imcms/swe/scripts/imcms_admin.js" type="text/javascript"></script>

</head>
<body class="imcmsAdmBgCont" style="border:0; margin:0" onload="Init()">


<form>
<table border="0" cellspacing="0" cellpadding="10" width="100%">
<tr>
	<td class="imcmsAdmBgHead">
	<span class="imcmsAdmHeadingTop"><%= lang("Insert Table") %></span></td>
</tr>
<tr>
	<td>
	<table border="0" cellspacing="0" cellpadding="4">
	<tr>
		<td align="right" class="imcmsAdmText"><%= lang("Rows") %>:</td>
		<td colspan="4"><input type="text" name="rows" id="f_rows" size="5" title="Number of rows" value="2" /></td>
	</tr>
	<tr>
		<td align="right" class="imcmsAdmText"><%= lang("Cols") %>:</td>
		<td><input type="text" name="cols" id="f_cols" size="5" title="Number of columns" value="4" /></td>
		<td align="right" class="imcmsAdmText"><%= lang("Width") %>:</td>
		<td><input type="text" name="width" id="f_width" size="5" title="Width of the table" value="100" /></td>
		<td>
		<select size="1" name="unit" id="f_unit" title="Width unit">
			<option value="%" selected="1"  ><%= lang("Percent") %></option>
			<option value="px"              ><%= lang("Pixels") %></option>
			<option value="em"              ><%= lang("Em") %></option>
		</select></td>
	</tr>
	</table>
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr valign="top">
		<td>
		<fieldset style="border: 1px solid #20568D; padding: 0px 5px;">
			<legend class="imcmsAdmText" style="padding: 0px 5px"><%= lang("Layout") %></legend>
			<table border="0" cellspacing="0" cellpadding="4">
			<tr>
				<td align="right" class="imcmsAdmText"><%= lang("Alignment") %>:</td>
				<td>
				<select size="1" name="align" id="f_align"
				  title="<%= lang("Positioning of this table") %>">
				  <option value="" selected="1"                ><%= lang("Not set") %></option>
				  <option value="left"                         ><%= lang("Left") %></option>
				  <option value="right"                        ><%= lang("Right") %></option>
				  <option value="texttop"                      ><%= lang("Texttop") %></option>
				  <option value="absmiddle"                    ><%= lang("Absmiddle") %></option>
				  <option value="baseline"                     ><%= lang("Baseline") %></option>
				  <option value="absbottom"                    ><%= lang("Absbottom") %></option>
				  <option value="bottom"                       ><%= lang("Bottom") %></option>
				  <option value="middle"                       ><%= lang("Middle") %></option>
				  <option value="top"                          ><%= lang("Top") %></option>
				</select></td>
			</tr>
			<tr>
				<td align="right" class="imcmsAdmText"><%= lang("Border thickness") %>:</td>
				<td><input type="text" name="border" id="f_border" size="5" value="1" title="<%= lang("Leave empty for no border") %>" /></td>
			</tr>
			</table>
		</fieldset></td>
		
		<td width="10">&nbsp;</td>
		
		<td>
		<fieldset style="border: 1px solid #20568D; padding: 0px 5px;">
			<legend class="imcmsAdmText" style="padding: 0px 5px"><%= lang("Spacing") %></legend>
			<table border="0" cellspacing="0" cellpadding="4">
			<tr>
				<td align="right" class="imcmsAdmText"><%= lang("Cell spacing") %>:</td>
				<td><input type="text" name="spacing" id="f_spacing" size="5" value="1" title="<%= lang("Space between adjacent cells") %>" /></td>
			</tr>
			<tr>
				<td align="right" class="imcmsAdmText"><%= lang("Cell padding") %>:</td>
				<td><input type="text" name="padding" id="f_padding" size="5" value="1" title="<%= lang("Space between content and border in cell") %>" /></td>
			</tr>
			</table>
		</fieldset></td>
	</tr>
	<tr>
		<td colspan="3"><img src="/imcms/swe/images/admin/1x1_20568d.gif" width="100%" height="1" vspace="8"></td>
	</tr>
	<tr>
		<td colspan="3" align="right">
		<button type="button" class="imcmsFormBtnSmall" style="width:60px" name="ok" onclick="return onOK();">OK</button> &nbsp;
		<button type="button" class="imcmsFormBtnSmall" style="width:60px" name="cancel" onclick="return onCancel();"><%= lang("Cancel") %></button></td>
	</tr>
	</table></td>
</tr>
</table>
</form>

</body>
</html>
