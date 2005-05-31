package com.imcode.imcms.servlet.chat;

import com.imcode.imcms.servlet.BackDoc;
import imcode.external.chat.*;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.Stack;
import java.util.Vector;

public class ChatLogin extends ChatBase {

    private String LOGIN_HTML = "clogin4.htm";	   // The login page
    private String LOGIN_ERROR_HTML = "chat_error.htm";

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        ImcmsServices imcref = Imcms.getServices();
        // Lets validate the session, e.g has the user logged in to Janus?
        HttpSession session = req.getSession( true );

        //lets get the ServletContext
        ServletContext myContext = getServletContext();

        // Lets get the user object
        imcode.server.user.UserDomainObject user = super.getUserObj( req );
        if ( user == null ) return;

        String metaId = req.getParameter( "meta_id" );

        int meta_id = Integer.parseInt( metaId );
        if ( !isUserAuthorized( res, meta_id, user, req ) ) {
            log( "user not Authorized" );
            return;
        }

        Stack history = (Stack)user.get( "history" );

        int parentMetaId = BackDoc.getLastTextDocumentFromHistory( history, false, imcref );
        if ( parentMetaId == 0 ) {
            try {
                parentMetaId = Integer.parseInt( req.getParameter( "chat_return_meta_id" ) );
            } catch ( NumberFormatException nfe ) {
                parentMetaId = imcref.getSystemData().getStartDocument();
            }
        }

        //ok lets get the chat
        imcode.external.chat.Chat myChat = (imcode.external.chat.Chat)myContext.getAttribute( "theChat" + metaId );

        if ( myChat == null ) {
            log( "OBS m廛te skapa en ny Chat" );
            myChat = createChat( meta_id );
            myContext.setAttribute( "theChat" + metaId, myChat );
            session.setAttribute( "myChat", myChat );
        }

        if ( session.getAttribute( "theChatMember" ) != null ) {
            res.sendRedirect( "ChatViewer" );
            return;
        }

        //**** sets up the different loginpages  ****

        Vector rolV = myChat.getSelectedAuto();

        //ok lets setup the booleans for the loginpage
        boolean loggedOnOk = false;
        boolean aliasBol;
        boolean imCmsRegBol;

        //sets up the booleans
        //Vector rolV = super.convert2Vector(roles);
        aliasBol = rolV.contains( "1" );
        imCmsRegBol = rolV.contains( "2" );

        //ok lets see if the user realy has loged in or
        if ( !user.getLoginName().equals( "user" ) ) {
            loggedOnOk = true;
        }

        if ( aliasBol && !imCmsRegBol ) {//only alias login
            //log("alt 1");
            LOGIN_HTML = "clogin1.htm";
        } else if ( !aliasBol && imCmsRegBol ) {//only registred imcms users
            if ( loggedOnOk ) {//ok the user has loged in so lets fill in he's name
                //log("alt 2");
                LOGIN_HTML = "clogin2.htm";
            } else {
                //log("alt 3");
                LOGIN_HTML = "clogin3.htm";
            }
        } else {
            if ( loggedOnOk ) {
                //log("alt 5");
                LOGIN_HTML = "clogin5.htm";
            } else {
                //log("alt 4");
                LOGIN_HTML = "clogin4.htm";
            }

        }


        // ********** LOGIN PAGE *********
        // Lets build the Responsepage to the loginpage

        //get chatname, we are using meta_headline as chatname
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument( meta_id ) ;
        String chatName = document.getHeadline() ;

        //ok lets add a alias error msg
        String error_msg = "";

