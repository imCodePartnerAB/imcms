package imcode.server.user;

import com.mockobjects.ExpectationList;
import com.mockobjects.MockObject;
import imcode.readrunner.ReadrunnerUserData;
import imcode.server.*;
import imcode.server.db.ConnectionPool;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.TemplateDomainObject;
import imcode.server.document.TextDocumentTextDomainObject;
import imcode.server.parser.ParserParameters;
import imcode.util.poll.PollHandlingSystem;
import imcode.util.shop.ShoppingOrderSystem;

import java.io.File;
import java.io.IOException;
import java.util.*;

class MockIMCServiceInterface extends MockObject implements IMCServiceInterface {

   private final ExpectationList sqlProcedureCalls = new ExpectationList( "sqlProcedureCalls" );

   private final ExpectationList sqlUpdateProcedureCalls = new ExpectationList( "sqlUpdateProcedureCalls" );

   private ArrayList expectedSQLResults = new ArrayList();

   public void addExpectedSQLProcedureCall( String sqlProcedure, String[] sqlResult ) {
      this.sqlProcedureCalls.addExpected( sqlProcedure );
      expectedSQLResults.add( sqlResult );
   }

   public void addExpectedSQLUpdateProcedureCall( String sqlUpdateProcedure ) {
      this.sqlUpdateProcedureCalls.addExpected( sqlUpdateProcedure );
   }

   public UserDomainObject verifyUser( String login, String password ) {
      return null;
   }

   public UserDomainObject getUserById( int userId ) {
      return null;
   }

   public boolean checkUserAdminrole( int userId, int adminRole ) {
      return false;
   }

   public void saveText( UserDomainObject user, int meta_id, int txt_no, TextDocumentTextDomainObject text, String text_type ) {
   }

   public TextDocumentTextDomainObject getText( int meta_id, int txt_no ) {
      return null;
   }

   public String parsePage( DocumentRequest docReq, int flags, ParserParameters paramsToParse ) throws IOException {
      return null;
   }

   // Save an image
   public void saveImage( int meta_id, UserDomainObject user, int img_no, Image image ) {
   }

   public void deleteDocAll( int meta_id, UserDomainObject user ) {
   }

   public void addExistingDoc( int meta_id, UserDomainObject user, int existing_meta_id, int doc_menu_no ) {
   }

   public void saveManualSort( int meta_id, UserDomainObject user, Vector childs, Vector sort_no ) {
   }

   public void deleteChilds( int meta_id, int menu, UserDomainObject user, String childsThisMenu[] ) {
   }

    public void updateLogs( String logMessage ) {
        //To change body of implemented methods use Options | File Templates.
    }

    public int sqlUpdateQuery( String sqlStr, String[] params ) {
        return 0;  //To change body of implemented methods use Options | File Templates.
    }

    public ConnectionPool getConnectionPool() {
        return null;  //To change body of implemented methods use Options | File Templates.
    }

    // archive childs
   public void archiveChilds( int meta_id, UserDomainObject user, String childsThisMenu[] ) {
   }

   public String[] copyDocs( int meta_id, int doc_menu_no, UserDomainObject user, String[] childsThisMenu, String copyPrefix ) {
      return new String[0];
   }

   // save textdoc
   public void saveTextDoc( IMCService service, UserDomainObject user, int meta_id, Table doc ) {
   }

   // Save a url_doc
   public void saveUrlDoc( int meta_id, UserDomainObject user, Table doc ) {
   }

   // Save a new url_doc
   public void saveNewUrlDoc( int meta_id, UserDomainObject user, Table doc ) {
   }

   // check if url doc
   public Table isUrlDoc( int meta_id, UserDomainObject user ) {
      return null;
   }

   // Save a new frameset
   public void saveNewFrameset( int meta_id, UserDomainObject user, Table doc ) {
   }

   // Save a frameset
   public void saveFrameset( int meta_id, UserDomainObject user, Table doc ) {
   }

   // check if url doc
   public String isFramesetDoc( int meta_id, UserDomainObject user ) {
      return null;
   }

   // check if external doc
   public ExternalDocType isExternalDoc( int meta_id, UserDomainObject user ) {
      return null;
   }

   // remove child from child table
   public void removeChild( int meta_id, int parent_meta_id, UserDomainObject user ) {
   }

   // activate child to child table
   public void activateChild( int meta_id, UserDomainObject user ) {
   }

    // Parse doc replace variables with data
   public String parseDoc( String htmlStr, Vector variables ) {
      return null;
   }

   // Send a sqlquery to the database and return a string array
   public String[] sqlQuery( String sqlQuery ) {
      return new String[0];
   }

