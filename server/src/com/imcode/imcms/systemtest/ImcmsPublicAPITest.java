package com.imcode.imcms.systemtest;

import com.meterware.httpunit.WebResponse;

public class ImcmsPublicAPITest extends ImcmWebApplicationTests {

    final static String URI_API_CATALOGUE = URI_WEB_APP_ROOT  + "apisamples/";
    final static String URI_API_INDEX_PAGE = "index.html";

    public void testGetSampleIndexPage() throws Exception {
        WebResponse indexPage = conversation.getResponse( URI_API_CATALOGUE + URI_API_INDEX_PAGE );
        assertTrue( 0 < indexPage.getLinks().length );
    }

    public void testGetLoggedInUser() throws Exception {
        String pageName = "logged_in_user.jsp";
        WebResponse page = getPageAndTestNotError( pageName );
        assertTrue( -1 != page.getText().indexOf("user") );
    }

    public void testUserListing() throws Exception {
        String pageName = "user_listing.jsp";
        WebResponse page = getPageAndTestNotError( pageName );
        assertTrue( -1 != page.getText().indexOf("user"));
        assertTrue( -1 != page.getText().indexOf("admin"));
        assertTrue( -1 != page.getText().indexOf("TestUser"));
    }

    public void testListAllRoles() throws Exception {
        String pageName = "role_list_all.jsp";
        WebResponse page = getPageAndTestNotError( pageName );
        assertTrue( -1 != page.getText().indexOf("Superadmin"));
        assertTrue( -1 != page.getText().indexOf("Useradmin"));
        assertTrue( -1 != page.getText().indexOf("Users"));
        assertTrue( -1 != page.getText().indexOf("LDAP"));
    }

    public void testAddRole() throws Exception {
        String pageName = "role_create_role.jsp";
        WebResponse page = conversation.getResponse( URI_API_CATALOGUE + pageName );
        assertTrue( page.getTitle().equalsIgnoreCase("Error") );
        logIn( "admin", "admin" );
        assertTrue( -1 != getPageAndTestNotError( pageName ).getText().indexOf("Test role"));
    }

    private WebResponse getPageAndTestNotError( String pageName ) throws Exception {
        WebResponse page = conversation.getResponse( URI_API_CATALOGUE + pageName );
        assertFalse( page.getTitle().equalsIgnoreCase("Error") );
        return page;
    }
}
