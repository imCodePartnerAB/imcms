<%@ page language="java"
import="java.util.*, java.text.*, imcode.server.*, imcode.util.*, imcode.util.poll.*"
%>
<%

final String RESULT_DEFAULT_TEMPLATE = "poll_result_default_template"; 
final String CONFIRMATION_TEMPLATE = "poll_confirmation_template";

// Get a reference to IMCServiceInterface //
    IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;

//Get a PollHandlingSystem
PollHandlingSystem poll = imcref.getPollHandlingSystem();

// Get the parameters from request //
String meta_id = request.getHeader("X-Meta-Id");

//Get PollParameters from db 
String[] poll_param;
int hide_result = 0; 
String result_template = RESULT_DEFAULT_TEMPLATE;;
int set_cookie = 0;
if ( meta_id !=null){
	poll_param = poll.getPollParameters(meta_id); 
	
	if ( poll_param != null && poll_param.length !=0 ){
		hide_result = Integer.parseInt( poll_param[6] );
		if ( poll_param[11] != null && Integer.parseInt(poll_param[11]) > 0 ){
			result_template = imcref.getText( Integer.parseInt(meta_id), Integer.parseInt(poll_param[11])).getText().trim();
		}
		set_cookie = Integer.parseInt(poll_param[5]);
	}
}


// Lets check if we have a cookie "imcms.poll" on the client.
// If we have a cookie lets hide button "Send Answer", and 
// instead show an explanation text. 
boolean saveAnswers = true;

if ( set_cookie == 1 && meta_id != null){

	String cookieName = "imcms.poll" + meta_id;
	Cookie[] cookies = request.getCookies() ;
	// Lets see if we got a pollCookie from client.
  	for (int i = 0; cookies != null && i < cookies.length; ++i) {
		if ( cookieName.equals(cookies[i].getName()) && ("true").equals(cookies[i].getValue()) ){
			saveAnswers = false;
		}
	}
}

%>
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
