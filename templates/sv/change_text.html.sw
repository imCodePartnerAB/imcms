<HTML>
<HEAD>
<TITLE>Shockwave</TITLE>
</HEAD>
<BODY bgcolor="#C0C0C0">
<OBJECT classid="clsid:166B1BCA-3F9C-11CF-8075-444553540000"
codebase="http://download.macromedia.com/pub/shockwave/cabs/director/sw.cab#version=7,0,0,0"
 ID=ht WIDTH=640 HEIGHT=480>
<PARAM NAME=src VALUE="/ht.dcr">
<PARAM NAME=sw1 VALUE="#meta_id#" >
<PARAM NAME=sw3 VALUE="#type#" >
<PARAM NAME=sw2 VALUE="#txt_no#" > 

<SCRIPT LANGUAGE=JavaScript>
<!--
var ShockMode = 0;
if (navigator.mimeTypes && navigator.mimeTypes["application/x-director"] && navigator.mimeTypes["application/x-director"].enabledPlugin) {
	if (navigator.plugins && navigator.plugins["Shockwave for Director"] && (versionIndex = navigator.plugins["Shockwave for Director"].description.indexOf(".")) != - 1) {
		var versionString = navigator.plugins["Shockwave for Director"].description.substring(versionIndex-1, versionIndex);
		versionIndex = parseInt( versionString );
		if ( versionIndex >= 7 )
			ShockMode = 1;
	}
}
if ( ShockMode ) {
	document.write('<EMBED SRC="/ht.dcr"');
	document.write(' swLiveConnect=FALSE WIDTH=640 HEIGHT=480 sw1="#meta_id#" sw3="#type#" sw2="#txt_no#"');
	document.write(' TYPE="application/x-director" PLUGINSPAGE="http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveDirector">');
	document.write('</EMBED>');
} else if (!(navigator.appName && navigator.appName.indexOf("Netscape")>=0 && navigator.appVersion.indexOf("2.")>=0)){


document.write("<FORM METHOD=POST ACTION='#servlet_url#SaveText' name=''>");
document.write("<TABLE width='430' align='center' cellpadding='2' cellspacing='0' border='1'>");
document.write("<CENTER>");
document.write("<TR bgcolor='#333366'>");
document.write("<TD align='left'><B><font face='Verdana, Arial, Helvetica, sans-serif' size='3' color='#FFFFFF'>&nbsp;");
document.write("<font size='2'>Ändra Text</font>&nbsp;</font>");
document.write("<font face='Verdana, Arial, Helvetica, sans-serif' size='2' color='#FFFFFF'>&nbsp;&nbsp;&nbsp;");
document.write("</font></B><font face='Verdana, Arial, Helvetica, sans-serif' size='2' color='#FFFFFF'>Txt#txt_no#</font>");
document.write("</TD>");
document.write("</TR>");
document.write("<TR bgcolor='#333366'>");
document.write("<TD><font face='Verdana, Arial, Helvetica, sans-serif' size='2' color='#FFFFFF'>&nbsp;MetaId&nbsp;&nbsp;&nbsp;&nbsp;#meta_id#</font></TD>");
document.write("</TR>");
document.write("<TR>");
document.write("<TD bgcolor='#BABABA'>");
document.write("<textarea name=text cols=70 rows=15 wrap='VIRTUAL'>#txt#</textarea>");
document.write("</TD>");
document.write("</TR>");
document.write("<TR>");
document.write("<TD valign='middle' align='left' bgcolor='#BABABA'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>&nbsp;Format:&nbsp;&nbsp;&nbsp;&nbsp;</font>");
document.write("<INPUT TYPE=RADIO NAME=type VALUE='0' #!html#><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>Vanlig text</font>&nbsp;&nbsp;");
document.write("<INPUT TYPE=RADIO NAME=type VALUE='1' #html#><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>HTML</font></TD>");
document.write("</TR>");
document.write("<INPUT TYPE=HIDDEN NAME=txt_no VALUE=#txt_no#>");
document.write("<TR>");
document.write("<TD valign='middle' align='center' bgcolor='#BABABA'>");
document.write("<INPUT TYPE=SUBMIT VALUE='OK' NAME='ok'>");
document.write("<INPUT TYPE=RESET VALUE='Rensa'>");
document.write("<INPUT TYPE=SUBMIT VALUE='Avbryt' NAME='cancel'>");
document.write("</TD>");
document.write("</TR>");
document.write("</CENTER>");
document.write("<INPUT TYPE=HIDDEN NAME=meta_id VALUE=#meta_id#>");
document.write("</TABLE>");
document.write("</FORM>");

}
//-->
</SCRIPT>
<NOEMBED>
<FORM METHOD=POST ACTION="#servlet_url#SaveText" name="">
    <TABLE width="430" align="center" cellpadding="2" cellspacing="0" border="1">
      <CENTER>
        <TR bgcolor="#333366" > 
          <TD align="left"><B><font face="Verdana, Arial, Helvetica, sans-serif" size="3" color="#FFFFFF">&nbsp;<font size="2">Ändra 
            Text</font>&nbsp;</font><font face="Verdana, Arial, Helvetica, sans-serif" size="2" color="#FFFFFF">&nbsp;&nbsp;&nbsp;</font></B><font face="Verdana, Arial, Helvetica, sans-serif" size="2" color="#FFFFFF">Txt#txt_no#</font></TD>
