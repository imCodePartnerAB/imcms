package imcode.server;

import imcode.server.db.Database;
import imcode.server.db.DatabaseCommand;
import imcode.server.document.DocumentMapper;
import imcode.server.document.TemplateMapper;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.parser.ParserParameters;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Clock;
import imcode.util.net.SMTP;
import imcode.util.poll.PollHandlingSystem;
import imcode.util.shop.ShoppingOrderSystem;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.*;
import java.security.KeyStore;

public class MockImcmsServices implements ImcmsServices {

    private ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper;

    private Database database ;
    private KeyStore keyStore;
    private TemplateMapper templateMapper;

    public UserDomainObject verifyUser( String login, String password ) {
        return null;
    }

    public TextDomainObject getText( int meta_id, int txt_no ) {
        return null;
    }

    public String parsePage( ParserParameters paramsToParse ) throws IOException {
        return null;
    }

    public String isFramesetDoc( int meta_id ) {
        return null;
    }

    // Send a procedure to the database and return a string array
    public String[] sqlProcedure( String procedure, String[] params ) {
        return database.sqlProcedure( procedure, params ) ;
    }

    // get external template folder
    public File getExternalTemplateFolder( int meta_id, UserDomainObject user ) {
        return null;
    }

    public void incrementSessionCounter() {

    }

    // set session counter
    public void setSessionCounter( int value ) {

    }

    // set  session counter date
    public void setSessionCounterDate( Date date ) {

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
        return null;
    }

    // parseExternaldoc use template
    public String getTemplateFromSubDirectoryOfDirectory( String adminTemplateName, UserDomainObject user,
                                                          List variables, String directory, String subDirectory ) {
        return null;
    }

    // get templatehome
    public String getTemplateData( int template_id ) throws IOException {
        return null;
    }

    // get templatehome
    public File getTemplatePath() {
        return null;
    }

    // get file-path to imcmsimages
    public File getImcmsPath() {
        return null;
    }

    // get language prefix by id
    public String getLanguagePrefixByLangId( int lang_id ) {
        return null;
    }

    // get language prefix for user
    public String getUserLangPrefixOrDefaultLanguage( UserDomainObject user ) {
        return null;
    }

    // get doctype
    public int getDocType( int meta_id ) {
        return 0;
    }

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
    public void saveDemoTemplate( int template_id, byte[] data, String suffix ) throws IOException {

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
    public void deleteDemoTemplate( int template_id ) throws IOException {

    }

    public SystemData getSystemData() {
        return null;
    }

    public void setSystemData( SystemData sd ) {

    }

    public String[][] getAllDocumentTypes( String langPrefixStr ) {
        return new String[0][];
    }

    public String getFortune( String path ) throws IOException {
        return null;
    }

    public List getQuoteList( String quoteListName ) {
        return null;
    }

    public void setQuoteList( String quoteListName, List quoteList ) throws IOException {

    }

    public List getPollList( String pollListName ) {
        return null;
    }

    public void setPollList( String pollListName, List pollList ) throws IOException {

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

    public void updateMainLog( String logMessage ) {

    }

    public DocumentMapper getDocumentMapper() {
        return null;
    }

    public ImcmsAuthenticatorAndUserAndRoleMapper getImcmsAuthenticatorAndUserAndRoleMapper() {
        return imcmsAuthenticatorAndUserAndRoleMapper ;
    }

    public String getDefaultLanguage() {
        return null;
    }

    public Map sqlProcedureHash( String procedure, String[] params ) {
        return database.sqlProcedureHash( procedure, params ) ;
    }

    public int sqlUpdateProcedure( String procedure, String[] params ) {
        return database.sqlUpdateProcedure( procedure, params ) ;
    }

    public String sqlProcedureStr( String procedure, String[] params ) {
        return database.sqlProcedureStr( procedure, params ) ;
    }

    public int sqlUpdateQuery( String sqlStr, String[] params ) {
        return database.sqlUpdateQuery( sqlStr, params ) ;
    }

    public String[][] sqlProcedureMulti( String procedure, String[] params ) {
        return database.sqlProcedureMulti( procedure, params );
    }

    public String[] sqlQuery( String sqlStr, String[] params ) {
        return database.sqlQuery( sqlStr, params ) ;
    }

    public String sqlQueryStr( String sqlStr, String[] params ) {
        return database.sqlQueryStr( sqlStr, params ) ;
    }

    public String[][] sqlQueryMulti( String sqlstr, String[] params ) {
        return database.sqlQueryMulti( sqlstr, params ) ;
    }

    public void executeTransaction( DatabaseCommand databaseCommand ) {

    }

    public TemplateMapper getTemplateMapper() {
        return templateMapper;
    }

    public SMTP getSMTP() {
        return null;
    }

    public Properties getLanguageProperties( UserDomainObject user ) {
        return null;
    }

    public File getIncludePath() {
        return null;
    }

    public Collator getDefaultLanguageCollator() {
        return null;
    }

    public VelocityEngine getVelocityEngine( UserDomainObject user ) {
        return null;
    }

    public VelocityContext getVelocityContext( UserDomainObject user ) {
        return null;
    }

    public Config getConfig() {
        return null;
    }

    public Database getDatabase() {
        return database;
    }

    public Clock getClock() {
        return null;
    }

    public File getRealContextPath() {
        return null;
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public void setImcmsAuthenticatorAndUserAndRoleMapper(
            ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper ) {
        this.imcmsAuthenticatorAndUserAndRoleMapper = imcmsAuthenticatorAndUserAndRoleMapper;
    }

    public void setDatabase( Database database ) {
        this.database = database;
    }

    public void setKeyStore( KeyStore keyStore ) {
        this.keyStore = keyStore;
    }

    public void setTemplateMapper( TemplateMapper templateMapper ) {
        this.templateMapper = templateMapper;
    }
}
