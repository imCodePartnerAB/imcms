package com.imcode.imcms.servlet.chat;

import imcode.external.chat.*;
import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.ListIterator;
import java.util.Vector;

public class ChatBoard extends ChatBase {

    private final static String HTML_TEMPLATE = "chat_messages.html";
    private final static String HTML_HR = "last_msg_hr.html";
    private final static String CHAT_KICKOUT_TEMPLATE = "chat_kickout_message.html";

    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException{
        res.setContentType("text/html");

        Utility.setNoCache(res);

	// Lets get the user object
	imcode.server.user.UserDomainObject user = getUserObj(req ) ;
	if(user == null) return ;

	if ( !isUserAuthorized( req, res, user ) ){
	    log("user not authorized");
	    return;
	}

	// Lets get serverinformation
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;

        ChatMember myMember = getChatMember( req );

        if ( myMember.isKickedOut()){

            String result = ChatControl.getParsedChatLeavePage(myMember, imcref, CHAT_KICKOUT_TEMPLATE);
            res.getWriter().write(result);

            HttpSession session = ChatSessionsSingleton.getSession(myMember );
            cleanUpSessionParams(session);
            return;

        }

	//this buffer is used to store all the msgs to send to the page
        StringBuffer sendMsgString = new StringBuffer( "" );

        Chat myChat = myMember.getParent();

        String libName = getTemplateLibName( myChat.getChatId());

        boolean showPrivateMessages = myMember.isShowPrivateMessagesEnabled();
        boolean autoReload = myMember.isAutoRefreshEnabled();
        String time = myMember.getRefreshTime() + "";
        String chatRefresh = getChatRefresh( autoReload, time, req );

	    int lastMsgInt = myMember.getLastMsgNr();
	    //let's get all the messages
        ListIterator msgIter = myMember.getMessages();

	    //lets fix the html-string containing all messags
        while ( msgIter.hasNext() ) {
            ChatMessage message = (ChatMessage)msgIter.next();

            if ( lastMsgInt == message.getIdNumber() ) {
                sendMsgString.append( imcref.getAdminTemplateFromSubDirectoryOfDirectory( HTML_HR, user, null, "103", getTemplateLibName( myChat.getChatId() ) ) );
                //sendMsgString.append( "<hr>\n" );

		    }

            sendMsgString.append(message.getLine( showPrivateMessages, myMember, imcref, user, libName ));
	    }//end while loop

        Vector tags = new Vector();
        tags.add( "#CHAT_REFRESH#" );
        tags.add( chatRefresh );
        tags.add( "#CHAT_MESSAGES#" );
        tags.add( sendMsgString.toString() );

        this.sendHtml( req, res, tags, HTML_TEMPLATE, null );

        myMember.setLastRequest(new Date());

    }

    private static String getChatRefresh( boolean autoReload, String time, HttpServletRequest req ) {
        String chatRefresh = "";
        if ( autoReload ) {
            chatRefresh = "<META HTTP-EQUIV=\"Refresh\" CONTENT=\"" + time + ";URL=" + req.getRequestURI() + "\">";

        }
        return chatRefresh;
	}

    private ChatMember getChatMember( HttpServletRequest req ) {
        // Lets get parameters
        HttpSession session = req.getSession( false );

        ChatMember myMember = (ChatMember)session.getAttribute( "theChatMember" );
        return myMember;
    }


} // End of class