</TR>
        <TR bgcolor="#333366"> 
          <TD><font face="Verdana, Arial, Helvetica, sans-serif" size="2" color="#FFFFFF">&nbsp;MetaId&nbsp;&nbsp;&nbsp;&nbsp;#meta_id#</font></TD>
        </TR>
        <TR > 
          <TD bgcolor="#BABABA"> 
            <textarea name=text cols=70 rows=15 wrap="VIRTUAL">#txt#</textarea>
          </TD>
        </TR>
        <TR> 
          <TD valign="middle" align="left" bgcolor="#BABABA"><font face="Verdana, Arial, Helvetica, sans-serif" size="2">&nbsp;Format:&nbsp;&nbsp;&nbsp;&nbsp;</font> 
            <INPUT TYPE=RADIO NAME=type VALUE="0" #!html#><font face="Verdana, Arial, Helvetica, sans-serif" size="2">Vanlig text</font>&nbsp;&nbsp; 
            <INPUT TYPE=RADIO NAME=type VALUE="1" #html#><font face="Verdana, Arial, Helvetica, sans-serif" size="2">HTML</font></TD>
        </TR>
        <INPUT TYPE=HIDDEN NAME=txt_no VALUE=#txt_no#>
        <TR> 
          <TD valign="middle" align="center" bgcolor="#BABABA"> 
            <INPUT TYPE=SUBMIT VALUE="OK" NAME="ok">
            <INPUT TYPE=RESET VALUE="Rensa">
            <INPUT TYPE=SUBMIT VALUE="Avbryt" NAME="cancel">
          </TD>
        </TR>
      </CENTER>
      <INPUT TYPE=HIDDEN NAME=meta_id VALUE=#meta_id#>
    </TABLE>
</FORM>

</NOEMBED>
<NOSCRIPT>
<FORM METHOD=POST ACTION="#servlet_url#SaveText" name="">
    <TABLE width="430" align="center" cellpadding="2" cellspacing="0" border="1">
      <CENTER>
        <TR bgcolor="#333366" > 
          <TD align="left"><B><font face="Verdana, Arial, Helvetica, sans-serif" size="3" color="#FFFFFF">&nbsp;<font size="2">Ändra 
            Text</font>&nbsp;</font><font face="Verdana, Arial, Helvetica, sans-serif" size="2" color="#FFFFFF">&nbsp;&nbsp;&nbsp;</font></B><font face="Verdana, Arial, Helvetica, sans-serif" size="2" color="#FFFFFF">Txt#txt_no#</font></TD>
</TR>
        <TR bgcolor="#333366"> 
          <TD><font face="Verdana, Arial, Helvetica, sans-serif" size="2" color="#FFFFFF">&nbsp;MetaId&nbsp;&nbsp;&nbsp;&nbsp;#meta_id#</font></TD>
        </TR>
        <TR > 
          <TD bgcolor="#BABABA"> 
            <textarea name=text cols=70 rows=15 wrap="VIRTUAL">#txt#</textarea>
          </TD>
        </TR>
        <TR> 
          <TD valign="middle" align="left" bgcolor="#BABABA"><font face="Verdana, Arial, Helvetica, sans-serif" size="2">&nbsp;Format:&nbsp;&nbsp;&nbsp;&nbsp;</font> 
            <INPUT TYPE=RADIO NAME=type VALUE="0" #!html#><font face="Verdana, Arial, Helvetica, sans-serif" size="2">Vanlig text</font>&nbsp;&nbsp; 
            <INPUT TYPE=RADIO NAME=type VALUE="1" #html#><font face="Verdana, Arial, Helvetica, sans-serif" size="2">HTML</font></TD>
        </TR>
        <INPUT TYPE=HIDDEN NAME=txt_no VALUE=#txt_no#>
        <TR> 
          <TD valign="middle" align="center" bgcolor="#BABABA"> 
            <INPUT TYPE=SUBMIT VALUE="OK" NAME="ok">
            <INPUT TYPE=RESET VALUE="Rensa">
            <INPUT TYPE=SUBMIT VALUE="Avbryt" NAME="cancel">
          </TD>
        </TR>
      </CENTER>
      <INPUT TYPE=HIDDEN NAME=meta_id VALUE=#meta_id#>
    </TABLE>
</FORM>

</NOSCRIPT>
</OBJECT>
</BODY>
</HTML>
