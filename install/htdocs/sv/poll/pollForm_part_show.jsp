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
<? sv/poll/pollForm_part_show.jsp/4 ?>
</SCRIPT>
		 
<% } %>
