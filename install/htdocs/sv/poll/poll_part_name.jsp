<%@ page language="java"
import="java.util.*, java.text.*, imcode.server.*, imcode.util.*, imcode.util.poll.*,
        imcode.server.document.TextDocumentDomainObject.Text,
        imcode.server.document.TextDocumentDomainObject"
%><%

// Get a reference to IMCServiceInterface //
    IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;

//Get a PollHandlingSystem
PollHandlingSystem poll = imcref.getPollHandlingSystem();

// Get the parameters from request //
String meta_id = request.getHeader("X-Meta-Id");

//Get PollParameters from db 
String[] poll_param;
TextDocumentDomainObject.Text poll_name = new TextDocumentDomainObject.Text("",0);
if ( meta_id !=null ) {
	poll_param = poll.getPollParameters(meta_id); 
	
	if ( poll_param != null && poll_param.length !=0 && poll_param[1] != null ){
		int text_no = Integer.parseInt( poll_param[1] );
		poll_name = imcref.getText(Integer.parseInt(meta_id), text_no);
		
	}
}
//System.out.println( "");

out.write( poll_name.getText() );
%> 