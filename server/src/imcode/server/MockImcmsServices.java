package imcode.server;

import imcode.server.db.Database;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.TemplateMapper;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.parser.ParserParameters;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.net.SMTP;
import imcode.util.poll.PollHandlingSystem;
import imcode.util.shop.ShoppingOrderSystem;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.*;

public class MockImcmsServices implements ImcmsServices {

    private ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper;

    private List sqlCalls = new ArrayList() ;
    private Map expectedSqlCalls = new HashMap() ;

    public UserDomainObject verifyUser( String login, String password ) {
        return null;  // TODO
    }

    public TextDomainObject getText( int meta_id, int txt_no ) {
        return null;  // TODO
    }

    public String parsePage( ParserParameters paramsToParse ) throws IOException {
        return null;  // TODO
    }

    public String isUrlDoc( int meta_id ) {
        return null;  // TODO
    }

    public String isFramesetDoc( int meta_id ) {
        return null;  // TODO
    }

    // Send a procedure to the database and return a string array
    public String[] sqlProcedure( String procedure, String[] params ) {
        sqlCalls.add( new SqlCall(procedure, params) ) ;
        return null ;
    }

    // get external template folder
    public File getExternalTemplateFolder( int meta_id, UserDomainObject user ) {
        return null;  // TODO
    }

    public void incrementSessionCounter() {
        // TODO
    }

    // set session counter
    public void setSessionCounter( int value ) {
        // TODO
    }

    // set  session counter date
    public void setSessionCounterDate( Date date ) {
        // TODO
    }

    // set  session counter date
    public Date getSessionCounterDate() {
        return null;  // TODO
    }

    // parsedoc use template
    public String getAdminTemplate( String adminTemplateName, UserDomainObject user, List tagsWithReplacements ) {
        return null;  // TODO
    }

    // parseExternaldoc use template
    public String getTemplateFromDirectory( String adminTemplateName, UserDomainObject user, List variables,
                                            String directory ) {
        return null;  // TODO
    }

    // parseExternaldoc use template
    public String getTemplateFromSubDirectoryOfDirectory( String adminTemplateName, UserDomainObject user,
                                                          List variables, String directory, String subDirectory ) {
        return null;  // TODO
    }

    // get templatehome
    public String getTemplateData( int template_id ) throws IOException {
        return null;  // TODO
    }

    // get templatehome
    public File getTemplatePath() {
        return null;  // TODO
    }

    // get file-path to imcmsimages
    public File getImcmsPath() {
        return null;  // TODO
    }

    // get language prefix by id
    public String getLanguagePrefixByLangId( int lang_id ) {
        return null;  // TODO
    }

    // get language prefix for user
    public String getUserLangPrefixOrDefaultLanguage( UserDomainObject user ) {
        return null;  // TODO
    }

    // get doctype
    public int getDocType( int meta_id ) {
        return 0;  // TODO
    }

    public boolean checkDocAdminRights( int meta_id, UserDomainObject user ) {
        return false;  // TODO
    }

    // save template to disk
    public int saveTemplate( String name, String file_name, byte[] data, boolean overwrite, String lang_prefix ) {
        return 0;  // TODO
    }

    // get demo template data
    public Object[] getDemoTemplate( int template_id ) throws IOException {
        return new Object[0];  // TODO
    }

    public boolean checkDocAdminRights( int meta_id, UserDomainObject user, int permissions ) {
        return false;  // TODO
    }

    public boolean checkDocAdminRightsAny( int meta_id, UserDomainObject user, int permissions ) {
        return false;  // TODO
    }

    // save demo template
    public void saveDemoTemplate( int template_id, byte[] data, String suffix ) throws IOException {
        // TODO
    }

    // get server date
    public Date getCurrentDate() {
        return null;  // TODO
    }

    // get demotemplates
    public String[] getDemoTemplateIds() {
        return new String[0];  // TODO
    }

    // delete demotemplate
    public void deleteDemoTemplate( int template_id ) throws IOException {
        // TODO
    }

    public SystemData getSystemData() {
        return null;  // TODO
    }

    public void setSystemData( SystemData sd ) {
        // TODO
    }

    public String[][] getAllDocumentTypes( String langPrefixStr ) {
        return new String[0][];  // TODO
    }

    public String getFortune( String path ) throws IOException {
        return null;  // TODO
    }

    public List getQuoteList( String quoteListName ) {
        return null;  // TODO
    }

    public void setQuoteList( String quoteListName, List quoteList ) throws IOException {
        // TODO
    }

    public List getPollList( String pollListName ) {
        return null;  // TODO
    }

