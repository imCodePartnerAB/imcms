package com.imcode.imcms.servlet.chat;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.diverse.*;
import imcode.external.chat.*;
import imcode.external.chat.ChatSessionsSingleton;


//meningen är att denna ska ladda framesetet och kolla
//all nödvändig data innan den gör detta

public class ChatViewer extends ChatBase {

    private final static String HTML_TEMPLATE = "Chat_Frameset.htm" ;

	public void service(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException{
		HttpSession session = req.getSession(true);

		// Lets get the standard SESSION parameters and validate them
		Properties params = super.getSessionParameters(req) ;

		// Lets get an user object
		imcode.server.user.UserDomainObject user = super.getUserObj(req ) ;
		if(user == null) return ;

		if ( !isUserAuthorized( req, res, user ) ){
			return;
		}

		// Lets get all parameters in a string which we'll send to every servlet in the frameset
		String paramStr = MetaInfo.passMeta(params) ;

		//lets clean up some in the session just incase
		session.removeAttribute("checkBoxTextarr"); //ska tas bort
		session.removeAttribute("chatParams");
		session.removeAttribute("chatChecked");

		//lets crete the ChatMember object and add it to the session if there isnt anyone
		ChatMember myMember = (ChatMember) session.getAttribute("theChatMember");
		if (myMember == null){
			log("there wasn't any member so return");
		}


        ChatMember member = (ChatMember)session.getAttribute( "theChatMember" );
        ChatSessionsSingleton.putSession( member, session );

        String _recipient =  req.getParameter( "recipient" ) == null ? "0" : req.getParameter( "recipient" ).trim();

		// Lets build the Responsepage
		Vector tags = new Vector();
        tags.add( "#CHAT_MESSAGES#" );
        tags.add( "ChatBoard?" + paramStr );
        tags.add( "#CHAT_CONTROL#" );
        tags.add( "ChatControl?" + paramStr + "&recipient=" + _recipient );
		this.sendHtml(req,res,tags, HTML_TEMPLATE,null) ;
		return ;
	}//end doGet

} // End of class
