<%@ page
	
	contentType="text/html; charset=windows-1252"
	
%><%@ include file="_editor_settings.jsp" %><%



%>
<!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML 3.2//EN">
<html>
<head>

<title>HTMLArea-3.0 Reference</title>

<style type="text/css">
  @import url(htmlarea.css);
  body, TD { font: 12px verdana,sans-serif; background: #fff; color: #000; }
  h1, h2 { font-family:tahoma,sans-serif; }
  h1 { border-bottom: 2px solid #000; }
  h2 { border-bottom: 1px solid #aaa; }
  h3, h4 { margin-bottom: 0px; font-family: Georgia,serif; font-style: italic; }
  h4 { font-size: 90%; margin-left: 1em; }
  acronym { border-bottom: 1px dotted #063; color: #063; }
  p { margin-left: 2em; margin-top: 0.3em; }
  li p { margin-left: 0px; }
  .abstract { padding: 5px; margin: 0px 10em; font-size: 90%; border: 1px dashed #aaa; background: #eee;}
  li { margin-left: 2em; }
  em { color: #042; }
  a { color: #00f; }
  a:hover { color: #f00; }
  a:active { color: #f80; }
  span.browser { font-weight: bold; color: #864; }
  .fixme { font-size: 20px; font-weight: bold; color: red; background: #fab;
padding: 5px; text-align: center; }
  .code {
   background: #e4efff; padding: 5px; border: 1px dashed #abc; margin-left: 2em; margin-right: 2em;
   font-family: fixed,"lucidux mono","andale mono","courier new",monospace;
  }
  .note, .warning { font-weight: bold; color: #0a0; font-variant: small-caps; }
  .warning { color: #a00; }

.string {
  color: #06c;
} /* font-lock-string-face */
.comment {
  color: #840;
} /* font-lock-comment-face */
.variable-name {
  color: #000;
} /* font-lock-variable-name-face */
.type {
  color: #008;
  font-weight: bold;
} /* font-lock-type-face */
.reference {
  color: #048;
} /* font-lock-reference-face */
.preprocessor {
  color: #808;
} /* font-lock-preprocessor-face */
.keyword {
  color: #00f;
  font-weight: bold;
} /* font-lock-keyword-face */
.function-name {
  color: #044;
} /* font-lock-function-name-face */
.html-tag {
  font-weight: bold;
} /* html-tag-face */
.html-helper-italic {
  font-style: italic;
} /* html-helper-italic-face */
.html-helper-bold {
  font-weight: bold;
} /* html-helper-bold-face */

</style>


</head>
<body>

<% if (isLangSwe) { %>

<h1>HTMLArea-3.0 Dokumentation</h1>

<h2>Tangentbords-kortkommandon</h2>

<p>Editorn möjliggör följande tangentbordskombinationer:</p>



<blockquote>
<table border="0" cellspacing="0" cellpadding="2">
<tr>
	<td>CTRL-A &nbsp;</td>
	<td>Markera allt</td>
</tr>
<tr>
	<td>CTRL-B &nbsp;</td>
	<td>Fetstil</td>
</tr>
<tr>
	<td>CTRL-I &nbsp;</td>
	<td>Kursiv stil</td>
</tr>
<tr>
	<td>CTRL-U &nbsp;</td>
	<td>Understruket</td>
</tr>
<tr>
	<td>CTRL-S &nbsp;</td>
	<td>Genomstruket</td>
</tr>
<tr>
	<td>CTRL-L &nbsp;</td>
	<td>Vänsterjusterat</td>
</tr>
<tr>
	<td>CTRL-E &nbsp;</td>
	<td>Centrerat</td>
</tr>
<tr>
	<td>CTRL-R &nbsp;</td>
	<td>Högerjusterat</td>
</tr>
<tr>
	<td>CTRL-J &nbsp;</td>
	<td>Marginaljusterat</td>
</tr><%
	if (isMac) { %>
<tr>
	<td><img src="images/icon_command.gif" width="13" height="13" alt="" align="absmiddle">-X &nbsp;</td>
	<td>Klipp ut</td>
</tr>
<tr>
	<td><img src="images/icon_command.gif" width="13" height="13" alt="" align="absmiddle">-C &nbsp;</td>
	<td>Kopiera</td>
</tr>
<tr>
	<td><img src="images/icon_command.gif" width="13" height="13" alt="" align="absmiddle">-V &nbsp;</td>
	<td>Klistra in (rensar EJ Word-kod)</td>
</tr><%
	} else { %>
<tr>
	<td>CTRL-X &nbsp;</td>
	<td>Klipp ut</td>
</tr>
<tr>
	<td>CTRL-C &nbsp;</td>
	<td>Kopiera</td>
</tr>
<tr>
	<td>CTRL-V &nbsp;</td>
	<td>Klistra in (rensar<%= isIE ? "" : " EJ" %> Word-kod)</td>
</tr><%
	} %>
<tr>
	<td>CTRL-1 .. CTRL-6 &nbsp;</td>
	<td>Rubriker (&lt;h1&gt; .. &lt;h6&gt;)</td>
</tr>
<tr>
	<td>CTRL-0 (noll) &nbsp;</td>
	<td>Rensa innehåll inklistrat från Word</td>
</tr>
</table>
</blockquote>




<% } else { %>

<h1>HTMLArea-3.0 Documentation</h1>

<h2>Keyboard shortcuts</h2>

<p>The editor provides the following key combinations:</p>

<blockquote>
<table border="0" cellspacing="0" cellpadding="2">
<tr>
	<td>CTRL-A &nbsp;</td>
	<td>select all</td>
</tr>
<tr>
	<td>CTRL-B &nbsp;</td>
	<td>bold</td>
</tr>
<tr>
	<td>CTRL-I &nbsp;</td>
	<td>italic</td>
</tr>
<tr>
	<td>CTRL-U &nbsp;</td>
	<td>underline</td>
</tr>
<tr>
	<td>CTRL-S &nbsp;</td>
	<td>strikethrough</td>
</tr>
<tr>
	<td>CTRL-L &nbsp;</td>
	<td>justify left</td>
</tr>
<tr>
	<td>CTRL-E &nbsp;</td>
	<td>justify center</td>
</tr>
<tr>
	<td>CTRL-R &nbsp;</td>
	<td>justify right</td>
</tr>
<tr>
	<td>CTRL-J &nbsp;</td>
	<td>justify full</td>
</tr><%
	if (isMac) { %>
<tr>
	<td><img src="images/icon_command.gif" width="13" height="13" alt="" align="absmiddle">-X &nbsp;</td>
	<td>Cut</td>
</tr>
<tr>
	<td><img src="images/icon_command.gif" width="13" height="13" alt="" align="absmiddle">-C &nbsp;</td>
	<td>Copy</td>
</tr>
<tr>
	<td><img src="images/icon_command.gif" width="13" height="13" alt="" align="absmiddle">-V &nbsp;</td>
	<td>Paste (do NOT clean Word code)</td>
</tr><%
	} else { %>
<tr>
	<td>CTRL-X &nbsp;</td>
	<td>Cut</td>
</tr>
<tr>
	<td>CTRL-C &nbsp;</td>
	<td>Copy</td>
</tr>
<tr>
	<td>CTRL-V &nbsp;</td>
	<td>Paste (<%= isIE ? "cleans" : "do NOT clean" %> Word code)</td>
</tr><%
	} %>
<tr>
	<td>CTRL-1 .. CTRL-6 &nbsp;</td>
	<td>headings (&lt;h1&gt; .. &lt;h6&gt;)</td>
</tr>
<tr>
	<td>CTRL-0 (zero) &nbsp;</td>
	<td>clean content pasted from Word</td>
</tr>
</table>
</blockquote>

<% } %>



<hr />


<address style="font-size: 9px;">
&copy; <a href="http://interactivetools.com" title="Visit our website">InteractiveTools.com</a> 2002-2004.<br />
&copy; <a href="http://dynarch.com">dynarch.com</a> 2003-2004<br />
HTMLArea v3.0 developed by <a href="http://dynarch.com/mishoo/">Mihai Bazon</a>.<br />
Documentation written by Mihai Bazon.<br />
Modified by imCode Partner AB
</address>

Last modified: Thu Dec 9 10:45:23 CET 2004


</body>
</html>

