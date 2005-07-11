package imcode.server;

import imcode.server.db.Database;
import imcode.server.db.impl.MockDatabase;
import com.imcode.imcms.mapping.DefaultDocumentMapper;
import com.imcode.imcms.mapping.CategoryMapper;
import imcode.server.document.TemplateMapper;
import imcode.server.parser.ParserParameters;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.net.SMTP;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.text.Collator;
import java.util.*;

public class MockImcmsServices implements ImcmsServices {

    private ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper;

    private Database database = new MockDatabase();
    private KeyStore keyStore;
    private TemplateMapper templateMapper;
    private DefaultDocumentMapper documentMapper;
    private CategoryMapper categoryMapper;
    private LanguageMapper languageMapper = new LanguageMapper(null, null);

    public UserDomainObject verifyUser( String login, String password ) {
        return null;
    }

    public String parsePage( ParserParameters paramsToParse ) throws IOException {
        return null;
    }

    public String isFramesetDoc( int meta_id ) {
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

    // get templatehome
    public String getTemplateData( int template_id ) throws IOException {
        return null;
    }

    // get templatehome
    public File getTemplatePath() {
        return null;
    }

    // get doctype
    public int getDocType( int meta_id ) {
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

    public int getSessionCounter() {
        return 0;
    }

    public String getSessionCounterDateAsString() {
        return null;
    }

    public void updateMainLog( String logMessage ) {

    }

    public DefaultDocumentMapper getDefaultDocumentMapper() {
        return documentMapper;
    }

    public ImcmsAuthenticatorAndUserAndRoleMapper getImcmsAuthenticatorAndUserAndRoleMapper() {
        return imcmsAuthenticatorAndUserAndRoleMapper ;
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
        return database ;
    }

    public CategoryMapper getCategoryMapper() {
        return categoryMapper ;
    }

    public LanguageMapper getLanguageMapper() {
        return languageMapper;
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

    public void setDocumentMapper( DefaultDocumentMapper documentMapper ) {
        this.documentMapper = documentMapper;
    }

    public void setCategoryMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public void setLanguageMapper(LanguageMapper languageMapper) {
        this.languageMapper = languageMapper;
    }
}
