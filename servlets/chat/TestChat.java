
import com.meterware.httpunit.*;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Random;

import junit.framework.TestCase;

/**
 * @author kreiger
 */
public class TestChat extends TestCase {

    //private final static int META_ID_START = 1001; //1003;
    //private final static int META_ID_CHAT = 1018; //10653;

    private final static int META_ID_START = 1001;
    private final static int META_ID_CHAT = 1018;

    private final static int THREAD_COUNT = 25;
    private final static int THREAD_PAUSE = 5000;
    private final static int QUOTE_COUNT = 5;
    private final static int QUOTE_PAUSE = 2000;

    private final static int ESTIMATED_TIME_FOR_ALL_ROBOTS_TO_JOIN = THREAD_COUNT * THREAD_PAUSE  ;
    private final static int ESTIMATED_TIME_FOR_ONE_ROBOT_TO_FINISH_SPEAKING = QUOTE_COUNT * QUOTE_PAUSE ;
    private final static int ESTIMATED_TEST_TIME = ESTIMATED_TIME_FOR_ALL_ROBOTS_TO_JOIN + ESTIMATED_TIME_FOR_ONE_ROBOT_TO_FINISH_SPEAKING;

   // private final static String URL_SERVLET = "http://www.gotland.se/imcms/servlet/";
    private final static String URL_SERVLET = "http://vale:8080/1_7_BRANCH_CHAT/servlet/";
    private final static String URL_LOGIN = URL_SERVLET + "VerifyUser";
    private final static String URL_CHATLOGIN = URL_SERVLET + "ChatLogin";
    private final static String URL_CHATCONTROL = URL_SERVLET + "ChatControl";
    private final static String URL_CHAT = URL_SERVLET + "GetDoc?meta_id=" + META_ID_CHAT;
    private static final String FRAME_MSG = "_ChatMsgFrame";
    private static final String FRAME_CONTROL = "_ChatControlFrame";

    private void loginToImCMS( WebConversation webConversation ) throws IOException, SAXException {
        WebRequest loginRequest = new PostMethodWebRequest( URL_LOGIN );
        //loginRequest.setParameter( "name", "imcode_admin" );
        //loginRequest.setParameter( "passwd", "im92ad" );
        loginRequest.setParameter( "name", "admin" );
        loginRequest.setParameter( "passwd", "admin" );
        WebResponse loginResponse = webConversation.getResponse( loginRequest );
        assertTrue( ( "meta_id=" + META_ID_START ).equals( loginResponse.getURL().getQuery() ) );
    }



    public void testChat() throws SAXException, IOException, InterruptedException {

        System.out.println( "Estimated test time "+ (ESTIMATED_TEST_TIME/1000)+" seconds" );

        long currentTimeMillis = System.currentTimeMillis();

        for ( int i = 0; i < THREAD_COUNT; ++i ) {
            final String testAlias = "Test_" + currentTimeMillis + "_" + i;
            Thread thread = new Thread( "" + i ) {
                public void run() {
                    try {
                        WebConversation webConversation = new WebConversation();
                        loginToImCMS( webConversation );
                        loginToChatAndSaySomething( webConversation, testAlias );
                    } catch ( IOException e ) {
                        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    } catch ( SAXException e ) {
                        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    } catch ( InterruptedException e ) {
                        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    }
                }
            };
            thread.start();
            Thread.sleep( THREAD_PAUSE );
            System.out.println( "Started Thread " + i );
        }
        Thread.sleep(ESTIMATED_TIME_FOR_ONE_ROBOT_TO_FINISH_SPEAKING+1000) ;

    }

    private static void loginToChatAndSaySomething( WebConversation webConversation, String testAlias ) throws IOException, SAXException, InterruptedException {
        WebResponse webResponse = webConversation.getResponse( URL_CHAT );

        assertTrue( webResponse.getURL().getPath().endsWith( "/ChatManager" ) );
        assertTrue( -1 != webResponse.getURL().getQuery().indexOf( "meta_id=" + META_ID_CHAT ) );
        WebForm chatLoginForm = webResponse.getFormWithName( "login" );

        assertEquals( "ChatLogin", chatLoginForm.getAction() );

        assertTrue( chatLoginForm.hasParameterNamed( "rooms" ) );
        String chatName = chatLoginForm.getParameterValue( "chatName" );
        String loginParameter = chatLoginForm.getParameterValue( "login" );

        WebRequest chatLoginRequest = new PostMethodWebRequest( URL_CHATLOGIN );
        chatLoginRequest.setParameter( "chatName", chatName );
        chatLoginRequest.setParameter( "login", loginParameter );
        chatLoginRequest.setParameter( "rooms", "1" );
        chatLoginRequest.setParameter( "alias", testAlias );
        chatLoginRequest.setParameter( "loginAlias", "clicked" );
        try {
            webConversation.getResponse( chatLoginRequest );
        } catch ( HttpInternalErrorException hiee ) {
            System.out.println( hiee.getMessage() );
            fail(hiee.toString()) ;
        }

        for ( int i = 0; i < QUOTE_COUNT; ++i ) {
            saySomething( webConversation, testAlias, i );
            Thread.sleep( QUOTE_PAUSE );
        }

        logoutFromChat(webConversation);

    }

    private static void saySomething( WebConversation webConversation, String testAlias, int quoteIndex ) throws IOException, SAXException {
        String wittyQuote = "Deddu" + quoteIndex;

        WebRequest talkRequest = new PostMethodWebRequest( URL_CHATCONTROL );
        talkRequest.setParameter( "msg", wittyQuote );
        talkRequest.setParameter( "msgTypes", "100" );
        talkRequest.setParameter( "recipient", "0" );
        talkRequest.setParameter( "sendMsg", "clicked" );

        WebResponse talkedResponse = null ;
        try {
            talkedResponse = webConversation.getResponse( talkRequest );
        } catch (HttpInternalErrorException hiee) {
            fail(hiee.toString()) ;
        }
        assertTrue( -1 != talkedResponse.getText().indexOf( "src=\"ChatBoard?meta_id=" + META_ID_CHAT ) );
        assertTrue( -1 != talkedResponse.getText().indexOf( "src=\"ChatControl?meta_id=" + META_ID_CHAT ) );
        assertTrue( -1 != talkedResponse.getSubframeContents( FRAME_MSG ).getText().indexOf( testAlias ) );
        assertTrue( -1 != talkedResponse.getSubframeContents( FRAME_MSG ).getText().indexOf( wittyQuote ) );
    }

    private static void logoutFromChat( WebConversation webConversation ) throws IOException, SAXException {
        WebRequest logoutRequest = new PostMethodWebRequest( URL_CHATCONTROL );
        logoutRequest.setParameter( "logOut", "clicked" );
        WebResponse logoutResponse = webConversation.getResponse( logoutRequest );
        assertTrue( ( "meta_id=" + META_ID_START ).equals( logoutResponse.getURL().getQuery() ) );
    }
}
