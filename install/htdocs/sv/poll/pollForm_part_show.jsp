<? sv/poll/pollForm_part_show.jsp/1 ?>
<input type="hidden" name="result_template" value="<%=result_template%>">
<input type="hidden" name="confirmation_template" value="<%=CONFIRMATION_TEMPLATE%>">
<input type="hidden" name="meta_id" value="<%=meta_id%>">

<%
if( saveAnswers ) {
	%>
	<input type="submit" name="Save" value="<? sv/poll/pollForm_part_show.jsp/2001 ?>">&nbsp;&nbsp;<input type="reset" name="Reset" value="<? sv/poll/pollForm_part_show.jsp/2002 ?>">&nbsp;&nbsp;<input type="button" name="cancel" value="<? sv/poll/pollForm_part_show.jsp/2003 ?>" onclick="javascript:window.close()">
	<? sv/poll/pollForm_part_show.jsp/2 ?>
	<input type="button" name="cancel" value="<? sv/poll/pollForm_part_show.jsp/2004 ?>" onclick="javascript:window.close()">
	<? sv/poll/pollForm_part_show.jsp/3 ?>

<SCRIPT language=JavaScript>
<!--
var window_width = 600;
var window_height = 400;
var sUrl = "@servleturl@/GetDoc?meta_id=<%=meta_id%>&template=<%=result_template%>";
var sName = "resultWin";
if(screen.height < 700){ 
	var window_top = 0;
 	var window_left = (screen.width-window_width)/2;
}else { 
	var window_top = (screen.height-window_height)/2;
 	var window_left = (screen.width-window_width)/2;
}
function openW(){
	popWindow = window.open(sUrl,sName,'resizable=yes,menubar=0,scrollbars=yes,width=' + window_width + ',height=' + window_height +  ',top='	+ window_top + ',left=' + window_left + '');
	popWindow.focus();
}

//-->
</SCRIPT>
		 
<% } %>
