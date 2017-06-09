<%@ tag import="java.text.DateFormat, java.text.SimpleDateFormat, org.apache.log4j.Logger"%><%@attribute name="value" type="java.util.Date" %>
<%@attribute name="dateid" required="true" %>
<%@attribute name="timeid"%>
<%
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat timeFormat = new SimpleDateFormat("HH:mm");
    String formattedDate = null == value ? "" : dateFormat.format(value);
    String formattedTime = null == value ? "" : timeFormat.format(value);
    try {
%>
<input id="${dateid}" name="${dateid}" size="11" maxlength="10" style="width: 7em;" value="<%= formattedDate %>" type="text"><%
if (null != timeid) { 
    %>&nbsp;<input id="${timeid}" name="${timeid}" size="5" maxlength="5" style="width: 4em;" value="<%= formattedTime %>" type="text"><%
}
%>&nbsp;<img src="<%= request.getContextPath() %>/imcms/jscalendar/images/img.gif" id="${dateid}_btn" style="cursor: pointer;" onmouseover="this.style.background='#000099';" onmouseout="this.style.background=''"><script type="text/javascript">
Calendar.setup({
    inputField   : "${dateid}",
    <% if (null != timeid) { %>
    inputFieldTime : "${timeid}",
    showsTime   : true,
    <% } %>
    button     : "${dateid}_btn"
}) ;
</script>
<%
    } catch(Exception e) {
        Logger.getLogger("jsp").error("Exception in datetime.tag.",e);
        throw e;
    }
%> 
