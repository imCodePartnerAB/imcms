<%@ page language="java"
import="java.util.*, java.text.*, imcode.server.*, imcode.util.*, imcode.util.poll.*"
%><%

// Get a reference to ImcmsServices //
    ImcmsServices imcref = Imcms.getServices() ;

//Get a PollHandlingSystem
PollHandlingSystem poll = imcref.getPollHandlingSystem();

// Get the parameters from request //
String meta_id = request.getHeader("X-Meta-Id");

//Get PollParameters from db
String[] poll_param;
if ( meta_id !=null ){
	poll_param = poll.getPollParameters(meta_id);

	if ( poll_param != null && poll_param.length !=0 && poll_param[7] != null){
		out.write( imcref.getText( Integer.parseInt(meta_id), Integer.parseInt(poll_param[7]) ).getText() );
	}
}
//System.out.println( "");
%>