   // Send a sql update query to the database
   public int sqlUpdateQuery( String sqlStr ) {
       return 0;
   }

   // Send a sqlquery to the database and return a string
   public String sqlQueryStr( String sqlQuery ) {
      return null;
   }

   // Send a procedure to the database and return a string array
   public String[] sqlProcedure( String procedure ) {
      return sqlProcedure( procedure, new String[0] );
   }

   // Send a procedure to the database and return a string array
   public String[] sqlProcedure( String procedure, String[] params ) {
      this.sqlProcedureCalls.addActual( procedure );
      return (String[])expectedSQLResults.remove( 0 );
   }

   // Send a procedure to the database and return a string array
   public String[] sqlProcedure( String procedure, String[] params, boolean trim ) {
      return new String[0];
   }

   // Send a procedure to the database and return a string
   public String sqlProcedureStr( String procedure ) {
      return "3";
   }

   // Send a procedure to the database and return a string
   public String sqlProcedureStr( String procedure, String[] params ) {
      return null;
   }

   // Send a procedure to the database and return a string
   public String sqlProcedureStr( String procedure, String[] params, boolean trim ) {
      return null;
   }

   // Send a update procedure to the database
   public int sqlUpdateProcedure( String procedure ) {
      return sqlUpdateProcedure( procedure, new String[0] );
   }

   // Send a update procedure to the database
   public int sqlUpdateProcedure( String procedure, String[] params ) {
      sqlUpdateProcedureCalls.addActual( procedure );
      return 0;
   }

   // Parse doc replace variables with data, uses two vectors
   public String parseDoc( String htmlStr, Vector variables, Vector data ) {
      return null;
   }

   // get external template folder
   public File getExternalTemplateFolder( int meta_id ) {
      return null;
   }

   // increment session counter
   public int incCounter() {
      return 0;
   }

   // get session counter
   public int getCounter() {
      return 0;
   }

   // set session counter
   public int setCounter( int value ) {
      return 0;
   }

   // set  session counter date
   public boolean setCounterDate( String date ) {
      return false;
   }

   // set  session counter date
   public String getCounterDate() {
      return null;
   }

   // Send a sqlquery to the database and return a string array and metadata
   public String[] sqlQueryExt( String sqlQuery ) {
      return new String[0];
   }

   // Send a procedure to the database and return a string array
   public String[] sqlProcedureExt( String procedure ) {
      return new String[0];
   }

   // Send a sqlquery to the database and return a Hashtable
   public Hashtable sqlQueryHash( String sqlQuery ) {
      return null;
   }

   // Send a procedure to the database and return a Hashtable
   public Hashtable sqlProcedureHash( String procedure ) {
      return null;
   }

   // parsedoc use template
   public String parseDoc( List variables, String admin_template_name, String lang_prefix ) {
      return null;
   }

   // parseExternaldoc use template
   public String parseExternalDoc( Vector variables, String external_template_name, String lang_prefix, String doc_type ) {
      return null;
   }

   // parseExternaldoc use template
   public String parseExternalDoc( Vector variables, String external_template_name, String lang_prefix, String doc_type, String templateSet ) {
      return null;
   }

   // get templatehome
   public byte[] getTemplateData( int template_id ) throws IOException {
      return new byte[0];
   }

   // get templatehome
   public File getTemplateHome() {
      return null;
   }

   // get url-path to images
   public String getImageUrl() {
      return null;
   }

   // get file-path to images
   public File getImagePath() {
      return null;
   }

   // get starturl
   public String getStartUrl() {
      return null;
   }

   // get language
   public String getLanguage() {
      return null;
   }

   // get doctype
   public int getDocType( int meta_id ) {
      return 0;
   }

   // checkDocAdminRights
   public boolean checkDocAdminRights( int meta_id, UserDomainObject user ) {
      return false;
   }

   //get greatest permission_set
   public int getUserHighestPermissionSet( int meta_id, int user_id ) {
      return 0;
   }

   // save template to disk
   public int saveTemplate( String name, String file_name, byte[] data, boolean overwrite, String lang_prefix ) {
      return 0;
   }

   // get demo template data
   public Object[] getDemoTemplate( int template_id ) throws IOException {
      return new Object[0];
   }

   // check if user can view internalDocument
   public boolean checkDocRights( int meta_id, UserDomainObject user ) {
      return false;
   }

   public boolean checkDocAdminRights( int meta_id, UserDomainObject user, int permissions ) {
      return false;
   }

   public boolean checkDocAdminRightsAny( int meta_id, UserDomainObject user, int permissions ) {
      return false;
   }

