import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import java.rmi.* ;
import java.rmi.registry.* ;

import imcode.external.chat.*;


//meningen är att denna ska ladda framesetet och kolla 
//all nödvändig data innan den gör detta

public class ChatViewer extends ChatBase {

	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	private final static String HTML_TEMPLATE = "Chat_Frameset.htm" ;


	public void doPost(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException{
		log("doPost");
		doGet(req,res);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException{
		HttpSession session = req.getSession(true);
		ServletContext myContext = getServletContext();
		
		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;

		// Lets get the standard SESSION parameters and validate them
		Properties params = super.getSessionParameters(req) ;

		if (super.checkParameters(req, res, params) == false){
			log("checkParameters == false so return");
			return;
		}

		// Lets get an user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null) return ;

		if ( !isUserAuthorized( req, res, user ) ){
			return;
		}
		String metaId = params.getProperty("META_ID") ;		
		
		// Lets get the url to the servlets directory
		MetaInfo metaInfo = new MetaInfo() ;
		String servletHome = MetaInfo.getServletPath(req) ;
		// Lets get all parameters in a string which we'll send to every servlet in the frameset	
		String paramStr = metaInfo.passMeta(params) ;
		
		//lets clean up some in the session just incase
		session.removeAttribute("checkBoxTextarr"); //ska tas bort
		session.removeAttribute("chatParams");
		session.removeAttribute("chatChecked");

		//ok lets get the chat from the session
		Chat chat = (Chat) myContext.getAttribute("theChat"+metaId);
		if (chat == null){
			log("the chat was null so we will return");
			return;
		}

		//lets crete the ChatMember object and add it to the session if there isnt anyone
		ChatMember myMember = (ChatMember) session.getAttribute("theChatMember");
		if (myMember == null){
			log("there wasn't any member so return");
		}

		//ok lets see if we have room
		ChatGroup myGroup = myMember.getMyGroup();
		if (myGroup == null){
			log("there wasn't any group so return");
		}
	
		//ok lets see if we have an bindingListener
		if (session.getAttribute("chatBinding") == null){
			session.setAttribute("chatBinding",new ChatBindingListener());
			
		}
		
		// Lets build the Responsepage
		Vector tags = new Vector();
		tags.add("#CHAT_MESSAGES#");tags.add(servletHome + "ChatBoard?" + paramStr);
		tags.add("#CHAT_CONTROL#"); tags.add(servletHome + "ChatControl?" + paramStr ) ;
		this.sendHtml(req,res,tags, HTML_TEMPLATE,null) ;
		log("Nu är ChatViewer klar") ;
		return ;
	}//end doGet


	/**
	Log function, will work for both servletexec and Apache
	**/
	public void log( String str){
		super.log("ChatViewer: " + str) ;
	}

} // End of class
