package imcode.server.user;

import com.mockobjects.ExpectationList;
import com.mockobjects.MockObject;
import imcode.server.IMCServiceInterface;
import imcode.server.SystemData;
import imcode.server.db.ConnectionPool;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.TemplateMapper;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.parser.ParserParameters;
import imcode.util.net.SMTP;
import imcode.util.poll.PollHandlingSystem;
import imcode.util.shop.ShoppingOrderSystem;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
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

    public TextDomainObject getText( int meta_id, int txt_no ) {
      return null;
   }

   public String parsePage( ParserParameters paramsToParse ) throws IOException {
      return null;
   }

   // Save an image
   public void saveImage( int meta_id, UserDomainObject user, int img_no, ImageDomainObject image ) {
   }

    public void saveManualSort( int meta_id, UserDomainObject user, List childs, List sort_no, int menuNumber ) {
        // TODO
    }

    public void updateLogs( String logMessage ) {
        //To change body of implemented methods use Options | File Templates.
    }

    public int sqlUpdateQuery( String sqlStr, String[] params ) {
        return 0;  //To change body of implemented methods use Options | File Templates.
    }

    public void saveTreeSortIndex( int meta_id, UserDomainObject user, List childs, List sort_no, int menuNumber ) {
        // TODO
    }

    public ConnectionPool getConnectionPool() {
        return null;  //To change body of implemented methods use Options | File Templates.
    }

    public String sqlQueryStr( String sqlStr, String[] params ) {
        return null;  //To change body of implemented methods use Options | File Templates.
    }

    public Map sqlQueryHash( String sqlStr, String[] params ) {
        return null;  // TODO
    }

    public String[][] sqlQueryMulti( String sqlStr, String[] params ) {
        return new String[0][];  //To change body of implemented methods use Options | File Templates.
    }

    public DocumentMapper getDocumentMapper() {
        return null;
    }

    public ImcmsAuthenticatorAndUserMapper getImcmsAuthenticatorAndUserAndRoleMapper() {
        return null;
    }

    public String getDefaultLanguageAsIso639_2() {
        return null;
    }

    public Map sqlProcedureHash( String procedure, String[] params ) {
        return null;  // TODO
    }

    // archive childs
   public void archiveChilds( int meta_id, UserDomainObject user, String[] childsThisMenu ) {
   }

    // check if url doc
    public String isUrlDoc( int meta_id ) {
        return null;  // TODO
    }

    // check if url doc
   public String isFramesetDoc( int meta_id ) {
      return null;
   }

    // activate child to child table
   public void activateChild( int meta_id, UserDomainObject user ) {
   }

    // Send a procedure to the database and return a string array
   public String[] sqlProcedure( String procedure, String[] params ) {
      this.sqlProcedureCalls.addActual( procedure );
      return (String[])expectedSQLResults.remove( 0 );
   }

    // Send a procedure to the database and return a string
   public String sqlProcedureStr( String procedure, String[] params ) {
      return "100";
   }

    // Send a update procedure to the database
   public int sqlUpdateProcedure( String procedure, String[] params ) {
      sqlUpdateProcedureCalls.addActual( procedure );
      return 0;
   }

   // Parse doc replace variables with data, uses two vectors
   public String replaceTagsInStringWithData( String htmlStr, Vector variables, Vector data ) {
      return null;
   }

   // get external template folder
   public File getExternalTemplateFolder(int meta_id, UserDomainObject user) {
      return null;
   }

    public void incrementSessionCounter() {
        // TODO
    }

    // set session counter
   public void setSessionCounter( int value ) {

   }

    // set  session counter date
    public void setSessionCounterDate( Date date ) {
        // TODO
    }

    // set  session counter date
   public Date getSessionCounterDate() {
      return null;
   }

    // parsedoc use template
   public String getAdminTemplate( String adminTemplateName, UserDomainObject user, List tagsWithReplacements ) {
      return null;
   }

    // parseExternaldoc use template
    public String getTemplateFromDirectory( String adminTemplateName, UserDomainObject user, List variables,
                                                 String directory ) {
        return null;  // TODO
    }

    // parseExternaldoc use template
    public String getTemplateFromSubDirectoryOfDirectory( String adminTemplateName, UserDomainObject user, List variables,
                                                               String directory, String subDirectory ) {
        return null;  // TODO
    }

    // get templatehome
   public String getTemplateData( int template_id ) throws IOException {
      return null;
   }

   // get templatehome
   public File getTemplatePath() {
      return null;
   }

   // get url-path to images
   public String getImageUrl() {
      return null;
   }

    // Return url-path to imcmsimages.
    public String getImcmsUrl() {
        return null;  // TODO
    }

    // get file-path to imcmsimages
    public File getImcmsPath() {
        return null;  // TODO
    }

    // get starturl
   public String getStartUrl() {
      return null;
   }

    // get language prefix by id
    public String getLanguagePrefixByLangId ( int lang_id ){
       return null;
   }

    // get language prefix for user
    public String getUserLangPrefixOrDefaultLanguage(UserDomainObject user) {
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

    // save template to disk
   public int saveTemplate( String name, String file_name, byte[] data, boolean overwrite, String lang_prefix ) {
      return 0;
   }

   // get demo template data
   public Object[] getDemoTemplate( int template_id ) throws IOException {
      return new Object[0];
   }

   public boolean checkDocAdminRights( int meta_id, UserDomainObject user, int permissions ) {
      return false;
   }

   public boolean checkDocAdminRightsAny( int meta_id, UserDomainObject user, int permissions ) {
      return false;
   }

   // save demo template
   public void saveDemoTemplate( int template_id, byte[] data, String suffix ) {

   }

    // Send a procedure to the database and return a multistring array
   public String[][] sqlProcedureMulti( String procedure, String[] params ) {
      return new String[0][];
   }

    // get server date
   public Date getCurrentDate() {
      return null;
   }

   // get demotemplates
   public String[] getDemoTemplateIds() {
      return new String[0];
   }

    // delete demotemplate
    public void deleteDemoTemplate( int template_id ) {
        // TODO
    }

    public String getAdminButtons( UserDomainObject user, DocumentDomainObject document ) {
        return null;  // TODO
    }

    public SystemData getSystemData() {
      return null;
   }

   public void setSystemData( SystemData sd ) {
   }

    public String[][] getAllDocumentTypes( String langPrefixStr ) {
        return new String[0][];  // TODO
    }

    public String getFortune( String path ) throws IOException {
      return null;
   }

    public List getQuoteList( String quoteListName ) {
        return null;  // TODO
    }

    public void setQuoteList( String quoteListName, List quoteList ) throws IOException {
   }

    public List getPollList( String pollListName ) {
        return null;  // TODO
    }

    public void setPollList( String pollListName, List pollList ) throws IOException {
   }

    public TemplateMapper getTemplateMapper() {
        return null;  // TODO
    }

    public SMTP getSMTP() {
        return null;  // TODO
    }

    public Properties getLanguageProperties(UserDomainObject user) {
        return null;
    }

    public File getFilePath() {
        return null;  // TODO
    }

    public File getIncludePath() {
        return null;  // TODO
    }

    public Collator getDefaultLanguageCollator() {
        return null;  // TODO
    }

    public VelocityEngine getVelocityEngine( UserDomainObject user ) {
        return null;  // TODO
    }

    public VelocityContext getVelocityContext( UserDomainObject user ) {
        return null;  // TODO
    }

    public boolean checkAdminRights( UserDomainObject user ) {
      return false;
   }

   public int getSessionCounter() {
      return 0;
   }

   public String getSessionCounterDateAsString() {
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