   // delete template from db/disk
   public void deleteTemplate( int template_id ) {
   }

   // save demo template
   public int saveDemoTemplate( int template_id, byte[] data, String suffix ) {
      return 0;
   }

   // save templategroup
   public void saveTemplateGroup( String group_name, UserDomainObject user ) {
   }

   // delete templategroup
   public void deleteTemplateGroup( int group_id ) {
   }

   // save templategroup
   public void changeTemplateGroupName( int group_id, String new_name ) {
   }

   //  unassign template from templategroups
   public void unAssignTemplate( int template_id, int group_id[] ) {
   }

   // Send a procedure to the database and return a multistring array
   public String[][] sqlProcedureMulti( String procedure ) {
      return new String[0][];
   }

   // Send a procedure to the database and return a multistring array
   public String[][] sqlProcedureMulti( String procedure, String[] params ) {
      return new String[0][];
   }

   // Send a sqlQuery to the database and return a multistring array
   public String[][] sqlQueryMulti( String sqlQuery ) {
      return new String[0][];
   }

   // get server date
   public Date getCurrentDate() {
      return null;
   }

   // get demotemplates
   public String[] getDemoTemplateList() {
      return new String[0];
   }

   // delete demotemplate
   public int deleteDemoTemplate( int template_id ) {
      return 0;
   }

   public String getMenuButtons( int meta_id, UserDomainObject user ) {
      return null;
   }

   public String getMenuButtons( String meta_id, UserDomainObject user ) {
      return null;
   }

   public String getLanguage( String lang_id ) {
      return null;
   }

   public SystemData getSystemData() {
      return null;
   }

   public void setSystemData( SystemData sd ) {
   }

   // Get the information for each selected metaid. Used by existing documents
   // Wow. Wonderful methodname. Indeed. Just beautiful.
   public Hashtable ExistingDocsGetMetaIdInfo( String[] meta_id ) {
      return null;
   }

   public String[] getDocumentTypesInList( String langPrefixStr ) {
      return new String[0];
   }

   public Hashtable getDocumentTypesInHash( String langPrefixStr ) {
      return null;
   }

   public boolean checkUserDocSharePermission( UserDomainObject user, int meta_id ) {
      return false;
   }

   public String getInclude( String path ) throws IOException {
      return null;
   }

   public String getFortune( String path ) throws IOException {
      return null;
   }

   public String getSearchTemplate( String path ) throws IOException {
      return null;
   }

   public File getInternalTemplateFolder( int meta_id ) {
      return null;
   }

   public void touchDocument( int meta_id, Date date ) {
   }

   public void touchDocument( int meta_id ) {
   }

   public List getQuoteList( String quoteListName ) throws IOException {
      return null;
   }

   public void setQuoteList( String quoteListName, List quoteList ) throws IOException {
   }

   public List getPollList( String pollListName ) throws IOException {
      return null;
   }

   public void setPollList( String pollListName, List pollList ) throws IOException {
   }

   public DocumentDomainObject getDocument( int meta_id ) {
      return null;
   }

   public String getSection( int meta_id ) {
      return null;
   }

   public String getFilename( int meta_id ) {
      return null;
   }

   public TemplateDomainObject getTemplate( int meta_id ) {
      return null;
   }

   public boolean checkAdminRights( UserDomainObject user ) {
      return false;
   }

   public void setReadrunnerUserData( UserDomainObject user, ReadrunnerUserData rrUserData ) {
   }

   public ReadrunnerUserData getReadrunnerUserData( UserDomainObject user ) {
      return null;
   }

   public Map getTexts( int meta_id ) {
      return null;
   }

   public int getSessionCounter() {
      return 0;
   }

   public String getSessionCounterDate() {
      return null;
   }

   public Map getUserFlags() {
      return null;
   }

   public Map getUserFlags( UserDomainObject user ) {
      return null;
   }

   public Map getUserFlags( int type ) {
      return null;
   }

   public Map getUserFlags( UserDomainObject user, int type ) {
      return null;
   }

   public void setUserFlag( UserDomainObject user, String flagName ) {
   }

   public void unsetUserFlag( UserDomainObject user, String flagName ) {
   }

   public PollHandlingSystem getPollHandlingSystem() {
      return null;
   }

   public ShoppingOrderSystem getShoppingOrderSystem() {
      return null;
   }

    public void updateModifiedDatesOnDocumentAndItsParent( int metaId, Date dateTime ) {
    }

    public String[] sqlQuery( String sqlQuery, String[] params ) {
        return new String[0];
    }

}