    public void setPollList( String pollListName, List pollList ) throws IOException {
        // TODO
    }

    public int getSessionCounter() {
        return 0;  // TODO
    }

    public String getSessionCounterDateAsString() {
        return null;  // TODO
    }

    public Map getUserFlags() {
        return null;  // TODO
    }

    public Map getUserFlags( UserDomainObject user ) {
        return null;  // TODO
    }

    public Map getUserFlags( int type ) {
        return null;  // TODO
    }

    public Map getUserFlags( UserDomainObject user, int type ) {
        return null;  // TODO
    }

    public void setUserFlag( UserDomainObject user, String flagName ) {
        // TODO
    }

    public void unsetUserFlag( UserDomainObject user, String flagName ) {
        // TODO
    }

    public PollHandlingSystem getPollHandlingSystem() {
        return null;  // TODO
    }

    public ShoppingOrderSystem getShoppingOrderSystem() {
        return null;  // TODO
    }

    public void updateMainLog( String logMessage ) {
        // TODO
    }

    public DocumentMapper getDocumentMapper() {
        return null;  // TODO
    }

    public ImcmsAuthenticatorAndUserAndRoleMapper getImcmsAuthenticatorAndUserAndRoleMapper() {
        return imcmsAuthenticatorAndUserAndRoleMapper ;
    }

    public String getDefaultLanguageAsIso639_2() {
        return null;  // TODO
    }

    public Map sqlProcedureHash( String procedure, String[] params ) {
        sqlCalls.add( new SqlCall( procedure, params ) );
        return null;
    }

    public int sqlUpdateProcedure( String procedure, String[] params ) {
        sqlCalls.add( new SqlCall( procedure, params ) );
        return 0;
    }

    public String sqlProcedureStr( String procedure, String[] params ) {
        return (String)getSqlCall( procedure, params ).getResult() ;
    }

    private SqlCall getSqlCall( String procedure, String[] params ) {
        SqlCall sqlCall = (SqlCall)expectedSqlCalls.remove( procedure ) ;
        if ( null == sqlCall ) {
            sqlCall = new SqlCall( procedure, params ) ;
        } else {
            sqlCall.setParameters(params) ;
        }
        sqlCalls.add( sqlCall );
        return sqlCall;
    }

    public int sqlUpdateQuery( String sqlStr, String[] params ) {
        sqlCalls.add( new SqlCall( sqlStr, params ) );
        return 0;
    }

    public String[][] sqlProcedureMulti( String procedure, String[] params ) {
        return (String[][])getSqlCall( procedure, params ).getResult();
    }

    public String[] sqlQuery( String sqlStr, String[] params ) {
        return (String[])getSqlCall( sqlStr, params ).getResult();
    }

    public String sqlQueryStr( String sqlStr, String[] params ) {
        return (String)getSqlCall( sqlStr, params ).getResult() ;
    }

    public String[][] sqlQueryMulti( String sqlstr, String[] params ) {
        return (String[][])getSqlCall( sqlstr, params ).getResult();
    }

    public TemplateMapper getTemplateMapper() {
        return null;  // TODO
    }

    public SMTP getSMTP() {
        return null;  // TODO
    }

    public Properties getLanguageProperties( UserDomainObject user ) {
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

    public Config getConfig() {
        return null;  // TODO
    }

    public Database getDatabase() {
        return null;  // TODO
    }

    public void setImcmsAuthenticatorAndUserAndRoleMapper(
            ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper ) {
        this.imcmsAuthenticatorAndUserAndRoleMapper = imcmsAuthenticatorAndUserAndRoleMapper;
    }

    public List getSqlCalls() {
        return sqlCalls;
    }

    public void addExpectedSqlCall( SqlCall sqlCall ) {
        expectedSqlCalls.put(sqlCall.getString(), sqlCall) ;
    }

    public void verifyExpectedSqlCalls() {
        if (!expectedSqlCalls.isEmpty()) {
            throw new junit.framework.AssertionFailedError( "Remaining expected sql calls: "+ expectedSqlCalls.values().toString() );
        }
    }

    public static class SqlCall {
        private String string ;
        private String[] parameters ;
        private Object result;

        public SqlCall( String string, String[] parameters ) {
            this.string = string ;
            this.parameters = parameters ;
        }

        public SqlCall( String string, String[] parameters, Object result ) {
            this(string, parameters) ;
            this.result = result ;
        }

        public String getString() {
            return string;
        }

        public String[] getParameters() {
            return parameters;
        }

        public Object getResult() {
            return result;
        }

        public String toString() {
            return getString() ;
        }

        public void setParameters( String[] parameters ) {
            this.parameters = parameters;
        }
    }
}