        if ( req.getParameter( "alias" ) != null ) {   // we only get it if the alias already exists
            Vector tags = new Vector();
            tags.add( "#ALIAS#" );
            tags.add( req.getParameter( "alias" ) );
            String libName = getTemplateSetDirectoryName( myChat.getChatId() );
            error_msg = imcref.getTemplateFromSubDirectoryOfDirectory( "alias_error_msg.html", user, tags, "103", libName );
        }
        //get the users username
        String userName = user.getLoginName();
        Vector tags = new Vector();
        tags.add( "#userName#" );
        tags.add( userName );
        tags.add( "#chatName#" );
        tags.add( chatName );
        tags.add( "#parent_meta_id#" );
        tags.add( "" + parentMetaId );
        tags.add( "#IMAGE_URL#" );
        tags.add( this.getExternalImageFolder( req ) );
        tags.add( "#ALIAS#" );
        tags.add( ( req.getParameter( "alias" ) == null ) ? "" : req.getParameter( "alias" ) );
        tags.add( "#ALIAS_ERROR#" );
        tags.add( error_msg );
        sendHtml( req, res, tags, LOGIN_HTML, null );

    }

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        ImcmsServices imcref = Imcms.getServices();

        HttpSession session = req.getSession( true );

        // Lets get the standard parameters and validate them
        Properties params = super.getSessionParameters( req );

        // Lets get the user object
        imcode.server.user.UserDomainObject user = super.getUserObj( req );
        if ( user == null ) return;

        String metaId = params.getProperty( "META_ID" );
        int meta_id = Integer.parseInt( metaId );

        if ( !isUserAuthorized( res, meta_id, user, req ) ) {
            return;
        }

        String loginType = ( req.getParameter( "login_type" ) == null ) ? "" : ( req.getParameter( "login_type" ) );
        log( "Logintype 酺 nu: " + loginType );

        String chatAlias = null;

        //*********logs a user into imcms and into the chat with username *****
        if ( req.getParameter( "loginToImCms" ) != null ) {
            //ok lets see if there is any user whith this id and pw
            log( "Ok, nu f顤s闥er vi verifiera logga in!" );
            Properties lparams = this.getLoginParams( req );

            // Ok, Lets check what the user has sent us. Lets verify the fields the user
            // have had to write freetext in to verify that the sql questions wont go mad.
            String userName = lparams.getProperty( "LOGIN_NAME" );
            String password = lparams.getProperty( "PASSWORD" );

            // Validate loginparams against DB
            String userId = imcref.getDatabase().executeStringProcedure( "GetUserIdFromName", new String[] {userName,
                                                                                                            password} );
            //log("Anv鄚darens id var: " + userId) ;

            // Lets check that we found the user. Otherwise send unvailid username password
            if ( userId == null ) {
                String header = "ChatLogin servlet.";
                ChatError err = new ChatError( req, res, header, 50, LOGIN_ERROR_HTML );
                log( header + err.getErrorMsg() );
                return;
            }

            //ok lets create a user obj and put it into the session

            UserDomainObject oldUser = user;
            user = allowUser( userName, password, imcref );
            user.put( "history", oldUser.get( "history" ) );

            Utility.makeUserLoggedIn(req, user);
            chatAlias = user.getLoginName();
        }

        // ************* LOG A USER INTO A CHAT **************
        //check if the intended chat already exists ServletContext
        ServletContext myContext = getServletContext();
        Chat theChat = (Chat)myContext.getAttribute( "theChat" + metaId );

        if ( theChat == null ) {
            theChat = createChat( meta_id );
            myContext.setAttribute( "theChat" + metaId, theChat );
            session.setAttribute( "myChat", theChat );
        }

        // login with alias
        if ( req.getParameter( "loginAlias" ) != null ) {
            chatAlias = req.getParameter( "alias" ).trim();
            if ( ( "" ).equals( chatAlias ) ) {
                res.sendRedirect( "ChatLogin?meta_id=" + metaId );
                return;
            }
        }

        // login with username
        if ( req.getParameter( "loginUserName" ) != null ) {
            chatAlias = user.getLoginName();
        }

        // we need to see if the username or alias already is in use , and if so send error
        if ( theChat.hasMemberName( chatAlias ) ) {
            //	System.out.println("中中中中中中中中夕n use fix");
            String urlEncodedChatAlias = URLEncoder.encode( chatAlias );
            res.sendRedirect( "ChatLogin?alias=" + urlEncodedChatAlias + "&meta_id=" + meta_id );
            return;
        }

        //ok lets create the chatmember obj
        int parentMetaId = Integer.parseInt( req.getParameter( "parent_meta_id" ) );
        imcode.external.chat.ChatMember myMember = theChat.createChatMember( user, parentMetaId );
        myMember.setName( chatAlias );
        myMember.setIpNr( req.getRemoteHost() );

        ChatGroup myGroup = theChat.getChatGroup();
        myGroup.addNewGroupMember( myMember );

        createEnterMessageAndAddToGroup( theChat, imcref, user, myMember, myGroup, metaId );

        myMember.addMessageHistory();

        session.setAttribute( "theChatMember", myMember );
        super.prepareChatBoardSettings( myMember, req, false );

        //lets redirect to ChatViewer
        String url = "ChatViewer";
        res.sendRedirect( url );
        return;

    }//end doPost

    /**
     * The getLoginParams method gets the login params from the requstobject
     */
    private Properties getLoginParams( HttpServletRequest req ) {
        Properties login = new Properties();
        // Lets get the parameters we know we are supposed to get from the request object
        String login_name = ( req.getParameter( "login_name" ) == null ) ? "" : ( req.getParameter( "login_name" ) );
        String password1 = ( req.getParameter( "password" ) == null ) ? "" : ( req.getParameter( "password" ) );
        login.setProperty( "LOGIN_NAME", login_name.trim() );
        login.setProperty( "PASSWORD", password1.trim() );
        return login;
    }

    /**
     * Test if user exist in the database
     */
    private imcode.server.user.UserDomainObject allowUser( String user_name, String passwd, ImcmsServices imcref ) {
        return imcref.verifyUser( user_name, passwd );
    }

} // End class
