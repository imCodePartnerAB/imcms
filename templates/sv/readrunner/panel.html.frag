<!-- * Click sensor div * -->
<DIV ID="RRbodyDiv" style="position:absolute; left:0; top:0; width:100%; height:100%; z-index:2" onMouseDown="redirectMouseClick(this);"></DIV>


<!-- * Wait div * -->
<DIV ID="RRwaitDiv" style="position:absolute; left:0; top:190; width:770; height:200; z-index:1000; filter:alpha(Opacity=60); display:none">
<table border="0" cellpadding="0" cellspacing="0" width="770" height="200">
<tr>
	<td align="center"><img src="@readrunnerimagesurl@/1x1.gif" width="1" height="50"><br>
	<table border="0" cellpadding="0" cellspacing="0" width="400" style="border: 3px groove #c00000">
	<tr>
		<td align="center" height="200" style="font: bold 56px/66px Verdana; background-color:#f0f0f0; color:#c00000">&nbsp;&nbsp;Vänta...</td>
	</tr>
	</table></td>
</tr>
</table></DIV>

<form name="form1">

<DIV ID="RRpanelDiv" style="position:absolute; left:-1000; top:-1000; width:249; height:36; clip:rect(0px 249px 36px 0px); padding:0; margin:0; z-index:10; display:block"><input type="image" name="stop" id="stop2" title="Stoppa" value="Stop" onClick="return false" disabled src="@readrunnerimagesurl@/btn_panel_stopp.gif" width="57" height="13" style="visibility:hidden"><input type="image" name="start" id="start" title="Starta" value="Go!" onClick="return false" disabled src="@readrunnerimagesurl@/btn_panel_start.gif" width="57" height="13" style="visibility:hidden"></DIV>

<DIV ID="RRpanelStandingDiv" style="position:absolute; left:-1000; top:-1000; width:62; height:116; clip:rect(0px 62px 116px 0px); padding:0; margin:0; z-index:10; display:none"></DIV>

<input type="hidden" name="MetaId" value="">
<input type="hidden" name="blnSaveSpeed" value="0">
<input type="hidden" name="blnSaveSettings" value="0">
<input type="hidden" name="colorWormField" value="">
<input type="hidden" name="colorWormMaskField" value="">
<input type="hidden" name="blnHidePanel" value="0">
<input type="hidden" name="blnPattern" value="0">
<input type="hidden" name="blnOpacity" value="1">
<input type="hidden" name="RRopacityLevel" value="0.750">
<input type="hidden" name="RRcolorBg" value="255,255,255">
<input type="hidden" name="RRcolorText" value="0,0,0">
<input type="hidden" name="RRcolorWorm" value="153,204,153">
<input type="checkbox" name="cbp1" value="1" style="display:none" checked>
<input type="hidden" name="p1" value="1.0">
<input type="checkbox" name="cbp2" value="1" style="display:none" checked>
<input type="hidden" name="p2" value="0.5">
<input type="checkbox" name="cbp3" value="1" style="display:none" checked>
<input type="hidden" name="p3" value="0.5">
<input type="checkbox" name="cbp4" value="1" style="display:none">
<input type="hidden" name="p4" value="0">
<input type="hidden" name="RRspeed0" value="10">
<input type="hidden" name="RRspeed" value="10">
</form>
<map name="RRposArrows"><!-- from top-left clockwise -->
	<area href="javascript://movePanel" alt="" shape="poly" coords="0,0,12,0,14,14,0,12" 
		onClick="sPos = '1'; RRpanelInit(); return false" onMouseOver="RRpanelPrintHTML('0')" onMouseOut="RRpanelPrintHTML('')">
	<area href="javascript://movePanel" alt="" shape="poly" coords="12,0,23,0,19,14,14,14" 
		onClick="sPos = '2'; RRpanelInit(); return false" onMouseOver="RRpanelPrintHTML('2')" onMouseOut="RRpanelPrintHTML('')">
	<area href="javascript://movePanel" alt="" shape="poly" coords="23,0,34,0,34,9,22,16,19,14" 
		onClick="sPos = '3'; RRpanelInit(); return false" onMouseOver="RRpanelPrintHTML('4')" onMouseOut="RRpanelPrintHTML('')">
	<area href="javascript://movePanel" alt="" shape="poly" coords="22,16,34,9,34,25,21,20" 
		onClick="sPos = '4'; RRpanelInit(); return false" onMouseOver="RRpanelPrintHTML('6')" onMouseOut="RRpanelPrintHTML('')">
	<area href="javascript://movePanel" alt="" shape="poly" coords="18,22,20,20,34,25,34,34,22,34" 
		onClick="sPos = '5'; RRpanelInit(); return false" onMouseOver="RRpanelPrintHTML('8')" onMouseOut="RRpanelPrintHTML('')">
	<area href="javascript://movePanel" alt="" shape="poly" coords="14,21,18,21,22,34,10,34" 
		onClick="sPos = '6'; RRpanelInit(); return false" onMouseOver="RRpanelPrintHTML('10')" onMouseOut="RRpanelPrintHTML('')">
	<area href="javascript://movePanel" alt="" shape="poly" coords="11,19,13,20,10,34,0,34,0,24" 
		onClick="sPos = '7'; RRpanelInit(); return false" onMouseOver="RRpanelPrintHTML('12')" onMouseOut="RRpanelPrintHTML('')">
	<area href="javascript://movePanel" alt="" shape="poly" coords="12,14,12,18,0,25,0,12" 
		onClick="sPos = '8'; RRpanelInit(); return false" onMouseOver="RRpanelPrintHTML('14')" onMouseOut="RRpanelPrintHTML('')">
	<area href="#" alt="" shape="default">
</map>


<!-- * The Content * -->
<DIV ID="RRcontentDiv" style="position:relative" onMouseDown="redirectMouseClick(this);">
