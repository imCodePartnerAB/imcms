<%@ page contentType="text/css" %><%
		response.setContentType( "text/css; charset=" + Imcms.DEFAULT_ENCODING);
%><%@ include file="_editor_methods.jsp" %>

.htmlarea {
	background: <%= htmlareaBg %>;
	/*border: 1px outset #cccccc;*/
}

.htmlarea IFRAME {
	font: 10px Verdana;
	/*border-left:  1px solid <%= imCMS_borderOffwhiteD %>;
	border-right: 1px solid <%= imCMS_borderOffwhiteL %>;*/
}

.htmlarea .toolbar {
  cursor: default;
  background: <%= htmlareaBg %>;
  padding: 1px 1px 2px 1px;
  border: 1px solid;
  border-color: <%= imCMS_borderOffwhiteL %> <%= imCMS_borderOffwhiteD %> <%= imCMS_borderOffwhiteD %> <%= imCMS_borderOffwhiteL %>;
}
.htmlarea .toolbar table { font-family: tahoma,verdana,sans-serif; font-size: 11px; }
.htmlarea .toolbar img { border: none; }
.htmlarea .toolbar .label { padding: 0px 3px; }

.htmlarea .toolbar .button {
  background: <%= ButtonFace %>;
  color: <%= ButtonText %>;
  border: 1px solid <%= ButtonFace %>;
  padding: 1px;
  margin: 0px;
  width: 18px;
  height: 18px;
}
.htmlarea .toolbar .buttonHover {
  border: 1px solid;
  border-color: <%= ButtonHighlight %> <%= ButtonShadow %> <%= ButtonShadow %> <%= ButtonHighlight %>;
}
.htmlarea .toolbar .buttonActive, .htmlarea .toolbar .buttonPressed {
  padding: 2px 0px 0px 2px;
  border: 1px solid;
  border-color: <%= ButtonShadow %> <%= ButtonHighlight %> <%= ButtonHighlight %> <%= ButtonShadow %>;
}
.htmlarea .toolbar .buttonPressed {
  background: <%= ButtonHighlight %>;
}
.htmlarea .toolbar .indicator {
  padding: 0px 3px;
  overflow: hidden;
  width: 20px;
  text-align: center;
  cursor: default;
  border: 1px solid <%= ButtonShadow %>;
}

.htmlarea .toolbar .buttonDisabled img {
  filter: alpha(opacity = 25);
  -moz-opacity: 0.25;
}

.htmlarea .toolbar .separator {
  position: relative;
  margin: 3px;
  border-left: 1px solid <%= ButtonShadow %>;
  border-right: 1px solid <%= ButtonHighlight %>;
  width: 0px;
  height: 16px;
  padding: 0px;
}

.htmlarea .toolbar .space { width: 5px; }

.htmlarea .toolbar select { font: 11px Tahoma,Verdana,sans-serif; }

.htmlarea .toolbar select,
.htmlarea .toolbar select:hover,
.htmlarea .toolbar select:active { background: FieldFace; color: <%= ButtonText %>; }

.htmlarea .statusBar {
  border: 1px solid;
  border-color: <%= imCMS_borderOffwhiteL %> <%= imCMS_borderOffwhiteD %> <%= imCMS_borderOffwhiteD %> <%= imCMS_borderOffwhiteL %>;/**/
  padding: 2px 4px;
  background-color: <%= htmlareaBg %>;
  color: <%= editorGuiText %>;
  font: 11px Tahoma,Verdana,sans-serif;
}

.htmlarea .statusBar .statusBarTree a {
  padding: 2px 5px;
  <%= linkStyle %>
}

.htmlarea .statusBar .statusBarTree a:visited { color: #00f; }
.htmlarea .statusBar .statusBarTree a:hover {
  background-color: <%= Highlight %>;
  color: <%= HighlightText %>;
  padding: 1px 4px;
  border: 1px solid <%= HighlightText %>;
}


/* Hidden DIV popup dialogs (PopupDiv) */

.dialog {
  border:0;
  margin:8px;
  color: #000000;
  background: <%= imCMS_offwhite %>;
}

.dialog, .dialog button, .dialog input, .dialog select, .dialog textarea, .dialog table {
  font: 11px Tahoma,Verdana,sans-serif;
}

.dialog table { border-collapse: collapse; }

.dialog .title {
  position: absolute;
  top:0px;
  left:0px;
  background: <%= imCMS_blue %>;  width:120%;
  padding: 8px 10px;
  font: bold 12px Tahoma,Verdana,sans-serif;
  color: #ffffff;
}

.dialog .content {
  margin-top:30px;
  padding: 5px 5px;
  font: 11px Tahoma,Verdana,sans-serif;
  color: #000000;
}

.dialog .content TD {
  font: 11px Tahoma,Verdana,sans-serif;
  color: #000000;
}

.dialog .title .button {
  float: right;
  border: 1px solid #66a;
  padding: 0px 1px 0px 2px;
  margin-right: 1px;
  color: #fff;
  text-align: center;
}

.dialog .title .button-hilite { border-color: #88f; background: #44c; }

.dialog button {
  width: 5em;
  padding: 0px;
}

.dialog .buttonColor {
  padding: 1px;
  cursor: default;
  border: 1px solid;
  border-color: <%= ButtonHighlight %> <%= ButtonShadow %> <%= ButtonShadow %> <%= ButtonHighlight %>;
}

.dialog .buttonColor-hilite {
  border-color: #000;
}

.dialog .buttonColor .chooser, .dialog .buttonColor .nocolor {
  height: 0.6em;
  border: 1px solid;
  padding: 0px 1em;
  border-color: <%= ButtonShadow %> <%= ButtonHighlight %> <%= ButtonHighlight %> <%= ButtonShadow %>;
}

.dialog .buttonColor .nocolor { padding: 0px; }
.dialog .buttonColor .nocolor-hilite { background-color: #fff; color: #f00; }

.dialog .label { text-align: right; width: 6em; }
.dialog .value input { width: 100%; }
.dialog .buttons { text-align: right; padding: 2px 4px 0px 4px; }

.dialog legend { font-weight: bold; }
.dialog fieldset table { margin: 2px 0px; }

.popupdiv {
  border: 2px solid;
  border-color: <%= ButtonHighlight %> <%= ButtonShadow %> <%= ButtonShadow %> <%= ButtonHighlight %>;
}

.popupwin {
  padding: 0px;
  margin: 0px;
}

.popupwin .title {
  position: absolute;
  top:0px;
  left:0px;
  background: <%= imCMS_blue %>;  width:120%;
  padding: 8px 10px;
  font: bold 12px Tahoma,Verdana,sans-serif;
  color: #ffffff;
}

.popupwin .content {
  position: absolute;
  top:0px;
  left:0px;
}

form { margin: 0px; border: none; }
