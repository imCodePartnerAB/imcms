package com.imcode.imcms.systemtest;

import junit.framework.TestCase;
import com.meterware.httpunit.*;

import java.io.IOException;

import org.xml.sax.SAXException;

/**
 * This class runns test from the web client perspective (i.e. simulates a conversation from a browser)
 * and test if the results are correct.
 * Make sure that the databse server is running
 *
 * Read more about HttpUnit on
 * @link http://www.httpunit.org/, start with
 * @link http://www.httpunit.org/doc/cookbook.html
 */
public class ImcmWebApplicationTests extends TestCase {
    static final String URI_WEB_APP_ROOT = "http://localhost:8080/imcms/";

    private static final String URI_LOGIN = "login/index.html";
    private static final String URI_GETDOC = "servlet/GetDoc";
    private static final String URI_LOGOUT = "servlet/LogOut";
    private static final String URI_VERIFY_USER = "servlet/VerifyUser";
    private static final String URI_ACCESS_DENIED = "access_denied.html";

    WebConversation conversation;

    public void setUp() throws Exception {
        conversation = new WebConversation();
        conversation.getResponse( URI_WEB_APP_ROOT + URI_LOGOUT );
    }

    public void testGetLoginPage() throws Exception {
        WebForm form = getLoginFormFromLoginPage();
        assertEquals( "/imcms/" + URI_VERIFY_USER, form.getAction() );
    }

    public void testLogInAdmin() throws Exception {
        String name = "admin";
        String password = "admin";
        String responceUrl = URI_WEB_APP_ROOT + URI_GETDOC + "?meta_id=1001";
        testLogin( name, password, responceUrl );
    }

    public void testLogInAdminFail() throws Exception {
        String name = "admin";
        String password = "asdfasdfasdfasfdadsfasdf";
        String responceUrl = URI_WEB_APP_ROOT + URI_ACCESS_DENIED;
        testLogin( name, password, responceUrl );
    }

    public void testNotLoggedInUserAccessToFirstDocument() throws Exception {
        WebResponse resp = getDoc( 1001 );
        WebTable[] tables = resp.getTables();
        assertEquals( 1, tables.length );
    }

    public void testMultipleCallsGetDocAsAnonumous() throws Exception {
        GetMethodWebRequest webRequest = new GetMethodWebRequest( URI_WEB_APP_ROOT + URI_GETDOC + "?meta_id=1001" );
        int noOfRequests = 10;
        double startTime = System.currentTimeMillis();
        for( int i = 0; i < noOfRequests ; i++ ) {
            conversation.getResponse( webRequest );
        }
        double stopTime = System.currentTimeMillis();
        double averageResponseTime = ((stopTime-startTime)/noOfRequests)/1000;
        boolean passedTimelimit = averageResponseTime < 0.2;
        assertTrue( passedTimelimit );
        if( !passedTimelimit ) {
            System.out.println( "Average execution time of GetDoc?1001 for user = " + averageResponseTime + " seconds" );
        }
    }

    private WebResponse getDoc( int meta_id ) throws IOException, SAXException {
        WebResponse resp = conversation.getResponse( URI_WEB_APP_ROOT + URI_GETDOC + "?meta_id=" + meta_id );
        return resp;
    }

    private void testLogin( String name, String password, String responceUrl ) throws Exception {
        WebResponse loginResponse = logIn( name, password );
        assertEquals( responceUrl, loginResponse.getURL().toExternalForm());
    }

    WebResponse logIn( String name, String password ) throws Exception {
        WebForm form = getLoginFormFromLoginPage();

        form.setParameter( "name", name );
        form.setParameter( "passwd", password );

        SubmitButton submit = form.getSubmitButton( "Logga in" );
        WebResponse loginResponse = form.submit(submit);
        return loginResponse;
    }

    WebForm getLoginFormFromLoginPage() throws Exception {
        WebResponse loginPage = conversation.getResponse( URI_WEB_APP_ROOT + URI_LOGIN );
        WebForm form = loginPage.getForms()[0];
        return form;
    }
}
